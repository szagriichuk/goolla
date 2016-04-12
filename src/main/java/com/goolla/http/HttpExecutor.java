package com.goolla.http;

import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.callback.objects.ResultObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Http method executor.
 *
 * @author szagriichuk.
 */
public class HttpExecutor {
    private static List<Integer> OK_STATUSES = new ArrayList<Integer>(){{
        add(HttpStatus.SC_OK);
        add(HttpStatus.SC_ACCEPTED);
        add(HttpStatus.SC_CREATED);
        add(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
        add(HttpStatus.SC_NO_CONTENT);
        add(HttpStatus.SC_RESET_CONTENT);
        add(HttpStatus.SC_PARTIAL_CONTENT);
        add(HttpStatus.SC_MULTI_STATUS);
    }};

    public void execute(HttpRequestBase method, final ResponseCallback callback) {
            execute(method, null, callback);
    }

    public void execute(HttpRequestBase method, CredentialsProvider credentialsProvider, final ResponseCallback callback) {
        final CloseableHttpAsyncClient httpAsyncClient;
        httpAsyncClient = createAndStartHttpClient(credentialsProvider);
        httpAsyncClient.execute(method, createFutureCallback(callback, httpAsyncClient));
    }

    private CloseableHttpAsyncClient createAndStartHttpClient(CredentialsProvider credentialsProvider) {
        CloseableHttpAsyncClient httpAsyncClient;
        if (credentialsProvider != null) {
            httpAsyncClient = HttpAsyncClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        } else {
            httpAsyncClient = HttpAsyncClients.createDefault();
        }
        httpAsyncClient.start();
        return httpAsyncClient;
    }

    private FutureCallback<HttpResponse> createFutureCallback(final ResponseCallback callback, final CloseableHttpAsyncClient httpAsyncClient) {
        return new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                if(!checkIfStatusIsSuccess(result))
                    return;
                HttpEntity entity = null;
                try {
                    entity = result.getEntity();
                    callback.onComplete(readValueAndCreateObject(entity));
                    close(httpAsyncClient, callback);
                } catch (IOException e) {
                    failed(e);
                } finally {
                    finallyClose(entity);
                }
            }

            private ResultObject readValueAndCreateObject(HttpEntity entity) throws IOException {
                return new ResultObject(EntityUtils.toByteArray(entity));
            }

            private void finallyClose(HttpEntity entity) {
                try {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                } catch (IOException e) {
                    // do nothing
                }
            }

            private boolean checkIfStatusIsSuccess(HttpResponse result) {
                if (!OK_STATUSES.contains(result.getStatusLine().getStatusCode())) {
                    failed(new HttpException(result.getStatusLine().getReasonPhrase()));
                    return false;
                }
                return true;
            }

            @Override
            public void failed(Exception ex) {
                callback.onError(ex);
                close(httpAsyncClient, callback);
            }

            @Override
            public void cancelled() {
                failed(new HttpException("The operation was canceled."));
            }
        };
    }

    private void close(CloseableHttpAsyncClient httpAsyncClient, ResponseCallback callback) {
        try {
            httpAsyncClient.close();
        } catch (IOException e) {
            callback.onError(e);
        }
    }
}
