package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.servlet.ServletContext;

public final class EnvUtil {
    private static final Object LOCK = new Object();
    private static Properties dotEnv;

    private EnvUtil() {
    }

    public static String get(String key, ServletContext context) {
        String value = System.getenv(key);
        if (!isBlank(value)) {
            return value.trim();
        }

        value = System.getProperty(key);
        if (!isBlank(value)) {
            return value.trim();
        }

        Properties properties = loadDotEnv(context);
        value = properties.getProperty(key);
        return value == null ? "" : value.trim();
    }

    private static Properties loadDotEnv(ServletContext context) {
        synchronized (LOCK) {
            if (dotEnv != null) {
                return dotEnv;
            }

            dotEnv = new Properties();
            File envFile = findDotEnv(context);
            if (envFile == null || !envFile.isFile()) {
                return dotEnv;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(envFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseLine(line, dotEnv);
                }
            } catch (Exception ignored) {
                // Keep configuration optional; callers validate required keys.
            }
            return dotEnv;
        }
    }

    private static File findDotEnv(ServletContext context) {
        File fromUserDir = searchUp(new File(System.getProperty("user.dir")));
        if (fromUserDir != null) {
            return fromUserDir;
        }

        if (context != null) {
            String realPath = context.getRealPath("/");
            if (!isBlank(realPath)) {
                File webRoot = new File(realPath);
                File fromWebRoot = searchUp(webRoot);
                if (fromWebRoot != null) {
                    return fromWebRoot;
                }

                File projectRoot = resolveProjectRootFromBuildWeb(webRoot);
                if (projectRoot != null) {
                    File env = new File(projectRoot, ".env");
                    if (env.isFile()) {
                        return env;
                    }
                }
            }
        }
        return null;
    }

    private static File searchUp(File start) {
        File current = start;
        for (int i = 0; current != null && i < 8; i++) {
            File env = new File(current, ".env");
            if (env.isFile()) {
                return env;
            }
            current = current.getParentFile();
        }
        return null;
    }

    private static File resolveProjectRootFromBuildWeb(File webRoot) {
        String path = webRoot.getAbsolutePath();
        String marker = File.separator + "build" + File.separator + "web";
        int index = path.indexOf(marker);
        if (index < 0) {
            return null;
        }
        return new File(path.substring(0, index));
    }

    private static void parseLine(String line, Properties properties) {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        int equals = trimmed.indexOf('=');
        if (equals <= 0) {
            return;
        }
        String key = trimmed.substring(0, equals).trim().replace("\uFEFF", "");
        String value = trimmed.substring(equals + 1).trim();
        if ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }
        properties.setProperty(key, value);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
