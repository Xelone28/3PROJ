using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface ICartService
    {
        Task<List<Cart>> GetCartsByUserId(int userId);
        Task DeleteCartById(int cartId);
        Task DeleteCartsByUserId(int userId);
        Task AddProductToCart(Cart cart);
    }
}
