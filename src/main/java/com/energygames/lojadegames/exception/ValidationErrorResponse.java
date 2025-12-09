package com.energygames.lojadegames.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {
	
	private int status;
	private String error;
	private String message;
	private Map<String, String> errors;
	private LocalDateTime timestamp;

	public ValidationErrorResponse(int status, String error, String message, Map<String, String> errors) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.errors = errors;
		this.timestamp = LocalDateTime.now();
	}

	// Getters e Setters
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
