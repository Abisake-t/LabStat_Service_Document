package com.scube.document.observability;

import com.scube.multi.tenant.annotations.NoAsync;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HttpRequestResponseFilter extends OncePerRequestFilter {

    private static final TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
    private static final TextMapGetter<HttpServletRequest> getter = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(HttpServletRequest carrier) {
            return carrier.getHeaderNames() != null ? Collections.list(carrier.getHeaderNames()) : Collections.emptyList();
        }

        @Override
        public String get(HttpServletRequest carrier, String key) {
            return carrier.getHeader(key);
        }
    };

    private static final TextMapSetter<HttpServletResponse> setter = new TextMapSetter<>() {
        @Override
        public void set(HttpServletResponse carrier, String key, String value) {
            carrier.setHeader(key, value);
        }
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var requestWrapper = new ContentCachingRequestWrapper(request);
        var responseWrapper = new ContentCachingResponseWrapper(response);

        if (ObjectUtils.isEmpty(requestWrapper)) {
            filterChain.doFilter(request, response);
        } else {
            // Extract the context from the incoming request
            Context context = propagator.extract(Context.current(), requestWrapper, getter);

            // Create a new span with the extracted context
            var tracer = GlobalOpenTelemetry.getTracer("org.springframework.boot");
            Span span = tracer.spanBuilder("Request Response Details")
                    .setParent(context)
                    .startSpan();

            try (Scope scope = span.makeCurrent()) {
                filterChain.doFilter(requestWrapper, responseWrapper);

                // exclude any html, css, js, or image requests
                var uri = requestWrapper.getRequestURI();
                if (!ObjectUtils.isEmpty(uri) || !uri.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|svg|ico|woff2|ttf|eot|otf|woff|mp4|webm|ogg|mp3|wav|flac|aac)$")) {
                    span.setAttribute("http.request.path_variables", getPathVariables(requestWrapper));
                    span.setAttribute("http.request.params", getRequestParams(requestWrapper));
                    span.setAttribute("http.request.body", getRequestBody(requestWrapper));
                    span.setAttribute("http.request.method", requestWrapper.getMethod());
                    span.setAttribute("http.route", uri);
                    span.setAttribute("http.response.status_code", responseWrapper.getStatus());
                    getResponseBody(requestWrapper, responseWrapper).thenAccept(body -> {
                        span.setAttribute("http.response.body", body);
                        span.end();
                    });
                }
            } finally {
                // Inject the context into the outgoing response
                propagator.inject(context, responseWrapper, setter);
            }
        }
    }

    public String getRequestParams(@NonNull ContentCachingRequestWrapper request) {
        return request.getParameterMap()
                .entrySet().stream()
                .map(e -> e.getKey() + "=" + Arrays.toString(e.getValue()))
                .collect(Collectors.joining(", "));
    }

    public String getPathVariables(@NonNull ContentCachingRequestWrapper request) {
        var pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return pathVariables == null ? "" : pathVariables.toString();
    }

    public String getRequestBody(@NonNull ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @NoAsync
    public CompletionStage<String> getResponseBody(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        var future = new CompletableFuture<String>();

        // for any reactive/async requests
        if (request.isAsyncStarted()) {
            request.getAsyncContext().addListener(new AsyncListener() {
                @NoAsync
                public void onComplete(AsyncEvent asyncEvent) throws IOException {
                    var body = new String(response.getContentAsByteArray());
                    response.copyBodyToResponse(); // IMPORTANT: copy response back into original response
                    future.complete(body);
                }

                public void onTimeout(AsyncEvent asyncEvent) {
                    //ignore
                }

                public void onError(AsyncEvent asyncEvent) {
                    //ignore
                }

                public void onStartAsync(AsyncEvent asyncEvent) {
                    //ignore
                }
            });
        } else {
            var body = new String(response.getContentAsByteArray());
            response.copyBodyToResponse(); // IMPORTANT: copy response back into original response
            future.complete(body);
        }
        return future;
    }
}
