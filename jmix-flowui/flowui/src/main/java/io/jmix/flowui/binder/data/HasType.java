package io.jmix.flowui.binder.data;

public interface HasType<T> {

    /**
     * @return type of value
     */
    Class<T> getType();
}
