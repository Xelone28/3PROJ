namespace DotNetAPI.Model
{
    public class Cart
    {
        public int Id { get; set; }
        public required int IdUser { get; set; }
        public required string IdProduct { get; set; }
    }
}