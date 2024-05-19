using Microsoft.EntityFrameworkCore;
using DotNetAPI.Models.Category;
using DotNetAPI.Models.Debt;
using DotNetAPI.Models.Expense;
using DotNetAPI.Models.Group;
using DotNetAPI.Models.Taxe;
using DotNetAPI.Models.User;
using DotNetAPI.Models.UserInGroup;
using DotNetAPI.Models.Payment;
using DotNetAPI.Models.UserInvolvedExpense;

namespace DotNetAPI
{
    public class UserDbContext : DbContext
    {
        public UserDbContext(DbContextOptions<UserDbContext> options) : base(options) { }

        public DbSet<User> User { get; set; }
        public DbSet<Group> Group { get; set; }
        public DbSet<UserInGroup> UserInGroup { get; set; }
        public DbSet<Expense> Expense { get; set; }
        public DbSet<Category> Category { get; set; }
        public DbSet<Payment> Payment { get; set; }
        public DbSet<Debt> Debt { get; set; }
        public DbSet<Taxe> Taxe { get; set; }
        public DbSet<UserInvolvedExpense> UserInvolvedExpense { get; set; }
        public DbSet<DebtAdjustment> DebtAdjustments { get; set; }
        public DbSet<DebtAdjustmentOriginalDebt> DebtAdjustmentOriginalDebt { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>(entity =>
            {
                entity.HasKey(u => u.Id);

                entity.Property(u => u.Email)
                    .IsRequired()
                    .HasMaxLength(256);

                entity.HasIndex(u => u.Email)
                    .IsUnique();
            });

            modelBuilder.Entity<UserInGroup>(entity =>
            {
                entity.HasKey(uig => new { uig.UserId, uig.GroupId });
            });

            modelBuilder.Entity<Category>(entity =>
            {
                entity.HasKey(e => e.Id);

                entity.Property(e => e.Name).IsRequired();

                entity.HasOne<Group>()
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<Expense>(entity =>
            {
                entity.HasKey(a => a.Id);

                entity.HasOne(e => e.User)
                      .WithMany()
                      .HasForeignKey(e => e.UserId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Group>()
                      .WithMany()
                      .HasForeignKey(e => e.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(e => e.Category)
                      .WithMany()
                      .HasForeignKey(e => e.CategoryId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<Debt>(entity =>
            {
                entity.HasKey(d => d.Id);

                entity.HasOne<Group>()
                      .WithMany()
                      .HasForeignKey(d => d.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne<Expense>()
                      .WithMany()
                      .HasForeignKey(d => d.ExpenseId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<Payment>(entity =>
            {
                entity.HasKey(p => p.Id);

                entity.HasOne(p => p.User)
                      .WithMany()
                      .HasForeignKey(p => p.UserId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(p => p.UserInCredit)
                      .WithMany()
                      .HasForeignKey(p => p.UserInCreditId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Group>(p => p.Group)
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne<DebtAdjustment>(p => p.DebtAdjustment)
                      .WithMany()
                      .HasForeignKey(p => p.DebtAdjustmentId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            modelBuilder.Entity<DebtAdjustment>(entity =>
            {
                entity.HasKey(da => da.Id);

                entity.HasOne(da => da.UserInCredit)
                      .WithMany()
                      .HasForeignKey(da => da.UserInCreditId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(da => da.UserInDebt)
                      .WithMany()
                      .HasForeignKey(da => da.UserInDebtId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Group>(da => da.Group)
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<UserInvolvedExpense>()
                .HasKey(uie => new { uie.UserId, uie.ExpenseId });

            modelBuilder.Entity<UserInvolvedExpense>()
                .HasOne(uie => uie.User)
                .WithMany()
                .HasForeignKey(uie => uie.UserId);

            modelBuilder.Entity<UserInvolvedExpense>()
                .HasOne(uie => uie.Expense)
                .WithMany()
                .HasForeignKey(uie => uie.ExpenseId);

            modelBuilder.Entity<DebtAdjustmentOriginalDebt>(entity =>
            {
                entity.HasKey(d => new { d.DebtAdjustmentId, d.OriginalDebtId });

                entity.HasOne<DebtAdjustment>(d => d.DebtAdjustment)
                      .WithMany(da => da.OriginalDebts)
                      .HasForeignKey(d => d.DebtAdjustmentId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne<Debt>(d => d.OriginalDebt)
                      .WithMany()
                      .HasForeignKey(d => d.OriginalDebtId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            base.OnModelCreating(modelBuilder);
        }
    }
}
