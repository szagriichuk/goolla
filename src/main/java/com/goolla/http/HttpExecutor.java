package com.goolla.http;

import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.callback.objects.ResultObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Http method executor.
 *
 * @author szagriichuk.
 */
public class HttpExecutor {

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
                checkIfStatusIsSuccess(result);
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

            private void checkIfStatusIsSuccess(HttpResponse result) {
                if (result.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                    failed(new HttpException(result.getStatusLine().getReasonPhrase()));
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
