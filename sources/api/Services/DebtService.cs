using Microsoft.EntityFrameworkCore;
using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public class DebtService : IDebtService
    {
        private readonly UserDbContext _context;

        public DebtService(UserDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Debt>> GetAllDebts()
        {
            return await _context.Debt.ToListAsync();
        }

        public async Task<Debt> GetDebtById(int id)
        {
            try
            {
                return await _context.Debt.FindAsync(id);
            }
            catch (Exception ex)
            {
                throw new ApplicationException("Error getting debt.", ex);
            }
        }

        public async Task<Debt> CreateDebt(Debt debt)
        {
            _context.Debt.Add(debt);
            await _context.SaveChangesAsync();
            return debt;
        }

        public async Task UpdateDebt(Debt debt)
        {
            _context.Entry(debt).State = EntityState.Modified;
            await _context.SaveChangesAsync();
        }

        public async Task DeleteDebt(int id)
        {
            var debt = await _context.Debt.FindAsync(id);
            _context.Debt.Remove(debt);
            await _context.SaveChangesAsync();
        }
    }
}
