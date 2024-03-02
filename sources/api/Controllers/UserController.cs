using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;

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
        public async Task<ActionResult<IEnumerable<User>>> GetUsers()
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
        public async Task<ActionResult<User>> GetUser(int id)
        {
            try
            {
                var user = await _userService.GetUserById(id);

                if (user == null)
                {
                    return NotFound($"User with ID {id} not found.");
                }

                return Ok(user);
            }
            catch (Exception ex)
            {
                return StatusCode(500, ex.Message);
            }
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

        [HttpPut("{id}")]
        [Authorize]
        public async Task<IActionResult> PutUser(int id, User user)
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if (id != user.Id)
                {
                    return BadRequest("Invalid request. The ID in the URL does not match the ID in the request body.");
                }

                if (currentUser.Id != user.Id)
                {
                    return Unauthorized("Invalid request. You do not have any right on this user.");
                }

                var updatedUser = await _userService.UpdateUser(user);

                if (updatedUser == null)
                {
                    return NotFound($"User with ID {id} not found.");
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
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
                var userToDelete = await _userService.GetUserById(id);

                if (userToDelete == null)
                {
                    return NotFound($"User with ID {id} not found.");
                }

                await _userService.DeleteUser(userToDelete);
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
                return BadRequest(new { message = "Username or password is incorrect" });

            return Ok(response);
        }
    }
}
