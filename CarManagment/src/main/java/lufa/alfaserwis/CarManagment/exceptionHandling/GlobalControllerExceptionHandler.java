package lufa.alfaserwis.CarManagment.exceptionHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {




    @ExceptionHandler(CarNotFoundException.class)
    public String handleException(CarNotFoundException ex, Model model) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimeStamp(System.currentTimeMillis());
        model.addAttribute("error", errorResponse);

        log.error("["+ errorResponse.getMessage() + "],[" + errorResponse.getStatus()
                + "],[" + errorResponse.getTimeStamp()+"]");
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleException(AccessDeniedException ex, Model model){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setTimeStamp(System.currentTimeMillis());
        model.addAttribute("error", errorResponse);
        return "error";
    }
}
