namespace DotNetAPI.Model
{
    public class UserInGroupCreateDTO
    {
        public required int UserId { get; set; }
        public required int GroupId { get; set; }
        public required bool IsGroupAdmin { get; set; }
    }
}
