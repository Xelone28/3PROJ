using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace workaround_ef.Migrations
{
    /// <inheritdoc />
    public partial class makeMig : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "IsActive",
                table: "UserInGroup",
                type: "boolean",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsActive",
                table: "UserInGroup");
        }
    }
}
