using DotNetAPI.Models.Payment;
using DotNetAPI.Models.Debt;
using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using DotNetAPI.Services.Interface;

namespace DotNetAPI.Services
{
    public class PaymentService : IPaymentService
    {
        private readonly UserDbContext _context;

        public PaymentService(UserDbContext context)
        {
            _context = context;
        }

        public async Task<Payment> CreatePayment(int userId, int groupId, float amount, int debtAdjustmentId)
        {
            var debtAdjustment = await _context.DebtAdjustments
                .Include(da => da.OriginalDebts)
                .ThenInclude(oda => oda.OriginalDebt)
                .FirstOrDefaultAsync(da => da.Id == debtAdjustmentId && da.UserInDebtId == userId && da.GroupId == groupId);

            if (debtAdjustment == null)
            {
                throw new InvalidOperationException("Debt adjustment not found or already paid.");
            }

            if (debtAdjustment.AdjustmentAmount != amount)
            {
                throw new InvalidOperationException("Payment amount must match the debt adjustment amount.");
            }

            var payment = new Payment
            {
                UserId = userId,
                GroupId = groupId,
                Amount = amount,
                PaymentDate = DateTime.UtcNow,
                DebtAdjustmentId = debtAdjustmentId
            };

            _context.Payments.Add(payment);
            await _context.SaveChangesAsync();

            // Mark all related original debts as paid
            foreach (var debtAdjustmentOriginalDebt in debtAdjustment.OriginalDebts)
            {
                var originalDebt = debtAdjustmentOriginalDebt.OriginalDebt;
                originalDebt.IsPaid = true;
            }

            await _context.SaveChangesAsync();

            return payment;
        }
    }
}
