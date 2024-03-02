using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services
{
    public class ProductService : IProductService
    {
        private readonly UserDbContext _dbContext;

        public ProductService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<Product>> GetAllProducts()
        {
            try
            {
                return await _dbContext.Product.ToListAsync();
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error getting all products.", ex);
            }
        }

        public async Task<Product> GetProductById(int productId)
        {
            try
            {
                return await _dbContext.Product.FindAsync(productId);
            }
            catch (Exception ex)
            {
                throw new ApplicationException($"Error getting product with ID {productId}.", ex);
            }
        }

        public async Task<Product> AddProduct(Product product)
        {
            try
            {
                _dbContext.Product.Add(product);
                await _dbContext.SaveChangesAsync();
                return product;
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error adding product.", ex);
            }
        }

        public async Task<Product> UpdateProduct(Product product)
        {
            try
            {
                var existingProduct = await _dbContext.Product.FindAsync(product.Id);

                if (existingProduct == null)
                {
                    throw new NotFoundException($"Product with ID {product.Id} not found.");
                }

                _dbContext.Entry(existingProduct).State = EntityState.Detached;
                _dbContext.Entry(product).State = EntityState.Modified;

                await _dbContext.SaveChangesAsync();
                return product;
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error updating product.", ex);
            }
        }

        public async Task DeleteProduct(int productId)
        {
            try
            {
                var product = await _dbContext.Product.FindAsync(productId);

                if (product == null)
                {
                    throw new NotFoundException($"Product with ID {productId} not found.");
                }

                _dbContext.Product.Remove(product);
                await _dbContext.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error deleting product.", ex);
            }
        }
    }
}
