using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Payment;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;
using DotNetAPI.Models.Expense;
using Microsoft.Extensions.Configuration;
using DotNetAPI.Services.Interface;
using DotNetAPI.Models.Debt;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Service;
using DotNetAPI.Models.PaymentType;

namespace DotNetAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PaymentController : ControllerBase
    {
        private readonly IPaymentService _paymentService;
        private readonly IConfiguration _configuration;
        private readonly IUtils _utils;

        public PaymentController(IPaymentService paymentService, IConfiguration configuration, IUtils utils)
        {
            _paymentService = paymentService;
            _configuration = configuration;
            _utils = utils;
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> CreatePayment([FromForm] PaymentDTO paymentDto)
        {
            if (paymentDto == null)
            {
                return BadRequest("Payment request is null.");
            }

            try
            {
                var payment = await _paymentService.CreatePayment(paymentDto.UserId, paymentDto.GroupId, paymentDto.Amount, paymentDto.DebtAdjustmentId, paymentDto.type);

                string fileName = "expense" + Path.GetExtension(paymentDto.Image.FileName);
                var s3Paths = _configuration.GetSection("S3Paths");
                string paymentPath = s3Paths["Payment"];
                string s3ImagePath = paymentPath + payment.Id + "/" + fileName;

                using (var memoryStream = new MemoryStream())
                {
                    await paymentDto.Image.CopyToAsync(memoryStream);
                    memoryStream.Position = 0;
                    await _utils.UploadFileAsync(memoryStream, s3ImagePath, paymentDto.Image.ContentType);
                }

                return NoContent();
            }
            catch (HttpException ex)
            {
                return StatusCode(ex.StatusCode, ex.Message);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
            }
        }

        [HttpGet("group/{groupId}")]
        [Authorize]
        public async Task<ActionResult<List<PaymentWithStatusDTO>>> GetPaymentByGroupId(int groupId)
        {
            try
            {
                var payments = await _paymentService.GetPaymentByGroupId(groupId);

                if (payments == null)
                {
                    return NotFound();
                }

                List<PaymentWithStatusDTO> paymentDtos = new List<PaymentWithStatusDTO>();

                foreach (var payment in payments)
                {
                    var s3Paths = _configuration.GetSection("S3Paths");
                    string expensePath = s3Paths["Payment"];
                    string cdnUrl = s3Paths["CDNURL"];

                    string s3ImagePath = $"{expensePath}{payment.Id}";
                    var attachmentFromExpense = await _utils.ListFiles(s3ImagePath);
                    var imageUrl = "";
                    if (attachmentFromExpense.Count > 0)
                    {
                        imageUrl = attachmentFromExpense[0];
                    }

                    Status status = (Status)payment.type;
                    string StatusDescription = EnumHelper.GetEnumDescription(status);

                    var paymentWithStatusDTO = new PaymentWithStatusDTO
                    {
                        type = StatusDescription,
                        Amount = payment.Amount,
                        DebtAdjustmentId = payment.DebtAdjustmentId,
                        GroupId = payment.GroupId,
                        Id = payment.Id,
                        Image = cdnUrl + imageUrl,
                        PaymentDate = payment.PaymentDate,
                        User = new UserDTO {
                            Email = payment.User.Email,
                            PaypalUsername = payment.User.PaypalUsername,
                            Rib = payment.User.Rib,
                            Username = payment.User.Username,
                            Id = payment.User.Id
                        },
                        UserInCredit = new UserDTO {
                            Email = payment.UserInCredit.Email,
                            PaypalUsername = payment.UserInCredit.PaypalUsername,
                            Rib = payment.UserInCredit.Rib,
                            Username = payment.UserInCredit.Username,
                            Id = payment.UserInCredit.Id
                        },
                    };
                    paymentDtos.Add(paymentWithStatusDTO);
                }
                return paymentDtos;
            }
            catch (HttpException ex)
            {
                return StatusCode(ex.StatusCode, ex.Message);
            }
            catch (Exception)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
            }
        }
    }
}
