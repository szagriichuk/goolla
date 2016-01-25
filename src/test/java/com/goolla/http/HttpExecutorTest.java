package com.goolla.http;

import com.google.code.tempusfugit.concurrency.IntermittentTestRunner;
import com.google.code.tempusfugit.concurrency.annotations.Intermittent;
import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.callback.objects.ResultObject;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * @author szagriichuk.
 */
@RunWith(IntermittentTestRunner.class)
public class HttpExecutorTest {

    @Test
    public void testSimpleCorrectExecute() throws Exception {
        Assert.assertTrue(executeHttpRequest("http://www.google.com"));
    }

    @Test
    public void testSimpleIncorrectExecute() throws Exception {
        Assert.assertFalse(executeHttpRequest("http://www.google.com/test"));
    }

    @Test
    @Intermittent(repetition = 200)
    public void test200IncorrectExecute() throws Exception {
        testSimpleIncorrectExecute();
    }

    @Test
    @Intermittent(repetition = 200)
    public void test200CorrectExecute() throws Exception {
        testSimpleCorrectExecute();
    }

    private boolean executeHttpRequest(String url) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isComplete = {false};
        HttpExecutor executor = new HttpExecutor();
        executor.execute(new HttpGet(url), new ResponseCallback() {
            @Override
            public void onComplete(ResultObject value) {
                latch.countDown();
                isComplete[0] = true;
                System.out.println(value);
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                isComplete[0] = false;
            }
        });
        latch.await();
        return isComplete[0];
    }

}