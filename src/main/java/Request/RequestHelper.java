package Request;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestHelper {

    public String getRequestMethod(String requestLine){
        String[] parts = requestLine.split("\\s");
        return parts[0];
    }
    public String getRequestedPath(String requestLine){
        String[] parts = requestLine.split("\\s");
        return parts[1];
    }

    public String getBody(String reader) {
        String pattern = "(?<=\\r\\n\\r\\n)(\\n?.*\\s*)*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(reader);
        String body ="";
        while(matcher.find()){
            body = matcher.group();
        }
        return body;
    }
    public String getRequestedPathTail(String requestLine){
        String[] parts = requestLine.split("\\s");
        String[] pathParts= parts[1].substring(1).split("\\/",2);
        return pathParts[1];
    }
    public String extractHeader(String body, String target) {
        String[] lines = body.split("\n"); // Split the body text into lines
        for (String line : lines) {
            if (line.startsWith(target)) {
                String targetValue = line.substring(target.length()+1).trim();
                return targetValue;
            }
        }
        return "";
    }
    public String getRequestToString(BufferedReader reader) {
        return reader.lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String getHeaderValue(String line, String target){
        String pattern = target.concat(":(\\s+\\w.*)");
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);
        String value = "";
        while(matcher.find()){
            value = matcher.group(1);
        }
        return value;
    }
    private Map<String, String> getHeadersMap(String request){
        String pattern = "(\\w.*):(\\s+\\w.*)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(request);
        Map<String, String> headers = new HashMap<>();
        while(matcher.find()){
            headers.put(matcher.group(1), matcher.group(2));
        }
        return headers;
    }

}
