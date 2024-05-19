using DotNetAPI.Models.Debt;
using DotNetAPI.Services.Interfaces;
using DotNetAPI.Helpers;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http;

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
            try
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
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting all debt adjustments.");
            }
        }

        public async Task<DebtAdjustmentDTO> GetDebtAdjustmentById(int id)
        {
            try
            {
                var debtAdjustment = await _context.DebtAdjustments
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

                if (debtAdjustment == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Debt adjustment not found.");
                }

                return debtAdjustment;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting debt adjustment by id.");
            }
        }

        public async Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByUserId(int userId)
        {
            try
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
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting debt adjustments by user id.");
            }
        }

        public async Task<IEnumerable<DebtAdjustmentDTO>> GetDebtAdjustmentsByGroupId(int groupId)
        {
            try
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
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "Error getting debt adjustments by group id.");
            }
        }

        public async Task DeleteDebtAdjustment(DebtAdjustment debtAdjustment)
        {
            if (debtAdjustment != null)
            {
                _context.DebtAdjustments.Remove(debtAdjustment);
                await _context.SaveChangesAsync();
            }
        }
    }
}
