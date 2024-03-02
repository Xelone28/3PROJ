using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI
{
    public class UserDbContext : DbContext
    {
        public UserDbContext(DbContextOptions<UserDbContext> options) : base(options)
        {
        }
        public DbSet<Category> Category { get; set; }
        public DbSet<DebtInGroup> DebtInGroup { get; set; }
        public DbSet<Expense> Expense{ get; set; }
        public DbSet<UserGroup> Group { get; set; }
        public DbSet<Payment> Payment { get; set; }
        public DbSet<Taxe> Taxe { get; set; }
        public DbSet<UserInGroup> UserInGroup { get; set; }
        public DbSet<User> User { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<DebtInGroup>()
                .HasNoKey();

            modelBuilder.Entity<Payment>()
                .HasNoKey();

            modelBuilder.Entity<UserInGroup>()
                .HasNoKey();

            modelBuilder.Entity<Payment>()
                .HasOne(p => p.DebtInGroup)
                .WithMany()
                .HasForeignKey(p => p.DebtId);


            ////Payment
            modelBuilder.Entity<Payment>()
                .HasOne<User>()
                .WithMany()
                .HasForeignKey(p => p.UserId)
                .OnDelete(DeleteBehavior.Restrict);

            ////Category
            modelBuilder.Entity<Category>()
                .HasOne<UserGroup>()
                .WithMany()
                .HasForeignKey(p => p.GroupId)
                .OnDelete(DeleteBehavior.Restrict);

            //modelBuilder.Entity<Category>()
            //   .HasOne<User>()
            //   .WithMany()
            //   .HasForeignKey(p => p.UserId)
            //   .OnDelete(DeleteBehavior.Restrict);



            base.OnModelCreating(modelBuilder);
        }
    }
}
