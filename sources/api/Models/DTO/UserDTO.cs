namespace DotNetAPI.Model.DTO
{
    public class UserDTO
    {
        public int Id { get; set; }
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string Rib { get; set; }
        public required string PaypalUsername { get; set; }
    }
}

