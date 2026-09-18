package io.github.im2back.observability;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logmanager.MDC;

import java.util.UUID;

@Provider
public class RequestLoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) {

        MDC.put(
                "requestId",
                UUID.randomUUID().toString().substring(0, 8)
        );

        MDC.put(
                "conversationId",
                request.getHeaderString("X-User-Name")
        );
    }

    @Override
    public void filter(
            ContainerRequestContext request,
            ContainerResponseContext response) {

        MDC.remove("requestId");
        MDC.remove("conversationId");
    }
}