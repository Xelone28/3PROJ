using Microsoft.EntityFrameworkCore;
using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public class GroupService : IGroupService
    {
        private readonly UserDbContext _dbContext;

        public GroupService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<UserGroup>> GetAllGroups()
        {
            return await _dbContext.Set<UserGroup>().ToListAsync();
        }

        public async Task<UserGroup> GetUserGroupById(int id)
        {
            return await _dbContext.Set<UserGroup>().FindAsync(id);
        }

        public async Task<UserGroup> CreateGroup(UserGroup userGroup)
        {
            _dbContext.Set<UserGroup>().Add(userGroup);
            await _dbContext.SaveChangesAsync();
            return userGroup;
        }

        public async Task UpdateGroup(UserGroup userGroup)
        {
            _dbContext.Entry(userGroup).State = EntityState.Modified;
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeleteGroup(int id)
        {
            var userGroup = await _dbContext.Set<UserGroup>().FindAsync(id);
            if (userGroup != null)
            {
                _dbContext.Set<UserGroup>().Remove(userGroup);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
