namespace DotNetAPI.Model.DTO
{
    public class UserUpdateDTO
    {
        public int Id { get; set; }
        public string? Username { get; set; }
        public string? Email { get; set; }
        public string? Password { get; set; }
        public string? Rib { get; set; }
        public string? PaypalUsername { get; set; }
    }
}

