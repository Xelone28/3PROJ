using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace workaround_ef.Migrations
{
    /// <inheritdoc />
    public partial class fk_test : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment");

            migrationBuilder.DropIndex(
                name: "IX_Payment_UserId",
                table: "Payment");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Category",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Balance",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "IsGroupAdmin",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "DebtId",
                table: "Expense");

            migrationBuilder.DropColumn(
                name: "TaxeId",
                table: "Expense");

            migrationBuilder.DropColumn(
                name: "CategoryId",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Amount",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Date",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Description",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Place",
                table: "Category");

            migrationBuilder.RenameColumn(
                name: "Type",
                table: "Expense",
                newName: "Place");

            migrationBuilder.RenameColumn(
                name: "TaxeValue",
                table: "Expense",
                newName: "CategoryId");

            migrationBuilder.RenameColumn(
                name: "UserId",
                table: "Category",
                newName: "Name");

            migrationBuilder.RenameColumn(
                name: "BillId",
                table: "Category",
                newName: "Id");

            migrationBuilder.AddColumn<int>(
                name: "Amount",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "Date",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "DebtId",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "Id",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "TaxeId",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "TaxeValue",
                table: "Payment",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "Type",
                table: "Payment",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AlterColumn<string>(
                name: "GroupDesc",
                table: "Group",
                type: "text",
                nullable: true,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.AddColumn<string>(
                name: "Description",
                table: "Expense",
                type: "text",
                nullable: true);

            migrationBuilder.AlterColumn<int>(
                name: "Id",
                table: "Category",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddPrimaryKey(
                name: "PK_Category",
                table: "Category",
                column: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_UserInGroup_GroupId",
                table: "UserInGroup",
                column: "GroupId");

            migrationBuilder.CreateIndex(
                name: "IX_UserInGroup_UserId",
                table: "UserInGroup",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_UserInGroup_Group_GroupId",
                table: "UserInGroup",
                column: "GroupId",
                principalTable: "Group",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_UserInGroup_User_UserId",
                table: "UserInGroup",
                column: "UserId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_UserInGroup_Group_GroupId",
                table: "UserInGroup");

            migrationBuilder.DropForeignKey(
                name: "FK_UserInGroup_User_UserId",
                table: "UserInGroup");

            migrationBuilder.DropIndex(
                name: "IX_UserInGroup_GroupId",
                table: "UserInGroup");

            migrationBuilder.DropIndex(
                name: "IX_UserInGroup_UserId",
                table: "UserInGroup");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Category",
                table: "Category");

            migrationBuilder.DropColumn(
                name: "Amount",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "Date",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "DebtId",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "Id",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "TaxeId",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "TaxeValue",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "Type",
                table: "Payment");

            migrationBuilder.DropColumn(
                name: "Description",
                table: "Expense");

            migrationBuilder.RenameColumn(
                name: "Place",
                table: "Expense",
                newName: "Type");

            migrationBuilder.RenameColumn(
                name: "CategoryId",
                table: "Expense",
                newName: "TaxeValue");

            migrationBuilder.RenameColumn(
                name: "Name",
                table: "Category",
                newName: "UserId");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "Category",
                newName: "BillId");

            migrationBuilder.AddColumn<float>(
                name: "Balance",
                table: "Payment",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.AddColumn<bool>(
                name: "IsGroupAdmin",
                table: "Payment",
                type: "boolean",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AlterColumn<string>(
                name: "GroupDesc",
                table: "Group",
                type: "text",
                nullable: false,
                defaultValue: "",
                oldClrType: typeof(string),
                oldType: "text",
                oldNullable: true);

            migrationBuilder.AddColumn<int>(
                name: "DebtId",
                table: "Expense",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "TaxeId",
                table: "Expense",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AlterColumn<int>(
                name: "BillId",
                table: "Category",
                type: "integer",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer")
                .OldAnnotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddColumn<int>(
                name: "CategoryId",
                table: "Category",
                type: "integer",
                nullable: false,
                defaultValue: 0)
                .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddColumn<float>(
                name: "Amount",
                table: "Category",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.AddColumn<string>(
                name: "Date",
                table: "Category",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "Description",
                table: "Category",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Place",
                table: "Category",
                type: "text",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_Category",
                table: "Category",
                column: "CategoryId");

            migrationBuilder.CreateIndex(
                name: "IX_Payment_UserId",
                table: "Payment",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_Payment_User_UserId",
                table: "Payment",
                column: "UserId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
