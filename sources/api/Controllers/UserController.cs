using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.User;
using DotNetAPI.Models.Authenticate;
using DotNetAPI.Services.Interface;
using Microsoft.Extensions.Configuration;
using DotNetAPI.Models.Expense;

namespace DotNetAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly AuthenticationService _authenticationService;
        private readonly IUtils _utils;
        private readonly IConfiguration _configuration;

        public UsersController(
            IUserService userService,
            AuthenticationService authenticationService,
            IUtils utils,
            IConfiguration configuration)
        {
            _userService = userService;
            _authenticationService = authenticationService;
            _utils = utils;
            _configuration = configuration;
        }

        [HttpGet]
        [Authorize]
        public async Task<ActionResult<IEnumerable<UserDTO>>> GetUsers()
        {
            try
            {
                var users = await _userService.GetAllUsers();
                return Ok(users);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpGet("{id}")]
        [Authorize]
        public async Task<ActionResult<UserDTO>> GetUser(int id)
        {
            var user = await _userService.GetUserById(id);
            if (user == null)
            {
                return NotFound();
            }
            var s3Paths = _configuration.GetSection("S3Paths");
            string userPath = s3Paths["User"];
            string cdnUrl = s3Paths["CDNURL"];

            string s3ImagePath = $"{userPath}{id}";
            var attachmentFromUser = await _utils.ListFiles(s3ImagePath);
            var imageUrl = "";
            if (attachmentFromUser.Count > 0)
            {
                //Permits to make the use of attachment evolutive
                imageUrl = attachmentFromUser[0];
            }

            var userDto = new UserWithImageURLDTO
            {
                Id = user.Id,
                Username = user.Username,
                Email = user.Email,
                Rib = user.Rib,
                PaypalUsername = user.PaypalUsername,
                Image = string.IsNullOrEmpty(imageUrl) ? null : cdnUrl + imageUrl
            };

            return Ok(userDto);
        }

        [HttpPost]
        public async Task<ActionResult<UserWithImage>> PostUser([FromForm] UserWithImage userWithImage)
        {
            try
            {
                var newUser = new User
                {
                    Id = userWithImage.Id,
                    Username = userWithImage.Username,
                    Email = userWithImage.Email,
                    Password = userWithImage.Password,
                    PaypalUsername = userWithImage.PaypalUsername,
                    Rib = userWithImage.Rib       
                };

                var createdUser = await _userService.AddUser(newUser);

                string fileName = "userPP" + Path.GetExtension(userWithImage.Image.FileName);

                var s3Paths = _configuration.GetSection("S3Paths");
                string userPath = s3Paths["User"];

                string s3ImagePath = userPath + createdUser.Id + "/" + fileName;

                using (var memoryStream = new MemoryStream())
                {
                    await userWithImage.Image.CopyToAsync(memoryStream);
                    memoryStream.Position = 0;
                    await _utils.UploadFileAsync(memoryStream, s3ImagePath, userWithImage.Image.ContentType);
                }
                return CreatedAtAction(
                    nameof(GetUser),
                    new { id = createdUser.Id },
                    new UserDTO
                    {
                        Id = createdUser.Id,
                        Email = createdUser.Email,
                        PaypalUsername = createdUser.PaypalUsername,
                        Rib = createdUser.Rib,
                        Username = createdUser.Username
                    });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpPatch("{id}")]
        [Authorize]
        public async Task<IActionResult> UpdateUserPartial(int id, [FromForm] UserUpdateDTO user)
        {
            if (user == null)
            {
                return BadRequest("Invalid user data");
            }

            var userFromDb = await _userService.GetUserById(id);
            if (userFromDb == null)
            {
                return NotFound($"User with ID {id} not found.");
            }

            var currentUser = (User)HttpContext.Items["User"];

            if (currentUser.Id != userFromDb.Id)
            {
                return Unauthorized("You do not have permission to modify this user.");
            }

            if (!string.IsNullOrEmpty(user.Username))
            {
                userFromDb.Username = user.Username;
            }

            if (!string.IsNullOrEmpty(user.Email))
            {
                userFromDb.Email = user.Email;
            }

            if (!string.IsNullOrEmpty(user.Password))
            {
                userFromDb.Password = user.Password;
            }

            if (user.Image != null)
            {
                string fileName = "userPP" + Path.GetExtension(user.Image.FileName);

                var s3Paths = _configuration.GetSection("S3Paths");
                string userPath = s3Paths["User"];

                string s3ImagePath = $"{userPath}{id}";

                var filesToDelete = await _utils.ListFiles(s3ImagePath);
                try
                {
                    using (var memoryStream = new MemoryStream())
                    {
                        await user.Image.CopyToAsync(memoryStream);
                        memoryStream.Position = 0;
                        string timestamp = DateTime.UtcNow.ToString("yyyyMMddHHmmssfff");
                        await _utils.UploadFileAsync(memoryStream, $"{s3ImagePath}/{timestamp}-{fileName}", user.Image.ContentType);
                    }
                    foreach (var file in filesToDelete)
                    {
                        await _utils.DeleteFile(file);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Something went wrong" + ex.Message);

                }
            }
            if (await _userService.UpdateUser(userFromDb, userFromDb.Password) == null)
            {
                return NotFound("The user does not exists");
            } else
            {
                return NoContent();
            }
        }


        [HttpDelete("{id}")]
        [Authorize]
        public async Task<IActionResult> DeleteUser(int id)
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if (currentUser.Id != id)
                {
                    return Unauthorized("Invalid request. You do not have any right on this user.");
                }

                await _userService.DeleteUser(id);
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> login(AuthenticateRequest model)
        {
            var response = await _userService.Authenticate(model);

            if (response == null)
                return BadRequest(new { message = "Email or password is incorrect" });

            return Ok(response);
        }

        [HttpGet("email/{email}")]
        [Authorize]
        public async Task<ActionResult<UserDTO>> GetUserByEmail(String email)
        {
            if (string.IsNullOrWhiteSpace(email))
            {
                return BadRequest("Email is required in the request body.");
            }

            var user = await _userService.GetUserByEmail(email);
            if (user == null)
            {
                return NotFound($"User with email {email} not found.");
            }

            var userDto = new UserDTO
            {
                Id = user.Id,
                Username = user.Username,
                Email = user.Email,
                Rib = user.Rib,
                PaypalUsername = user.PaypalUsername
            };

            return Ok(userDto);
        }
    }
}
