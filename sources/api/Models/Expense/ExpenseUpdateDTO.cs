using Microsoft.AspNetCore.Http;
using System.Collections.Generic;

namespace DotNetAPI.Models.Expense
{
    public class ExpenseUpdateDTO
    {
        public int? GroupId { get; set; }
        public IList<int>? UserIdInvolved { get; set; }
        public IList<float>? Weights { get; set; }
        public int? CategoryId { get; set; }
        public float? Amount { get; set; }
        public int? Date { get; set; }
        public string? Place { get; set; }
        public string? Description { get; set; }
        public IFormFile? Image { get; set; }
    }
}
