﻿using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Category;
using DotNetAPI.Services.Interface;

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
            return await _dbContext.Set<Category>().ToListAsync();
        }

        public async Task<Category> GetCategoryById(int id)
        {
            try
            {
                return await _dbContext.Set<Category>().FindAsync(id);
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error getting category.", ex);
            }
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
        //Categoryt by group id
        public async Task<IEnumerable<Category>> GetCategoriesByGroupId(int groupId)
        {
            return await _dbContext.Set<Category>().Where(c => c.GroupId == groupId).ToListAsync();
        }
    }
}