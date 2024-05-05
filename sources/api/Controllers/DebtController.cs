using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interface;

[ApiController]
[Route("[controller]")]

public class DebtController : ControllerBase
{
private readonly IDebtService _debtService;
    private readonly AuthenticationService _authenticationService;

    public DebtController(IDebtService debtService, AuthenticationService authenticationService)
    {
        _debtService = debtService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Debt>>> Get()
    {
        var debts = await _debtService.GetAllDebts();
        return Ok(debts);
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Debt>> Get(int id)
    {
        var debt = await _debtService.GetDebtById(id);
        if (debt == null)
        {
            return NotFound();
        }
        return Ok(debt);
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult<Debt>> Post([FromBody] Debt debt)
    {
        var newDebt = await _debtService.CreateDebt(debt);
        return CreatedAtAction(nameof(Get), new { id = newDebt.Id }, newDebt);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] Debt debt)
    {
        if (debt == null)
        {
            return BadRequest("Invalid patch data");
        }

        var debtToUpdate = await _debtService.GetDebtById(id);
        if (debtToUpdate == null)
        {
            return NotFound();
        }

        await _debtService.UpdateDebt(debtToUpdate);
        return Ok(debtToUpdate);
    }

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        var debt = await _debtService.GetDebtById(id);
        if (debt == null)
        {
            return NotFound();
        }
        await _debtService.DeleteDebt(id);
        return NoContent();
    }
}