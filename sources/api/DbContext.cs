﻿using DotNetAPI.Model;
using Microsoft.EntityFrameworkCore;

namespace DotNetAPI
{
    public class UserDbContext : DbContext
    {
        public UserDbContext(DbContextOptions<UserDbContext> options) : base(options) { }
        public DbSet<User> User { get; set; }
        public DbSet<Group> Group { get; set; }
        public DbSet<UserInGroup> UserInGroup { get; set; }
        public DbSet<Expense> Expense{ get; set; }
        public DbSet<Category> Category { get; set; }
        public DbSet<Payment> Payment { get; set; }
        public DbSet<Debt> Debt { get; set; }
        public DbSet<Taxe> Taxe { get; set; }

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
                entity.HasKey(uig => new { uig.UserId, uig.GroupId});

                entity.HasOne(uig => uig.User)
                      .WithMany()
                      .HasForeignKey(uig => uig.UserId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(uig => uig.Group)
                      .WithMany()
                      .HasForeignKey(uig => uig.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<Category>(entity =>
            {
                entity.HasKey(e => e.Id);

                entity.Property(e => e.Name);

                entity.HasOne<Group>()
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<Expense>(entity =>
            {
                entity.HasKey(a => a.Id);

                entity.HasOne<User>()
                      .WithMany()
                      .HasForeignKey(p => p.UserId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Group>()
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Category>()
                      .WithMany()
                      .HasForeignKey(p => p.CategoryId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<Debt>(entity =>
            {
                entity.HasKey(d => d.Id);

                entity.HasOne(d => d.Group)
                      .WithMany()
                      .HasForeignKey(d => d.GroupId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(d => d.Expense)
                      .WithMany()
                      .HasForeignKey(d => d.BillId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(d => d.UserInCredit)
                      .WithMany()
                      .HasForeignKey(d => d.UserIdInCredit)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(d => d.UserInDebt)
                      .WithMany()
                      .HasForeignKey(d => d.UserIdInDebt)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            modelBuilder.Entity<Payment>(entity =>
            {
                entity.HasKey(p => p.Id);

                entity.HasOne(p => p.User)
                      .WithMany()
                      .HasForeignKey(p => p.UserId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(p => p.UserGroup)
                      .WithMany()
                      .HasForeignKey(p => p.GroupId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(p => p.Debt)
                      .WithMany()
                      .HasForeignKey(p => p.DebtId)
                      .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(p => p.Taxe)
                      .WithMany()
                      .HasForeignKey(p => p.TaxeId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            base.OnModelCreating(modelBuilder);
        }
    }
}
