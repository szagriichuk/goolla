package com.goolla.http;

import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.params.Param;
import com.goolla.serializer.Serializer;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.nio.entity.NStringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.goolla.http.HttpMethod.*;

/**
 * Provides common methods for
 *
 * @author szagriichuk.
 */
public abstract class BaseHttpApi extends Key {
    private static final Logger LOG = LoggerFactory.getLogger(BaseHttpApi.class);
    private HttpExecutor executor;

    public BaseHttpApi(String key, HttpExecutor executor) {
        super(key);
        this.executor = executor;
    }

    public void post(String url, ResponseCallback callBack, Param<?> params) {
        executor.execute(createPostRequest(createHttpEntityData(params), url), callBack);
    }

    public void put(String url, ResponseCallback callBack, Param<?> params) {
        executor.execute(createPutRequest(createHttpEntityData(params), url), callBack);
    }

    public void get(String url, List<Header> headers, ResponseCallback callBack, Param<?>... params) {
        executor.execute(createGetRequestWithHeader(createUrlWithParams(url + "?", createRequestString("&", params)), headers), callBack);
    }

    public void get(String url, ResponseCallback callBack, String field) {
        executor.execute(createGetRequestWithHeader(createUrlWithParams(url + "/",field), Headers.create()), callBack);
    }

    public void get(String url, ResponseCallback callBack, Param<?>... params) {
        executor.execute(createGetRequestWithHeader(createUrlWithParams(url + "?", createRequestString("&", params)), Headers.create()), callBack);
    }

    public void delete(String url, ResponseCallback callBack, Param<?>... params) {
        executor.execute(createDeleteRequest(createUrlWithParams(url + "?", createRequestString("&", params))), callBack);
    }


    protected String createRequestString(String delim, Param<?>... params) {
        if (params == null || params.length == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length - 1; i++) {
            builder.append(params[i]).append(delim);
        }
        builder.append(params[params.length - 1]);
        return builder.toString().trim();
    }


    protected HttpEntityEnclosingRequestBase createPostRequest(String postData, String url) {
        HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) POST.create(url);
        HttpEntity entity = createHttpEntity(postData);
        if (entity != null) {
            post.setEntity(entity);
        }
        return post;
    }

    protected HttpRequestBase createDeleteRequest(String url) {
        return DELETE.create(url);
    }

    protected HttpEntityEnclosingRequestBase createPutRequest(String postData, String url) {
        HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) HttpMethod.PUT.create(url);
        HttpEntity entity = createHttpEntity(postData);
        if (entity != null) {
            post.setEntity(entity);
        }
        return post;
    }

    protected HttpRequestBase createGetRequestWithHeader(String url, List<Header> headers) {
        HttpRequestBase requestBase = GET.create(url);
        addHeaders(headers, requestBase);
        return requestBase;
    }

    private void addHeaders(List<Header> headers, HttpRequestBase requestBase) {
        for (Header header : headers) {
            requestBase.addHeader(header);
        }
    }

    private HttpEntity createHttpEntity(String data) {
        try {
            return new NStringEntity(data);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Cannot create http request entity from String data.", e);
        }
        return null;
    }

    private String createUrlWithParams(String url, String paramsValues) {
        return url + paramsValues;
    }

    protected String createHttpEntityData(Param<?> params) {
        return Serializer.serialize(params);
    }

    public HttpExecutor getExecutor() {
        return executor;
    }
}
