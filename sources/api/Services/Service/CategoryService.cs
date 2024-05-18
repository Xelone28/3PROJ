using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Category;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Services.Service
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
            try
            {
                return await _dbContext.Set<Category>().ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting all categories.");
            }
        }

        public async Task<Category> GetCategoryById(int id)
        {
            try
            {
                var category = await _dbContext.Set<Category>().FindAsync(id);
                if (category == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Category not found.");
                }
                return category;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting category.");
            }
        }

        public async Task<Category> CreateCategory(Category category)
        {
            try
            {
                _dbContext.Set<Category>().Add(category);
                await _dbContext.SaveChangesAsync();
                return category;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating category. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the category.");
            }
        }

        public async Task UpdateCategory(Category category)
        {
            try
            {
                _dbContext.Entry(category).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating category. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the category.");
            }
        }

        public async Task DeleteCategory(int id)
        {
            try
            {
                var category = await _dbContext.Set<Category>().FindAsync(id);
                if (category != null)
                {
                    _dbContext.Set<Category>().Remove(category);
                    await _dbContext.SaveChangesAsync();
                }
                else
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Category not found.");
                }
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Cannot delete the category because it is referenced by an expense.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the category.");
            }
        }

        public async Task<IEnumerable<Category>> GetCategoriesByGroupId(int groupId)
        {
            try
            {
                return await _dbContext.Set<Category>().Where(c => c.GroupId == groupId).ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting categories by group ID.");
            }
        }
    }
}
