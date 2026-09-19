package io.github.froideexplica.exceptions;

import java.util.ArrayList;
import java.util.List;

import io.github.froideexplica.service.exceptions.ProductNotFoundException;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

public class GlobalHandlerExceptions {

	@Provider
	public static class ProductNotFoundExceptionMapper implements ExceptionMapper<ProductNotFoundException> {

		@Context
		UriInfo uriInfo;

		@Override
		public Response toResponse(ProductNotFoundException ex) {
			List<String> messages = new ArrayList<>();
			messages.add(ex.getMessage());

			StandardError response = new StandardError(
					Response.Status.NOT_FOUND.getStatusCode(),
					"Not Found",
					messages,
					getPath()
			);

			return Response.status(Response.Status.NOT_FOUND)
					.entity(response)
					.build();
		}

		private String getPath() {
			return uriInfo != null ? uriInfo.getPath() : "";
		}
	}

	@Provider
	public static class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

		@Context
		UriInfo uriInfo;

		@Override
		public Response toResponse(ConstraintViolationException ex) {
			List<String> messages = ex.getConstraintViolations()
					.stream()
					.map(violation -> violation.getPropertyPath() + " : " + violation.getMessage())
					.toList();

			StandardError response = new StandardError(
					Response.Status.BAD_REQUEST.getStatusCode(),
					"Bad Request",
					messages,
					getPath()
			);

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(response)
					.build();
		}

		private String getPath() {
			return uriInfo != null ? uriInfo.getPath() : "";
		}
	}

	@Provider
	public static class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

		@Context
		UriInfo uriInfo;

		@Override
		public Response toResponse(PersistenceException ex) {
			List<String> messages = new ArrayList<>();

			Throwable rootCause = getRootCause(ex);
			messages.add(rootCause != null ? rootCause.getMessage() : ex.getMessage());

			StandardError response = new StandardError(
					Response.Status.CONFLICT.getStatusCode(),
					"Data Integrity Violation",
					messages,
					getPath()
			);

			return Response.status(Response.Status.CONFLICT)
					.entity(response)
					.build();
		}

		private Throwable getRootCause(Throwable throwable) {
			Throwable cause = throwable;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			return cause;
		}

		private String getPath() {
			return uriInfo != null ? uriInfo.getPath() : "";
		}
	}

	@Provider
	public static class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

		@Context
		UriInfo uriInfo;

		@Override
		public Response toResponse(WebApplicationException ex) {
			int statusCode = ex.getResponse() != null
					? ex.getResponse().getStatus()
					: Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

			List<String> messages = new ArrayList<>();
			messages.add(ex.getMessage());

			StandardError response = new StandardError(
					statusCode,
					"Client Error",
					messages,
					getPath()
			);

			return Response.status(statusCode)
					.entity(response)
					.build();
		}

		private String getPath() {
			return uriInfo != null ? uriInfo.getPath() : "";
		}
	}
}