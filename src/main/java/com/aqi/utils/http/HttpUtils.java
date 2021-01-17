package com.aqi.utils.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * http 工具类.
 * demo :
 *
     public void get() throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url("http://localhost:8090/peoples/");
        HttpRequestResult result = HttpClientUtils.get(config);
        log.info(result.getResponseText());
     }

     public void post() throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url("http://localhost:8090/peoples/")
        .addParam("a,2)
        .addParam("b","333");
        HttpRequestResult result = HttpClientUtils.post(config);
        log.info(result.getResponseText());
     }

     public void postJson() throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url("http://localhost:8090/peoples/body")
        .httpEntityType(HttpEntityType.ENTITY_STRING)
        .json("{\"a\",\"2\"}");
        HttpRequestResult result = HttpClientUtils.post(config);
        log.info(result.getResponseText());
     }

     public void postByte() throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url("http://localhost:8090/peoples/body")
        .httpEntityType(HttpEntityType.ENTITY_BYTES)
        .bytes("{\"a\",\"2\"}".getBytes("UTF-8"));
        HttpRequestResult result = HttpClientUtils.post(config);
        log.info(result.getResponseText());
     }

     public void postInputStream() throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url("http://localhost:8090/peoples/inputStream")
        .httpEntityType(HttpEntityType.ENTITY_STRING)
        .json("{\"a\",\"2\"}");
        HttpRequestResult result = HttpClientUtils.post(config);
        log.info(result);
     }

     public void down() throws IOException {
        String imgUrl = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white_fe6da1ec.png"; //百度logo
        File file = new File("/data/a/b/test/baidu.png");
        HttpClientUtils.down(HttpRequestConfig.create().url(imgUrl).out(new FileOutputStream(file)));
        if (file.exists()) {
            System.out.println("图片下载成功了！存放在：" + file.getPath());
        }

    }
 *
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 默认采用的http协议的HttpClient对象
     */
    private static HttpClient httpClient;

    /**
     * 默认采用的https协议的HttpClient对象
     */
    private static HttpClient httpsClient;

    private final static Object SYNC_LOCK = new Object();


    private static final int CONNECT_TIMEOUT = 30000;

    private static final int SOCKET_TIMEOUT = 30000;

    private static final int CONNECT_REQUEST_TIMEOUT = 30000;

    private static final int MAX_TOTAL = 100;

    private static final int MAX_PER_ROUTE = 100;

    private static final int RETRY_TIMES = 3;


    /**
     * 初始化 httpClient
     *
     * @param httpRequestConfig
     */
    private static void setHttpClient(HttpRequestConfig httpRequestConfig) {

        if (httpRequestConfig.getHttpClient() == null) {
            if (httpRequestConfig.isUseSSL()) {
                if (httpsClient == null) {
                    synchronized (SYNC_LOCK) {
                        if (httpsClient == null) {
                            httpsClient = DefaultHttpClientBuilder.custom()
                                    .ssl()
                                    .timeout(CONNECT_TIMEOUT, SOCKET_TIMEOUT, CONNECT_REQUEST_TIMEOUT)
                                    .pool(MAX_TOTAL, MAX_PER_ROUTE)
                                    .retry(RETRY_TIMES, false).build();

                        }
                    }
                }
                httpRequestConfig.client(httpsClient);
            } else {
                if (httpClient == null) {
                    synchronized (SYNC_LOCK) {
                        if (httpClient == null) {
                            httpClient = DefaultHttpClientBuilder.custom()
                                    .timeout(CONNECT_TIMEOUT, SOCKET_TIMEOUT, CONNECT_REQUEST_TIMEOUT)
                                    .pool(MAX_TOTAL, MAX_PER_ROUTE)
                                    .retry(RETRY_TIMES, false).build();

                        }
                    }
                }
                httpRequestConfig.client(httpClient);
            }
        }
    }

    /**
     * GET 请求
     *
     * @param httpRequestConfig 请求配置类
     * @return 请求结果
     * @throws IOException
     */
    public static HttpRequestResult get(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.GET)), httpRequestConfig);
    }

    /**
     * POST 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult post(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.POST)), httpRequestConfig);
    }

    /**
     * PUT 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult put(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.PUT)), httpRequestConfig);
    }

    /**
     * HEADER 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult header(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.HEAD)), httpRequestConfig);
    }

    /**
     * PATCH 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult patch(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.PATCH)), httpRequestConfig);
    }

    /**
     * DELETE 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult delete(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.DELETE)), httpRequestConfig);
    }


    /**
     * TRACE 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult trace(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.TRACE)), httpRequestConfig);
    }

    /**
     * OPTIONS 请求
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static HttpRequestResult options(HttpRequestConfig httpRequestConfig) throws IOException {
        return transformHttpRequestResult(execute(httpRequestConfig.requestMethod(HttpRequestMethod.OPTIONS)), httpRequestConfig);
    }

    /**
     * 下载
     *
     * @param httpRequestConfig
     * @return
     * @throws IOException
     */
    public static OutputStream down(HttpRequestConfig httpRequestConfig) throws IOException {
        HttpResponse response = execute(httpRequestConfig.requestMethod(HttpRequestMethod.GET));
        response.getEntity().writeTo(HttpRequestConfig.getOut());
        close(response);
        return HttpRequestConfig.getOut();
    }


    /**
     * 请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回HttpResponse对象
     */
    private static HttpResponse execute(HttpRequestConfig config) throws IOException {

        if (config.getRequestMethod() == null) {
            throw new NullPointerException("request method is null!");
        }

        //设置httpClient
        setHttpClient(config);

        //创建请求对象
        HttpRequestBase request = getRequest(HttpRequestConfig.getUrl(), config.getRequestMethod());

        //设置header信息
        request.setHeaders(config.getHeaders());

        //判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
        if (HttpEntityEnclosingRequestBase.class.isAssignableFrom(request.getClass())) {
            //装填参数
            HttpEntity entity = config.getEntity();
            //设置参数到请求对象中
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);

            if (config.getParamMap().size() > 0) {
                logger.debug("请求参数：{}", config.getParamMap());
            }

            if (StringUtils.isNotBlank(config.getJsonParam())) {
                logger.debug("请求参数：{}", config.getJsonParam());
            }

        } else {
            int index = HttpRequestConfig.getUrl().indexOf("?");
            if (index > 0) {
                logger.debug("请求参数：{}", HttpRequestConfig.getUrl().substring(index + 1));
            }
        }
        //执行请求操作，并拿到结果（同步阻塞）
        if (config.getContext() == null) {
            return config.getHttpClient().execute(request);
        } else {
            return config.getHttpClient().execute(request, config.getContext());
        }


    }

    private static HttpRequestResult transformHttpRequestResult(HttpResponse response, HttpRequestConfig config) throws IOException {
        // 获取结果实体
        HttpRequestResult result = new HttpRequestResult(response.getStatusLine().getStatusCode(), response.getAllHeaders());
        if (response.getEntity() != null) {
            // content 编码
            String charset = (response.getEntity().getContentEncoding() == null) ? config.getResponseCharset() : response.getEntity().getContentEncoding().getValue();
            result.setContentEncoding(charset);
            // content 类型
            String contentType = (response.getEntity().getContentType() == null) ? "" : response.getEntity().getContentType().getValue();
            result.setContentType(contentType);
            result.setResponseText(EntityUtils.toString(response.getEntity(), config.getResponseCharset()));
        } else {// 有可能是head请求
            result.setResponseText(response.getStatusLine().toString());
        }

        close(response);
        return result;
    }

    /**
     * 尝试关闭
     *
     * @param response
     */
    private static void close(HttpResponse response) {
        try {
            EntityUtils.consume(response.getEntity());
            //如果CloseableHttpResponse 是resp的父类，则支持关闭
            if (CloseableHttpResponse.class.isAssignableFrom(response.getClass())) {
                ((CloseableHttpResponse) response).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求方法名，获取request对象
     *
     * @param url    资源地址
     * @param method 请求方式
     * @return 返回Http处理request基类
     */
    private static HttpRequestBase getRequest(String url, HttpRequestMethod method) {
        HttpRequestBase request;
        switch (method.getCode()) {
            case 0:
                request = new HttpGet(url);
                break;
            case 1:
                request = new HttpPost(url);
                break;
            case 2:
                request = new HttpHead(url);
                break;
            case 3:
                request = new HttpPut(url);
                break;
            case 4:
                request = new HttpDelete(url);
                break;
            case 5:
                request = new HttpTrace(url);
                break;
            case 6:
                request = new HttpPatch(url);
                break;
            case 7:
                request = new HttpOptions(url);
                break;
            default:
                request = new HttpPost(url);
                break;
        }
        System.out.println(request.hashCode());
        return request;
    }
}
