using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Services
{
    public class DebtBalancingService : IDebtBalancingService
    {
        private readonly UserDbContext _context;

        public DebtBalancingService(UserDbContext context)
        {
            _context = context;
        }

        public async Task BalanceDebts(int groupId)
        {
            try
            {
                var debts = await _context.Debt
                    .Include(d => d.UserInCredit)
                    .Include(d => d.UserInDebt)
                    .Where(d => d.GroupId == groupId && !d.IsPaid)
                    .ToListAsync();

                var balanceMatrix = new Dictionary<(int, int), float>();

                foreach (var debt in debts)
                {
                    var key = (debt.UserInCredit.Id, debt.UserInDebt.Id);
                    var reverseKey = (debt.UserInDebt.Id, debt.UserInCredit.Id);

                    if (balanceMatrix.ContainsKey(key))
                    {
                        balanceMatrix[key] += debt.Amount;
                    }
                    else if (balanceMatrix.ContainsKey(reverseKey))
                    {
                        balanceMatrix[reverseKey] -= debt.Amount;
                    }
                    else
                    {
                        balanceMatrix[key] = debt.Amount;
                    }
                }

            var previousAdjustments = await _context.DebtAdjustments
                .Where(da => da.GroupId == groupId)
                .ToListAsync();
            _context.DebtAdjustments.RemoveRange(previousAdjustments);

            var adjustments = new List<DebtAdjustment>();
            foreach (var entry in balanceMatrix)
            {
                var (userInCreditId, userInDebtId) = entry.Key;
                var amount = entry.Value;

                    if (amount != 0)
                    {
                        var newAdjustment = new DebtAdjustment
                        {
                            GroupId = groupId,
                            UserInCreditId = userInCreditId,
                            UserInDebtId = userInDebtId,
                            AdjustmentAmount = amount,
                            AdjustmentDate = DateTime.UtcNow,
                            OriginalDebts = new List<DebtAdjustmentOriginalDebt>()
                    };

                    var relevantDebts = debts.Where(d =>
                        (d.UserInCredit.Id == userInCreditId && d.UserInDebt.Id == userInDebtId) ||
                        (d.UserInCredit.Id == userInDebtId && d.UserInDebt.Id == userInCreditId)).ToList();

                        foreach (var debt in relevantDebts)
                        {
                            newAdjustment.OriginalDebts.Add(new DebtAdjustmentOriginalDebt
                            {
                                DebtAdjustment = newAdjustment,
                                OriginalDebt = debt
                            });
                        }

                        adjustments.Add(newAdjustment);
                    }
                }

                _context.DebtAdjustments.AddRange(adjustments);
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating the database while balancing debts.");
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while balancing debts.");
            }
        }
    }
}
