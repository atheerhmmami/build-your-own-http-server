package Request.startegies;


import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler extends RequestHandler{
    private final String directory;
    public FileHandler(String directory) {
        this.directory = directory;
    }

    @Override
    public String process(String requestString) throws FileNotFoundException {
        String requestLine = requestString.split("\n")[0];
        String method = requestHelper.getRequestMethod(requestLine);
        String fileName = requestHelper.getRequestedPathTail(requestLine);
        if(method.equalsIgnoreCase("get")) {
            return getFileContent(fileName);
        }else if(method.equalsIgnoreCase("post")) {
            String fileContent = requestHelper.getBody(requestString);
            createFile(fileName, fileContent);
            return "";
        }
       throw  new NotImplementedException();
    }



    private String getFileContent(String fileName) throws FileNotFoundException {
        if(!isFileExist(this.directory, fileName)){
           throw new FileNotFoundException();
        }
        return readFile(Paths.get(this.directory, fileName));
    }
    private boolean isFileExist(String dir, String fileName){
        Path filePath = Paths.get(dir, fileName);
        return Files.exists(filePath);
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
    private void createFile(String fileName, String fileContent){
        BufferedWriter bWriter = null;
        try {
            bWriter = new BufferedWriter(new FileWriter(this.directory + "/" + fileName));
            bWriter.write(fileContent);
            bWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
