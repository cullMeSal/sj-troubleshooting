package sj.sj_troubleshooting.exception;

public class EmailUnavailableException extends RuntimeException{
    public EmailUnavailableException(String message){
        super(message);
    }
}
