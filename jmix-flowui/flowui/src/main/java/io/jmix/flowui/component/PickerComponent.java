package io.jmix.flowui.component;


import io.jmix.flowui.binder.data.SupportsValueSource;
import io.jmix.flowui.component.HasActions;
import io.jmix.flowui.component.SupportsUserAction;

public interface PickerComponent<V> extends SupportsValueSource<V>, HasActions, SupportsUserAction<V> {
}
