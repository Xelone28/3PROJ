using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Models.Expense
{
    public class ExpenseUpdateDTO
    {
        public int? UserId { get; set; }
        public int? GroupId { get; set; }
        public int? CategoryId { get; set; }
        public List<int>? UserIdInvolved { get; set; }
        public float? Amount { get; set; }
        public int? Date { get; set; }
        public string? Place { get; set; }
        public string? Description { get; set; }
        public IFormFile? Image { get; set; }
    }
}
