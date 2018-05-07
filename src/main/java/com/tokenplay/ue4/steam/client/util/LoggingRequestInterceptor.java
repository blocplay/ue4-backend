package com.tokenplay.ue4.steam.client.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.jooq.lambda.Unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.steam.client.SteamApiClient;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

@Data
@Slf4j
/**
 * An OkHttp interceptor that logs information about the requests and responses depending on the log level set.
 *
 * INFO logs just the most basic information about the request and response. DEBUG adds headers information TRACE shows the request, if present, and
 * response bodies, beautifying them as JSON objects if they match.
 *
 * Inspired by: https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/java/okhttp3/logging/HttpLoggingInterceptor.java
 */
public final class LoggingRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!log.isInfoEnabled()) {
            return chain.proceed(request);
        } else {
            final RequestBody requestBody = request.body();
            final boolean hasRequestBody = requestBody != null;
            final Connection connection = chain.connection();
            final Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            final StringBuilder requestInformation = new StringBuilder("Request: ");
            requestInformation.append(ObjectJoiner.join(" ", protocol.toString().toUpperCase(), request.method(), request.url()));
            long requestBodySize = -1;
            if (hasRequestBody) {
                requestBodySize = requestBody.contentLength();
                requestInformation.append(", body:");
                requestInformation.append(requestBodySize);
                requestInformation.append(" bytes");
            }
            log.info(requestInformation.toString());

            if (log.isDebugEnabled()) {
                // If the request has a body, sometimes these headers are not present, so let's make them explicit
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        logHeader(SteamApiClient.CONTENT_TYPE_HEADER, requestBody.contentType().toString());
                    }
                    if (requestBodySize != -1) {
                        logHeader(SteamApiClient.CONTENT_LENGTH_HEADER, Long.toString(requestBodySize));
                    }
                }
                // Log the other headers
                for (String header : request.headers().names()) {
                    if (!SteamApiClient.CONTENT_TYPE_HEADER.equalsIgnoreCase(header)
                        && !SteamApiClient.CONTENT_LENGTH_HEADER.equalsIgnoreCase(header)) {
                        for (String value : request.headers().values(header)) {
                            logHeader(header, value);
                        }
                    }
                }
                if (log.isTraceEnabled() && hasRequestBody) {
                    Supplier<Buffer> requestBufferSupplier = Unchecked.supplier(() -> {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);
                        return buffer;
                    });
                    logBody(requestBufferSupplier, requestBody.contentType(), request.headers());
                }
            }
            final long requestStart = System.nanoTime();
            final Response response = chain.proceed(request);
            final long totalRequestTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStart);

            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            log.info("Response: {}", ObjectJoiner.join(" ", response.code(), response.message()));
            if (contentLength >= 0) {
                log.info("  {}: {}", SteamApiClient.CONTENT_LENGTH_HEADER, contentLength);
            }
            log.info("  Request took {} ms", totalRequestTime);

            if (log.isDebugEnabled()) {
                for (String header : response.headers().names()) {
                    for (String value : response.headers().values(header)) {
                        logHeader(header, value);
                    }
                }
                if (log.isTraceEnabled() && HttpEngine.hasBody(response)) {
                    MediaType contentType = responseBody.contentType();
                    Supplier<Buffer> responseBufferSupplier = Unchecked.supplier(() -> {
                        BufferedSource source = responseBody.source();
                        source.request(Long.MAX_VALUE);
                        return source.buffer().clone();
                    });
                    logBody(responseBufferSupplier, contentType, response.headers());
                }
            }
            return response;
        }
    }

    private void logBody(Supplier<Buffer> bufferSupplier, MediaType contentType, Headers headers) {
        if (bodyEncoded(headers)) {
            log.trace("  Body: encoded, not shown");
        } else {
            Buffer buffer = bufferSupplier.get();
            Charset charset = StandardCharsets.UTF_8;
            if (contentType != null) {
                try {
                    charset = contentType.charset(StandardCharsets.UTF_8);
                } catch (UnsupportedCharsetException e) {
                    log.error("  Body: Could not be decoded {}", e.getMessage());
                }
            }
            String body = buffer.readString(charset);
            String bodyContentType = headers.get(SteamApiClient.CONTENT_TYPE_HEADER);
            if (bodyContentType != null && bodyContentType.toLowerCase().startsWith(SteamApiClient.APPLICATION_JSON_HEADER)) {
                log.trace("  JSON Body: {}", writeJSON(body));
            } else {
                log.trace("  Body: {}", body);
            }
        }
    }

    private void logHeader(final String headerName, final String headerValue) {
        log.debug("  Header: {}: \"{}\"", headerName, headerValue);
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get(SteamApiClient.CONTENT_ENCODING_HEADER);
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    public static String writeJSON(final Object object) {
        ObjectMapper mapper = null;
        String result = null;
        mapper = new ObjectMapper();
        try {
            if (object instanceof String) {
                Object json = mapper.readValue((String) object, Object.class);
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } else {
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
        } catch (final IOException e) {
            log.warn("Body is not a json object {}", e.getMessage());
        }
        return result;
    }

}
