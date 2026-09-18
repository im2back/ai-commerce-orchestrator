package io.github.im2back.observability;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PyroscopeConfig {

    @PostConstruct
    void init() {

        PyroscopeAgent.start(
                new Config.Builder()
                        .setApplicationName("assistant-ai")
                        .setProfilingEvent(EventType.ITIMER)
                        .setFormat(Format.JFR)
                        .setServerAddress("http://localhost:4040")
                        .build()
        );
    }
}