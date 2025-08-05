package sj.sj_troubleshooting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailUnavailableException.class)
    public ResponseEntity<?> hanldeEmailUnavailable(EmailUnavailableException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<?> handleInvalidEmailFormat(InvalidEmailFormatException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @ExceptionHandler(DeniedUserInfoRequestException.class)
    public ResponseEntity<?> handleDeniedUserInfoRequest(DeniedUserInfoRequestException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    @ExceptionHandler(NonPositiveInputException.class)
    public ResponseEntity<?> handleNonPositiveInput(NonPositiveInputException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(UserQueryOutOfBoundException.class)
    public ResponseEntity<?> handleUserQueryOutOfBound(UserQueryOutOfBoundException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(InvalidUserQueryRequestException.class)
    public ResponseEntity<?> handleInvalidUserQueryRequest(InvalidUserQueryRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
