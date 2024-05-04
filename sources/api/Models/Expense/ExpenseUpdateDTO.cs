namespace DotNetAPI.Models.Expense
{
    public class ExpenseUpdateDTO
    {
        public int? UserId { get; set; }
        public int? CategoryId { get; set; }
        public float? Amount { get; set; }
        public int? Date { get; set; }
        public string? Place { get; set; }
        public string? Description { get; set; }
    }
}
