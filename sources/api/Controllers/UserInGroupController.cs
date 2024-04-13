using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Authorization;

[ApiController]
[Route("[controller]")]
public class UserInGroupController : ControllerBase
{
    private readonly IUserInGroupService _userInGroupService;

    public UserInGroupController(IUserInGroupService userInGroupService)
    {
        _userInGroupService = userInGroupService;
    }

    [HttpGet]
    [Authorize]
    public async Task<IActionResult> GetAll()
    {
        try
        {
            var memberships = await _userInGroupService.GetAllMemberships();
            return Ok(memberships);
        }
        catch (Exception ex)
        {
            // Log the exception details here for debugging purposes.
            return StatusCode(500, "An error occurred while retrieving memberships.");
        }
    }

    [HttpGet("{userId}/{groupId}")]
    [Authorize]
    public async Task<IActionResult> Get(int userId, int groupId)
    {
        try
        {
            var membership = await _userInGroupService.GetMembershipById(userId, groupId);
            if (membership == null) return NotFound("Membership not found.");
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
            CreatedAtAction(nameof(Get), new { userId = result.UserId, groupId = result.GroupId }, result);
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
            var userInGroup = await _userInGroupService.GetMembershipById(userId, groupId);
            if (userInGroup == null)
            {
                return NotFound("Membership not found.");
            }

            userInGroup.IsGroupAdmin = dto.IsGroupAdmin;

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
