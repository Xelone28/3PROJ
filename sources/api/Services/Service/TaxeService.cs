using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Taxe;
using DotNetAPI.Services.Interface;

namespace DotNetAPI.Services.Service
{
    public class TaxeService : ITaxeService
    {
        private readonly UserDbContext _dbContext;

        public TaxeService(UserDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<IEnumerable<Taxe>> GetAllTaxes()
        {
            return await _dbContext.Set<Taxe>().ToListAsync();
        }

        public async Task<Taxe> GetTaxeById(int id)
        {
            return await _dbContext.Set<Taxe>().FindAsync(id);
        }

        public async Task<Taxe> CreateTaxe(Taxe taxe)
        {
            _dbContext.Set<Taxe>().Add(taxe);
            await _dbContext.SaveChangesAsync();
            return taxe;
        }

        public async Task UpdateTaxe(Taxe taxe)
        {
            _dbContext.Entry(taxe).State = EntityState.Modified;
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeleteTaxe(int id)
        {
            var taxe = await _dbContext.Set<Taxe>().FindAsync(id);
            if (taxe != null)
            {
                _dbContext.Set<Taxe>().Remove(taxe);
                await _dbContext.SaveChangesAsync();
            }
        }
    }
}
