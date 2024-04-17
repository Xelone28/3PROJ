using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;

[ApiController]
[Route("[controller]")]
public class GroupController : ControllerBase
{
    private readonly IGroupService _groupService;
    private readonly AuthenticationService _authenticationService;

    public GroupController(IGroupService groupService, AuthenticationService authenticationService)
    {
        _groupService = groupService;
        _authenticationService = authenticationService;

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
        var group = await _groupService.GetUserGroupById(id);
        if (group == null)
        {
            return NotFound();
        }
        return Ok(group);
    }

    [HttpPost]
    public async Task<ActionResult<Group>> Post([FromBody] Group userGroup)
    {
        var newGroup = await _groupService.CreateGroup(userGroup);
        return CreatedAtAction(nameof(Get), new { id = newGroup.Id }, newGroup);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] GroupUpdateDTO groupUpdateDto)
    {
        if (groupUpdateDto == null)
        {
            return BadRequest("Invalid patch data");
        }

        var userGroup = await _groupService.GetUserGroupById(id);
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
