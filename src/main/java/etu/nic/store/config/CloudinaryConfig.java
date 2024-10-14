package etu.nic.store.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryConfig.class);
    @Bean
    public Cloudinary cloudinary() {
        String cloudName = "dwzjwh6tb";
        String apiKey = "429168221755551";
        String apiSecret = "zRvcEGNZ5ZgnEFDf05_7DL4pcSo";

        logger.info("Cloudinary Configuration - cloudName: {}, apiKey: {}", cloudName, apiKey);

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
