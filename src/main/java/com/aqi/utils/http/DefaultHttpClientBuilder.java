package com.aqi.utils.http;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


/**
 * @author wubaoxin
 */
public class DefaultHttpClientBuilder extends HttpClientBuilder {

    private DefaultHttpClientBuilder() {
    }

    public static DefaultHttpClientBuilder custom() {
        return new DefaultHttpClientBuilder();
    }

    /**
     * 设置超时
     */
    public DefaultHttpClientBuilder timeout(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                // 从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(connectionRequestTimeout)
                // 建立连接超时
                .setConnectTimeout(connectTimeout)
                // 等待响应超时（读取数据超时）
                .setSocketTimeout(socketTimeout)
                .build();
        return (DefaultHttpClientBuilder) this.setDefaultRequestConfig(requestConfig);
    }

    /**
     * 设置连接池
     *
     * @param maxTotal    最大连接数
     * @param maxPerRoute 每个路由的最大连接数
     * @param maxRoute    最大路由数据
     * @param hostname    主机
     * @param port        端口
     */
    public DefaultHttpClientBuilder pool(int maxTotal, int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);
        return (DefaultHttpClientBuilder) this.setConnectionManager(cm);
    }

    /**
     * 设置连接池
     *
     * @param maxTotal    最大连接数
     * @param maxPerRoute 每个路由的最大连接数
     */
    public DefaultHttpClientBuilder pool(int maxTotal, int maxPerRoute) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);

        return (DefaultHttpClientBuilder) this.setConnectionManager(cm);
    }


    /**
     * 重试（如果请求是幂等的，就再次尝试）
     *
     * @param tryTimes               重试次数
     * @param retryWhenInterruptedIO 连接拒绝时，是否重试
     * @return 返回当前对象
     */
    public DefaultHttpClientBuilder retry(final int tryTimes, final boolean retryWhenInterruptedIO) {
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {

            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // 如果已经重试了n次，就放弃
                if (executionCount >= tryTimes) {
                    return false;
                }
                // 如果服务器丢掉了连接，那么就重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // 不要重试SSL握手异常
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                // 超时重连
                if (exception instanceof InterruptedIOException) {
                    return retryWhenInterruptedIO;
                }
                // 目标服务器不可达
                if (exception instanceof UnknownHostException) {
                    return true;
                }
                // SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };
        return (DefaultHttpClientBuilder) this.setRetryHandler(httpRequestRetryHandler);
    }

    /**
     * 重试
     *
     * @param tryTimes 重试次数
     */
    public DefaultHttpClientBuilder retry(final int tryTimes) {
        return retry(tryTimes, false);
    }

    /**
     * 设置代理
     *
     * @param hostOrIP 代理host或者ip
     * @param port     代理端口
     */
    public DefaultHttpClientBuilder proxy(String hostOrIP, int port) {
        // 依次是代理地址，代理端口号，协议类型
        HttpHost proxy = new HttpHost(hostOrIP, port, "http");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return (DefaultHttpClientBuilder) this.setRoutePlanner(routePlanner);
    }

    /**
     * 设置ssl安全链接
     *
     * @return 返回当前对象
     */
    public DefaultHttpClientBuilder ssl() {
        return (DefaultHttpClientBuilder) this.setSSLSocketFactory(createSSLConnSocketFactory());
    }

    /**
     * create SSLConnectionSocketFactory to trust all
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                    .build();
            return new SSLConnectionSocketFactory(sslcontext,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

}
