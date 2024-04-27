using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface IUserInGroupService
    {
        Task<List<UserInGroup>?> GetMembershipsByUserId(int userId, bool isActive);
        Task<UserInGroup?> GetMembership(int userId, int groupId);
        Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroup);
        Task UpdateMembership(UserInGroup userInGroup);
        Task DeleteMembership(int userId, int groupId);
    }
}