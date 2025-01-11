package com.github.nramc.dev.journey.api.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryProperties;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(CloudinaryProperties.class)
public class CloudinaryConfig {

    @Bean
    Cloudinary cloudinary(CloudinaryProperties properties) {
        Map<String, String> map = new HashMap<>();
        map.put("cloud_name", properties.cloudName());
        map.put("api_key", properties.apiKey());
        map.put("api_secret", properties.apiSecret());
        map.putAll(properties.additionalProperties());

        return new Cloudinary(map);
    }

    @Bean
    public CloudinaryGateway cloudinaryService(Cloudinary cloudinary, CloudinaryProperties cloudinaryProperties) {
        return new CloudinaryGateway(cloudinary, cloudinaryProperties);
    }

}
