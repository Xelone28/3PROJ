using DotNetAPI.Models.Debt;

namespace DotNetAPI.Models.Payment
{
    public class Payment
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public User.User User { get; set; }
        public int GroupId { get; set; }
        public Group.Group Group { get; set; }
        public float Amount { get; set; }
        public DateTime PaymentDate { get; set; }
        public int DebtAdjustmentId { get; set; }
        public DebtAdjustment DebtAdjustment { get; set; }
    }
}
