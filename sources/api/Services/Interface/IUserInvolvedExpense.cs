using DotNetAPI.Models.UserInvolvedExpense;

namespace DotNetAPI.Services.Interface
{
    public interface IUserInvolvedExpense
    {
        Task<List<UserInvolvedExpense>?> GetUserInvolvedByExpenseId(int id);
        Task<UserInvolvedExpense> AddUserInExpense(UserInvolvedExpense userInvolvedExpense);
        Task DeleteFromExpenseId(int expenseId);
    }
}
