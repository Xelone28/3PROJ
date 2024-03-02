namespace DotNetAPI.Model
{
    public class Expense
    {
        public required int Id { get; set; }

        public required int GroupId { get; set; }
        public required UserGroup UserGroup { get; set; }

        public required int UserId { get; set; }
        public required User User { get; set; }

        public required int Date { get; set; }
        public required int Amount { get; set; }
        public required string Place { get; set; } // Enum ??

        public required int CategoryId { get; set; }
        public required Category Category { get; set; }

        public string? Description { get; set; }
    }
}
