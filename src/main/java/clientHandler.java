import java.io.IOException;
import java.net.Socket;
public class clientHandler extends Thread{
    Socket clientSocket;
    public clientHandler(Socket clientSocket) { this.clientSocket = clientSocket; }
    @Override
    public void run() {
        try {
            String OK = "HTTP/1.1 200 OK\r\n\r\n";
            clientSocket.getOutputStream().write(OK.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}