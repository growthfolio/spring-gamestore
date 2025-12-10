package com.energygames.lojadegames.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request) {
		
		log.warn("Recurso não encontrado: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			"Not Found",
			ex.getMessage(),
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(
			BusinessException ex,
			HttpServletRequest request) {
		
		log.warn("Erro de negócio: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Business Error",
			ex.getMessage(),
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(
			DuplicateResourceException ex,
			HttpServletRequest request) {
		
		log.warn("Recurso duplicado: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.CONFLICT.value(),
			"Conflict",
			ex.getMessage(),
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(
			UnauthorizedException ex,
			HttpServletRequest request) {
		
		log.warn("Acesso não autorizado: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),
			"Unauthorized",
			ex.getMessage(),
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(
			AccessDeniedException ex,
			HttpServletRequest request) {
		
		log.warn("Acesso negado: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.FORBIDDEN.value(),
			"Forbidden",
			"Você não tem permissão para acessar este recurso",
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		
		log.warn("Erro de validação: {}", errors);
		
		ValidationErrorResponse error = new ValidationErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Validation Error",
			"Erro de validação dos campos",
			errors
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex,
			HttpServletRequest request) {
		
		log.error("Erro inesperado", ex);
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"Internal Server Error",
			"Ocorreu um erro inesperado. Tente novamente mais tarde.",
			request.getRequestURI()
		);
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
