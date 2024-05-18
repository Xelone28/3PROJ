using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using DotNetAPI.Services.Interface;
using DotNetAPI.Models.UserInvolvedExpense;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

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
            try
            {
                _dbContext.Set<UserInvolvedExpense>().Add(userInvolvedExpense);
                await _dbContext.SaveChangesAsync();
                return userInvolvedExpense;
            }
            catch (DbUpdateException ex)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error adding user involved in expense. Possible constraint violation: " + ex.Message);
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while adding user involved in expense: " + ex.Message);
            }
        }

        public async Task<List<UserInvolvedExpense>?> GetUserInvolvedByExpenseId(int expenseId)
        {
            try
            {
                return await _dbContext.Set<UserInvolvedExpense>()
                    .Include(c => c.Expense)
                    .Include(c => c.User)
                    .Where(c => c.Expense.Id == expenseId)
                    .ToListAsync();
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting users involved by expense ID: " + ex.Message);
            }
        }

        public async Task DeleteFromExpenseId(int expenseId)
        {
            try
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
            catch (DbUpdateException ex)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting users involved in expense. Possible constraint violation: " + ex.Message);
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting users involved in expense: " + ex.Message);
            }
        }
    }
}
