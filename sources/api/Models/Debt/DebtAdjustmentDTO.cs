namespace DotNetAPI.Models.Debt
{
    public class DebtAdjustmentDTO
    {
        public int Id { get; set; }
        public int GroupId { get; set; }
        public int UserInCreditId { get; set; }
        public int UserInDebtId { get; set; }
        public float AdjustmentAmount { get; set; }
        public DateTime AdjustmentDate { get; set; }
    }
}
