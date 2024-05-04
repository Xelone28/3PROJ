using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Expense;
using DotNetAPI.Services.Interface;

namespace DotNetAPI.Services.Service
{
    public class ExpenseService : IExpenseService
    {
        private readonly UserDbContext _dbContext;

        public ExpenseService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<Expense>> GetAllExpenses()
        {
            return await _dbContext.Set<Expense>().ToListAsync();
        }

        public async Task<Expense> GetExpenseById(int id)
        {
            return await _dbContext.Set<Expense>().FindAsync(id);
        }

        public async Task<Expense> CreateExpense(Expense expense)
        {
            _dbContext.Set<Expense>().Add(expense);
            await _dbContext.SaveChangesAsync();
            return expense;
        }

        public async Task UpdateExpense(Expense expense)
        {
            try
            {
                _dbContext.Entry(expense).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error updating expense.", ex);
            }

        }
        public async Task DeleteExpense(int id)
        {
            var expense = await _dbContext.Set<Expense>().FindAsync(id);
            if (expense != null)
            {
                _dbContext.Set<Expense>().Remove(expense);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
