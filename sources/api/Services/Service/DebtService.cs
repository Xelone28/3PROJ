using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

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
            try
            {
                _context.Debt.Add(debt);
                await _context.SaveChangesAsync();
                return debt;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating debt. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the debt.");
            }
        }

        public async Task CreateDebtsFromExpense(Expense expense, IList<User> usersInDebt, IList<float> weights)
        {
            try
            {
                if (usersInDebt.Count != weights.Count)
                {
                    throw new HttpException(StatusCodes.Status400BadRequest, "The number of weights must match the number of users involved.");
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
                            UserInCredit = expense.User,
                            UserInDebt = user
                        };
                        _context.Debt.Add(debt);
                    }
                    if (user.Id == expense.User.Id)
                    {
                        Debt debt = new Debt
                        {
                            GroupId = expense.GroupId,
                            ExpenseId = expense.Id,
                            Amount = (float)Math.Round(userAmount, 2),
                            IsPaid = true,
                            UserInCredit = expense.User,
                            UserInDebt = user
                        };
                        _context.Debt.Add(debt);
                    }
                }

                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating debts from expense. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating debts from expense.");
            }
        }

        public async Task<IEnumerable<Debt>> GetAllDebts()
        {
            try
            {
                return await _context.Debt.ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting all debts.");
            }
        }

        public async Task<Debt?> GetDebtById(int id)
        {
            try
            {
                var debt = await _context.Debt
                    .Include(d => d.UserInCredit)
                    .Include(d => d.UserInDebt)
                    .FirstOrDefaultAsync(d => d.Id == id);

                if (debt == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Debt not found.");
                }

                return debt;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the debt.");
            }
        }

        public async Task<IEnumerable<Debt>> GetDebtsByUserIdInCredit(int userId)
        {
            try
            {
                return await _context.Debt.Where(debt => debt.UserInCredit.Id == userId).ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting debts by user ID in credit.");
            }
        }

        public async Task<IEnumerable<Debt>> GetDebtsByUserId(int userId)
        {
            try
            {
                return await _context.Debt.Where(debt => debt.UserInDebt.Id == userId).ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting debts by user ID.");
            }
        }

        public async Task<IEnumerable<Debt>> GetDebtsByGroupId(int groupId)
        {
            try
            {
                return await _context.Debt.Where(debt => debt.GroupId == groupId).ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting debts by group ID.");
            }
        }

        public async Task<IEnumerable<Debt>> GetDebtsByExpenseId(int expenseId)
        {
            try
            {
                return await _context.Debt.Where(debt => debt.ExpenseId == expenseId).ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting debts by expense ID.");
            }
        }

        public async Task UpdateDebt(Debt debt)
        {
            try
            {
                _context.Entry(debt).State = EntityState.Modified;
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating debt. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the debt.");
            }
        }

        public async Task UpdateDebtsFromExpense(Expense expense, IList<User> usersInDebt, IList<float> weights)
        {
            try
            {
                var debts = await _context.Debt.Where(debt => debt.ExpenseId == expense.Id).ToListAsync();

                foreach (Debt debt in debts)
                {
                    _context.Debt.Remove(debt);
                }

                await _context.SaveChangesAsync();
                await CreateDebtsFromExpense(expense, usersInDebt, weights);
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating debts from expense. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating debts from expense.");
            }
        }

        public async Task DeleteDebt(int id)
        {
            try
            {
                var debt = await _context.Debt.FindAsync(id);
                if (debt == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Debt not found.");
                }
                _context.Debt.Remove(debt);
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting debt. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the debt.");
            }
        }

        public async Task DeleteDebtsByExpenseId(int expenseId)
        {
            try
            {
                var debtsToDelete = _context.Debt.Where(d => d.ExpenseId == expenseId);
                _context.Debt.RemoveRange(debtsToDelete);
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting debts by expense ID. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting debts by expense ID.");
            }
        }
    }
}
