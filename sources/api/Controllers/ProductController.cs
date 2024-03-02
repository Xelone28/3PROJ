using DotNetAPI.Services;
using DotNetAPI.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ProductsController : ControllerBase
    {
        private readonly IProductService _productService;

        public ProductsController(IProductService productService)
        {
            _productService = productService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Product>>> GetProducts()
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if ("seller" != currentUser.Role)
                {
                    return Unauthorized("Invalid request. You do not have any right on products.");
                }

                var products = await _productService.GetAllProducts();
                return Ok(products);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Product>> GetProduct(int id)
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if ("seller" != currentUser.Role)
                {
                    return Unauthorized("Invalid request. You do not have any right on products.");
                }

                var product = await _productService.GetProductById(id);

                if (product == null)
                {
                    return NotFound($"Product with ID {id} not found.");
                }

                return Ok(product);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Product>> PostProduct(
            [FromForm] string name,
            [FromForm] float price,
            [FromForm] bool available,
            [FromForm] IFormFile image)
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if ("seller" != currentUser.Role)
                {
                    return Unauthorized("Invalid request. You do not have any right on products.");
                }

                var product = new Product
                {
                    Name = name,
                    Price = price,
                    Available = available,
                    UserId = currentUser.Id,

                    // Convert the image file to bytes
                    Image = await GetFileBytes(image),

                    // Set the Added_time property to the current date and time
                    Added_time = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                };

                var createdProduct = await _productService.AddProduct(product);
                return CreatedAtAction(nameof(GetProduct), new { id = createdProduct.Id }, createdProduct);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        private async Task<byte[]> GetFileBytes(IFormFile file)
        {
            using (var memoryStream = new MemoryStream())
            {
                await file.CopyToAsync(memoryStream);
                return memoryStream.ToArray();
            }
        }


        [HttpPut("{id}")]
        public async Task<IActionResult> PutProduct(int id, Product product)
        {
            try
            {
                var currentUser = (User)HttpContext.Items["User"];

                if (currentUser.Id != product.UserId || currentUser.Role != "seller")
                {
                    return Unauthorized("Invalid request. You do not have any right on this product.");
                }

                var updatedProduct = await _productService.UpdateProduct(product);

                if (updatedProduct == null)
                {
                    return NotFound($"Product with ID {id} not found.");
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProduct(int id)
        {
            try
            {
                var product = await _productService.GetProductById(id);

                var currentUser = (User)HttpContext.Items["User"];

                if (product == null)
                {
                    return NotFound($"Product with ID {id} not found.");
                }

                if (currentUser.Id != product.UserId || currentUser.Role != "seller")
                {
                    return Unauthorized("Invalid request. You do not have any right on this product.");
                }

                await _productService.DeleteProduct(id);
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Internal Server Error: {ex.Message}");
            }
        }
    }
}
