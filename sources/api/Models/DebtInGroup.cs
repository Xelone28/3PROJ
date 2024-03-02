namespace DotNetAPI.Model
{
    public class DebtInGroup
    {
        public int Id { get; set; }
        public required int GroupId { get; set; }
        public required UserGroup UserGroup { get; set; }
        public required int BillId { get; set; }
        public required Expense Expense{ get; set; } // --- doubt here ---
        public required int UserIdInCredit { get; set; }
        public required User UserInCredit { get; set; }
        public required int UserIdInDebt { get; set; }
        public required User UserInDebt { get; set; }
        public required float Amount { get; set; }
        public required bool IsPaid { get; set; }
        public required bool IsCanceled { get; set; }
    }
}
