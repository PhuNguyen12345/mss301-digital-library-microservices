package fu.edu.mss301.digilib.identity.api.controller;

import fu.edu.mss301.digilib.identity.application.DuplicateResourceException;
import fu.edu.mss301.digilib.identity.application.ResourceNotFoundException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IdentityExceptionHandler {

	@ExceptionHandler(DuplicateResourceException.class)
	ResponseEntity<ApiError> duplicate(DuplicateResourceException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(Instant.now(), ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(Instant.now(), ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(Instant.now(), ex.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<ApiError> conflict(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(Instant.now(), ex.getMessage()));
	}

	record ApiError(Instant timestamp, String message) {
	}
}
