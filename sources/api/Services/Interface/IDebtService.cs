using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.User;

namespace DotNetAPI.Services.Interface
{
    public interface IDebtService
    {
        Task<Debt> CreateDebt(Debt debt);
        Task CreateDebtsFromExpense(Expense expense, IList<User> UsersInDebt, IList<float> weights);
        Task<IEnumerable<Debt>> GetAllDebts();
        Task<Debt?>GetDebtById(int id);
        Task<IEnumerable<Debt>> GetDebtsByUserIdInCredit(int userId);
        Task<IEnumerable<Debt>> GetDebtsByUserId(int userId);
        Task<IEnumerable<Debt>> GetDebtsByGroupId(int groupId);
        Task<IEnumerable<Debt>> GetDebtsByExpenseId(int expenseId);
        Task UpdateDebt(Debt debt);
        Task UpdateDebtsFromExpense(Expense expense, IList<User> UsersInDebt, IList<float> weights);
        Task DeleteDebt(int id);
        Task DeleteDebtsByExpenseId(int expenseId);
    }
}
