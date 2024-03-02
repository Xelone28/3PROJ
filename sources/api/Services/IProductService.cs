using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface IProductService
    {
        Task<IEnumerable<Product>> GetAllProducts();
        Task<Product> GetProductById(int productId);
        Task<Product> AddProduct(Product product);
        Task<Product> UpdateProduct(Product product);
        Task DeleteProduct(int productId);
    }
}
