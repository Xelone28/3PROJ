namespace DotNetAPI.Models.Expense
{
    public class ExpenseMinimal
    {
        public int Id { get; set; }
        public required User.UserDTO User { get; set; }
        public required int GroupId { get; set; }
        public required IList<int> UserIdInvolved { get; set; }
        public required int CategoryId { get; set; }
        public required float Amount { get; set; }
        public required int Date { get; set; }
        public required string Place { get; set; }
        public string? Description { get; set; }
    }
}
