using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Payment;
using DotNetAPI.Services.Interfaces;
using System.Threading.Tasks;

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
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}
