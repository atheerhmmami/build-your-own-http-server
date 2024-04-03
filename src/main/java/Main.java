import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) {
     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
       serverSocket = new ServerSocket(4222);
       serverSocket.setReuseAddress(true);
         while(true){
         clientSocket = serverSocket.accept(); // Wait for connection from client.
         if(clientSocket.isConnected()){
             ClientHandler clientHandler = new ClientHandler(clientSocket);
             clientHandler.start();
             clientHandler.setDirectory(getDirectory(Arrays.asList(args)));
             System.out.println("accepted new connection");
         }
    }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static String getDirectory(List<String> arguments){
      String directory = ".";
      if (arguments.contains("--directory")) {
          directory = arguments.get(arguments.indexOf("--directory") + 1);
      }
      return directory;
  }
}