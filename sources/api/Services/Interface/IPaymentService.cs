using DotNetAPI.Models.Payment;
using System.Threading.Tasks;

namespace DotNetAPI.Services.Interfaces
{
    public interface IPaymentService
    {
        Task<PaymentDTO> CreatePayment(int userId, int groupId, float amount, int debtAdjustmentId);
    }
}
