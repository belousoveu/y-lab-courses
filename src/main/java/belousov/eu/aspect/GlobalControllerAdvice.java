package belousov.eu.aspect;

import belousov.eu.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({UserNotFoundException.class,
            BudgetNotFoundException.class,
            CategoryNotFoundException.class,
            GoalNotFoundException.class,
            TransactionNotFoundException.class,
            PathNotFoundException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ValidationParametersException.class,
            JsonProcessingException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            RequiredArgumentException.class
    })
    public ResponseEntity<String> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistsException.class,
            IllegalStateException.class})
    public ResponseEntity<String> handleConflictException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler({InvalidPasswordException.class,
            UnAuthorizedAccessException.class})
    public ResponseEntity<String> handleUnauthorizedAccessException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class,
            UserWasBlockedException.class})
    public ResponseEntity<String> handleForbiddenException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
