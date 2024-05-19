using DotNetAPI.Models.Payment;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
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
        private readonly IDebtAdjustmentService _debtAdjustmentService;


        public PaymentService(
            UserDbContext context,
            IDebtAdjustmentService debtAdjustmentService
            )
        {
            _context = context;
            _debtAdjustmentService = debtAdjustmentService;
        }

        public async Task<PaymentDTO> CreatePayment(int userId, int groupId, float amount, int debtAdjustmentId)
        {
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

            foreach (var debtAdjustmentOriginalDebt in debtAdjustment.OriginalDebts)
            {
                var originalDebt = debtAdjustmentOriginalDebt.OriginalDebt;
                originalDebt.IsPaid = true;
            }
                if (group == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Group not found.");
                }

            await _context.SaveChangesAsync();
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
                _context.Payment.Add(payment);
                
                await _context.SaveChangesAsync();

                // Mark all related original debts as paid
                foreach (var debtAdjustmentOriginalDebt in debtAdjustment.OriginalDebts)
                {
                    var originalDebt = debtAdjustmentOriginalDebt.OriginalDebt;
                    originalDebt.IsPaid = true;
                }

                await _context.SaveChangesAsync();
                
                await _debtAdjustmentService.DeleteDebtAdjustment(debtAdjustment);

                return new PaymentDTO
                {
                    UserId = payment.UserId,
                    GroupId = payment.GroupId,
                    Amount = payment.Amount,
                    DebtAdjustmentId = payment.DebtAdjustmentId,
                    PaymentDate = payment.PaymentDate
                };
            }
            catch (HttpException)
            {
                throw;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating payment. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the payment.");
            }
        }
    }
}
