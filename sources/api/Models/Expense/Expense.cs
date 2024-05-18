using System.ComponentModel.DataAnnotations.Schema;
using System.Collections.Generic;

namespace DotNetAPI.Models.Expense
{
    public class Expense
    {
        public int Id { get; set; }
        public required User.User User { get; set; }
        public required int GroupId { get; set; }
        public required IList<int> UserIdsInvolved { get; set; }
        public required Category.Category Category { get; set; }
        public required IList<float> Weights { get; set; }
        public required float Amount { get; set; }
        public required int Date { get; set; }
        public required string Place { get; set; }
        public string? Description { get; set; }
    }
}
