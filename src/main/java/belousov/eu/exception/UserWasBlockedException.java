package belousov.eu.exception;

public class UserWasBlockedException extends RuntimeException {

  public UserWasBlockedException(String username) {
      super(String.format("Пользователь %s заблокирован", username));
    }
}
