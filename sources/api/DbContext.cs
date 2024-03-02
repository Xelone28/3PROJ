using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI
{
    public class UserDbContext : DbContext
    {
        public UserDbContext(DbContextOptions<UserDbContext> options) : base(options)
        {
        }

        public DbSet<User> User { get; set; }
        public DbSet<Product> Product { get; set; }
        public DbSet<Cart> Cart { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Product>()
                .Property(p => p.Image)
                .HasColumnType("bytea");
            base.OnModelCreating(modelBuilder);
        }
    }
}
