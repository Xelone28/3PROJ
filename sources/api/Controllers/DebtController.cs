using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interface;
using DotNetAPI.Models.User;

[ApiController]
[Route("[controller]")]

public class DebtController : ControllerBase
{
    private readonly IDebtService _debtService;
    private readonly IExpenseService _expenseService;
    private readonly IUserInGroupService _userInGroupService;
    private readonly AuthenticationService _authenticationService;

    public DebtController(
        IDebtService debtService,
        IExpenseService expenseService,
        IUserInGroupService userInGroupService,
        AuthenticationService authenticationService
        )
    {
        _debtService = debtService;
        _expenseService = expenseService;
        _userInGroupService = userInGroupService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Debt>>> Get()
    {
        var debts = await _debtService.GetAllDebts();
        return Ok(debts);
    }

    [HttpGet("expense/{id}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Debt>>> GetByExpenseId(int id)
    {
        var expense = await _expenseService.GetExpenseById(id);
        if (expense == null)
        {
            return NotFound();
        } else
        {
            var userId = (HttpContext.Items["User"] as User)?.Id ?? null;
            if (userId != null)
            {
                var users = await _userInGroupService.GetUsersFromGroup(expense.GroupId);
                if (users != null) {
                    foreach (var user in users)
                    {
                        if (user.UserId == userId)
                        {
                            return Ok(await _debtService.GetDebtsByExpenseId(id));
                        }
                    }
                }
                
            }
        }
        return Unauthorized("You do not have access to this expense");
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