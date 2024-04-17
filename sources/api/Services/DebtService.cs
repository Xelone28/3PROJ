using Microsoft.EntityFrameworkCore;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Http.HttpResults;

namespace DotNetAPI.Services
{
    public class DebtService : IDebtService
    {
        private readonly UserDbContext _context;

        public DebtService(UserDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Debt>> GetAllDebts()
        {
            return await _context.Debt.ToListAsync();
        }

        public async Task<Debt> GetDebtById(int id)
        {
            try
            {
                return await _context.Debt.FindAsync(id);
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error getting debt.", ex);
            }
        }

        public async Task<Debt> CreateDebt(Debt debt)
        {
            _context.Debt.Add(debt);
            await _context.SaveChangesAsync();
            return debt;
        }

        public async Task UpdateDebt(Debt debt)
        {
            _context.Entry(debt).State = EntityState.Modified;
            await _context.SaveChangesAsync();
        }

        public async Task DeleteDebt(int id)
        {
            var debt = await _context.Debt.FindAsync(id);
            _context.Debt.Remove(debt);
            await _context.SaveChangesAsync();
        }

        /*Debt creation logic here \/ */
        public async Task CreateDebtsFromExpense(Expense expense)
        {
            // Get the group of the expense
            var group = await _context.Group.FindAsync(expense.GroupId);
            // Get all users in the group
            var users = await _context.UserInGroup.Where(u => u.GroupId == group.Id).ToListAsync();
            // Calculate the amount each user owes
            var amountPerUser = expense.Amount / users.Count;
            // Create a debt for each user in the group
            foreach (var user in users)
            {

                //Print the user.UserId
                Console.WriteLine("user.UserId: " + user.UserId);

                // Skip the user who paid the expense
                if (user.UserId == expense.UserId)
                {
                    continue;
                }
                var debt = new Debt
                {
                    ExpenseId = expense.Id,
                    UserIdInCredit = expense.UserId,
                    UserIdInDebt = user.UserId,
                    GroupId = group.Id,
                    Amount = amountPerUser,
                    IsPaid = false,
                    IsCanceled = false
                };
                _context.Debt.Add(debt);
            }
            await _context.SaveChangesAsync();
        }
    }
}
