import java.io.*;
import java.util.regex.*;

public class HelloServer {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            String id = find(line, "\"id\"\\s*:\\s*(\\d+|\"[^\"]*\")");
            String method = find(line, "\"method\"\\s*:\\s*\"([^\"]*)\"");
            if (id == null || method == null) continue; // ignore notifications

            String result = switch (method) {
                case "initialize" -> "{\"protocolVersion\":\"2024-11-05\","
                    + "\"capabilities\":{\"tools\":{}},"
                    + "\"serverInfo\":{\"name\":\"hello\",\"version\":\"1.0.0\"}}";
                case "tools/list" -> "{\"tools\":[{\"name\":\"hello\","
                    + "\"description\":\"Greet someone by name\","
                    + "\"inputSchema\":{\"type\":\"object\","
                    + "\"properties\":{\"name\":{\"type\":\"string\"}},"
                    + "\"required\":[\"name\"]}}]}";
                case "tools/call" -> {
                    String name = find(line, "\"arguments\"\\s*:\\s*\\{\\s*\"name\"\\s*:\\s*\"([^\"]*)\"");
                    yield "{\"content\":[{\"type\":\"text\",\"text\":\"Hello, " + name + "!\"}]}";
                }
                default -> "{}";
            };

            System.out.println("{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"result\":" + result + "}");
            System.out.flush();
        }
    }

    private static String find(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : null;
    }
}
