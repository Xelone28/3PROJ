namespace DotNetAPI.Models.UserInGroup
{
    public class UserInGroupMinimalDTO
    {
        public required int UserId { get; set; }
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required bool IsActive { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
