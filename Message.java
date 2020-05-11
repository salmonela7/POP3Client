package KompiuteriuTinklai_2;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Message {
    private int id;

    private final Map<String, List<String>> headers;

    private final String body;

    public int getId() { return id; }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    protected Message(int id, Map<String, List<String>> headers, String body) {
        this.id = id;
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }
}
