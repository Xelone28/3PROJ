namespace DotNetAPI.Models.Group
{
    public class Group
    {
        public int Id { get; set; }
        public required string GroupName { get; set; }
        public string? GroupDesc { get; set; }
    }
}
