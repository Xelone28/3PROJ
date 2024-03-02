using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace workaround_ef.Migrations
{
    /// <inheritdoc />
    public partial class db : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "DebtId",
                table: "Payment",
                newName: "UserGroupId");

            migrationBuilder.AddColumn<int>(
                name: "UserGroupId",
                table: "Expense",
                type: "integer",
                nullable: false,
                defaultValue: 0);

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
                name: "IX_Payment_TaxeId",
                table: "Payment",
                column: "TaxeId");

            migrationBuilder.CreateIndex(
                name: "IX_Payment_UserGroupId",
                table: "Payment",
                column: "UserGroupId");

            migrationBuilder.CreateIndex(
                name: "IX_Payment_UserId",
                table: "Payment",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Expense_CategoryId",
                table: "Expense",
                column: "CategoryId");

            migrationBuilder.CreateIndex(
                name: "IX_Expense_UserGroupId",
                table: "Expense",
                column: "UserGroupId");

            migrationBuilder.CreateIndex(
                name: "IX_Expense_UserId",
                table: "Expense",
                column: "UserId");

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
                name: "FK_Expense_Category_CategoryId",
                table: "Expense",
                column: "CategoryId",
                principalTable: "Category",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Expense_Group_UserGroupId",
                table: "Expense",
                column: "UserGroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Expense_User_UserId",
                table: "Expense",
                column: "UserId",
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

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
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
                name: "FK_Expense_Category_CategoryId",
                table: "Expense");

            migrationBuilder.DropForeignKey(
                name: "FK_Expense_Group_UserGroupId",
                table: "Expense");

            migrationBuilder.DropForeignKey(
                name: "FK_Expense_User_UserId",
                table: "Expense");

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
                name: "IX_Payment_TaxeId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Payment_UserGroupId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Payment_UserId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Expense_CategoryId",
                table: "Expense");

            migrationBuilder.DropIndex(
                name: "IX_Expense_UserGroupId",
                table: "Expense");

            migrationBuilder.DropIndex(
                name: "IX_Expense_UserId",
                table: "Expense");

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
                name: "UserGroupId",
                table: "Expense");

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
                newName: "DebtId");
        }
    }
}
