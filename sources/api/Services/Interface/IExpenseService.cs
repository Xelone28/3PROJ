using DotNetAPI.Models.Expense;

namespace DotNetAPI.Services.Interface
{
    public interface IExpenseService
    {
        Task<IEnumerable<Expense>> GetAllExpenses();
        Task<Expense> GetExpenseById(int id);
        Task<Expense> CreateExpense(Expense expense);
        Task UpdateExpense(Expense expense);
        Task DeleteExpense(int id);
        Task<IEnumerable<Expense>> GetExpensesByGroupId(int groupId);
        Task<IEnumerable<Expense>> GetExpensesByUserId(int userId);
        Task<IEnumerable<Expense>> GetExpensesByUserIdAndGroupId(int userId, int groupId);
    }
}
