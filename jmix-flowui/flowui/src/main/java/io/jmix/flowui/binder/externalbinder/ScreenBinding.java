package io.jmix.flowui.binder.externalbinder;

import com.vaadin.flow.component.HasValue;
import io.jmix.flowui.model.InstanceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("flowui_ScreenBinding")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ScreenBinding {

    @Autowired
    protected EntityBinder valueBinder;

    protected Map<HasValue, Binding> bindings = new HashMap<>(4);

    public void add(InstanceContainer container, BinderContext context) {
        Binding binding = valueBinder.bind(container, context);
        add(binding);
    }

    public void add(Binding binding) {
        bindings.put(binding.getComponent(), binding);
    }

    public void addAll(InstanceContainer container, BinderContext... contexts) {
        List<Binding> bindings = valueBinder.bindAll(container, contexts);
        addAll(bindings);
    }

    public void addAll(List<Binding> bindings) {
        for (Binding binding : bindings) {
            this.bindings.put(binding.getComponent(), binding);
        }
    }

    @Nullable
    public Binding getBinding(HasValue component) {
        return bindings.get(component);
    }

    public boolean validate() {
        if (!bindings.isEmpty()) {
            for (Binding binding : bindings.values()) {
                if (!binding.validate()) {
                    return false;
                }
            }
        }
        return true;
    }
}
