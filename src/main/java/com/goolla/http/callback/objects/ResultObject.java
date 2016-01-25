package com.goolla.http.callback.objects;

import java.util.Arrays;

/**
 * @author szagriichuk.
 */
public class ResultObject {
    private byte[] data;

    public ResultObject(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResultObject{" +
                "data=" + new String(data) +
                '}';
    }
}
