using DotNetAPI.Model;
using DotNetAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DotNetAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class CartController : ControllerBase
    {
        private readonly ICartService _cartService;

        public CartController(ICartService cartService)
        {
            _cartService = cartService;
        }

        [HttpGet("{userId}")]
        public async Task<ActionResult<List<Cart>>> GetCartsByUserId(int userId)
        {
            try
            {
                var carts = await _cartService.GetCartsByUserId(userId);
                return Ok(carts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpDelete("{cartId}")]
        public async Task<IActionResult> DeleteCartById(int cartId)
        {
            try
            {
                await _cartService.DeleteCartById(cartId);
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpDelete("pay/{userId}")]
        public async Task<IActionResult> DeleteCartsByUserId(int userId)
        {
            try
            {
                await _cartService.DeleteCartsByUserId(userId);
                // If there was payments, should be implemted here :)
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Cart>> AddProductToCart(Cart cart)
        {
            try
            {
                await _cartService.AddProductToCart(cart);
                return CreatedAtAction(nameof(GetCartsByUserId), new { userId = cart.IdUser }, cart);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }
    }
}
