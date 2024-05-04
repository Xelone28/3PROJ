namespace DotNetAPI.Models.UserInGroup
{
    public class UserInGroupInvitationDTO
    {
        public required int GroupId { get; set; }
        public required string GroupName { get; set; }
        public required string GroupDescription { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
