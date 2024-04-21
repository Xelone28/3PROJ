using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using DotNetAPI.Model.DTO;

namespace DotNetAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly AuthenticationService _authenticationService;

        public UsersController(IUserService userService, AuthenticationService authenticationService)
        {
            _userService = userService;
            _authenticationService = authenticationService;

        }

        [HttpGet]
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
        public async Task<ActionResult<UserDTO>> GetUser(int id)
        {
            var user = await _userService.GetUserById(id);
            if (user == null)
            {
                return NotFound();
            }

            var userDto = new UserDTO
            {
                // Map properties from User to UserDTO
                Id = user.Id,
                Username = user.Username,
                Email = user.Email,
                Rib = user.Rib,
                PaypalUsername = user.PaypalUsername
            };

            return Ok(userDto);
        }

        [HttpPost]
        public async Task<ActionResult<User>> PostUser(User user)
        {
            try
            {
                var createdUser = await _userService.AddUser(user);
                return CreatedAtAction(nameof(GetUser), new { id = createdUser.Id }, createdUser);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpPatch("{id}")]
        [Authorize]
        public async Task<IActionResult> UpdateUserPartial(int id, [FromBody] UserUpdateDTO user)
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
                // Make sure to hash the password before saving it
                userFromDb.Password = user.Password;
            }

            await _userService.UpdateUser(userFromDb);
            return NoContent();
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
