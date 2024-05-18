using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Models.User
{
    public class UserWithImage
    {
        public int Id { get; set; }
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string Password { get; set; }
        public required string Rib { get; set; }
        public required string PaypalUsername { get; set; }
        public required IFormFile? Image { get; set; }

    }
}
