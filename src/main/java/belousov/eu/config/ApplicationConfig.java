package belousov.eu.config;

import belousov.eu.exception.ConfigFileReadingException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ApplicationConfig {

    private static final String DEFAULT_FILENAME = "src/main/resources/application.yml";
    private static final String DEMO_FILENAME = "src/main/resources/application-demo.yml";
    private final Map<String, Object> config;
    private final boolean demoMode;


    public ApplicationConfig() {
        Dotenv dotenv = Dotenv.load();
        String mode = dotenv.get("PMT_DEMO_MODE");
        demoMode = mode != null && mode.equalsIgnoreCase("on");

        String filePath = demoMode ? DEMO_FILENAME : DEFAULT_FILENAME;

        Yaml yaml = new Yaml();


        try (FileInputStream fis = new FileInputStream(filePath)) {
            Map<String, Object> objectMap = yaml.load(fis);
            config = flatten("", objectMap);
            replacePlaceholders(config, dotenv);
        } catch (IOException e) {
            throw new ConfigFileReadingException(filePath, e);
        }
    }

    private void replacePlaceholders(Map<String, Object> map, Dotenv dotenv) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String value && value.startsWith("${") && value.endsWith("}")) {
                String envVar = value.substring(2, value.length() - 1);
                String envValue = dotenv.get(envVar);
                if (envValue != null) {
                    map.put(entry.getKey(), envValue);
                }
            }
        }
    }

    private static Map<String, Object> flatten(String prefix, Object value) {
        Map<String, Object> result = new HashMap<>();
        if (value instanceof Map<?, ?> map) {
            map.forEach((k, v) -> result.putAll(flatten(Objects.equals(prefix, "") ? k.toString() : prefix + "." + k, v)));
        } else {
            result.put(prefix, value.toString());
        }
        return result;
    }
}
