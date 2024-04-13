using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace workaround_ef.Migrations
{
    /// <inheritdoc />
    public partial class AddUniqueEmail : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Category_Group_UserGroupId",
                table: "Category");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_Expense_ExpenseId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_Group_UserGroupId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_User_UserInCreditId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_User_UserInDebtId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_Group_UserGroupId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_Taxe_TaxeId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_ExpenseId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_UserGroupId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_UserInCreditId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_UserInDebtId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_Category_UserGroupId",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "ExpenseId",
                table: "DebtInGroup");

            migrationBuilder.DropColumn(
                name: "UserGroupId",
                table: "DebtInGroup");

            migrationBuilder.DropColumn(
                name: "UserInCreditId",
                table: "DebtInGroup");

            migrationBuilder.DropColumn(
                name: "UserInDebtId",
                table: "DebtInGroup");

            migrationBuilder.DropColumn(
                name: "UserGroupId",
                table: "Category");

            migrationBuilder.RenameColumn(
                name: "UserGroupId",
                table: "Payment",
                newName: "UserId1");

            migrationBuilder.RenameIndex(
                name: "IX_Payment_UserGroupId",
                table: "Payment",
                newName: "IX_Payment_UserId1");

            migrationBuilder.AlterColumn<string>(
                name: "Email",
                table: "User",
                type: "character varying(256)",
                maxLength: 256,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.AlterColumn<int>(
                name: "Id",
                table: "Payment",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddColumn<int>(
                name: "DebtId",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AlterColumn<int>(
                name: "Id",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddPrimaryKey(
                name: "PK_Payment",
                table: "Payment",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_DebtInGroup",
                table: "DebtInGroup",
                column: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_User_Email",
                table: "User",
                column: "Email",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_Payment_DebtId",
                table: "Payment",
                column: "DebtId");

            migrationBuilder.CreateIndex(
                name: "IX_Payment_GroupId",
                table: "Payment",
                column: "GroupId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_BillId",
                table: "DebtInGroup",
                column: "BillId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_GroupId",
                table: "DebtInGroup",
                column: "GroupId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_UserIdInCredit",
                table: "DebtInGroup",
                column: "UserIdInCredit");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_UserIdInDebt",
                table: "DebtInGroup",
                column: "UserIdInDebt");

            migrationBuilder.CreateIndex(
                name: "IX_Category_GroupId",
                table: "Category",
                column: "GroupId");

            migrationBuilder.AddForeignKey(
                name: "FK_Category_Group_GroupId",
                table: "Category",
                column: "GroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_Expense_BillId",
                table: "DebtInGroup",
                column: "BillId",
                principalTable: "Expense",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_Group_GroupId",
                table: "DebtInGroup",
                column: "GroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_User_UserIdInCredit",
                table: "DebtInGroup",
                column: "UserIdInCredit",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_User_UserIdInDebt",
                table: "DebtInGroup",
                column: "UserIdInDebt",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_DebtInGroup_DebtId",
                table: "Payment",
                column: "DebtId",
                principalTable: "DebtInGroup",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_Group_GroupId",
                table: "Payment",
                column: "GroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_Taxe_TaxeId",
                table: "Payment",
                column: "TaxeId",
                principalTable: "Taxe",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment",
                column: "UserId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_User_UserId1",
                table: "Payment",
                column: "UserId1",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Category_Group_GroupId",
                table: "Category");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_Expense_BillId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_Group_GroupId",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_User_UserIdInCredit",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_DebtInGroup_User_UserIdInDebt",
                table: "DebtInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_DebtInGroup_DebtId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_Group_GroupId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_Taxe_TaxeId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment");

            migrationBuilder.DropForeignKey(
                name: "FK_Payment_User_UserId1",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_User_Email",
                table: "User");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Payment",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Payment_DebtId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Payment_GroupId",
                table: "Payment");

            migrationBuilder.DropPrimaryKey(
                name: "PK_DebtInGroup",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_BillId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_GroupId",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_UserIdInCredit",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_DebtInGroup_UserIdInDebt",
                table: "DebtInGroup");

            migrationBuilder.DropIndex(
                name: "IX_Category_GroupId",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "DebtId",
                table: "Payment");

            migrationBuilder.RenameColumn(
                name: "UserId1",
                table: "Payment",
                newName: "UserGroupId");

            migrationBuilder.RenameIndex(
                name: "IX_Payment_UserId1",
                table: "Payment",
                newName: "IX_Payment_UserGroupId");

            migrationBuilder.AlterColumn<string>(
                name: "Email",
                table: "User",
                type: "text",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "character varying(256)",
                oldMaxLength: 256);

            migrationBuilder.AlterColumn<int>(
                name: "Id",
                table: "Payment",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .OldAnnotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AlterColumn<int>(
                name: "Id",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .OldAnnotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddColumn<int>(
                name: "ExpenseId",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "UserGroupId",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "UserInCreditId",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "UserInDebtId",
                table: "DebtInGroup",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "UserGroupId",
                table: "Category",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_ExpenseId",
                table: "DebtInGroup",
                column: "ExpenseId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_UserGroupId",
                table: "DebtInGroup",
                column: "UserGroupId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_UserInCreditId",
                table: "DebtInGroup",
                column: "UserInCreditId");

            migrationBuilder.CreateIndex(
                name: "IX_DebtInGroup_UserInDebtId",
                table: "DebtInGroup",
                column: "UserInDebtId");

            migrationBuilder.CreateIndex(
                name: "IX_Category_UserGroupId",
                table: "Category",
                column: "UserGroupId");

            migrationBuilder.AddForeignKey(
                name: "FK_Category_Group_UserGroupId",
                table: "Category",
                column: "UserGroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_Expense_ExpenseId",
                table: "DebtInGroup",
                column: "ExpenseId",
                principalTable: "Expense",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_Group_UserGroupId",
                table: "DebtInGroup",
                column: "UserGroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_User_UserInCreditId",
                table: "DebtInGroup",
                column: "UserInCreditId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_DebtInGroup_User_UserInDebtId",
                table: "DebtInGroup",
                column: "UserInDebtId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_Group_UserGroupId",
                table: "Payment",
                column: "UserGroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_Taxe_TaxeId",
                table: "Payment",
                column: "TaxeId",
                principalTable: "Taxe",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment",
                column: "UserId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
