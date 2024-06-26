﻿using Microsoft.AspNetCore.Http;

namespace DotNetAPI.Models.User
{
    public class UserUpdateDTO
    {
        public int Id { get; set; }
        public string? Username { get; set; }
        public string? Email { get; set; }
        public string? Password { get; set; }
        public string? Rib { get; set; }
        public string? PaypalUsername { get; set; }
        public IFormFile? Image { get; set; }
    }
}

