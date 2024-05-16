using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Models.Expense
{
    public class ExpenseWithImageUrlDTO
    {
        public int Id { get; set; }
        public required User.UserDTO User { get; set; }
        public required int GroupId { get; set; }
        public required IList<User.UserDTO> UsersInvolved { get; set; }
        public required Category.Category Category { get; set; }
        public required float Amount { get; set; }
        public required int Date { get; set; }
        public required string Place { get; set; }
        public string? Description { get; set; }
        public string? Image { get; set; }
    }

}
