package Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseObject {

    private String statusLine;
    private String contentType;
    private String contentLength;

    public String getResponseAsString( String body){
        return String.format("%s\r\nContent-Type: %s\r\nContent-Length: %s\r\n\r\n%s",
                this.getStatusLine(),
                this.getContentType(),
                this.getContentLength(),
                body);
    }
}
