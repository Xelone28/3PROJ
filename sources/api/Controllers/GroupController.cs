using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Group;
using DotNetAPI.Models.User;
using DotNetAPI.Models.UserInGroup;
using DotNetAPI.Services.Interface;

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
        var groups = await _groupService.GetAllGroups();
        return Ok(groups);
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Group>> Get(int id)
    {
        var group = await _groupService.GetGroupById(id);
        if (group == null)
        {
            return NotFound();
        }
        return Ok(group);
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult> Post([FromBody] Group userGroup)
    {
        var newGroup = await _groupService.CreateGroup(userGroup);
        var userId = (HttpContext.Items["User"] as User)?.Id ?? null;
        
        if (userId is int) {
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
                return Created();
            }
        }
        return Unauthorized("You are not logged in.");
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] GroupUpdateDTO groupUpdateDto)
    {
        if (groupUpdateDto == null)
        {
            return BadRequest("Invalid patch data");
        }

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

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        await _groupService.DeleteGroup(id);
        return NoContent();
    }
}
