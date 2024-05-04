namespace DotNetAPI.Models.Category
{
    public class Category
    {
        public int Id { get; set; }
        public required int GroupId { get; set; }
        public required string Name { get; set; }
    }
}
