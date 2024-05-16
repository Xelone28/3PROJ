using DotNetAPI.Models.Payment;

namespace DotNetAPI.Services.Interface
{
    public interface IPaymentService
    {
        Task<Payment> CreatePayment(int userId, int groupId, float amount, int debtAdjustmentId);
    }
}
