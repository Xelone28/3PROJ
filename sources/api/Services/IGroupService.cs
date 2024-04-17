using DotNetAPI.Model;
using DotNetAPI.Model.DTO;

namespace DotNetAPI.Services
{
    public interface IGroupService
    {
        Task<IEnumerable<Group>> GetAllGroups();
        Task<Group> GetUserGroupById(int id);
        Task<Group> CreateGroup(Group userGroup);
        Task UpdateGroup(Group userGroup);
        Task DeleteGroup(int id);
    }
}

