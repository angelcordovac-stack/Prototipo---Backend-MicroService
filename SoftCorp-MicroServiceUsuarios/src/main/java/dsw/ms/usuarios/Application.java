package dsw.ms.usuarios;

import dsw.ms.usuarios.config.EnvLoader;
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
