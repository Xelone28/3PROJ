namespace DotNetAPI.Models.UserInvolvedExpense
{
    public class UserInvolvedExpense
    {
        public int ExpenseId { get; set; }
        public required Expense.Expense Expense { get; set; }
        public int UserId { get; set; }
        public required User.User User { get; set; }
        public required float Weight { get; set; }
    }
}