namespace DotNetAPI.Models.UserInGroup
{
    public class UserInGroupInvitationDTO
    {
        public required Group.Group Group { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
