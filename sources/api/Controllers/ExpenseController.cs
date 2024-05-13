using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Expense;
using DotNetAPI.Services.Interface;
using DotNetAPI.Services;
using Microsoft.Extensions.Configuration;

[ApiController]
[Route("[controller]")]

public class ExpenseController : ControllerBase
{
    private readonly IDebtService _debtService;
    private readonly IExpenseService _expenseService;
    private readonly AuthenticationService _authenticationService;
    private readonly IUtils _utils;
    private readonly IConfiguration _configuration;

    public ExpenseController(
        IDebtService debtService,
        IExpenseService expenseService,
        AuthenticationService authenticationService,
        IUtils utils,
        IConfiguration configuration)
    {
        _debtService = debtService;
        _expenseService = expenseService;
        _authenticationService = authenticationService;
        _utils = utils;
        _configuration = configuration;
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
        var s3Paths = _configuration.GetSection("S3Paths");
        string expensePath = s3Paths["Expense"];
        string cdnUrl = s3Paths["CDNURL"];
        
        string s3ImagePath = $"{expensePath}{id}";
        var attachmentFromExpense = await _utils.ListFiles(s3ImagePath);
        var imageUrl = "";
        if (attachmentFromExpense.Count > 0) {
            //Permits to make the use of attachment evolutive
            imageUrl = attachmentFromExpense[0];
        }
        var expenseWithImageUrl = new ExpenseWithImageUrlDTO
        {
            Amount = expense.Amount,
            CategoryId = expense.CategoryId,
            Date = expense.Date,
            GroupId = expense.GroupId,
            Place = expense.Place,
            UserId = expense.UserId,
            UserIdInvolved = expense.UserIdInvolved,
            Description = expense.Description,
            Id = expense.Id,
            Image = string.IsNullOrEmpty(imageUrl) ? null : cdnUrl+imageUrl
        };
        return Ok(expenseWithImageUrl);
    }

    [HttpPost]
    [Authorize]
    public async Task<ActionResult<Expense>> Post([FromForm] ExpenseWithImageDTO expenseModel)
    {
        if (expenseModel == null || expenseModel.Image == null)
        {
            return BadRequest("No image uploaded.");
        }

        var expense = new Expense
        {
            UserId = expenseModel.UserId,
            GroupId = expenseModel.GroupId,
            UserIdInvolved = expenseModel.UserIdInvolved,
            CategoryId = expenseModel.CategoryId,
            Amount = expenseModel.Amount,
            Date = expenseModel.Date,
            Place = expenseModel.Place,
            Description = expenseModel.Description
        };

        var newExpense = await _expenseService.CreateExpense(expense);
        await _debtService.CreateDebtsFromExpense(expense);

        string fileName = "expense" + Path.GetExtension(expenseModel.Image.FileName);

        var s3Paths = _configuration.GetSection("S3Paths");
        string expensePath = s3Paths["Expense"];

        string s3ImagePath = expensePath + expense.Id + "/" + fileName;

        using (var memoryStream = new MemoryStream())
        {
            await expenseModel.Image.CopyToAsync(memoryStream);
            memoryStream.Position = 0;
            await _utils.UploadFileAsync(memoryStream, s3ImagePath, expenseModel.Image.ContentType);
        }

        return CreatedAtAction(nameof(Get), new { id = newExpense.Id }, newExpense);
    }

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromForm] ExpenseUpdateDTO expense)
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
        expenseToUpdate.UserIdInvolved = expense.UserIdInvolved ?? expenseToUpdate.UserIdInvolved;
        expenseToUpdate.GroupId = expense.GroupId ?? expenseToUpdate.GroupId;
        expenseToUpdate.Place = expense.Place ?? expenseToUpdate.Place;

        if (expense.Image != null)
        {
            string fileName = "expense" + Path.GetExtension(expense.Image.FileName);

            var s3Paths = _configuration.GetSection("S3Paths");
            string expensePath = s3Paths["Expense"];

            string s3ImagePath = $"{expensePath}/{id}";

            var filesToDelete = await _utils.ListFiles(s3ImagePath);
            try
            {
                using (var memoryStream = new MemoryStream())
                {
                    await expense.Image.CopyToAsync(memoryStream);
                    memoryStream.Position = 0;
                    string timestamp = DateTime.UtcNow.ToString("yyyyMMddHHmmssfff");
                    await _utils.UploadFileAsync(memoryStream, $"{s3ImagePath}/{timestamp}-{fileName}", expense.Image.ContentType);
                }
                foreach (var file in filesToDelete)
                {
                    await _utils.DeleteFile(file);
                }
            } catch (Exception ex)
            {
                Console.WriteLine("Something went wrong" + ex.Message);

            }
        }

        await _expenseService.UpdateExpense(expenseToUpdate);
        await _debtService.UpdateDebtsFromExpense(expenseToUpdate);
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

        var s3Paths = _configuration.GetSection("S3Paths");
        string expensePath = s3Paths["Expense"];

        await _debtService.DeleteDebtsByExpenseId(id);
        await _expenseService.DeleteExpense(id);
        var filesToDelete = await _utils.ListFiles($"{expensePath}{id}");
        foreach (var file in filesToDelete)
        {
            await _utils.DeleteFile(file);
        }
        return NoContent();
    }
    //GetExpensesByGroupId
    [HttpGet("group/{groupId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> GetExpensesByGroupId(int groupId)
    {
        var expenses = await _expenseService.GetExpensesByGroupId(groupId);
        return Ok(expenses);
    }

    //GetExpensesByUserId
    [HttpGet("user/{userId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> GetExpensesByUserId(int userId)
    {
        var expenses = await _expenseService.GetExpensesByUserId(userId);
        return Ok(expenses);
    }

    //GetExpensesByUserIdAndGroupId
    [HttpGet("user/{userId}/group/{groupId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> GetExpensesByUserIdAndGroupId(int userId, int groupId)
    {
        var expenses = await _expenseService.GetExpensesByUserIdAndGroupId(userId, groupId);
        return Ok(expenses);
    }
}