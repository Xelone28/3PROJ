namespace DotNetAPI.Model
{
    public class UserInGroup
    {
        public int UserId { get; set; }
        public User User { get; set; }

        public int GroupId { get; set; }
        public Group Group { get; set; }

        public bool IsGroupAdmin { get; set; }
    }
}
