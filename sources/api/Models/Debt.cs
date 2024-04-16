namespace DotNetAPI.Model
{
    public class Debt
    {
        public int Id { get; set; }
        public required int GroupId { get; set; }
        public required Group Group { get; set; }
        public required int BillId { get; set; }
        public required Expense Expense{ get; set; }
        public required int UserIdInCredit { get; set; }
        public required User UserInCredit { get; set; }
        public required int UserIdInDebt { get; set; }
        public required User UserInDebt { get; set; }
        public required float Amount { get; set; }
        public required bool IsPaid { get; set; }
        public required bool IsCanceled { get; set; }
    }
}
