namespace DotNetAPI.Model
{
    public class Product
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public float Price { get; set; }
        public bool Available { get; set; }
        public byte[] Image { get; set; }
        public string Added_time { get; set; }

        public int UserId { get; set; }

     
    }
}
