package tn.educanet.pfe.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

	@Bean
	public Cloudinary cloudinary(@Value("${app.cloudinary.cloud-name}") String cloudName,
			@Value("${app.cloudinary.api-key}") String apiKey, @Value("${app.cloudinary.api-secret}") String apiSecret) {
		Cloudinary cloudinary = new Cloudinary(Map.of("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
		cloudinary.config.secure = true;
		return cloudinary;
	}
}
