namespace DotNetAPI.Models.Payment
{
    public class PaymentWithStatusDTO
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public int GroupId { get; set; }
        public float Amount { get; set; }
        public int? DebtAdjustmentId { get; set; }
        public DateTime PaymentDate { get; set; }
        public string type { get; set; }
        public string? Image { get; set; }
    }
}
