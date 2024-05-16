using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Services.Interface;
using Microsoft.EntityFrameworkCore;

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
            var debts = await _context.Debt
                .Where(d => d.GroupId == groupId && !d.IsPaid && !d.IsCanceled)
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

            // Remove previous DebtAdjustments for the group
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
                        AdjustmentDate = DateTime.UtcNow,  // Ensure UTC
                        OriginalDebts = new List<DebtAdjustmentOriginalDebt>()
                    };

                    // Find all relevant original debts for the adjustment
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

        public async Task HandleNewExpense(Expense expense)
        {
            // Create debts based on the new expense
            var userCount = expense.UserIdInvolved.Count;
            var individualShare = expense.Amount / userCount;

            foreach (var userId in expense.UserIdInvolved)
            {
                if (userId != expense.User.Id)
                {
                    var debt = new Debt
                    {
                        GroupId = expense.GroupId,
                        ExpenseId = expense.Id,
                        UserInCredit = expense.User,
                        UserInDebt = await _context.User.FindAsync(userId),
                        Amount = individualShare,
                        IsPaid = false,
                        IsCanceled = false
                    };

                    _context.Debt.Add(debt);
                }
            }

            await _context.SaveChangesAsync();

            // Rebalance all debts in the group
            await BalanceDebts(expense.GroupId);
        }
    }
}
