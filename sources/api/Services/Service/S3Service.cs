using Amazon.S3;
using Amazon.S3.Model;
using DotNetAPI.Services.Interface;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace DotNetAPI.Services
{
    public class S3Service : IUtils
    {
        private readonly IAmazonS3 _s3Client;
        private readonly string _bucketName;
        private readonly ILogger<S3Service> _logger;
        private readonly IConfiguration _configuration;


        public S3Service(string accessKey, string secretKey, string bucketName, ILogger<S3Service> logger, IConfiguration configuration)
        {
            _configuration = configuration;
            var AwsSettings = _configuration.GetSection("AWSSettings");
            string serviceUrl = AwsSettings["ServiceURL"];

            AmazonS3Config config = new AmazonS3Config();
            config.ServiceURL = serviceUrl;

            _s3Client = new AmazonS3Client(
                    accessKey,
                    secretKey,
                    config
                    );

            _bucketName = bucketName;
            _logger = logger;
        }

        public async Task UploadFileAsync(Stream fileStream, string key, string contentType)
        {
            try
            {
                var putRequest = new PutObjectRequest
                {
                    BucketName = _bucketName,
                    Key = key,
                    InputStream = fileStream,
                    ContentType = contentType,
                    CannedACL = S3CannedACL.PublicRead
                };

                await _s3Client.PutObjectAsync(putRequest);
                _logger.LogInformation($"File uploaded to S3. Key: {key}, ContentType: {contentType}");
            }
            catch (Exception ex)
            {
                _logger.LogError($"Error uploading file to S3. Key: {key}, ContentType: {contentType}. Error: {ex.Message}");
                throw;
            }
        }
        public async Task<List<string>> ListFiles(string prefix)
        {
            List<string> filePaths = new List<string>();

            try
            {
                var request = new ListObjectsV2Request
                {
                    BucketName = _bucketName,
                    Prefix = prefix
                };

                ListObjectsV2Response response;
                do
                {
                    response = await _s3Client.ListObjectsV2Async(request);
                    foreach (var obj in response.S3Objects)
                    {
                        filePaths.Add(obj.Key);
                    }

                    request.ContinuationToken = response.NextContinuationToken;
                } while (response.IsTruncated);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error listing objects: " + ex.Message);
                throw;
            }

            return filePaths;
        }
        public async Task DeleteFile(string fileKey)
        {
            try
            {
                var deleteRequest = new DeleteObjectRequest
                {
                    BucketName = _bucketName,
                    Key = fileKey
                };

                await _s3Client.DeleteObjectAsync(deleteRequest);
            }
            catch (AmazonS3Exception s3Ex)
            {
                Console.WriteLine($"Error encountered on server when deleting the object. Exception: {s3Ex.Message}");
                throw;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Unknown encountered on server when deleting the object. Exception: {ex.Message}");
                throw;
            }
        }
    }
    
}
