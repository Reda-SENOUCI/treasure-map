package util;

import java.util.Objects;
import java.util.UUID;

public class TestUtil {
    private TestUtil() {
    }

    public static String generateID() {
        return UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }

    public static synchronized String getFilePath(String fileName) {
        return Objects.requireNonNull(TestUtil.class.getClassLoader().getResource(fileName)).getPath();
    }
}
