using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.EntityFrameworkCore;
using DotNetAPI;
using DotNetAPI.Helpers;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using Microsoft.AspNetCore.Authentication;
using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.SwaggerUI;
using DotNetAPI.Services.Service;
using DotNetAPI.Services.Interface;
using DotNetAPI.Services;
using Microsoft.Extensions.Logging;

public class Startup
{
    public Startup(IConfiguration configuration)
    {
        Configuration = configuration;
    }

    public IConfiguration Configuration { get; }

    public void ConfigureServices(IServiceCollection services)
    {
services.AddCors(options =>
{
    options.AddPolicy("AllowMyOrigin",
    builder => builder.WithOrigins("http://localhost:3001")
                      .AllowAnyHeader()
                      .AllowAnyMethod());
});
        services.AddDbContext<UserDbContext>(options =>
            options.UseNpgsql(Configuration.GetConnectionString("PgsqlConnectionString")));

        services.AddScoped<AuthenticationService>();
        services.AddScoped<DebtService>();
        
        services.AddScoped<IUserService, UserService>();
        services.AddScoped<IGroupService, GroupService>();
        services.AddScoped<IUserInGroupService, UserInGroupService>();
        services.AddScoped<ITaxeService, TaxeService>();
        services.AddScoped<ICategoryService, CategoryService>();
        services.AddScoped<IExpenseService, ExpenseService>();
        services.AddScoped<IDebtService,DebtService >();
        services.AddSingleton<IUtils>(sp =>
        {
            var configuration = sp.GetRequiredService<IConfiguration>();
            var awsSettings = configuration.GetSection("AWSSettings");
            var accessKey = awsSettings["AccessKey"];
            var secretKey = awsSettings["SecretKey"];
            var bucketName = awsSettings["BucketName"];

            // Create logger instance
            var logger = sp.GetRequiredService<ILogger<S3Service>>();

            // Create and return an instance of S3Service with logger
            return new S3Service(accessKey, secretKey, bucketName, logger, configuration);
        });

        services.AddControllers();

        services.AddAuthentication(options =>
        {
            options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
            options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
        })
        .AddJwtBearer(options =>
        {
            options.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(Configuration["AppSettings:Secret"])),
                ValidateIssuer = false,
                ValidateAudience = false
            };
        });

        services.AddAuthorization();

        services.AddSwaggerGen(c =>
        {
            c.SwaggerDoc("v1", new OpenApiInfo { Title = "3PROJ - RatCord", Version = "v1" });
            c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
            {
                In = ParameterLocation.Header,
                Description = "Please enter your JWT token",
                Name = "Authorization",
                Type = SecuritySchemeType.ApiKey
            });

            c.AddSecurityRequirement(new OpenApiSecurityRequirement
            {
                {
                    new OpenApiSecurityScheme
                    {
                    Reference = new OpenApiReference
                        {
                            Type = ReferenceType.SecurityScheme,
                            Id = "Bearer"
                        }
                    },
                    new string[] { }
                }
            });
        });
    }

    public void Configure(IApplicationBuilder app, UserDbContext dbContext)
    {
        // Migration done here
        app.UseDeveloperExceptionPage();
        dbContext.Database.Migrate();

        app.UseRouting();
        app.UseAuthentication();
        app.UseAuthorization();
        app.UseMiddleware<JwtMiddleware>();
        app.UseCors("AllowMyOrigin");
        app.UseEndpoints(endpoints =>
        {
            endpoints.MapControllers();
        });

        app.UseSwagger();
        app.UseSwaggerUI(c =>
        {
            c.SwaggerEndpoint("/swagger/v1/swagger.json", "3PROJ");
            c.DocExpansion(DocExpansion.None);
        });
    }
}
