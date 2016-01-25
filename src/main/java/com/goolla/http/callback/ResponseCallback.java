package com.goolla.http.callback;

import com.goolla.http.callback.objects.ResultObject;

/**
 * @author szagriichuk.
 */
public interface ResponseCallback {
    void onComplete(ResultObject value);
    void onError(Throwable throwable);

}
