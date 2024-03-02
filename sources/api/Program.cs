using DotNetAPI;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.EntityFrameworkCore;


class Program
{
    static void Main(string[] args)
    {
        using (var scope = CreateHostBuilder(args).Build().Services.CreateScope())
        {
            var services = scope.ServiceProvider;

            //var context = services.GetRequiredService<UserDbContext>();
            //if (context.Database.GetPendingMigrations().Any())
            //{
            //    context.Database.Migrate();
            //}
        }
        CreateHostBuilder(args).Build().Run();
    }

    public static IHostBuilder CreateHostBuilder(string[] args) =>
        Host.CreateDefaultBuilder(args)
            .ConfigureWebHostDefaults(webBuilder =>
            {
                webBuilder.UseStartup<Startup>();
            });
}
