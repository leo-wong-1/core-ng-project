package core.framework.http;

import core.framework.impl.http.HTTPClientImpl;
import core.framework.impl.http.RetryHandler;
import core.framework.util.StopWatch;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author neo
 */
public final class HTTPClientBuilder {
    private final Logger logger = LoggerFactory.getLogger(HTTPClientBuilder.class);

    private Duration timeout = Duration.ofSeconds(60);
    private int maxConnections = 100;
    private Duration keepAliveTimeout = Duration.ofSeconds(60);
    private Duration slowOperationThreshold = Duration.ofSeconds(30);
    private boolean enableCookie = false;
    private boolean enableRedirect = false;
    private Integer maxRetries;
    private String userAgent = "HTTPClient";

    public HTTPClient build() {
        StopWatch watch = new StopWatch();
        try {
            HttpClientBuilder builder = HttpClients.custom();
            builder.setUserAgent(userAgent);

            builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                   .setSSLContext(new SSLContextBuilder().loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE).build());

            // builder use PoolingHttpClientConnectionManager by default
            builder.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).build());
            builder.setDefaultRequestConfig(RequestConfig.custom()
                                                         .setSocketTimeout((int) timeout.toMillis())
                                                         .setConnectionRequestTimeout((int) timeout.toMillis())
                                                         .setConnectTimeout((int) timeout.toMillis()).build());
            builder.setKeepAliveStrategy((response, context) -> keepAliveTimeout.toMillis());
            builder.setConnectionTimeToLive(keepAliveTimeout.toMillis(), TimeUnit.MILLISECONDS);
            builder.evictIdleConnections(keepAliveTimeout.toMillis(), TimeUnit.MILLISECONDS);

            builder.setMaxConnPerRoute(maxConnections)
                   .setMaxConnTotal(maxConnections);

            builder.disableAuthCaching();
            builder.disableConnectionState();
            if (!enableRedirect) builder.disableRedirectHandling();
            if (!enableCookie) builder.disableCookieManagement();

            if (maxRetries != null) {
                builder.setRetryHandler(new RetryHandler(maxRetries));
                builder.setServiceUnavailableRetryStrategy(new DefaultServiceUnavailableRetryStrategy(maxRetries, 500));
            } else {
                builder.disableAutomaticRetries();
            }

            CloseableHttpClient httpClient = builder.build();
            return new HTTPClientImpl(httpClient, userAgent, slowOperationThreshold);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new Error(e);
        } finally {
            logger.info("create http client, elapsedTime={}", watch.elapsedTime());
        }
    }

    public HTTPClientBuilder maxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public HTTPClientBuilder timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public HTTPClientBuilder keepAliveTimeout(Duration keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
        return this;
    }

    public HTTPClientBuilder slowOperationThreshold(Duration slowOperationThreshold) {
        this.slowOperationThreshold = slowOperationThreshold;
        return this;
    }

    public HTTPClientBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public HTTPClientBuilder enableCookie() {
        enableCookie = true;
        return this;
    }

    public HTTPClientBuilder enableRedirect() {
        enableRedirect = true;
        return this;
    }

    public HTTPClientBuilder enableRetry(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }
}
