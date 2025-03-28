package belousov.eu.config;

import belousov.eu.exception.ConfigFileReadingException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ConfigLoader {

    private static final String DEFAULT_FILENAME = "application.yml";
    private static final String DEMO_FILENAME = "application-demo.yml";
    private static final String TEST_FILENAME = "application-test.yml";
    private final Map<String, Object> config;
    private final boolean demoMode;


    public ConfigLoader() {
        this("production");
    }

    public ConfigLoader(String profile) {
        Dotenv dotenv = Dotenv.load();
        String mode = dotenv.get("PMT_DEMO_MODE");
        demoMode = mode != null && mode.equalsIgnoreCase("on");

        String filePath = DEFAULT_FILENAME;
        if (profile.equals("test")) {
            filePath = TEST_FILENAME;
        } else if (demoMode && profile.equals("production")) {
            filePath = DEMO_FILENAME;
        }

        Yaml yaml = new Yaml();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new ConfigFileReadingException(filePath, new IllegalArgumentException("Файл не найден в classpath"));
            }


            Map<String, Object> objectMap = yaml.load(inputStream);
            config = flatten("", objectMap);
            replacePlaceholders(config, dotenv);
            config.put("profile", profile);
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
