using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class DebtAdjustmentController : ControllerBase
    {
        private readonly IDebtAdjustmentService _debtAdjustmentService;

        public DebtAdjustmentController(IDebtAdjustmentService debtAdjustmentService)
        {
            _debtAdjustmentService = debtAdjustmentService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<DebtAdjustmentDTO>>> GetAllDebtAdjustments()
        {
            try
            {
                var debtAdjustments = await _debtAdjustmentService.GetAllDebtAdjustments();
                return Ok(debtAdjustments);
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
        public async Task<ActionResult<DebtAdjustmentDTO>> GetDebtAdjustmentById(int id)
        {
            try
            {
                var debtAdjustment = await _debtAdjustmentService.GetDebtAdjustmentById(id);
                if (debtAdjustment == null)
                {
                    return NotFound();
                }
                return Ok(debtAdjustment);
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
        public async Task<ActionResult<IEnumerable<DebtAdjustmentDTO>>> GetDebtAdjustmentsByUserId(int userId)
        {
            try
            {
                var debtAdjustments = await _debtAdjustmentService.GetDebtAdjustmentsByUserId(userId);
                return Ok(debtAdjustments);
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
        public async Task<ActionResult<IEnumerable<DebtAdjustmentDTO>>> GetDebtAdjustmentsByGroupId(int groupId)
        {
            try
            {
                var debtAdjustments = await _debtAdjustmentService.GetDebtAdjustmentsByGroupId(groupId);
                return Ok(debtAdjustments);
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
}
