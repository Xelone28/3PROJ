namespace DotNetAPI.Model
{
    public class UserInGroup
    {
        public int UserId { get; set; }

        public int GroupId { get; set; }

        public bool IsActive { get; set; }

        public bool IsGroupAdmin { get; set; }
    }
}
