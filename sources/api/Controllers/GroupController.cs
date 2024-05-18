using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Group;
using DotNetAPI.Models.User;
using DotNetAPI.Models.UserInGroup;
using DotNetAPI.Services.Interface;
using Microsoft.AspNetCore.Http;

[ApiController]
[Route("[controller]")]
public class GroupController : ControllerBase
{
    private readonly IGroupService _groupService;
    private readonly IUserInGroupService _userInGroupService;
    private readonly IUserService _userService;

    public GroupController(IGroupService groupService, IUserInGroupService userInGroupService, IUserService userService)
    {
        _groupService = groupService;
        _userInGroupService = userInGroupService;
        _userService = userService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Group>>> Get()
    {
        try
        {
            var groups = await _groupService.GetAllGroups();
            return Ok(groups);
        }
        catch (HttpException ex)
        {
            return StatusCode(ex.StatusCode, ex.Message);
        }
        catch (Exception)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
        }
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Group>> Get(int id)
    {
        try
        {
            var group = await _groupService.GetGroupById(id);
            if (group == null)
            {
                return NotFound();
            }
            return Ok(group);
        }
        catch (HttpException ex)
        {
            return StatusCode(ex.StatusCode, ex.Message);
        }
        catch (Exception)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
        }
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult> Post([FromBody] Group userGroup)
    {
        try
        {
            var newGroup = await _groupService.CreateGroup(userGroup);
            var userId = (HttpContext.Items["User"] as User)?.Id ?? null;

            if (userId is int)
            {
                var loggedInUser = await _userService.GetUserById((int)userId);
                if (loggedInUser is User)
                {
                    var acceptedInvitation = new UserInGroupCreateDTO
                    {
                        UserId = (int)userId,
                        GroupId = newGroup.Id,
                        IsGroupAdmin = true
                    };

                    var invitation = await _userInGroupService.CreateMembership(acceptedInvitation, loggedInUser);
                    invitation.IsActive = true;
                    invitation.IsGroupAdmin = true;
                    await _userInGroupService.UpdateMembership(invitation);
                    return CreatedAtAction(nameof(Get), new { id = newGroup.Id }, newGroup);
                }
            }
            return Unauthorized("You are not logged in.");
        }
        catch (HttpException ex)
        {
            return StatusCode(ex.StatusCode, ex.Message);
        }
        catch (Exception)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
        }
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] GroupUpdateDTO groupUpdateDto)
    {
        if (groupUpdateDto == null)
        {
            return BadRequest("Invalid patch data");
        }

        try
        {
            var userGroup = await _groupService.GetGroupById(id);
            if (userGroup == null)
            {
                return NotFound();
            }

            userGroup.GroupName = groupUpdateDto.GroupName ?? userGroup.GroupName;
            userGroup.GroupDesc = groupUpdateDto.GroupDesc ?? userGroup.GroupDesc;

            await _groupService.UpdateGroup(userGroup);
            return NoContent();
        }
        catch (HttpException ex)
        {
            return StatusCode(ex.StatusCode, ex.Message);
        }
        catch (Exception)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
        }
    }

    [HttpDelete("{groupId}")]
    [Authorize]
    public async Task<IActionResult> Delete(int groupId)
    {
        try
        {
            var userId = (HttpContext.Items["User"] as User)?.Id ?? null;

            if (userId is int)
            {
                var loggedInUser = await _userService.GetUserById((int)userId);
                if (loggedInUser is User)
                {
                    var membership = await _userInGroupService.GetMembership((int)userId, groupId);
                    if (membership is UserInGroup && membership.IsActive && membership.IsGroupAdmin)
                    {
                        await _groupService.DeleteGroup(groupId);
                        return NoContent();
                    }
                    else
                    {
                        return Unauthorized("You don't have the right to delete the group id: " + groupId);
                    }
                }
            }
            return Unauthorized("To delete a group you must be logged in.");
        }
        catch (HttpException ex)
        {
            return StatusCode(ex.StatusCode, ex.Message);
        }
        catch (Exception)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An unexpected error occurred.");
        }
    }
}
