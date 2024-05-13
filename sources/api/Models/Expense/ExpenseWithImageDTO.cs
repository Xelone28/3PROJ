using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Models.Expense
{
    public class ExpenseWithImageDTO
    {
        public int Id { get; set; }
        public required int UserId { get; set; }
        public required int GroupId { get; set; }
        public required List<int> UserIdInvolved { get; set; }
        public required int CategoryId { get; set; }
        public required float Amount { get; set; }
        public required int Date { get; set; }
        public required string Place { get; set; }
        public string? Description { get; set; }
        public IFormFile? Image { get; set; }
    }

}
