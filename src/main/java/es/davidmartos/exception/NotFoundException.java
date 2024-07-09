package es.davidmartos.exception;

import javax.management.OperationsException;

public class NotFoundException extends OperationsException {

  /* Serial version */
  private static final long serialVersionUID = 6511584241791106984L;

  /** Default constructor. */
  public NotFoundException() {
    super();
  }

  /**
   * Constructor that allows a specific error message to be specified.
   *
   * @param message detail message.
   */
  public NotFoundException(String message) {
    super(message);
  }
}
