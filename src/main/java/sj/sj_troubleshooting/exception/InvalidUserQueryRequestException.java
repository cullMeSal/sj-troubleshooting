package sj.sj_troubleshooting.exception;

public class InvalidUserQueryRequestException extends RuntimeException {
  public InvalidUserQueryRequestException(String message) {
    super(message);
  }
}
