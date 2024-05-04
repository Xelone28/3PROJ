using DotNetAPI.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace DotNetAPI.Helpers;

public class HttpException : Exception
{
    public int StatusCode { get; }

    public HttpException(int statusCode, string message) : base(message)
    {
        StatusCode = statusCode;
    }
}
