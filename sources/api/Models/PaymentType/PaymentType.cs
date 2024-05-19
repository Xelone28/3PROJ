using System.ComponentModel;
using System.Reflection;

namespace DotNetAPI.Models.PaymentType
{
    public enum Status
    {
        [Description("wired")]
        wired = 1,

        [Description("paid with Stripe")]
        stripe = 2,

        [Description("paid With Paypal")]
        paypal = 3
    }

}

public static class EnumHelper
{
    public static string GetEnumDescription<TEnum>(TEnum value) where TEnum : Enum
    {
        FieldInfo field = value.GetType().GetField(value.ToString());
        DescriptionAttribute attribute = field.GetCustomAttribute<DescriptionAttribute>();

        return attribute == null ? value.ToString() : attribute.Description;
    }
}
