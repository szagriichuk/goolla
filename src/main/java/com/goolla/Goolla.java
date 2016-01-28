package com.goolla;

import com.goolla.http.BaseHttpApi;
import com.goolla.http.callback.ResponseCallback;
import com.goolla.http.callback.objects.ResultObject;
import com.goolla.http.params.Param;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author szagriichuk.
 */
public class Goolla {
    private static final Logger LOG = LoggerFactory.getLogger(Goolla.class);

    public static SyncApi syncApi(BaseHttpApi httpApi) {
        return new SyncApi(httpApi);
    }

    public static AsyncApi asyncApi(BaseHttpApi httpApi) {
        return new AsyncApi(httpApi);
    }

    private static class API {
        protected BaseHttpApi httpApi;

        public API(BaseHttpApi httpApi) {
            this.httpApi = httpApi;
        }
    }

    private interface Func {
        void apply(CountDownLatch latch, ResultObject[] objects);
    }

    public static class SyncApi extends API {
        public SyncApi(BaseHttpApi httpApi) {
            super(httpApi);
        }

        public ResultObject post(final String url, final Param<?> params) {
            return execute(new Func() {
                @Override
                public void apply(CountDownLatch latch, ResultObject[] objects) {
                    httpApi.post(url, createCallBack(latch, objects), params);
                }
            });
        }

        public ResultObject put(final String url, final Param<?> params) {
            return execute(new Func() {
                @Override
                public void apply(CountDownLatch latch, ResultObject[] objects) {
                    httpApi.put(url, createCallBack(latch, objects), params);
                }
            });
        }

        public ResultObject get(final String url, final List<Header> headers, final Param<?>... params) {
            return execute(new Func() {
                @Override
                public void apply(CountDownLatch latch, ResultObject[] objects) {
                    httpApi.get(url, headers, createCallBack(latch, objects), params);
                }
            });
        }

        public ResultObject get(final String url, final Param<?>... params) {
            return execute(new Func() {
                @Override
                public void apply(CountDownLatch latch, ResultObject[] objects) {
                    httpApi.get(url, createCallBack(latch, objects), params);
                }
            });
        }

        public ResultObject delete(final String url, final Param<?>... params) {
            return execute(new Func() {
                @Override
                public void apply(CountDownLatch latch, ResultObject[] objects) {
                    httpApi.delete(url, createCallBack(latch, objects), params);
                }
            });
        }

        private ResultObject execute(Func func) {
            final CountDownLatch latch = new CountDownLatch(1);
            final ResultObject[] object = new ResultObject[1];
            func.apply(latch, object);
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOG.warn(e.getMessage(), e);
            }
            return object[0];
        }


        private ResponseCallback createCallBack(final CountDownLatch latch, final ResultObject[] object) {
            return new ResponseCallback() {
                @Override
                public void onComplete(ResultObject value) {
                    object[0] = value;
                    latch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    LOG.error(throwable.getMessage(), throwable);
                    latch.countDown();
                }
            };
        }
    }

    public static class AsyncApi extends API {
        public AsyncApi(BaseHttpApi httpApi) {
            super(httpApi);
        }

        public void post(String url, ResponseCallback callBack, Param<?> params) {
            httpApi.post(url, callBack, params);
        }

        public void put(String url, ResponseCallback callBack, Param<?> params) {
            httpApi.put(url, callBack, params);
        }

        public void get(String url, List<Header> headers, ResponseCallback callBack, Param<?>... params) {
            httpApi.get(url, headers, callBack, params);
        }

        public void get(String url, ResponseCallback callBack, Param<?>... params) {
            httpApi.get(url, callBack, params);
        }

        public void delete(String url, ResponseCallback callBack, Param<?>... params) {
            httpApi.delete(url, callBack, params);
        }
    }
}
