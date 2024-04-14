using DotNetAPI.Model;

namespace DotNetAPI.Services
{
    public interface ITaxeService
    {
        Task<IEnumerable<Taxe>> GetAllTaxes();
        Task<Taxe> GetTaxeById(int id);
        Task<Taxe> CreateTaxe(Taxe taxe);
        Task UpdateTaxe(Taxe taxe);
        Task DeleteTaxe(int id);
    }
}
