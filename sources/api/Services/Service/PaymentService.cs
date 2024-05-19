using DotNetAPI.Models.Payment;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
using DotNetAPI.Models.Debt;
using DotNetAPI.Models.User;
using DotNetAPI.Models.Category;

namespace DotNetAPI.Services
{
    public class PaymentService : IPaymentService
    {
        private readonly UserDbContext _context;
        private readonly IDebtAdjustmentService _debtAdjustmentService;


        public PaymentService(UserDbContext context, IDebtAdjustmentService debtAdjustmentService)
        {
            _context = context;
            _debtAdjustmentService = debtAdjustmentService;
        }

        public async Task<Payment> CreatePayment(int userId, int groupId, float amount, int? debtAdjustmentId, int? type)
        {
            if (debtAdjustmentId == null)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Debt Already paid");
            }

            if(type == null)
            {
                throw new HttpException(StatusCodes.Status400BadRequest, "Type must be given");
            }
            try
            {
                var debtAdjustment = await _context.DebtAdjustments
                    .Include(da => da.OriginalDebts)
                    .ThenInclude(oda => oda.OriginalDebt)
                    .FirstOrDefaultAsync(da => da.Id == debtAdjustmentId && da.UserInDebtId == userId && da.GroupId == groupId);

                if (debtAdjustment == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Debt adjustment not found or already paid.");
                }

                if (debtAdjustment.AdjustmentAmount != amount)
                {
                    throw new HttpException(StatusCodes.Status400BadRequest, "Payment amount must match the debt adjustment amount.");
                }

                var user = await _context.User.FindAsync(userId);
                var group = await _context.Group.FindAsync(groupId);

                if (user == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "User not found.");
                }

                if (group == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Group not found.");
                }

                var payment = new Payment
                {
                    UserId = userId,
                    GroupId = groupId,
                    Amount = amount,
                    PaymentDate = DateTime.UtcNow,
                    DebtAdjustmentId = debtAdjustmentId,
                    User = user,
                    Group = group,
                    DebtAdjustment = debtAdjustment,
                    type = (int)type,
                    UserInCredit = debtAdjustment.UserInCredit
                };

                var paymentInserted = _context.Set<Payment>().Add(payment);

                await _context.SaveChangesAsync();

                // Mark all related original debts as paid
                foreach (var debtAdjustmentOriginalDebt in debtAdjustment.OriginalDebts)
                {
                    var originalDebt = debtAdjustmentOriginalDebt.OriginalDebt;
                    originalDebt.IsPaid = true;
                }

                await _context.SaveChangesAsync();

                // Remove the relationship before deleting DebtAdjustment
                payment.DebtAdjustment = null;
                payment.DebtAdjustmentId = null;
                await _context.SaveChangesAsync();

                await _debtAdjustmentService.DeleteDebtAdjustment(debtAdjustment);

                return payment;
                
            }
            catch (HttpException)
            {
                throw;
            }
            catch (DbUpdateException ex)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating payment. Possible constraint violation."+ex);
            }
        }

        public async Task<List<Payment>> GetPaymentByGroupId(int groupId)
        {
            try
            {
                var payments = await _context.Payment
                    .Where(p => p.GroupId == groupId)
                    .Include(d => d.User)
                    .Include(d => d.Group)
                    .Include(d => d.UserInCredit)
                    .OrderByDescending(p => p.PaymentDate)
                    .ToListAsync();


                if (payments == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Payments not found.");
                }

                return payments;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the debt.");
            }
        }
    }
}
