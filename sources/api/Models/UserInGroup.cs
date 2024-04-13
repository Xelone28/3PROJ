namespace DotNetAPI.Model
{
    public class UserInGroup
    {
        public required int UserId { get; set; }
        public required User User { get; set; }

        public required int GroupId { get; set; }
        public required UserGroup Group { get; set; }

        public required bool IsGroupAdmin { get; set; }
        public required float Balance{ get; set; } // revoir
    }
}
