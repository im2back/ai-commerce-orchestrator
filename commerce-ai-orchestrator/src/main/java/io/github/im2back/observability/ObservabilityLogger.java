package io.github.im2back.observability;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logmanager.MDC;

@ApplicationScoped
public class ObservabilityLogger {

    public void info(String event, String message, Object... fields) {

        if (fields.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Os campos devem ser informados em pares: chave, valor"
            );
        }

        try {
            MDC.put("event", event);

            for (int i = 0; i < fields.length; i += 2) {

                String key = String.valueOf(fields[i]);
                Object value = fields[i + 1];

                if (value != null) {
                    MDC.put(key, String.valueOf(value));
                }
            }

            Log.info(message);

        } finally {

            MDC.remove("event");

            for (int i = 0; i < fields.length; i += 2) {
                MDC.remove(String.valueOf(fields[i]));
            }
        }
    }
}