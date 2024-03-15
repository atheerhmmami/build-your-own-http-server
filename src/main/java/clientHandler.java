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
            boolean isAcceptedPath = pathAccepted(reader.readLine());
            if(isAcceptedPath){
                clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                return;
            }
            clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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