using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI.Services
{
    public class DebtAdjustmentService : IDebtAdjustmentService
    {
        private readonly UserDbContext _context;

        public DebtAdjustmentService(UserDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<DebtAdjustmentDTO>> GetAllDebtAdjustments()
        {
            return await _context.DebtAdjustments
                .Select(da => new DebtAdjustmentDTO
                {
                    Id = da.Id,
                    GroupId = da.GroupId,
                    UserInCreditId = da.UserInCreditId,
                    UserInDebtId = da.UserInDebtId,
                    AdjustmentAmount = da.AdjustmentAmount,
                    AdjustmentDate = da.AdjustmentDate
                })
                .ToListAsync();
        }

        public async Task<DebtAdjustmentDTO> GetDebtAdjustmentById(int id)
        {
            return await _context.DebtAdjustments
                .Where(da => da.Id == id)
                .Select(da => new DebtAdjustmentDTO
                {
                    Id = da.Id,
                    GroupId = da.GroupId,
                    UserInCreditId = da.UserInCreditId,
                    UserInDebtId = da.UserInDebtId,
                    AdjustmentAmount = da.AdjustmentAmount,
                    AdjustmentDate = da.AdjustmentDate
                })
                .FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByUserId(int userId)
        {
            return await _context.DebtAdjustments
                .Where(da => da.UserInCreditId == userId || da.UserInDebtId == userId)
                .Select(da => new DebtAdjustmentDTO
                {
                    Id = da.Id,
                    GroupId = da.GroupId,
                    UserInCreditId = da.UserInCreditId,
                    UserInDebtId = da.UserInDebtId,
                    AdjustmentAmount = da.AdjustmentAmount,
                    AdjustmentDate = da.AdjustmentDate
                })
                .ToListAsync();
        }

        public async Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByGroupId(int groupId)
        {
            return await _context.DebtAdjustments
                .Where(da => da.GroupId == groupId)
                .Select(da => new DebtAdjustmentDTO
                {
                    Id = da.Id,
                    GroupId = da.GroupId,
                    UserInCreditId = da.UserInCreditId,
                    UserInDebtId = da.UserInDebtId,
                    AdjustmentAmount = da.AdjustmentAmount,
                    AdjustmentDate = da.AdjustmentDate
                })
                .ToListAsync();
        }
    }
}
