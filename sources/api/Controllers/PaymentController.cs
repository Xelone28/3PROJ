using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Payment;
using System.Threading.Tasks;
using DotNetAPI.Services.Interface;

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
        public async Task<IActionResult> CreatePayment([FromBody] Payment payment)
        {
            if (payment == null)
            {
                return BadRequest("Payment request is null.");
            }

            try
            {
                var createdPayment = await _paymentService.CreatePayment(payment.UserId, payment.GroupId, payment.Amount, payment.DebtAdjustmentId);
                return Ok(createdPayment);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}
