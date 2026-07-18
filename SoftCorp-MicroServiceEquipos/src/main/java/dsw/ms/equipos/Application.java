package dsw.ms.equipos;

import dsw.ms.equipos.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(Application.class, args);
	}

}
