using DotNetAPI.Models.Payment;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services
{
    public class PaymentService : IPaymentService
    {
        private readonly UserDbContext _context;

        public PaymentService(UserDbContext context)
        {
            _context = context;
        }

        public async Task<PaymentDTO> CreatePayment(int userId, int groupId, float amount, int debtAdjustmentId)
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

            var user = await _context.User.FindAsync(userId);
            var group = await _context.Group.FindAsync(groupId);

            var payment = new Payment
            {
                UserId = userId,
                GroupId = groupId,
                Amount = amount,
                PaymentDate = DateTime.UtcNow,
                DebtAdjustmentId = debtAdjustmentId,
                User = user,
                Group = group,
                DebtAdjustment = debtAdjustment
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

            return new PaymentDTO
            {
                UserId = payment.UserId,
                GroupId = payment.GroupId,
                Amount = payment.Amount,
                DebtAdjustmentId = payment.DebtAdjustmentId,
                PaymentDate = payment.PaymentDate
            };
        }
    }
}
