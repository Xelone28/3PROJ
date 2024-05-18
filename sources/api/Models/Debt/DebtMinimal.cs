namespace DotNetAPI.Models.Expense
{
    public class DebtMinimal
    {
        public int Id { get; set; }
        public required User.UserDTO UserInCredit { get; set; }
        public required User.UserDTO UserInDebt { get; set; }
        public required float Amount { get; set; }
        public required bool IsPaid { get; set; }
    }
}