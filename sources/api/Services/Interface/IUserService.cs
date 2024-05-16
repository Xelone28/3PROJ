using DotNetAPI.Models.Authenticate;
using DotNetAPI.Models.User;

namespace DotNetAPI.Services.Interface
{
    public interface IUserService
    {
        Task<IEnumerable<UserDTO>> GetAllUsers();
        Task<User?> GetUserById(int id);
        Task<User> AddUser(User user);
        Task<User?> UpdateUser(User user, string? password);
        Task DeleteUser(int id);
        Task<User> GetUserByEmailAndPassword(string email, string password);
        Task<User?> GetUserByEmail(string email);
        Task<AuthenticateResponse?> Authenticate(AuthenticateRequest model);
    }
}
