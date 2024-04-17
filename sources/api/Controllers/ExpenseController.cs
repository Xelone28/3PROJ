using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using DotNetAPI.Model.DTO;

[ApiController]
[Route("[controller]")]

public class ExpenseController : ControllerBase
{
    private readonly DebtService _debtService;
    private readonly IExpenseService _expenseService;
    private readonly AuthenticationService _authenticationService;

    public ExpenseController(DebtService debtService, IExpenseService expenseService, AuthenticationService authenticationService)
    {
        _debtService = debtService;
        _expenseService = expenseService;
        _authenticationService = authenticationService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> Get()
    {
        var expenses = await _expenseService.GetAllExpenses();
        return Ok(expenses);
    }

    [HttpGet("{id}")]
    [Authorize]
    public async Task<ActionResult<Expense>> Get(int id)
    {
        var expense = await _expenseService.GetExpenseById(id);
        if (expense == null)
        {
            return NotFound();
        }
        return Ok(expense);
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult<Expense>> Post([FromBody] Expense expense)
    {
        var newExpense = await _expenseService.CreateExpense(expense);
        await _debtService.CreateDebtsFromExpense(expense);
        return CreatedAtAction(nameof(Get), new { id = newExpense.Id }, newExpense);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromBody] ExpenseUpdateDTO expense)
    {
        if (expense == null)
        {
            return BadRequest("Invalid patch data");
        }

        var expenseToUpdate = await _expenseService.GetExpenseById(id);
        if (expenseToUpdate == null)
        {
            return NotFound();
        }

        expenseToUpdate.CategoryId = expense.CategoryId ?? expenseToUpdate.CategoryId;
        expenseToUpdate.UserId = expense.UserId ?? expenseToUpdate.UserId;
        expenseToUpdate.Date = expense.Date ?? expenseToUpdate.Date;
        expenseToUpdate.Amount = expense.Amount ?? expenseToUpdate.Amount;
        expenseToUpdate.Description = expense.Description ?? expenseToUpdate.Description;

        await _expenseService.UpdateExpense(expenseToUpdate);
        return NoContent();
    }

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        var expense = await _expenseService.GetExpenseById(id);
        if (expense == null)
        {
            return NotFound();
        }

        await _expenseService.DeleteExpense(id);
        return NoContent();
    }
}