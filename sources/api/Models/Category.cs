﻿namespace DotNetAPI.Model
{
    public class Category
    {
        public required int Id{ get; set; }
        public required int GroupId { get; set; }
        public required UserGroup UserGroup { get; set; }
        public required int Name { get; set; }
    }
}