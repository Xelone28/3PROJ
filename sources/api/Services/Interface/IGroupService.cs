using DotNetAPI.Models.Group;

namespace DotNetAPI.Services.Interface
{
    public interface IGroupService
    {
        Task<IEnumerable<Group>> GetAllGroups();
        Task<Group?> GetGroupById(int id);
        Task<Group> CreateGroup(Group userGroup);
        Task UpdateGroup(Group userGroup);
        Task DeleteGroup(int id);
    }
}

