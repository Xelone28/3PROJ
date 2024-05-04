using System.ComponentModel.DataAnnotations;

namespace DotNetAPI.Model
{
    public class Expense
    {
        public int Id { get; set; }
        public required int UserId { get; set; }
        public required int GroupId { get; set; }
        public required ICollection<int> UserIdInvolved { get; set; } // Collection of user IDs involved in the expense
        public required int CategoryId { get; set; }
        public required float Amount { get; set; }
        public required int Date { get; set; }
        public required string Place { get; set; } // Enum ??
        public string? Description { get; set; }
    }
}
