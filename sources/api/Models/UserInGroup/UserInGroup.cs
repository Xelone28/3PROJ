namespace DotNetAPI.Models.UserInGroup
{
    public class UserInGroup
    {
        public int UserId { get; set; }
        public required User.User User { get; set; }
        public int GroupId { get; set; }
        public required Group.Group Group { get; set; }
        public required bool IsActive { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
