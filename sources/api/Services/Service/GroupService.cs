using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Group;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace DotNetAPI.Services.Service
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
            try
            {
                return await _dbContext.Set<Group>().ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting all groups.");
            }
        }

        public async Task<Group?> GetGroupById(int id)
        {
            try
            {
                var group = await _dbContext.Set<Group>().FindAsync(id);
                if (group == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Group not found.");
                }
                return group;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the group.");
            }
        }

        public async Task<Group> CreateGroup(Group userGroup)
        {
            try
            {
                _dbContext.Set<Group>().Add(userGroup);
                await _dbContext.SaveChangesAsync();
                return userGroup;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating group. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the group.");
            }
        }

        public async Task UpdateGroup(Group userGroup)
        {
            try
            {
                _dbContext.Entry(userGroup).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating group. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the group.");
            }
        }

        public async Task DeleteGroup(int id)
        {
            try
            {
                var userGroup = await _dbContext.Set<Group>().FindAsync(id);
                if (userGroup == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Group not found.");
                }
                _dbContext.Set<Group>().Remove(userGroup);
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting group. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the group.");
            }
        }
    }
}
