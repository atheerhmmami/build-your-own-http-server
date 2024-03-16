import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clientHandler extends Thread{
    Socket clientSocket;
    public clientHandler(Socket clientSocket) { this.clientSocket = clientSocket; }
    @Override
    public void run() {
        try {
//            Read from client
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String req = reader.readLine();
            String response=getResponse("");

            String receivedPath = getPath(req);
            boolean isAcceptedPath = isPathAccepted(receivedPath);
            if(!isAcceptedPath) {
                clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                return;
            }

            if(receivedPath.contains("/echo")){
                response = getResponse(getPathTail(req));
            }else if(receivedPath.contains("/user-agent")){
                response = getResponse(extractHeader(reader, "User-Agent"));
            }
            System.out.println(response);
            clientSocket.getOutputStream().write(response.getBytes());
            reader.close();
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void close(){
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getResponse(String body){
        return "HTTP/1.1 200 OK\r\n".concat("Content-Type: text/plain\r\n")
                .concat(String.format("Content-Length: %d\r\n", body.length())).concat(String
                        .format("\r\n%s\r\n", body));
    }

    private String extractHeader(BufferedReader reader, String target){
        String header="";
        String targetValue="";
        try {
            String line = reader.readLine();
            while(line != null){
                if(line.contains(target)){
                    header = line;
                    break;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int startIndex = header.indexOf(target);
        if (startIndex != -1) {
            startIndex += target.length() + 2; // Skip "User-Agent:" and the following space and colon
             targetValue = header.substring(startIndex);
            System.out.println("User-Agent: " + targetValue);
        }
        return targetValue;
    }
    private String getPathTail(String requestLine){
        String pattern = "^(\\S+)\\s+(/(\\S+)).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(requestLine);
        if(!matcher.matches()){
            System.out.println("INVALID REQUEST");
            return "";
        }
        String fullPath = matcher.group(2).substring(1);
        String[] pathParts = fullPath.split("/",2);
        System.out.println(pathParts[1]);
        return pathParts[1];
    }
    private boolean isPathAccepted(String path){
        return path.equals("/") || path.contains("/echo") || path.contains("/user-agent");
    }

    private String getPath(String requestLine){
        String pattern = "^(\\S+)\\s+(/\\S*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(requestLine);

        if(!matcher.matches()){
            System.out.println("INVALID REQUEST");
            return "invalid";
        }
        return matcher.group(2);
    }
}