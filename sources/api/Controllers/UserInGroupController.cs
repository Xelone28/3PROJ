using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Helpers;
using DotNetAPI.Models.UserInGroup;
using DotNetAPI.Services.Interface;
using DotNetAPI.Models.Group;
using DotNetAPI.Models.User;

[ApiController]
[Route("[controller]")]
public class UserInGroupController : ControllerBase
{
    private readonly IUserInGroupService _userInGroupService;
    private readonly IUserService _userService;

    public UserInGroupController(IUserInGroupService userInGroupService, IUserService userService)
    {
        _userInGroupService = userInGroupService;
        _userService = userService;
    }

    [HttpGet("user/{userId}")]
    [Authorize]
    public async Task<IActionResult> GetMemberShipsByUserId(int userId)
    {
        try
        {
            var memberships = await _userInGroupService.GetMembershipsByUserId(userId, true);
            if (memberships == null || memberships.Count == 0)
            {
                return NotFound("Membership not found.");
            }

            var membershipsDtos = new List<Group>();
            foreach (UserInGroup membership in memberships)
            {
                membershipsDtos.Add(membership.Group);
            }
            return Ok(membershipsDtos);
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while retrieving the memberships: " + ex.Message);
        }
    }

    [HttpGet("invitation/{userId}")]
    [Authorize]
    public async Task<IActionResult> GetInvitationByUserId(int userId)
    {
        try
        {
            var memberships = await _userInGroupService.GetMembershipsByUserId(userId, false);
            if (memberships == null || memberships.Count == 0)
            {
                return NotFound("Membership not found.");
            }

            var membershipsDtos = new List<UserInGroupInvitationDTO>();
            foreach (UserInGroup membership in memberships)
            {
                var invitation = new UserInGroupInvitationDTO
                {
                    Group = membership.Group,
                    IsGroupAdmin = membership.IsGroupAdmin
                };
                membershipsDtos.Add(invitation);
            }
            return Ok(membershipsDtos);
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while retrieving the invitations: " + ex.Message);
        }
    }

    [HttpGet("users/{groupId}")]
    [Authorize]
    public async Task<IActionResult> GetUsersFromGroup(int groupId)
    {
        try
        {
            var usersFromGroup = await _userInGroupService.GetUsersFromGroup(groupId);
            if (usersFromGroup == null || usersFromGroup.Count == 0)
            {
                return NotFound("Membership not found.");
            }

            var usersFromGroupDtos = new List<UserInGroupMinimalDTO>();
            foreach (UserInGroup membership in usersFromGroup)
            {
                usersFromGroupDtos.Add(new UserInGroupMinimalDTO
                {
                    UserId = membership.User.Id,
                    Username = membership.User.Username,
                    Email = membership.User.Email,
                    IsActive = membership.IsActive,
                    IsGroupAdmin = membership.IsGroupAdmin
                });
            }
            return Ok(usersFromGroupDtos);
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while retrieving the users from group: " + ex.Message);
        }
    }

    [HttpGet("{userId}/groupusers")]
    [Authorize]
    public async Task<IActionResult> GetUsersInUserGroups(int userId)
    {
        try
        {
            var users = await _userInGroupService.GetUsersInUserGroups(userId);
            if (users == null || users.Count == 0)
            {
                return NotFound("No users found in user's groups.");
            }

            var usersDto = new List<UserDTO>();
            foreach (var user in users)
            {
                var userDto = new UserDTO
                {
                    Email = user.Email,
                    PaypalUsername = user.PaypalUsername,
                    Rib = user.Rib,
                    Username = user.Username,
                    Id = user.Id
                };
                usersDto.Add(userDto);
            }
            return Ok(usersDto);
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while retrieving the users in user groups: " + ex.Message);
        }
    }

    [HttpPost]
    [Authorize]
    public async Task<IActionResult> CreateUserInGroup([FromBody] UserInGroupCreateDTO userInGroupDto)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        try
        {
            var user = await _userService.GetUserById(userInGroupDto.UserId);
            if (user == null)
            {
                return BadRequest("The user does not exist.");
            }

            await _userInGroupService.CreateMembership(userInGroupDto, user);
            return NoContent();
        }
        catch (HttpException httpEx)
        {
            return StatusCode(httpEx.StatusCode, httpEx.Message);
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while creating the membership: " + ex.Message);
        }
    }

    [HttpPatch("{userId}/{groupId}")]
    [Authorize]
    public async Task<IActionResult> Patch(int userId, int groupId, [FromBody] UserInGroupUpdateDTO dto)
    {
        try
        {
            var userInGroup = await _userInGroupService.GetMembership(userId, groupId);
            if (userInGroup == null)
            {
                return NotFound("Membership not found.");
            }

            if (dto.IsGroupAdmin.HasValue)
            {
                userInGroup.IsGroupAdmin = dto.IsGroupAdmin.Value;
            }

            if (dto.IsActive.HasValue)
            {
                userInGroup.IsActive = dto.IsActive.Value;
            }

            await _userInGroupService.UpdateMembership(userInGroup);
            return NoContent();
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while updating the membership: " + ex.Message);
        }
    }

    [HttpDelete("{userId}/{groupId}")]
    [Authorize]
    public async Task<IActionResult> Delete(int userId, int groupId)
    {
        try
        {
            await _userInGroupService.DeleteMembership(userId, groupId);
            return NoContent();
        }
        catch (Exception ex)
        {
            return StatusCode(500, "An error occurred while deleting the membership: " + ex.Message);
        }
    }
}
