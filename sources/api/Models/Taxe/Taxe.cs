namespace DotNetAPI.Models.Taxe
{
    public class Taxe
    {
        public int Id { get; set; }
        public required string Name { get; set; }
        public required string Rate { get; set; }
    }
}
