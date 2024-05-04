namespace DotNetAPI.Models.UserInGroup
{
    public class UserInGroupCreateDTO
    {
        public required int UserId { get; set; }
        public required int GroupId { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
