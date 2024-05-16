using System;
using System.Collections.Generic;

namespace DotNetAPI.Models.Debt
{
    public class DebtAdjustment
    {
        public int Id { get; set; }
        public int GroupId { get; set; }
        public Group.Group Group { get; set; }
        public int UserInCreditId { get; set; }
        public User.User UserInCredit { get; set; }
        public int UserInDebtId { get; set; }
        public User.User UserInDebt { get; set; }
        public float AdjustmentAmount { get; set; }
        public DateTime AdjustmentDate { get; set; }
        public ICollection<DebtAdjustmentOriginalDebt> OriginalDebts { get; set; }  // Many-to-many relationship
    }
}
