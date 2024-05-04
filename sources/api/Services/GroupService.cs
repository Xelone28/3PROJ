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

        public async Task<IEnumerable<Group>> GetAllGroups()
        {
            return await _dbContext.Set<Group>().ToListAsync();
        }

        public async Task<Group?> GetGroupById(int id)
        {
            return await _dbContext.Set<Group>().FindAsync(id);
        }

        public async Task<Group> CreateGroup(Group userGroup)
        {
            _dbContext.Set<Group>().Add(userGroup);
            await _dbContext.SaveChangesAsync();
            return userGroup;
        }

        public async Task UpdateGroup(Group userGroup)
        {
            _dbContext.Entry(userGroup).State = EntityState.Modified;
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeleteGroup(int id)
        {
            var userGroup = await _dbContext.Set<Group>().FindAsync(id);
            if (userGroup != null)
            {
                _dbContext.Set<Group>().Remove(userGroup);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
