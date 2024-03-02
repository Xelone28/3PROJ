using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services
{
    public class CartService : ICartService
    {
        private readonly UserDbContext _dbContext;

        public CartService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<List<Cart>> GetCartsByUserId(int userId)
        {
            return await _dbContext.Cart.Where(c => c.IdUser == userId).ToListAsync();
        }

        public async Task DeleteCartById(int cartId)
        {
            var cartToDelete = await _dbContext.Cart.FindAsync(cartId);

            if (cartToDelete != null)
            {
                _dbContext.Cart.Remove(cartToDelete);
                await _dbContext.SaveChangesAsync();
            }
        }

        public async Task DeleteCartsByUserId(int userId)
        {
            var cartsToDelete = await _dbContext.Cart.Where(c => c.IdUser == userId).ToListAsync();

            if (cartsToDelete != null && cartsToDelete.Any())
            {
                _dbContext.Cart.RemoveRange(cartsToDelete);
                await _dbContext.SaveChangesAsync();
            }
        }

        public async Task AddProductToCart(Cart cart)
        {
            _dbContext.Cart.Add(cart);
            await _dbContext.SaveChangesAsync();
        }
    }
}
