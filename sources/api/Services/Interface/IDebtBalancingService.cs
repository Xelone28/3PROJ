namespace DotNetAPI.Services.Interface
{
    public interface IDebtBalancingService
    {
        Task BalanceDebts(int groupId);
    }
}
