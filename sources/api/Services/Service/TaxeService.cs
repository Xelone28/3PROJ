using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Taxe;
using DotNetAPI.Services.Interface;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Http;

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
            try
            {
                return await _dbContext.Set<Taxe>().ToListAsync();
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting all taxes.");
            }
        }

        public async Task<Taxe> GetTaxeById(int id)
        {
            try
            {
                var taxe = await _dbContext.Set<Taxe>().FindAsync(id);
                if (taxe == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Taxe not found.");
                }
                return taxe;
            }
            catch (HttpException)
            {
                throw;
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while getting the taxe.");
            }
        }

        public async Task<Taxe> CreateTaxe(Taxe taxe)
        {
            try
            {
                _dbContext.Set<Taxe>().Add(taxe);
                await _dbContext.SaveChangesAsync();
                return taxe;
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error creating taxe. Possible duplicate or constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while creating the taxe.");
            }
        }

        public async Task UpdateTaxe(Taxe taxe)
        {
            try
            {
                _dbContext.Entry(taxe).State = EntityState.Modified;
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error updating taxe. It may have been modified or deleted by another user.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while updating the taxe.");
            }
        }

        public async Task DeleteTaxe(int id)
        {
            try
            {
                var taxe = await _dbContext.Set<Taxe>().FindAsync(id);
                if (taxe == null)
                {
                    throw new HttpException(StatusCodes.Status404NotFound, "Taxe not found.");
                }
                _dbContext.Set<Taxe>().Remove(taxe);
                await _dbContext.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                throw new HttpException(StatusCodes.Status409Conflict, "Error deleting taxe. Possible constraint violation.");
            }
            catch (Exception)
            {
                throw new HttpException(StatusCodes.Status500InternalServerError, "An unexpected error occurred while deleting the taxe.");
            }
        }
    }
}
