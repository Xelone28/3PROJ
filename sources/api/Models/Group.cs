namespace DotNetAPI.Model
{
    public class Group
    {
        public int Id { get; set; }
        public required string GroupName { get; set; }
        public string? GroupDesc { get; set; }
    }
}
