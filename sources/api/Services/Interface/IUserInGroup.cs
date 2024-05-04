using DotNetAPI.Models.User;
using DotNetAPI.Models.UserInGroup;

namespace DotNetAPI.Services.Interface
{
    public interface IUserInGroupService
    {
        Task<List<UserInGroup>?> GetMembershipsByUserId(int userId, bool isActive);
        Task<List<UserInGroup>?> GetUsersFromGroup(int userId);
        Task<UserInGroup?> GetMembership(int userId, int groupId);
        Task<UserInGroup> CreateMembership(UserInGroupCreateDTO userInGroup, User user);
        Task UpdateMembership(UserInGroup userInGroup);
        Task DeleteMembership(int userId, int groupId);
    }
}