using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Authorization;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json.Linq;

[ApiController]
[Route("[controller]")]
public class UserInGroupController : ControllerBase
{
    private readonly IUserInGroupService _userInGroupService;

    public UserInGroupController(IUserInGroupService userInGroupService)
    {
        _userInGroupService = userInGroupService;
    }

    [HttpGet("{userId}")]
    [Authorize]
    public async Task<IActionResult> GetMemberShipsByUserId(int userId)
    {
        try
        {
            var membership = await _userInGroupService.GetMembershipsByUserId(userId, true);
            if (membership == null)
            {
                return NotFound("Membership not found.");
            }
            return Ok(membership);
        }
        catch (Exception ex)
        {
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while retrieving the membership.");
        }
    }

    [HttpGet("invitation/{userId}")]
    [Authorize]
    public async Task<IActionResult> GetInvitationByUserId(int userId)
    {
        try
        {
            var membership = await _userInGroupService.GetMembershipsByUserId(userId, false);
            if (membership == null)
            {
                return NotFound("Membership not found.");
            }
            return Ok(membership);
        }
        catch (Exception ex)
        {
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while retrieving the membership.");
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
            var result = await _userInGroupService.CreateMembership(userInGroupDto);
            if (result == null)
            {
                return BadRequest("Unable to create user in group");
            }
            return NoContent();
        }
        catch (Exception ex)
        {
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while creating the membership.");
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
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while updating the membership.");
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
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while deleting the membership.");
        }
    }
}
