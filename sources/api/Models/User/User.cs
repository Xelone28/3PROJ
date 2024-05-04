namespace DotNetAPI.Models.User
{
    public class User
    {
        public int Id { get; set; }
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string Password { get; set; }
        public required string Rib { get; set; }
        public required string PaypalUsername { get; set; }
    }
}
