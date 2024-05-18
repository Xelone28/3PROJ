using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Interface;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services.Service
{
    public class DebtService : IDebtService
    {
        private readonly UserDbContext _context;
        public DebtService(UserDbContext context)
        {
            _context = context;
        }
        public async Task<Debt> CreateDebt(Debt debt)
        {
            _context.Debt.Add(debt);
            await _context.SaveChangesAsync();
            return debt;
        }

        public async Task CreateDebtsFromExpense(Expense expense, IList<User> usersInDebt, IList<float> weights)
        {
            if (usersInDebt.Count != weights.Count)
            {
                throw new InvalidOperationException("The number of weights must match the number of users involved.");
            }

            float totalWeight = weights.Sum();

            for (int i = 0; i < usersInDebt.Count; i++)
            {
                User user = usersInDebt[i];
                float userWeight = weights[i];
                float userAmount = (userWeight / totalWeight) * expense.Amount;

                if (user.Id != expense.User.Id)
                {
                    Debt debt = new Debt
                    {
                        GroupId = expense.GroupId,
                        ExpenseId = expense.Id,
                        Amount = (float)Math.Round(userAmount, 2),
                        IsPaid = false,
                        IsCanceled = false,
                        UserInCredit = expense.User,
                        UserInDebt = user
                    };
                    _context.Debt.Add(debt);
                }
            }

            await _context.SaveChangesAsync();
        }

        public async Task<IEnumerable<Debt>> GetAllDebts()
        {
            return await _context.Debt.ToListAsync();
        }

        public async Task<Debt?> GetDebtById(int id)
        {
            try
            {
                return await _context.Debt
                             .Include(d => d.UserInCredit)
                             .Include(d => d.UserInDebt)
                             .FirstOrDefaultAsync(d => d.Id == id);
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error getting debt.", ex);
            }
        }

        public async Task<IEnumerable<Debt>> GetDebtsByUserIdInCredit(int userId)
        {
            return await _context.Debt.Where(debt => debt.UserInCredit.Id == userId).ToListAsync();
        }

        public async Task<IEnumerable<Debt>> GetDebtsByUserId(int userId)
        {
            return await _context.Debt.Where(debt => debt.UserInDebt.Id == userId).ToListAsync();
        }

        public async Task<IEnumerable<Debt>> GetDebtsByGroupId(int groupId)
        {
            return await _context.Debt.Where(debt => debt.GroupId == groupId).ToListAsync();
        }

        public async Task<IEnumerable<Debt>> GetDebtsByExpenseId(int expenseId)
        {
            return await _context.Debt.Where(debt => debt.ExpenseId == expenseId).ToListAsync();
        }

        public async Task UpdateDebt(Debt debt)
        {
            _context.Entry(debt).State = EntityState.Modified;
            await _context.SaveChangesAsync();
        }

        public async Task UpdateDebtsFromExpense(Expense expense, IList<User> usersInDebt, IList<float> weights)
        {
            var debts = await _context.Debt.Where(debt => debt.ExpenseId == expense.Id).ToListAsync();

            foreach (Debt debt in debts)
            {
                _context.Debt.Remove(debt);
            }

            await _context.SaveChangesAsync();
            await CreateDebtsFromExpense(expense, usersInDebt, weights);
        }

        public async Task DeleteDebt(int id)
        {
            var debt = await _context.Debt.FindAsync(id);
            _context.Debt.Remove(debt);
            await _context.SaveChangesAsync();
        }

        public async Task DeleteDebtsByExpenseId(int expenseId)
        {
            var debtsToDelete = _context.Debt.Where(d => d.ExpenseId == expenseId);
            _context.Debt.RemoveRange(debtsToDelete);
            await _context.SaveChangesAsync();
        }
    }
}
