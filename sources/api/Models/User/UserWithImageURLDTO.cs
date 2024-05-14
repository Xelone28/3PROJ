namespace DotNetAPI.Models.User
{
    public class UserWithImageURLDTO
    {
        public int Id { get; set; }
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string Rib { get; set; }
        public required string PaypalUsername { get; set; }
        public string? Image { get; set; }
    }
}

