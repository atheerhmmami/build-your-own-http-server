import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler extends Thread{
    Socket clientSocket;
    String directory;
    public ClientHandler(Socket clientSocket) { this.clientSocket = clientSocket; }
    @Override
    public void run() {
        try {
//            Read from client
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String req = reader.readLine();
            String response=getResponse("","text/plain");

            String receivedPath = getPath(req);
            boolean isAcceptedPath = isPathAccepted(receivedPath);
            if(!isAcceptedPath) {
                clientSocket.getOutputStream().write(notFoundResponse().getBytes());
                return;
            }

            if(receivedPath.contains("/echo")){
                response = getResponse(getPathTail(req),"text/plain");
            }else if(receivedPath.contains("/user-agent")){
                response = getResponse(extractHeader(reader, "User-Agent"),"text/plain");
            }else if(receivedPath.contains("/files")){
                String method = getAction(req);
                String fileName = getPathTail(req);
                if(method.equalsIgnoreCase("get")){
                    if(!isFileExist(this.directory, fileName)){
                        clientSocket.getOutputStream().write(notFoundResponse().getBytes());
                        clientSocket.getOutputStream().flush();
                        reader.close();
                        return;
                    }
                    String fileContent = readFile(Paths.get(this.directory, fileName));
                    response = getResponse(fileContent,"application/octet-stream");
                }else if(method.equalsIgnoreCase("post")){
                    String fileContent = readBody(reader);
                    System.out.println(fileContent);
                    createFile(fileName, fileContent);
                    response = createdResponse();
                }else{
                    System.out.println("No implementation available");
                }
            }
            System.out.println(response);
            clientSocket.getOutputStream().write(response.getBytes());
            reader.close();
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String notFoundResponse() throws IOException {
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
    private String createdResponse(){
        return "HTTP/1.1 201 Created\r\n\r\n";
    }
    private boolean isFileExist(String dir, String fileName){
        Path filePath = Paths.get(dir, fileName);
        return Files.exists(filePath);
    }
    private void createFile(String fileName, String fileContent){
        BufferedWriter bWriter =
                null;
        try {
            bWriter = new BufferedWriter(new FileWriter(this.directory + "/" + fileName));
            bWriter.write(fileContent);
            bWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String readFile(Path filePath){
        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            System.out.println("fileBytes length:"+fileBytes.length+" as str length:"+new String(fileBytes).length());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    private String getResponse(String body, String contentType){
        return "HTTP/1.1 200 OK\r\n".concat(String.format("Content-Type: %s\r\n",contentType))
                .concat(String.format("Content-Length: %d\r\n", body.length())).concat(String
                        .format("\r\n%s\r\n", body));
    }

    private String readBody(BufferedReader reader){
        String[] splitted;
        StringBuilder body = new StringBuilder();
        boolean isBodyPart = false;
        try {
            while (reader.ready()) {
                body.append((char)reader.read());
            }
             splitted = String.valueOf(body).split("\r\n\r\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return splitted[1];
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
        return path.equals("/") || path.contains("/echo") || path.contains("/user-agent") || path.contains("/files");
    }

    private String getAction(String requestLine){
        String pattern = "^(\\S+)\\s+(/\\S*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(requestLine);

        if(!matcher.matches()){
            System.out.println("INVALID REQUEST");
            return "invalid";
        }
        return matcher.group(1);
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