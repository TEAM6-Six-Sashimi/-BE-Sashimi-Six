package com.sashimi.global.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class AwsS3Properties {

    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private S3 s3 = new S3();
    private CloudFront cloudFront = new CloudFront();

    @Getter
    @Setter
    public static class S3 {
        private String bucketImages;
        private String bucketVideos;
        private String bucketAttachments;
        private String bucketDocs;
    }

    @Getter
    @Setter
    public static class CloudFront {
        private String imagesDomain;
        private String videosDomain;
    }
}
