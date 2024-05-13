using Amazon.S3;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging; // Add this namespace

namespace DotNetAPI.Services
{
    public class Utils
    {
        private static Utils _instance;
        private readonly S3Service _s3Service;
        private readonly IConfiguration _configuration;

        private Utils(IConfiguration configuration, ILogger<S3Service> logger)
        {
            _configuration = configuration;
            var awsSettings = _configuration.GetSection("AWSSettings");
            var accessKey = awsSettings["AccessKey"];
            var secretKey = awsSettings["SecretKey"];
            var bucketName = awsSettings["BucketName"];

            _s3Service = new S3Service(accessKey, secretKey, bucketName, logger, configuration);
        }

        public static Utils GetInstance(IConfiguration configuration, ILogger<S3Service> logger)
        {
            _instance ??= new Utils(configuration, logger);
            return _instance;
        }

        public S3Service S3ServiceInstance => _s3Service;
    }
}
