using DotNetAPI.Models.Category;

namespace DotNetAPI.Services.Interface
{
    public interface ICategoryService
    {
        Task<IEnumerable<Category>> GetAllCategories();
        Task<Category> GetCategoryById(int id);
        Task<Category> CreateCategory(Category category);
        Task UpdateCategory(Category category);
        Task DeleteCategory(int id);
        Task<IEnumerable<Category>> GetCategoriesByGroupId(int groupId);
    }
}
