package Request.startegies;

import Request.RequestHelper;

import java.io.FileNotFoundException;


public abstract class RequestHandler {
    RequestHelper requestHelper = new RequestHelper();
    abstract String process(String head) throws FileNotFoundException;

}
