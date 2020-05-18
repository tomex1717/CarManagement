package lufa.alfaserwis.CarManagment.exceptionHandling;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String message;
    private long timeStamp;


    public ErrorResponse() {
    }
}
