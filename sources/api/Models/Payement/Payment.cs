namespace DotNetAPI.Models.Payement
{
    public class Payment
    {
        public required int Id { get; set; }

        public required int UserId { get; set; }
        public required User.User User { get; set; }

        public required int GroupId { get; set; }
        public required Group.Group UserGroup { get; set; }

        public required int DebtId { get; set; }
        public required Debt.Debt Debt { get; set; }

        public required int Date { get; set; }
        public required int Amount { get; set; }
        public required string Type { get; set; } // Enum ??

        public required int TaxeId { get; set; }
        public required Taxe.Taxe Taxe { get; set; }

        public required int TaxeValue { get; set; }
    }
}