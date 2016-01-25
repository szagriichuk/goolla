package com.goolla.http.params;

/**
 * Base abstraction of the Http's parameters.
 *
 * @author szagriichuk.
 */
public abstract class Param<T> {
    T value;

    public Param(T param) {
        this.value = param;
    }

    @Override
    public String toString() {
        return name() + "=" + value;
    }

    public abstract String name();
    public T value(){
        return value;
    }
}
