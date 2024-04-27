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

        public async Task<List<UserInGroup>>? GetMembershipsByUserId(int userId, bool isActive)
        {
            return await _dbContext.Set<UserInGroup>()
                .Where(u => u.UserId == userId && u.IsActive == isActive)
                .ToListAsync();
        }

        public async Task<UserInGroup?> GetMembership(int userId, int groupId)
        {
            return await _dbContext.Set<UserInGroup>()
                .FirstOrDefaultAsync(u => u.UserId == userId && u.GroupId == groupId);
        }

        public async Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroupDto)
        {
            try {
                var userInGroup = new UserInGroup
                {
                    UserId = userInGroupDto.UserId,
                    GroupId = userInGroupDto.GroupId,
                    IsGroupAdmin = userInGroupDto.IsGroupAdmin,
                    IsActive = false
                };

                _dbContext.UserInGroup.Add(userInGroup);
                await _dbContext.SaveChangesAsync();
                return userInGroup;
            } catch (Exception ex)
            {
                throw new ApplicationException("Error adding user.", ex);
            }

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
