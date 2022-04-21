package io.jmix.flowui.binder.binding;


import io.jmix.flowui.binder.data.Options;

public interface OptionsBinding<V> extends JmixBinding {

    Options<V> getSource();

    <T> T getComponent();

    void activate();
}
