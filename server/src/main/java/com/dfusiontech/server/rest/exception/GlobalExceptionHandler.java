package com.dfusiontech.server.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ServerException genericServerExceptionException(BadRequestException exception) {
		log.warn(exception.getMessage(), exception);
		return exception;
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ServerException genericServerExceptionException(ConflictException exception) {
		log.warn(exception.getMessage(), exception);
		return exception;
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ServerException genericServerExceptionException(ForbiddenException exception) {
		String errorMessage = MessageFormat.format("User [{0}] tried to execute forbidden action. Message: [{1}]", exception.getUsername(), exception.getMessage());
		log.warn(errorMessage);
		return exception;
	}

	@ExceptionHandler(ItemNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ServerException genericServerExceptionException(ItemNotFoundException exception) {
		log.warn(exception.getMessage(), exception);
		return exception;
	}

	@ExceptionHandler(ServerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ServerException genericServerExceptionException(ServerException exception) {
		log.warn(exception.getMessage(), exception);
		return exception;
	}

	@ExceptionHandler(NotAuthenticatedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ServerException genericServerExceptionException(NotAuthenticatedException exception) {
		log.warn(exception.getMessage(), exception);
		return exception;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ServerException dataIntegrityViolationException(DataIntegrityViolationException exception) {
		InternalServerErrorException result = new InternalServerErrorException("Failed to proceed your request because of Data Integrity Violation Error. Please contact support.", 100500);
		log.warn(exception.getMessage(), exception);
		return result;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ServerException unknownException(Exception exception) {
		InternalServerErrorException result = new InternalServerErrorException("Something went wrong. Our engineers are working to localize the issue. Please try again later or contact support.", 417);
		log.warn(exception.getMessage(), exception);
		return result;
	}

}
