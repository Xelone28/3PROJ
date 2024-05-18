using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using DotNetAPI.Services.Interface;
using DotNetAPI.Models.UserInvolvedExpense;

namespace DotNetAPI.Services.Service
{
    public class UserInvolvedExpenseService : IUserInvolvedExpense
    {
        private readonly UserDbContext _dbContext;
        private readonly AppSettings _appSettings;


        public UserInvolvedExpenseService(UserDbContext dbContext, IOptions<AppSettings> appSettings)
        {
            _dbContext = dbContext ?? throw new ArgumentNullException(nameof(dbContext));
            _appSettings = appSettings.Value;

        }
        public async Task<UserInvolvedExpense> AddUserInExpense(UserInvolvedExpense userInvolvedExpense)
        {
            _dbContext.Set<UserInvolvedExpense>().Add(userInvolvedExpense);
            await _dbContext.SaveChangesAsync();
            return userInvolvedExpense;
        }
        
        public async Task<List<UserInvolvedExpense>?> GetUserInvolvedByExpenseId(int expenseId)
        {
            return await _dbContext.Set<UserInvolvedExpense>()
                .Include(c => c.Expense)
                .Include(c => c.User)
                .Where(c => c.Expense.Id == expenseId).ToListAsync();
        }

        public async Task DeleteFromExpenseId(int expenseId)
        {
            var userInvolvedExpenses = await _dbContext.Set<UserInvolvedExpense>()
                .Where(e => e.Expense.Id == expenseId)
                .ToListAsync();

            if (userInvolvedExpenses.Any())
            {
                _dbContext.Set<UserInvolvedExpense>().RemoveRange(userInvolvedExpenses);

                await _dbContext.SaveChangesAsync();
            }
        }

    }
}
