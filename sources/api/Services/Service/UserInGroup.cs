using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using DotNetAPI.Helpers;
using DotNetAPI.Models.UserInGroup;
using DotNetAPI.Models.User;
using DotNetAPI.Models.Group;
using DotNetAPI.Services.Interface;

namespace DotNetAPI.Services.Service
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
            try
            {
                return await _dbContext.Set<UserInGroup>()
                    .Include(u => u.User)
                    .Include(u => u.Group)
                    .Where(u => u.User.Id == userId && u.IsActive == isActive)
                    .ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting memberships by user ID.");
            }
        }

        public async Task<List<UserInGroup>?> GetUsersFromGroup(int groupId)
        {
            try
            {
                return await _dbContext.Set<UserInGroup>()
                    .Include(u => u.User)
                    .Where(u => u.Group.Id == groupId && u.IsActive == true)
                    .ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting users from group.");
            }
        }

        public async Task<UserInGroup?> GetMembership(int userId, int groupId)
        {
            try
            {
                return await _dbContext.Set<UserInGroup>()
                    .Where(u => u.User.Id == userId && u.Group.Id == groupId)
                    .FirstOrDefaultAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the membership.");
            }
        }

        public async Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroupDto, User user)
        {
            try
            {
                var group = await _dbContext.Set<Group>().FindAsync(userInGroupDto.GroupId);
                if (group == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "The group :" + userInGroupDto.GroupId + " does not exist.");
                }

                var userInGroup = new UserInGroup
                {
                    User = user,
                    Group = group,
                    IsGroupAdmin = userInGroupDto.IsGroupAdmin,
                    IsActive = false
                };

                _dbContext.UserInGroup.Add(userInGroup);
                await _dbContext.SaveChangesAsync();
                return userInGroup;
            }
            catch (DbUpdateException ex) when (ex.InnerException is PostgresException postgresEx && postgresEx.SqlState == "23505")
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
            try
            {
                _dbContext.Entry(userInGroup).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating membership. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the membership.");
            }
        }

        public async Task DeleteMembership(int userId, int groupId)
        {
            try
            {
                var membership = await _dbContext.Set<UserInGroup>().FindAsync(userId, groupId);
                if (membership == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Membership not found.");
                }

                _dbContext.Set<UserInGroup>().Remove(membership);
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting membership. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the membership.");
            }
        }

        public async Task<List<User>> GetUsersInUserGroups(int userId)
        {
            try
            {
                var usersInGroup = await _dbContext.UserInGroup
                    .Where(ug => ug.UserId == userId)
                    .SelectMany(ug => _dbContext.UserInGroup
                        .Where(u => u.GroupId == ug.GroupId && u.UserId != userId)
                        .Select(u => u.User))
                    .Distinct()
                    .ToListAsync();

                return usersInGroup;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting users in user groups.");
            }
        }
    }
}
