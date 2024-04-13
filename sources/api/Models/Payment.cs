namespace DotNetAPI.Model
{
    public class Payment
    {
        public required int Id { get; set; }

        public required int UserId { get; set; }
        public required User User { get; set; }

        public required int GroupId { get; set; }
        public required UserGroup UserGroup { get; set; }

        public required int DebtId { get; set; }
        public required DebtInGroup DebtInGroup { get; set; }

        public required int Date { get; set; }
        public required int Amount { get; set; }
        public required string Type { get; set; } // Enum ??

        public required int TaxeId { get; set; }
        public required Taxe Taxe { get; set; }
        
        public required int TaxeValue { get; set; }
    }
}