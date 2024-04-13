using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services
{
    public class UserInGroupService : IUserInGroupService
    {
        private readonly UserDbContext _dbContext;

        public UserInGroupService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<UserInGroup>> GetAllMemberships()
        {
            return await _dbContext.Set<UserInGroup>().Include(u => u.User).Include(g => g.Group).ToListAsync();
        }

        public async Task<UserInGroup> GetMembershipById(int userId, int groupId)
        {
            return await _dbContext.Set<UserInGroup>()
                .Include(u => u.User)
                .Include(g => g.Group)
                .FirstOrDefaultAsync(m => m.UserId == userId && m.GroupId == groupId);
        }

        public async Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroupDto)
        {
            var userInGroup = new UserInGroup
            {
                UserId = userInGroupDto.UserId,
                GroupId = userInGroupDto.GroupId,
                IsGroupAdmin = userInGroupDto.IsGroupAdmin
            };

            _dbContext.UserInGroup.Add(userInGroup);
            await _dbContext.SaveChangesAsync();
            return userInGroup;
        }

        public async Task UpdateMembership(UserInGroup userInGroup)
        {
            _dbContext.Entry(userInGroup).State = EntityState.Modified;
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeleteMembership(int userId, int groupId)
        {
            var membership = await _dbContext.Set<UserInGroup>().FindAsync(userId, groupId);
            if (membership != null)
            {
                _dbContext.Set<UserInGroup>().Remove(membership);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
