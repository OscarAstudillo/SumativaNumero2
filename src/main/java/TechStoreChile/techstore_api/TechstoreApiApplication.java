package TechStoreChile.techstore_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@SpringBootApplication
public class TechstoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechstoreApiApplication.class, args);
    }

    // Ponemos el Bean directamente aquí para que Spring lo encuentre sí o sí
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}