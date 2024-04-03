package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Responses {
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n"),
    SUCCESS("HTTP/1.1 200 OK"),
    CREATED("HTTP/1.1 201 Created\r\n\r\n");


    private final String message;
}
