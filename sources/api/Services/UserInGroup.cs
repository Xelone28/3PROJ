using DotNetAPI.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using DotNetAPI.Helpers;

namespace DotNetAPI.Services
{
    public class UserInGroupService : IUserInGroupService
    {
        private readonly UserDbContext _dbContext;

        public UserInGroupService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<List<UserInGroup>?> GetMembershipsByUserId(int userId, bool isActive)
        {
            return await _dbContext.Set<UserInGroup>()
                .Where(u => u.User.Id == userId && u.IsActive == isActive)
            .ToListAsync();
        }

        public async Task<List<UserInGroup>?> GetUsersFromGroup(int groupId)
        {
            return await _dbContext.Set<UserInGroup>()
                .Include(u => u.User)
                .Where(u => u.Group.Id == groupId)
                .ToListAsync();
        }

        public async Task<UserInGroup?> GetMembership(int userId, int groupId)
        {
            return await _dbContext.Set<UserInGroup>()
                .Where(u => u.User.Id == userId && u.Group.Id == groupId)
                .FirstOrDefaultAsync();
        }

        public async Task CreateMembership(UserInGroupCreateDTO userInGroupDto, User user)
        {
            try {
                var group = await _dbContext.Set<Group>().FindAsync(userInGroupDto.GroupId);
                if (group == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "The group :"+ userInGroupDto.GroupId + "does not exists");
                } else
                {
                    var userInGroup = new UserInGroup
                    {
                        User = user,
                        Group = group,
                        IsGroupAdmin = userInGroupDto.IsGroupAdmin,
                        IsActive = false
                    };

                    _dbContext.UserInGroup.Add(userInGroup);
                    await _dbContext.SaveChangesAsync();
                    return;
                }
                
            } catch (DbUpdateException ex) when (ex.InnerException is PostgresException postgresEx && postgresEx.SqlState == "23505")
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Duplicate key value violates unique constraint.");
            }
            catch (Exception ex)
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