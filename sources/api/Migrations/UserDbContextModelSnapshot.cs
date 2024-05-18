﻿// <auto-generated />
using System;
using DotNetAPI;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace DotNetAPI.Migrations
{
    [DbContext(typeof(UserDbContext))]
    partial class UserDbContextModelSnapshot : ModelSnapshot
    {
        protected override void BuildModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "8.0.1")
                .HasAnnotation("Relational:MaxIdentifierLength", 63);

            NpgsqlModelBuilderExtensions.UseIdentityByDefaultColumns(modelBuilder);

            modelBuilder.Entity("DotNetAPI.Models.Category.Category", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.HasIndex("GroupId");

                    b.ToTable("Category");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.Debt", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<float>("Amount")
                        .HasColumnType("real");

                    b.Property<int>("ExpenseId")
                        .HasColumnType("integer");

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<bool>("IsPaid")
                        .HasColumnType("boolean");

                    b.Property<int>("UserInCreditId")
                        .HasColumnType("integer");

                    b.Property<int>("UserInDebtId")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("ExpenseId");

                    b.HasIndex("GroupId");

                    b.HasIndex("UserInCreditId");

                    b.HasIndex("UserInDebtId");

                    b.ToTable("Debt");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.DebtAdjustment", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<float>("AdjustmentAmount")
                        .HasColumnType("real");

                    b.Property<DateTime>("AdjustmentDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<int>("UserInCreditId")
                        .HasColumnType("integer");

                    b.Property<int>("UserInDebtId")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("GroupId");

                    b.HasIndex("UserInCreditId");

                    b.HasIndex("UserInDebtId");

                    b.ToTable("DebtAdjustments");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.DebtAdjustmentOriginalDebt", b =>
                {
                    b.Property<int>("DebtAdjustmentId")
                        .HasColumnType("integer");

                    b.Property<int>("OriginalDebtId")
                        .HasColumnType("integer");

                    b.HasKey("DebtAdjustmentId", "OriginalDebtId");

                    b.HasIndex("OriginalDebtId");

                    b.ToTable("DebtAdjustmentOriginalDebt");
                });

            modelBuilder.Entity("DotNetAPI.Models.Expense.Expense", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<float>("Amount")
                        .HasColumnType("real");

                    b.Property<int>("CategoryId")
                        .HasColumnType("integer");

                    b.Property<int>("Date")
                        .HasColumnType("integer");

                    b.Property<string>("Description")
                        .HasColumnType("text");

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<string>("Place")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<int>("UserId")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("CategoryId");

                    b.HasIndex("GroupId");

                    b.HasIndex("UserId");

                    b.ToTable("Expense");
                });

            modelBuilder.Entity("DotNetAPI.Models.Group.Group", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<string>("GroupDesc")
                        .HasColumnType("text");

                    b.Property<string>("GroupName")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("Group");
                });

            modelBuilder.Entity("DotNetAPI.Models.Payment.Payment", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<float>("Amount")
                        .HasColumnType("real");

                    b.Property<int>("DebtAdjustmentId")
                        .HasColumnType("integer");

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<DateTime>("PaymentDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<int>("UserId")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("DebtAdjustmentId");

                    b.HasIndex("GroupId");

                    b.HasIndex("UserId");

                    b.ToTable("Payment");
                });

            modelBuilder.Entity("DotNetAPI.Models.Taxe.Taxe", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("Rate")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("Taxe");
                });

            modelBuilder.Entity("DotNetAPI.Models.User.User", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("Id"));

                    b.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(256)
                        .HasColumnType("character varying(256)");

                    b.Property<string>("Password")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("PaypalUsername")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("Rib")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("Username")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.HasIndex("Email")
                        .IsUnique();

                    b.ToTable("User");
                });

            modelBuilder.Entity("DotNetAPI.Models.UserInGroup.UserInGroup", b =>
                {
                    b.Property<int>("UserId")
                        .HasColumnType("integer");

                    b.Property<int>("GroupId")
                        .HasColumnType("integer");

                    b.Property<bool>("IsActive")
                        .HasColumnType("boolean");

                    b.Property<bool>("IsGroupAdmin")
                        .HasColumnType("boolean");

                    b.HasKey("UserId", "GroupId");

                    b.HasIndex("GroupId");

                    b.ToTable("UserInGroup");
                });

            modelBuilder.Entity("DotNetAPI.Models.UserInvolvedExpense.UserInvolvedExpense", b =>
                {
                    b.Property<int>("UserId")
                        .HasColumnType("integer");

                    b.Property<int>("ExpenseId")
                        .HasColumnType("integer");

                    b.Property<float>("Weight")
                        .HasColumnType("real");

                    b.HasKey("UserId", "ExpenseId");

                    b.HasIndex("ExpenseId");

                    b.ToTable("UserInvolvedExpense");
                });

            modelBuilder.Entity("DotNetAPI.Models.Category.Category", b =>
                {
                    b.HasOne("DotNetAPI.Models.Group.Group", null)
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.Debt", b =>
                {
                    b.HasOne("DotNetAPI.Models.Expense.Expense", null)
                        .WithMany()
                        .HasForeignKey("ExpenseId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.Group.Group", null)
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "UserInCredit")
                        .WithMany()
                        .HasForeignKey("UserInCreditId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "UserInDebt")
                        .WithMany()
                        .HasForeignKey("UserInDebtId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("UserInCredit");

                    b.Navigation("UserInDebt");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.DebtAdjustment", b =>
                {
                    b.HasOne("DotNetAPI.Models.Group.Group", "Group")
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "UserInCredit")
                        .WithMany()
                        .HasForeignKey("UserInCreditId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "UserInDebt")
                        .WithMany()
                        .HasForeignKey("UserInDebtId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.Navigation("Group");

                    b.Navigation("UserInCredit");

                    b.Navigation("UserInDebt");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.DebtAdjustmentOriginalDebt", b =>
                {
                    b.HasOne("DotNetAPI.Models.Debt.DebtAdjustment", "DebtAdjustment")
                        .WithMany("OriginalDebts")
                        .HasForeignKey("DebtAdjustmentId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.Debt.Debt", "OriginalDebt")
                        .WithMany()
                        .HasForeignKey("OriginalDebtId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("DebtAdjustment");

                    b.Navigation("OriginalDebt");
                });

            modelBuilder.Entity("DotNetAPI.Models.Expense.Expense", b =>
                {
                    b.HasOne("DotNetAPI.Models.Category.Category", "Category")
                        .WithMany()
                        .HasForeignKey("CategoryId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.Group.Group", null)
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "User")
                        .WithMany()
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.Navigation("Category");

                    b.Navigation("User");
                });

            modelBuilder.Entity("DotNetAPI.Models.Payment.Payment", b =>
                {
                    b.HasOne("DotNetAPI.Models.Debt.DebtAdjustment", "DebtAdjustment")
                        .WithMany()
                        .HasForeignKey("DebtAdjustmentId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.Group.Group", "Group")
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "User")
                        .WithMany()
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Restrict)
                        .IsRequired();

                    b.Navigation("DebtAdjustment");

                    b.Navigation("Group");

                    b.Navigation("User");
                });

            modelBuilder.Entity("DotNetAPI.Models.UserInGroup.UserInGroup", b =>
                {
                    b.HasOne("DotNetAPI.Models.Group.Group", "Group")
                        .WithMany()
                        .HasForeignKey("GroupId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "User")
                        .WithMany()
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Group");

                    b.Navigation("User");
                });

            modelBuilder.Entity("DotNetAPI.Models.UserInvolvedExpense.UserInvolvedExpense", b =>
                {
                    b.HasOne("DotNetAPI.Models.Expense.Expense", "Expense")
                        .WithMany()
                        .HasForeignKey("ExpenseId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("DotNetAPI.Models.User.User", "User")
                        .WithMany()
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Expense");

                    b.Navigation("User");
                });

            modelBuilder.Entity("DotNetAPI.Models.Debt.DebtAdjustment", b =>
                {
                    b.Navigation("OriginalDebts");
                });
#pragma warning restore 612, 618
        }
    }
}
