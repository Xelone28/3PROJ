namespace DotNetAPI.Models.Payment
{
    public class PaymentDTO
    {
        public int UserId { get; set; }
        public int GroupId { get; set; }
        public float Amount { get; set; }
        public int DebtAdjustmentId { get; set; }
        public DateTime PaymentDate { get; set; }
    }
}
