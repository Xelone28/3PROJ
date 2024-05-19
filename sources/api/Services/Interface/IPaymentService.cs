using DotNetAPI.Models.Payment;

namespace DotNetAPI.Services.Interfaces
{
    public interface IPaymentService
    {
        Task<Payment> CreatePayment(int userId, int groupId, float amount, int? debtAdjustmentId, int? type);
        Task<List<Payment>> GetPaymentByGroupId(int groupId);
    }
}
