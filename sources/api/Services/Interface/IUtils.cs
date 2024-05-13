namespace DotNetAPI.Services.Interface
{
    public interface IUtils
    {
        Task UploadFileAsync(Stream fileStream, string key, string contentType);
        Task<List<string>> ListFiles(string prefix);
        Task DeleteFile(string prefix);
    }
}
