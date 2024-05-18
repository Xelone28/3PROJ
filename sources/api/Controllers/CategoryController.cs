using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Category;
using DotNetAPI.Services.Interface;
using Microsoft.AspNetCore.Http;
using DotNetAPI.Services.Service;
using DotNetAPI.Models.Group;

[ApiController]
[Route("[controller]")]
public class CategoryController : ControllerBase
{
    private readonly IGroupService _groupService;
    private readonly ICategoryService _categoryService;
    private readonly AuthenticationService _authenticationService;

    public CategoryController(ICategoryService categoryService, AuthenticationService authenticationService, IGroupService groupService)
    {
        _categoryService = categoryService;
        _groupService = groupService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Category>>> Get()
    {
        try
        {
            var categories = await _categoryService.GetAllCategories();
            return Ok(categories);
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
    public async Task<ActionResult<Category>> Get(int id)
    {
        try
        {
            var category = await _categoryService.GetCategoryById(id);
            if (category == null)
            {
                return NotFound();
            }
            return Ok(category);
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
    public async Task<ActionResult<Category>> Post([FromBody] Category category)
    {
        try
        {
            var newCategory = await _categoryService.CreateCategory(category);
            return CreatedAtAction(nameof(Get), new { id = newCategory.Id }, newCategory);
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
    public async Task<IActionResult> Patch(int id, [FromBody] Category category)
    {
        if (category == null)
        {
            return BadRequest("Invalid patch data");
        }

        try
        {
            var categoryToUpdate = await _categoryService.GetCategoryById(id);
            if (categoryToUpdate == null)
            {
                return NotFound();
            }

            categoryToUpdate.Name = category.Name;

            await _categoryService.UpdateCategory(categoryToUpdate);
            return Ok(categoryToUpdate);
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

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        try
        {
            var category = await _categoryService.GetCategoryById(id);
            if (category == null)
            {
                return NotFound();
            }

            await _categoryService.DeleteCategory(id);
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

    [HttpGet("group/{groupId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Category>>> GetCategoriesByGroupId(int groupId)
    {
        try
        {
            var group = await _groupService.GetGroupById(groupId);
            if (group is null)
            {
                return NotFound("The group does not exists");
            }
            var categories = await _categoryService.GetCategoriesByGroupId(groupId);
            return Ok(categories);
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
