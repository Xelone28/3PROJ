using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Expense;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

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
            try
            {
                return await _dbContext.Set<Expense>().ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting all expenses.");
            }
        }

        public async Task<Expense?> GetExpenseById(int id)
        {
            try
            {
                var expense = await _dbContext.Set<Expense>()
                    .Include(d => d.User)
                    .Include(d => d.Category)
                    .FirstOrDefaultAsync(d => d.Id == id);

                if (expense == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Expense not found.");
                }

                return expense;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the expense.");
            }
        }

        public async Task<Expense> CreateExpense(Expense expense)
        {
            try
            {
                _dbContext.Set<Expense>().Add(expense);
                await _dbContext.SaveChangesAsync();
                return expense;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating expense. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the expense.");
            }
        }

        public async Task UpdateExpense(Expense expense)
        {
            try
            {
                _dbContext.Entry(expense).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating expense. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the expense.");
            }
        }

        public async Task DeleteExpense(int id)
        {
            try
            {
                var expense = await _dbContext.Set<Expense>().FindAsync(id);
                if (expense == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Expense not found.");
                }
                _dbContext.Set<Expense>().Remove(expense);
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting expense. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the expense.");
            }
        }

        public async Task<IEnumerable<Expense>?> GetExpensesByGroupId(int groupId)
        {
            try
            {
                return await _dbContext.Set<Expense>()
                    .Include(e => e.User)
                    .Include(e => e.Category)
                    .Where(e => e.GroupId == groupId)
                    .ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting expenses by group ID.");
            }
        }

        public async Task<IEnumerable<Expense>?> GetExpensesByUserId(int userId)
        {
            try
            {
                return await _dbContext.Set<Expense>()
                    .Where(e => e.User.Id == userId)
                    .ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting expenses by user ID.");
            }
        }

        public async Task<IEnumerable<Expense>?> GetExpensesByUserIdAndGroupId(int userId, int groupId)
        {
            try
            {
                return await _dbContext.Set<Expense>()
                    .Where(e => e.User.Id == userId && e.GroupId == groupId)
                    .ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting expenses by user ID and group ID.");
            }
        }
    }
}
