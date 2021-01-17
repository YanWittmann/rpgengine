import java.util.UUID;

public class UID {
    public static String generateUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static void printUID() {
        System.out.println(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
    }
}