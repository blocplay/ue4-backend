package com.tokenplay.ue4.steam.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.steam.client.types.AllowedMethod;
import com.tokenplay.ue4.steam.client.types.ApiException;
import com.tokenplay.ue4.steam.client.types.ApiPath;
import com.tokenplay.ue4.steam.client.types.api.ApiError;
import com.tokenplay.ue4.steam.client.types.api.ApiGenericResponse;
import com.tokenplay.ue4.steam.client.types.api.AuthenticateUserTicketRS;
import com.tokenplay.ue4.steam.client.types.api.CheckAppOwnershipRS;
import com.tokenplay.ue4.steam.client.util.LoggingRequestInterceptor;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

@Slf4j
@Data
public class SteamApiClient implements AutoCloseable {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String APPLICATION_JSON_HEADER = "application/json";
    public static final String KEY_PROPERTY_NAME = "api.key";
    public static final String STEAMAPI_PROPERTIES_FILE_NAME = "steamapi.properties";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    public static final String DEFAULT_LANGUAGE = "ENG";
    private static final int DEFAULT_TIME_OUT = 30000;

    private static final String STEAM_API_PATH = "https://api.steampowered.com/";
    private static final int BASIC_GAME_ID = 416020;
    private static final int VETERAN_PACK_ID = 568051;
    private static final int ACE_PACK_ID = 568052;

    private String defaultLanguage;
    private boolean defaultUseSecondaryLanguage;
    private Properties properties = null;
    private OkHttpClient okHttpClient = null;
    private boolean initialised = false;
    private int readTimeout = DEFAULT_TIME_OUT;
    private int connectTimeout = DEFAULT_TIME_OUT;
    private int connectionRequestTimeout = DEFAULT_TIME_OUT;
    private ExecutorService executorService = null;
    private ObjectMapper mapper = null;
    private final String steamapiKey;

    public SteamApiClient() {
        steamapiKey = getApiKey();
    }

    public void init() {
        // @formatter:off
        okHttpClient = new OkHttpClient.Builder()
        .writeTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS)
        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .addInterceptor(new LoggingRequestInterceptor())
        .hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          })
        .build();
        // @formatter:on
        initialised = true;
        executorService = Executors.newFixedThreadPool(8);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        if (isInitialised()) {
            log.warn("SteamApiClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        if (isInitialised()) {
            log.warn("SteamApiClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        if (isInitialised()) {
            log.warn("SteamApiClient is already initialised, new timeout will have no effect.");
        }
    }

    private String getSteamApiProperty(String propertyName) {
        if (properties == null) {
            try (InputStream stemApiPropertiesIS = ClassLoader.getSystemResourceAsStream(STEAMAPI_PROPERTIES_FILE_NAME)) {
                properties = new Properties();
                if (stemApiPropertiesIS != null) {
                    properties.load(stemApiPropertiesIS);
                }
            } catch (IOException e) {
                log.error("Error loading properties (){}.", STEAMAPI_PROPERTIES_FILE_NAME, e);
            }
        }
        return properties.getProperty(propertyName);
    }

    private String getApiKey() {
        final String result = getValueFromProperties("Steam API key", KEY_PROPERTY_NAME);
        if (StringUtils.isBlank(result)) {
            throw new IllegalArgumentException(KEY_PROPERTY_NAME + " is mandatory!");
        }
        return result;
    }

    private String getValueFromProperties(String name, String propertyName) {
        String apiKey = System.getProperty(propertyName);
        if (apiKey == null) {
            apiKey = getSteamApiProperty(propertyName);
            if (apiKey != null) {
                log.debug("{} loaded from properties file. {}", name, apiKey);
            } else {
                log.debug("No {} loaded from properties, value not specified.", name);
            }
        } else {
            apiKey = apiKey.trim();
            log.debug("{} loaded from system properties. {}", name, apiKey);
        }
        return apiKey;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// INTERNALS
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    //    private <R extends ApiGenericResponse> R callRemoteAPI(ApiPath<R> path) throws ApiException {
    //        return callRemoteAPI(null, null, path);
    //    }

    private <R extends ApiGenericResponse> R callRemoteAPI(final Map<String, String> params, ApiPath<R> path) throws ApiException {
        return callRemoteAPI(null, params, path);
    }

    //    private <R extends ApiGenericResponse> R callRemoteAPI(final Object request, ApiPath<R> path) throws ApiException {
    //        return callRemoteAPI(request, null, path);
    //    }

    private <R extends ApiGenericResponse> R callRemoteAPI(final Object request, final Map<String, String> params, ApiPath<R> path)
        throws ApiException {
        if (isInitialised()) {
            final AllowedMethod allowedMethod = path.getAllowedMethod();
            final String url;
            if (AllowedMethod.GET == allowedMethod && !path.getAllowedParams().isEmpty()) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(path.getUrl(params, STEAM_API_PATH)).newBuilder();
                for (String param : path.getAllowedParams()) {
                    String value = params.get(param);
                    if (value != null) {
                        urlBuilder.addQueryParameter(param, value);
                    }
                }
                url = urlBuilder.build().toString();
            } else {
                url = path.getUrl(params, STEAM_API_PATH);
            }
            try {
                Request.Builder requestBuilder = new Request.Builder().headers(getHeaders(allowedMethod)).url(url);
                switch (allowedMethod) {
                    case DELETE:
                        requestBuilder.delete(transformToRequestBody(request));
                        break;
                    case POST:
                        requestBuilder.post(transformToRequestBody(request));
                        break;
                    default:
                        break;
                }

                Response response = okHttpClient.newCall(requestBuilder.build()).execute();
                try (ResponseBody body = response.body()) {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    Charset charset = StandardCharsets.UTF_8;
                    if (body.contentType() != null) {
                        try {
                            charset = body.contentType().charset(StandardCharsets.UTF_8);
                        } catch (UnsupportedCharsetException e) {
                            log.error("Response body could not be decoded {}", e.getMessage());
                        }
                    }
                    String theContent = buffer.readString(charset);
                    if (response.headers().get(CONTENT_TYPE_HEADER).toLowerCase().startsWith(SteamApiClient.APPLICATION_JSON_HEADER)) {
                        R genericResponse = transformToGenericResponse(theContent, path.getResponseClass());
                        if (genericResponse.getError() != null) {
                            throw new ApiException(genericResponse.getError());
                        }
                        return genericResponse;
                    } else {
                        throw new ApiException(new ApiError("Invalid response", "Wrong content type" + response.headers().get(CONTENT_TYPE_HEADER)));
                    }
                }
            } catch (ApiException e) {
                throw e;
            } catch (IOException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new ApiException(new ApiError("Timeout", e.getCause().getMessage()));
                } else {
                    throw new ApiException(new ApiError("Error accessing API", e.getMessage()));
                }
            } catch (Exception e) {
                throw new ApiException(new ApiError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new ApiException(new ApiError("SteamApiClient not initialised", "You have to call init() first, to be able to use this object."));
        }
    }

    private <R extends ApiGenericResponse> R transformToGenericResponse(String content, Class<R> responseClass) throws ApiException {
        try {
            return mapper.readValue(content, responseClass);
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new ApiException(new ApiError("Error parsing JSON response", e.getMessage()));
        }
    }

    private RequestBody transformToRequestBody(Object request) throws ApiException {
        try {
            return RequestBody.create(JSON, mapper.writeValueAsString(request));
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new ApiException(new ApiError("Error parsing JSON response", e.getMessage()));
        }
    }

    private Headers getHeaders(AllowedMethod httpMethod) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("User-Agent", "api-client-java, " + getClass().getPackage().getImplementationVersion());
        // Hash the Api Key + Shared Secret + Current timestamp in seconds
        switch (httpMethod) {
            case GET:
            case DELETE:
                headersBuilder.add("Accept", APPLICATION_JSON_HEADER);
                break;
            case POST:
                // case PUT:
                headersBuilder.add("Content-Type", APPLICATION_JSON_HEADER);
                break;
            default:
                break;
        }
        return headersBuilder.build();
    }

    @Override
    public void close() {
        try {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error closing SteamApiClient resources", e);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// PUBLIC OPERATIONS
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    public CheckAppOwnershipRS checkAcePackOwnership(String steamid) throws ApiException {
        return checkAppOwnership(steamid, ACE_PACK_ID);
    }

    public CheckAppOwnershipRS checkVeteranPackOwnership(String steamid) throws ApiException {
        return checkAppOwnership(steamid, VETERAN_PACK_ID);
    }

    public CheckAppOwnershipRS checkBaseGameOwnership(String steamid) throws ApiException {
        return checkAppOwnership(steamid, BASIC_GAME_ID);
    }

    private CheckAppOwnershipRS checkAppOwnership(String steamid, int appid) throws ApiException {
        final Map<String, String> params = new HashMap<>();
        params.put("key", steamapiKey);
        params.put("steamid", steamid);
        params.put("appid", Integer.toString(appid));
        return callRemoteAPI(params, ApiPath.CHECK_APP_OWNERSHIP);
    }

    public AuthenticateUserTicketRS checkSteamSession(String ticket) throws ApiException {
        final Map<String, String> params = new HashMap<>();
        params.put("key", steamapiKey);
        params.put("ticket", ticket);
        params.put("appid", Integer.toString(BASIC_GAME_ID));
        return callRemoteAPI(params, ApiPath.CHECK_SESSION);
    }
}
