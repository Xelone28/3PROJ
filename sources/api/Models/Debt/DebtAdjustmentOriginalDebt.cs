namespace DotNetAPI.Models.Debt
{
    public class DebtAdjustmentOriginalDebt
    {
        public int DebtAdjustmentId { get; set; }
        public DebtAdjustment DebtAdjustment { get; set; }
        public int OriginalDebtId { get; set; }
        public Debt OriginalDebt { get; set; }
    }
}
