package Request.startegies;

public class UserAgentHandler extends RequestHandler{

    private final String USER_AGENT_HANDLER = "User-Agent";
    @Override
    public String process(String request) {
        return requestHelper.extractHeader(request, USER_AGENT_HANDLER);
    }

}
