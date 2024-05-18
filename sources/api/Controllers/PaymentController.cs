using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Payment;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PaymentController : ControllerBase
    {
        private readonly IPaymentService _paymentService;

        public PaymentController(IPaymentService paymentService)
        {
            _paymentService = paymentService;
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> CreatePayment([FromBody] PaymentDTO paymentDto)
        {
            if (paymentDto == null)
            {
                return BadRequest("Payment request is null.");
            }

            try
            {
                var createdPayment = await _paymentService.CreatePayment(paymentDto.UserId, paymentDto.GroupId, paymentDto.Amount, paymentDto.DebtAdjustmentId);
                return Ok(createdPayment);
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
    }
}
