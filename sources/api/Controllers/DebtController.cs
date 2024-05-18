using DotNetAPI.Helpers;
using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Interface;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Mvc;

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
    public async Task<ActionResult<IList<DebtMinimal>>> GetByExpenseId(int id)
    {
        var expense = await _expenseService.GetExpenseById(id);
        if (expense == null)
        {
            return NotFound();
        }
        else
        {
            var userId = (HttpContext.Items["User"] as User)?.Id ?? null;
            if (userId != null)
            {
                var users = await _userInGroupService.GetUsersFromGroup(expense.GroupId);
                if (users != null)
                {
                    foreach (var user in users)
                    {
                        if (user.User.Id == userId)
                        {
                            IList<DebtMinimal> debtsDto = new List<DebtMinimal>();
                            var debts = await _debtService.GetDebtsByExpenseId(id);
                            foreach (Debt debt in debts)
                            {
                                var userInCredit = new UserDTO
                                {
                                    Id = debt.UserInCredit.Id,
                                    Email = debt.UserInCredit.Email,
                                    PaypalUsername = debt.UserInCredit.PaypalUsername,
                                    Rib = debt.UserInCredit.Rib,
                                    Username = debt.UserInCredit.Username
                                };

                                var userInDebt = new UserDTO
                                {
                                    Id = debt.UserInDebt.Id,
                                    Email = debt.UserInDebt.Email,
                                    PaypalUsername = debt.UserInDebt.PaypalUsername,
                                    Rib = debt.UserInDebt.Rib,
                                    Username = debt.UserInDebt.Username
                                };

                                debtsDto.Add(new DebtMinimal
                                {
                                    Amount = debt.Amount,
                                    IsCanceled = debt.IsCanceled,
                                    IsPaid = debt.IsPaid,
                                    UserInCredit = userInCredit,
                                    UserInDebt = userInDebt,
                                    Id = debt.Id
                                });
                            }
                            return Ok(debtsDto);
                        }
                    }
                }
            }
        }
        return Unauthorized("You do not have access to this expense");
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<DebtMinimal>> Get(int id)
    {
        var debt = await _debtService.GetDebtById(id);
        if (debt == null)
        {
            return NotFound();
        }

        var userInCredit = new UserDTO
        {
            Id = debt.UserInCredit.Id,
            Email = debt.UserInCredit.Email,
            PaypalUsername = debt.UserInCredit.PaypalUsername,
            Rib = debt.UserInCredit.Rib,
            Username = debt.UserInCredit.Username
        };

        var userInDebt = new UserDTO
        {
            Id = debt.UserInDebt.Id,
            Email = debt.UserInDebt.Email,
            PaypalUsername = debt.UserInDebt.PaypalUsername,
            Rib = debt.UserInDebt.Rib,
            Username = debt.UserInDebt.Username
        };

        return Ok(new DebtMinimal
        {
            Amount = debt.Amount,
            IsCanceled = debt.IsCanceled,
            IsPaid = debt.IsPaid,
            UserInCredit = userInCredit,
            UserInDebt = userInDebt,
            Id = debt.Id
        });
    }

    //HERE
    // thoses methods are not used in the project
    // but they are here for future use if needed

/*    [HttpPost]
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
    }*/
}