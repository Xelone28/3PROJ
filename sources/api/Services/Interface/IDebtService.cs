using DotNetAPI.Models.Debt;

namespace DotNetAPI.Services.Interface
{
    public interface IDebtService
    {
        Task<IEnumerable<Debt>> GetAllDebts();
        Task<Debt> GetDebtById(int id);
        Task<Debt> CreateDebt(Debt debt);
        Task UpdateDebt(Debt debt);
        Task DeleteDebt(int id);
    }
}
