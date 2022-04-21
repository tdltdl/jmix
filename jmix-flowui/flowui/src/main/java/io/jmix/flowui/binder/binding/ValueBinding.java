package io.jmix.flowui.binder.binding;

import com.vaadin.flow.component.HasValue;
import io.jmix.flowui.binder.data.ValueSource;

public interface ValueBinding<V> extends JmixBinding {

    ValueSource<V> getValueSource();

    HasValue<?, V> getComponent();

    void activate();
}
