using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface IUserInGroupService
    {
        Task<IEnumerable<UserInGroup>> GetAllMemberships();
        Task<UserInGroup> GetMembershipById(int userId, int groupId);
        Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroup);
        Task UpdateMembership(UserInGroup userInGroup);
        Task DeleteMembership(int userId, int groupId);
    }
}
