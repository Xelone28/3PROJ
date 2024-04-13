using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace workaround_ef.Migrations
{
    /// <inheritdoc />
    public partial class getRidOfBalance : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_UserInGroup_UserId",
                table: "UserInGroup");

            migrationBuilder.DropColumn(
                name: "Balance",
                table: "UserInGroup");

            migrationBuilder.AddPrimaryKey(
                name: "PK_UserInGroup",
                table: "UserInGroup",
                columns: new[] { "UserId", "GroupId" });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_UserInGroup",
                table: "UserInGroup");

            migrationBuilder.AddColumn<float>(
                name: "Balance",
                table: "UserInGroup",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.CreateIndex(
                name: "IX_UserInGroup_UserId",
                table: "UserInGroup",
                column: "UserId");
        }
    }
}
