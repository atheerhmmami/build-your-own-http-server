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
            String path = pathParser(reader.readLine());
            String response = "HTTP/1.1 200 OK\r\n".concat("Content-Type: text/plain\r\n")
                    .concat(String.format("Content-Length: %d \r\n", path.length())).concat(String
                            .format("\r\n %s \r\n", path));
            System.out.println(response);
             clientSocket.getOutputStream().write(response.getBytes());

//            boolean isAcceptedPath = pathAccepted(reader.readLine());
//            if(isAcceptedPath){
//                clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
//                return;
//            }
//            clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String pathParser(String requestLine){
        String pattern = "^(\\S+)\\s+(/(\\S+)).*";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(requestLine);
        if(!matcher.matches()){
            System.out.println("INVALID REQUEST");
            return "undefined";
        }
        String fullPath = matcher.group(2).substring(1);
        String[] pathParts = fullPath.split("/",2);
        System.out.println(pathParts[1]);
        return pathParts[1];
    }
    private boolean pathAccepted(String requestLine){
        String pattern = "^(\\S+)\\s+(/\\S*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(requestLine);

        if(!matcher.matches()){
            System.out.println("INVALID REQUEST");
            return false;
        }
        String path = matcher.group(2);
        if(!path.equals("/")){
            return false;
        }
        return true;
    }
}