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
        //GetExpensesByGroupId
        public async Task<IEnumerable<Expense>> GetExpensesByGroupId(int groupId)
        {
            return await _dbContext.Set<Expense>().Where(e => e.GroupId == groupId).ToListAsync();
        }
        //GetExpensesByUserId
        public async Task<IEnumerable<Expense>> GetExpensesByUserId(int userId)
        {
            return await _dbContext.Set<Expense>().Where(e => e.User.Id == userId).ToListAsync();
        }
        //GetExpensesByUserIdAndGroupId
        public async Task<IEnumerable<Expense>> GetExpensesByUserIdAndGroupId(int userId, int groupId)
        {
            return await _dbContext.Set<Expense>().Where(e => e.User.Id == userId && e.GroupId == groupId).ToListAsync();
        }
    }
}
