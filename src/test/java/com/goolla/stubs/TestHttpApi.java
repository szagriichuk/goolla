package com.goolla.stubs;

import com.goolla.http.BaseHttpApi;
import com.goolla.http.HttpExecutor;

/**
 * @author szagriichuk.
 */
public class TestHttpApi extends BaseHttpApi {
    public TestHttpApi(String key) {
        super(key, new HttpExecutor());
    }
}
