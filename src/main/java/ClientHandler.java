import Response.ResponseHandler;
import enums.Responses;
import Request.startegies.EchoHandler;
import Request.startegies.FileHandler;
import Request.RequestHelper;
import Request.startegies.UserAgentHandler;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread{
    Socket clientSocket;
    String directory;
    RequestHelper requestHelper;
    public ClientHandler(Socket clientSocket) { this.clientSocket = clientSocket; }

    private ResponseHandler responseHandler = new ResponseHandler();
    @Override
    public void run() {
        try {
            requestHelper = new RequestHelper();
            String response = responseHandler.getResponse();
//            Read from client
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String requestToString = requestHelper.getRequestToString(reader);
            String requestLine = requestToString.split("\n")[0];

            // save header
            String receivedPath = requestHelper.getRequestedPath(requestLine);


            boolean isAcceptedPath = isPathAccepted(receivedPath);
            if(!isAcceptedPath) {
                rejectConnection();
                reader.close();
                return;
            }

            if(receivedPath.contains("/echo")){
                response = responseHandler.getResponse(new EchoHandler().process(requestLine));
            }else if(receivedPath.contains("/user-agent")){
                response = responseHandler.getResponse(new UserAgentHandler().process(requestToString));
            }else if(receivedPath.contains("/files")){
                response = responseHandler.getResponse(new FileHandler(this.directory)
                        .process((requestToString)),"application/octet-stream");
            }
            System.out.println(response);
            clientSocket.getOutputStream().write(response.getBytes());
            reader.close();
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isPathAccepted(String path){
        return path.equals("/") || path.contains("/echo") || path.contains("/user-agent") || path.contains("/files");
    }
    private void rejectConnection() throws IOException {
        clientSocket.getOutputStream().write(Responses.NOT_FOUND.getMessage().getBytes());
        clientSocket.getOutputStream().flush();
        close();
    }

    public void setDirectory(String dir){
        this.directory = dir;
    }
    private void close(){
        try {
            this.clientSocket.close();
            this.directory=".";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}