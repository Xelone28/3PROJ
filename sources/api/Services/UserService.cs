using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace DotNetAPI.Services
{
    public class UserService : IUserService
    {
        private readonly UserDbContext _dbContext;
        private readonly AppSettings _appSettings;


        public UserService(UserDbContext dbContext, IOptions<AppSettings> appSettings)
        {
            _dbContext = dbContext ?? throw new ArgumentNullException(nameof(dbContext));
            _appSettings = appSettings.Value;

        }

        public async Task<IEnumerable<User>> GetAllUsers()
        {
            return await _dbContext.User.ToListAsync();
        }

        public async Task<User> GetUserById(int id)
        {
            var user = await _dbContext.User.FindAsync(id);

            if (user == null)
            {
                throw new NotFoundException($"User with ID {id} not found.");
            }

            return user;
        }

        public async Task<User> GetUserByEmailAndPassword(string email, string password)
        {
            var user = await _dbContext.User
                .FirstOrDefaultAsync(u => u.Email == email && u.Password == password);
            if (user == null)
            {
                throw new NotFoundException($"User with Email: {email} not found.");
            }

            return user;
        }

        public async Task<User> AddUser(User user)
        {
            try
            {
                _dbContext.User.Add(user);
                await _dbContext.SaveChangesAsync();
                return user;
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error adding user.", ex);
            }
        }

        public async Task<User> UpdateUser(User user)
        {
            try
            {
                _dbContext.Entry(user).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
                return user;
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error updating user.", ex);
            }
        }

        public async Task DeleteUser(User user)
        {
            try
            {
                _dbContext.User.Remove(user);
                await _dbContext.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error deleting user.", ex);
            }
        }
        public async Task<AuthenticateResponse?> Authenticate(AuthenticateRequest model)
        {
            var user = await _dbContext.User.SingleOrDefaultAsync(x => x.Username == model.Username && x.Password == model.Password);

            if (user == null) return null;

            var token = await generateJwtToken(user);

            return new AuthenticateResponse(user, token);

        }

        private async Task<string> generateJwtToken(User user)
        {
            try
    {
        var tokenHandler = new JwtSecurityTokenHandler();
        
        if (string.IsNullOrEmpty(_appSettings.Secret))
        {
            throw new InvalidOperationException("JWT secret is empty or null.");
        }

        var key = Encoding.ASCII.GetBytes(_appSettings.Secret);

        if (key.Length == 0)
        {
            throw new InvalidOperationException("JWT key is empty after encoding.");
        }

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(new[] { new Claim("id", user.Id.ToString()) }),
            Expires = DateTime.UtcNow.AddDays(7),
            SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
        };

        var token = await Task.Run(() => tokenHandler.CreateToken(tokenDescriptor));
        return tokenHandler.WriteToken(token);
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Error generating JWT token: {ex.Message}");
        throw;
    }
}

    }

    public class NotFoundException : Exception
    {
        public NotFoundException(string message) : base(message)
        {
        }
    }
}
