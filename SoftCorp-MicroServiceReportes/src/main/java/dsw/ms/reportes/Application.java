package dsw.ms.reportes;

import dsw.ms.reportes.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(Application.class, args);
	}

}
