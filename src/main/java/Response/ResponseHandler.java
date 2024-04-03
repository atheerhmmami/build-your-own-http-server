package Response;

import enums.Responses;

public class ResponseHandler {

    private final String DEFAULT_CONTENT_TYPE = "text/plain";
    public String getResponse() {
        return getResponse("", DEFAULT_CONTENT_TYPE);
    }
    public String getResponse(String body) {
        return getResponse(body, DEFAULT_CONTENT_TYPE);
    }

    public String getResponse(String body, String contentType){
        ResponseObject responseObject = ResponseObject.builder()
                .statusLine(Responses.SUCCESS.getMessage())
                .contentType(contentType)
                .contentLength(String.valueOf(body.length()))
                .build();
        return responseObject.getResponseAsString(body);
    }
}
