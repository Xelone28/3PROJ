using DotNetAPI.Model;
using DotNetAPI.Model.DTO;

namespace DotNetAPI.Services
{
    public interface IGroupService
    {
        Task<IEnumerable<UserGroup>> GetAllGroups();
        Task<UserGroup> GetUserGroupById(int id);
        Task<UserGroup> CreateGroup(UserGroup userGroup);
        Task UpdateGroup(UserGroup userGroup);
        Task DeleteGroup(int id);
    }
}

