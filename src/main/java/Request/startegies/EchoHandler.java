package Request.startegies;

public class EchoHandler extends RequestHandler{
    @Override
    public String process(String line) {
        return requestHelper.getRequestedPathTail(line);
    }

}
