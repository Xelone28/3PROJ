using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authentication;
using DotNetAPI.Helpers;
using DotNetAPI.Models.Expense;
using DotNetAPI.Services.Interface;
using Microsoft.Extensions.Configuration;
using DotNetAPI.Models.User;
using DotNetAPI.Models.Category;
using DotNetAPI.Models.UserInvolvedExpense;
using Microsoft.AspNetCore.Http;


[ApiController]
[Route("[controller]")]
public class ExpenseController : ControllerBase
{
    private readonly IDebtService _debtService;
    private readonly IExpenseService _expenseService;
    private readonly IUserInvolvedExpense _userInvolvedExpense;
    private readonly IUserService _userService;
    private readonly ICategoryService _categoryService;
    private readonly AuthenticationService _authenticationService;
    private readonly IUtils _utils;
    private readonly IConfiguration _configuration;
    private readonly IDebtBalancingService _debtBalancingService;

    public ExpenseController(
        IDebtService debtService,
        IExpenseService expenseService,
        ICategoryService categoryService,
        IUserInvolvedExpense userInvolvedExpense,
        AuthenticationService authenticationService,
        IUtils utils,
        IConfiguration configuration,
        IUserService userService,
        IDebtBalancingService debtBalancingService)
    {
        _debtService = debtService;
        _expenseService = expenseService;
        _userService = userService;
        _categoryService = categoryService;
        _userInvolvedExpense = userInvolvedExpense;
        _authenticationService = authenticationService;
        _utils = utils;
        _configuration = configuration;
        _debtBalancingService = debtBalancingService;
    }

    [HttpGet]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> Get()
    {
        try
        {
            var expenses = await _expenseService.GetAllExpenses();
            return Ok(expenses);
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
    public async Task<ActionResult<Expense>> Get(int id)
    {
        try
        {
            var expense = await _expenseService.GetExpenseById(id);
            if (expense == null)
            {
                return NotFound();
            }
            var usersInvolvedExpense = await _userInvolvedExpense.GetUserInvolvedByExpenseId(expense.Id);

            var s3Paths = _configuration.GetSection("S3Paths");
            string expensePath = s3Paths["Expense"];
            string cdnUrl = s3Paths["CDNURL"];

        string s3ImagePath = $"{expensePath}{id}";
        var attachmentFromExpense = await _utils.ListFiles(s3ImagePath);
        var imageUrl = "";
        if (attachmentFromExpense.Count > 0)
        {
            imageUrl = attachmentFromExpense[0];
        }

            var userDTO = new UserDTO
            {
                Email = expense.User.Email,
                PaypalUsername = expense.User.PaypalUsername,
                Rib = expense.User.Rib,
                Username = expense.User.Username,
                Id = expense.User.Id
            };

            IList<UserDTO> usersInvolved = new List<UserDTO>();
            IList<float> usersInvolvedWeight = new List<float>();

            if (usersInvolvedExpense is List<UserInvolvedExpense>)
            {
                foreach (var userInvolvedExpense in usersInvolvedExpense)
                {
                    usersInvolved.Add(new UserDTO
                    {
                        Id = userInvolvedExpense.User.Id,
                        Username = userInvolvedExpense.User.Username,
                        Email = userInvolvedExpense.User.Email,
                        PaypalUsername = userInvolvedExpense.User.PaypalUsername,
                        Rib = userInvolvedExpense.User.Rib

                    });
                    usersInvolvedWeight.Add(userInvolvedExpense.Weight);
                }
            }

            var expenseWithImageUrl = new ExpenseWithImageUrlDTO
            {
                Amount = expense.Amount,
                Category = expense.Category,
                Date = expense.Date,
                GroupId = expense.GroupId,
                Place = expense.Place,
                User = userDTO,
                UsersInvolved = usersInvolved,
                Weights = usersInvolvedWeight,
                Description = expense.Description,
                Id = expense.Id,
                Image = string.IsNullOrEmpty(imageUrl) ? null : cdnUrl + imageUrl
            };
            return Ok(expenseWithImageUrl);
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
    public async Task<ActionResult<Expense>> Post([FromForm] ExpenseWithImageDTO expenseModel)
    {
        try
        {
            User? user = await _userService.GetUserById(expenseModel.UserId);
            IList<User> usersInvolved = new List<User>();

            if (expenseModel == null)
            {
                return BadRequest("Given values are not correct");
            }
            var category = await _categoryService.GetCategoryById(expenseModel.CategoryId);
            if (category == null)
            {
                return NotFound($"The category {expenseModel.CategoryId} does not exists");
            }

            if (user == null)
            {
                return NotFound($"The user {expenseModel.UserId} does not exist");
            }

            if (expenseModel.UserIdsInvolved.Count != expenseModel.Weights.Count)
            {
                return BadRequest("The number of weights must match the number of users involved.");
            }

            var expense = new Expense
            {
                GroupId = expenseModel.GroupId,
                Amount = expenseModel.Amount,
                Date = expenseModel.Date,
                Place = expenseModel.Place,
                Description = expenseModel.Description,
                Category = category,
                User = user,
                CategoryId = category.Id,
                UserId = user.Id
                
            };

            var newExpense = await _expenseService.CreateExpense(expense);
            for (int i = 0; i < expenseModel.UserIdsInvolved.Count; i++)
            {
                int userId = expenseModel.UserIdsInvolved[i];
                float weight = expenseModel.Weights[i];

                User? userInvolved = await _userService.GetUserById(userId);
                if (userInvolved is User)
                {
                    usersInvolved.Add(userInvolved);

                    var userInvolvedExpense = new UserInvolvedExpense
                    {
                        Expense = newExpense,
                        User = userInvolved,
                        Weight = weight
                    };
                    await _userInvolvedExpense.AddUserInExpense(userInvolvedExpense);
                }
                else
                {
                    return NotFound($"The user {userId} does not exist");
                }
            }

            await _debtService.CreateDebtsFromExpense(expense, usersInvolved, expenseModel.Weights);

        await _debtBalancingService.BalanceDebts(expenseModel.GroupId);

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

    [HttpPatch("{id}")]
    [Authorize]
    public async Task<IActionResult> Patch(int id, [FromForm] ExpenseUpdateDTO expense)
    {
        if (expense == null)
        {
            return BadRequest("Invalid patch data");
        }

        try
        {
            var expenseToUpdate = await _expenseService.GetExpenseById(id);
            if (expenseToUpdate == null)
            {
                return NotFound();
            }

            IList<User> usersInvolved = new List<User>();
            IList<float> usersInvolvedWeights = new List<float>();
            if (expense.UserIdsInvolved != null && expense.Weights != null)
            {
                // Ensure the lists have the same length
                if (expense.UserIdsInvolved.Count != expense.Weights.Count)
                {
                    return BadRequest("The number of weights must match the number of users involved.");
                }

                // Delete the old relation between expense and user
                await _userInvolvedExpense.DeleteFromExpenseId(expenseToUpdate.Id);

                for (int i = 0; i < expense.UserIdsInvolved.Count; i++)
                {
                    int userId = expense.UserIdsInvolved[i];
                    float weight = expense.Weights[i];

                    User? user = await _userService.GetUserById(userId);
                    if (user == null)
                    {
                        return NotFound($"The user id {userId} does not exist");
                    }
                    else
                    {
                        var userInvolvedExpense = new UserInvolvedExpense
                        {
                            Expense = expenseToUpdate,
                            User = user,
                            Weight = weight
                        };
                        await _userInvolvedExpense.AddUserInExpense(userInvolvedExpense);
                        usersInvolved.Add(user);
                        usersInvolvedWeights.Add(weight);
                    }
                }
            }

            Category? category = null;
            if (expense.CategoryId is int)
            {
                category = await _categoryService.GetCategoryById((int)expense.CategoryId);
            }

            expenseToUpdate.Category = category ?? expenseToUpdate.Category;
            expenseToUpdate.Amount = expense.Amount ?? expenseToUpdate.Amount;
            expenseToUpdate.Date = expense.Date ?? expenseToUpdate.Date;
            expenseToUpdate.Description = expense.Description ?? expenseToUpdate.Description;
            expenseToUpdate.Place = expense.Place ?? expenseToUpdate.Place;

            if (expense.Image != null)
            {
                string fileName = "expense" + Path.GetExtension(expense.Image.FileName);

                var s3Paths = _configuration.GetSection("S3Paths");
                string expensePath = s3Paths["Expense"];

                string s3ImagePath = $"{expensePath}{id}";

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
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Something went wrong" + ex.Message);
                }
            }

        await _expenseService.UpdateExpense(expenseToUpdate);
        await _debtService.UpdateDebtsFromExpense(expenseToUpdate, usersInvolved, usersInvolvedWeights);
        await _debtBalancingService.BalanceDebts(expenseToUpdate.GroupId);

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

    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        try
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
            await _userInvolvedExpense.DeleteFromExpenseId(id);
            var filesToDelete = await _utils.ListFiles($"{expensePath}{id}");
            foreach (var file in filesToDelete)
            {
                await _utils.DeleteFile(file);
            }
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
    public async Task<ActionResult<IEnumerable<Expense>>?> GetExpensesByGroupId(int groupId)
    {
        try
        {
            var expenses = await _expenseService.GetExpensesByGroupId(groupId);
            IList<ExpenseMinimal> expenseMinimals = new List<ExpenseMinimal>();

            if (expenses is IEnumerable<Expense>)
            {
                foreach (Expense expense in expenses)
                {
                    List<UserInvolvedExpense>? usersInvolvedExpense = await _userInvolvedExpense.GetUserInvolvedByExpenseId(expense.Id);
                    if (usersInvolvedExpense is List<UserInvolvedExpense>)
                    {
                        List<int> userIds = new List<int>();
                        if (usersInvolvedExpense != null)
                        {
                            userIds = usersInvolvedExpense.Select(u => u.User.Id).ToList();
                        }

                        var user = new UserDTO
                        {
                            Email = expense.User.Email,
                            PaypalUsername = expense.User.PaypalUsername,
                            Rib = expense.User.Rib,
                            Username = expense.User.Username,
                            Id = expense.User.Id
                        };

                        var expenseMinimal = new ExpenseMinimal
                        {
                            Amount = expense.Amount,
                            CategoryId = expense.Category.Id,
                            Date = expense.Date,
                            GroupId = expense.Date,
                            Place = expense.Place,
                            User = user,
                            UserIdsInvolved = userIds,
                            Description = expense.Description,
                            Id = expense.Id
                        };
                        expenseMinimals.Add(expenseMinimal);
                    }
                    else
                    {
                        return BadRequest("An error occured, please contact an administrator");
                    }
                }
                return Ok(expenseMinimals);
            }
            return NotFound("There are no expenses for the group id :" + groupId);
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

    [HttpGet("user/{userId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> GetExpensesByUserId(int userId)
    {
        try
        {
            var expenses = await _expenseService.GetExpensesByUserId(userId);
            return Ok(expenses);
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
    [HttpGet("user/{userId}/group/{groupId}")]
    [Authorize]
    public async Task<ActionResult<IEnumerable<Expense>>> GetExpensesByUserIdAndGroupId(int userId, int groupId)
    {
        try
        {
            var expenses = await _expenseService.GetExpensesByUserIdAndGroupId(userId, groupId);
            return Ok(expenses);
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
