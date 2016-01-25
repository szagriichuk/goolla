package com.goolla;

import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.callback.objects.ResultObject;
import com.goolla.stubs.TestHttpApi;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author szagriichuk.
 */
public class GoollaTest {

    @Test
    public void asyncApi() throws Exception {
        Goolla.AsyncApi asyncApi = Goolla.asyncApi(new TestHttpApi("key"));
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        asyncApi.get("http://www.google.com", new ResponseCallback() {
            @Override
            public void onComplete(ResultObject value) {
                assertTrue(true);
                System.out.println(value);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertTrue(false);
                System.out.println(throwable.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @Test
    public void syncApi() throws Exception {
        Goolla.SyncApi asyncApi = Goolla.syncApi(new TestHttpApi("key"));
        ResultObject object = asyncApi.get("http://www.google.com");
        assertNotNull(object);
        System.out.println(object);
    }
}