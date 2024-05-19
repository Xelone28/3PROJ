using DotNetAPI.Models.Debt;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace DotNetAPI.Services.Interfaces
{
    public interface IDebtAdjustmentService
    {
        Task<IEnumerable<DebtAdjustmentDTO>> GetAllDebtAdjustments();
        Task<DebtAdjustmentDTO> GetDebtAdjustmentById(int id);
        Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByUserId(int userId);
        Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByGroupId(int groupId);
        Task DeleteDebtAdjustment(DebtAdjustment debtAdjustment);
    }
}
