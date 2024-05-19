using DotNetAPI.Models.Debt;
using DotNetAPI.Models.User;

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
        public int? DebtAdjustmentId { get; set; }
        public DebtAdjustment? DebtAdjustment { get; set; }
        public int type { get; set; }
    }
}