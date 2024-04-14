using Microsoft.EntityFrameworkCore;
using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public class CategoryService : ICategoryService
    {
        private readonly UserDbContext _dbContext;

        public CategoryService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<Category>> GetAllCategories()
        {
            return await _dbContext.Set<Category>().ToListAsync();
        }

        public async Task<Category> GetCategoryById(int id)
        {
            return await _dbContext.Set<Category>().FindAsync(id);
        }

        public async Task<Category> CreateCategory(Category category)
        {
            _dbContext.Set<Category>().Add(category);
            await _dbContext.SaveChangesAsync();
            return category;
        }

        public async Task UpdateCategory(Category category)
        {
            _dbContext.Entry(category).State = EntityState.Modified;
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeleteCategory(int id)
        {
            var category = await _dbContext.Set<Category>().FindAsync(id);
            if (category != null)
            {
                _dbContext.Set<Category>().Remove(category);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
