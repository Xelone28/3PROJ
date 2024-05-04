using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Taxe;
using DotNetAPI.Services.Interface;

[ApiController]
[Route("[controller]")]

public class TaxeController : ControllerBase
{
    private readonly ITaxeService _taxeService;
    private readonly AuthenticationService _authenticationService;

    public TaxeController(ITaxeService taxeService, AuthenticationService authenticationService)
    {
        _taxeService = taxeService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Taxe>>> Get()
    {
        var taxes = await _taxeService.GetAllTaxes();
        return Ok(taxes);
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Taxe>> Get(int id)
    {
        var taxe = await _taxeService.GetTaxeById(id);
        if (taxe == null)
        {
            return NotFound();
        }
        return Ok(taxe);
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult<Taxe>> Post([FromBody] Taxe taxe)
    {
        var newTaxe = await _taxeService.CreateTaxe(taxe);
        return CreatedAtAction(nameof(Get), new { id = newTaxe.Id }, newTaxe);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] Taxe taxe)
    {
        if (taxe == null)
        {
            return BadRequest("Invalid patch data");
        }

        var taxeToUpdate = await _taxeService.GetTaxeById(id);
        if (taxeToUpdate == null)
        {
            return NotFound();
        }

        taxeToUpdate.Name = taxe.Name;
        taxeToUpdate.Rate = taxe.Rate;


        await _taxeService.UpdateTaxe(taxeToUpdate);
        return NoContent();
    }

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        await _taxeService.DeleteTaxe(id);
        return NoContent();
    }
}