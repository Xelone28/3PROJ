using Microsoft.AspNetCore.Mvc;
using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

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
            var debtAdjustments = await _debtAdjustmentService.GetAllDebtAdjustments();
            return Ok(debtAdjustments);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<DebtAdjustmentDTO>> GetDebtAdjustmentById(int id)
        {
            var debtAdjustment = await _debtAdjustmentService.GetDebtAdjustmentById(id);
            if (debtAdjustment == null)
            {
                return NotFound();
            }
            return Ok(debtAdjustment);
        }

        [HttpGet("user/{userId}")]
        public async Task<ActionResult<IEnumerable<DebtAdjustmentDTO>>> GetDebtAdjustmentsByUserId(int userId)
        {
            var debtAdjustments = await _debtAdjustmentService.GetDebtAdjustmentsByUserId(userId);
            return Ok(debtAdjustments);
        }

        [HttpGet("group/{groupId}")]
        public async Task<ActionResult<IEnumerable<DebtAdjustmentDTO>>> GetDebtAdjustmentsByGroupId(int groupId)
        {
            var debtAdjustments = await _debtAdjustmentService.GetDebtAdjustmentsByGroupId(groupId);
            return Ok(debtAdjustments);
        }
    }
}
