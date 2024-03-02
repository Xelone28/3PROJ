using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface IUserService
    {
        Task<IEnumerable<User>> GetAllUsers();
        Task<User> GetUserById(int id);
        Task<User> AddUser(User user);
        Task<User> UpdateUser(User user);
        Task DeleteUser(User user);
        Task<User> GetUserByEmailAndPassword(string email, string password);
        Task<AuthenticateResponse?> Authenticate(AuthenticateRequest model);
    }
}
