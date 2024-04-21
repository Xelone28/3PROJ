using DotNetAPI.Model;
using DotNetAPI.Model.DTO;

namespace DotNetAPI.Services
{
    public interface IUserService
    {
        Task<IEnumerable<UserDTO>> GetAllUsers();
        Task<User> GetUserById(int id);
        Task<User> AddUser(User user);
        Task<User> UpdateUser(User user);
        Task DeleteUser(int id);
        Task<User> GetUserByEmailAndPassword(string email, string password);
        Task<User?> GetUserByEmail(string email);
        Task<AuthenticateResponse?> Authenticate(AuthenticateRequest model);
    }
}
