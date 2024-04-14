using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;

[ApiController]
[Route("[controller]")]

public class CategoryController : ControllerBase
{
    private readonly ICategoryService _categoryService;
    private readonly AuthenticationService _authenticationService;

    public CategoryController(ICategoryService categoryService, AuthenticationService authenticationService)
    {
        _categoryService = categoryService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Category>>> Get()
    {
        var categories = await _categoryService.GetAllCategories();
        return Ok(categories);
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Category>> Get(int id)
    {
        var category = await _categoryService.GetCategoryById(id);
        if (category == null)
        {
            return NotFound();
        }
        return Ok(category);
    }

    [HttpPost]
    public async Task<ActionResult<Category>> Post([FromBody] Category category)
    {
        var newCategory = await _categoryService.CreateCategory(category);
        return CreatedAtAction(nameof(Get), new { id = newCategory.Id }, newCategory);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] Category category)
    {
        if (category == null)
        {
            return BadRequest("Invalid patch data");
        }

        var categoryToUpdate = await _categoryService.GetCategoryById(id);
        if (categoryToUpdate == null)
        {
            return NotFound();
        }

        category.Name = category.Name;

        await _categoryService.UpdateCategory(category);
        return Ok(category);
    }

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        var category = await _categoryService.GetCategoryById(id);
        if (category == null)
        {
            return NotFound();
        }

        await _categoryService.DeleteCategory(id);
        return NoContent();
    }
}