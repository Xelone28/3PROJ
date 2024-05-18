using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using DotNetAPI.Models.Authenticate;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Interface;
using System.Security.Cryptography;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace DotNetAPI.Services.Service
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

        private byte[] GenerateSalt()
        {
            byte[] salt = new byte[16];
            using (var rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(salt);
            }
            return salt;
        }

        private string HashPassword(string password, byte[] salt)
        {
            using (var pbkdf2 = new Rfc2898DeriveBytes(password, salt, 10000, HashAlgorithmName.SHA256))
            {
                byte[] hash = pbkdf2.GetBytes(20);
                byte[] hashBytes = new byte[36];
                Array.Copy(salt, 0, hashBytes, 0, 16);
                Array.Copy(hash, 0, hashBytes, 16, 20);
                return Convert.ToBase64String(hashBytes);
            }
        }

        private bool VerifyPassword(string enteredPassword, string storedPassword)
        {
            byte[] hashBytes = Convert.FromBase64String(storedPassword);
            byte[] salt = new byte[16];
            Array.Copy(hashBytes, 0, salt, 0, 16);
            using (var pbkdf2 = new Rfc2898DeriveBytes(enteredPassword, salt, 10000, HashAlgorithmName.SHA256))
            {
                byte[] hash = pbkdf2.GetBytes(20);
                for (int i = 0; i < 20; i++)
                {
                    if (hashBytes[i + 16] != hash[i])
                        return false;
                }
                return true;
            }
        }

        public async Task<User?> GetUserById(int id)
        {
            try
            {
                return await _dbContext.User.FindAsync(id);
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "User cannot be retrieved: " + ex.Message);
            }
        }

        public async Task<User?> GetUserByEmail(string email)
        {
            try
            {
                var user = await _dbContext.User.FirstOrDefaultAsync(u => u.Email == email);
                return user;
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error retrieving user by email: " + ex.Message);
            }
        }

        public async Task<User> GetUserByEmailAndPassword(string email, string password)
        {
            try
            {
                var user = await _dbContext.User
                    .FirstOrDefaultAsync(u => u.Email == email && u.Password == password);
                if (user == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, $"User with Email: {email} not found.");
                }
                return user;
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "User cannot be retrieved: " + ex.Message);
            }
        }

        public async Task<User> AddUser(User user)
        {
            try
            {
                byte[] salt = GenerateSalt();
                user.Password = HashPassword(user.Password, salt);
                _dbContext.User.Add(user);
                await _dbContext.SaveChangesAsync();
                return user;
            }
            catch (DbUpdateException ex)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error adding user. Possible constraint violation: " + ex.Message);
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error adding user: " + ex.Message);
            }
        }

        public async Task<User?> UpdateUser(User user, string? newPassword)
        {
            try
            {
                if (user != null)
                {
                    if (newPassword != null)
                    {
                        byte[] salt = GenerateSalt();
                        user.Password = HashPassword(newPassword, salt);
                    }

                    await _dbContext.SaveChangesAsync();
                    return user;
                }
                else
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error updating user: " + ex.Message);
            }
        }

        public async Task DeleteUser(int id)
        {
            var user = await _dbContext.User.FindAsync(id);
            if (user != null)
            {
                try
                {
                    _dbContext.User.Remove(user);
                    await _dbContext.SaveChangesAsync();
                }
                catch (Exception ex)
                {
                    throw new HttpException(StatusCodes.Status500InternalServerError, "Error deleting user: " + ex.Message);
                }
            }
            else
            {
                throw new HttpException(StatusCodes.Status404NotFound, "The user does not exist.");
            }
        }

        public async Task<AuthenticateResponse?> Authenticate(AuthenticateRequest model)
        {
            try
            {
                var user = await _dbContext.User.SingleOrDefaultAsync(x => x.Email == model.Email);

                if (user != null && VerifyPassword(model.Password, user.Password))
                {
                    var token = await GenerateJwtToken(user);
                    return new AuthenticateResponse(user, token);
                }

                return null;
            }
            catch (Exception ex)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error logging in user: " + ex.Message);
            }
        }

        private async Task<string> GenerateJwtToken(User user)
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
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error generating JWT token: " + ex.Message);
            }
        }
    }
}
