package dsw.ms.usuarios.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Utility class to load environment variables from a .env file
 * into System properties before Spring Boot startup.
 */
public final class EnvLoader {

	private static final Logger log = LoggerFactory.getLogger(EnvLoader.class);
	private static final String DEFAULT_ENV_FILE = ".env";
	private static final String FALLBACK_ENV_FILE = "SoftCorp-MicroServiceUsuarios/.env";

	private EnvLoader() {
		// Prevent instantiation of utility class
	}

	/**
	 * Loads environment variables from the .env file if present.
	 */
	public static void load() {
		File envFile = findEnvFile();
		if (envFile == null) {
			log.info("No .env file found. Skipping environment variable loading.");
			return;
		}

		log.info("Loading environment variables from: {}", envFile.getAbsolutePath());
		try {
			List<String> lines = Files.readAllLines(envFile.toPath());
			for (String line : lines) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				int delimiterIndex = line.indexOf('=');
				if (delimiterIndex > 0) {
					String key = line.substring(0, delimiterIndex).trim();
					String value = line.substring(delimiterIndex + 1).trim();

					// Strip surrounding quotes if present
					if ((value.startsWith("\"") && value.endsWith("\"")) ||
						(value.startsWith("'") && value.endsWith("'"))) {
						value = value.substring(1, value.length() - 1);
					}

					// Set system property if not already defined in env or system properties
					if (System.getProperty(key) == null && System.getenv(key) == null) {
						System.setProperty(key, value);
						log.debug("Loaded property: {}", key);
					} else {
						log.debug("Property {} is already set in system/env, skipping.", key);
					}
				}
			}
			log.info("Environment variables loaded successfully.");
		} catch (IOException e) {
			log.error("Failed to read .env file from path: {}", envFile.getPath(), e);
		}
	}

	private static File findEnvFile() {
		File primary = new File(DEFAULT_ENV_FILE);
		if (primary.exists() && primary.isFile()) {
			return primary;
		}
		File fallback = new File(FALLBACK_ENV_FILE);
		if (fallback.exists() && fallback.isFile()) {
			return fallback;
		}
		return null;
	}
}
