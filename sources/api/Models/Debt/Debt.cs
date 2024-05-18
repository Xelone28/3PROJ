namespace DotNetAPI.Models.Debt
{
    public class Debt
    {
        public int Id { get; set; }
        public required int GroupId { get; set; }
        public required int ExpenseId { get; set; }
        public required User.User UserInCredit { get; set; }
        public required User.User UserInDebt { get; set; }
        public required float Amount { get; set; }
        public required bool IsPaid { get; set; }
    }
}
