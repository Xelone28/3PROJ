using DotNetAPI.Helpers;
using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.User;
using DotNetAPI.Services.Interface;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;


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
        AuthenticationService authenticationService)
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
        try
        {
            var debts = await _debtService.GetAllDebts();
            return Ok(debts);
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

    [HttpGet("expense/{id}")]
    [Authorize]
    public async Task<ActionResult<IList<DebtMinimal>>> GetByExpenseId(int id)
    {
        try
        {
            var expense = await _expenseService.GetExpenseById(id);
            if (expense == null)
            {
                return NotFound();
            }

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
            return Unauthorized("You do not have access to this expense");
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
    public async Task<ActionResult<DebtMinimal>> Get(int id)
    {
        try
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
                IsPaid = debt.IsPaid,
                UserInCredit = userInCredit,
                UserInDebt = userInDebt,
                Id = debt.Id
            });
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
