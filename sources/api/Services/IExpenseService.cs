using DotNetAPI.Model;
using DotNetAPI.Model.DTO;

namespace DotNetAPI.Services
{
    public interface IExpenseService
    {
        Task<IEnumerable<Expense>> GetAllExpenses();
        Task<Expense> GetExpenseById(int id);
        Task<Expense> CreateExpense(Expense expense);
        Task UpdateExpense(Expense expense);
        Task DeleteExpense(int id);
    }
}
