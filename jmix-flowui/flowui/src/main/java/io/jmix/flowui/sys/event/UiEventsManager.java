package io.jmix.flowui.sys.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import io.jmix.core.annotation.Internal;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.sys.UiControllerDependencyInjector;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.core.ResolvableType;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @see UiEventPublisher
 * @see UiControllerDependencyInjector
 * @see UiEventListenerMethodAdapter
 */
@Internal
@SpringComponent("flowui_UiEventsMulticaster")
@SessionScope
public class UiEventsManager {

    protected ApplicationContext applicationContext;

    protected Set<ComponentListeners> listeners = ConcurrentHashMap.newKeySet();

    @Autowired
    public UiEventsManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Adds application listener that should be invoked when corresponding application event is fired.
     *
     * @param component the component that contains listener definition.
     * @param listener  application listener
     */
    public void addApplicationListener(Component component, ApplicationListener<?> listener) {
        ComponentListeners componentListeners = listeners.stream()
                .filter(item -> item.isSameComponent(component))
                .findFirst()
                .orElseGet(() -> {
                    ComponentListeners newItem = new ComponentListeners(component, item -> listeners.remove(item));
                    newItem.addListener(listener);
                    listeners.add(newItem);
                    return newItem;
                });

        if (!componentListeners.contains(listener)) {
            componentListeners.addListener(listener);
        }
    }

    /**
     * Removes application listener.
     *
     * @param listener listener to remove
     */
    public void removeApplicationListener(ApplicationListener<?> listener) {
        listeners.stream()
                .filter(componentListeners -> componentListeners.contains(listener))
                .findFirst().ifPresent(componentListeners -> {
                    componentListeners.removeListener(listener);

                    if (componentListeners.isEmpty()) {
                        listeners.remove(componentListeners);
                    }
                });
    }

    /**
     * Removes all listeners that have definition in the given component.
     *
     * @param component component
     */
    public void removeApplicationListeners(Component component) {
        listeners.stream().filter(componentListeners -> componentListeners.isSameComponent(component))
                .findFirst()
                .ifPresent(componentListeners -> listeners.remove(componentListeners));
    }

    /**
     * Removes all listeners.
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * Publishes event only in the current UI.
     *
     * @param event application event
     */
    public void publishInCurrentUI(ApplicationEvent event) {
        Object source = event.getSource();
        Class<?> sourceType = (source != null ? source.getClass() : null);

        ResolvableType type = resolveDefaultEventType(event);
        for (ApplicationListener<?> listener : retrieveApplicationListeners(
                Collections.singletonList(UI.getCurrent()), type, sourceType)) {
            invokeListener(listener, event);
        }
    }

    /**
     * Publishes event in all UIs in the current session.
     *
     * @param event application event
     */
    public void publish(ApplicationEvent event) {
        Object source = event.getSource();
        Class<?> sourceType = (source != null ? source.getClass() : null);

        ResolvableType type = resolveDefaultEventType(event);
        for (ApplicationListener<?> listener : retrieveApplicationListeners(type, sourceType)) {
            invokeListener(listener, event);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void invokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        } catch (ClassCastException ex) {
            String msg = ex.getMessage();
            if (msg == null || msg.startsWith(event.getClass().getName())) {
                // Possibly a lambda-defined listener which we could not resolve the generic event type for
                LoggerFactory.getLogger(UiEventsManager.class)
                        .debug("Non-matching event type for listener: {}", listener, ex);
            } else {
                throw ex;
            }
        }
    }

    protected ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }

    protected Collection<ApplicationListener<?>> retrieveApplicationListeners(Collection<UI> uis,
                                                                              ResolvableType eventType,
                                                                              @Nullable Class<?> sourceType) {
        List<ApplicationListener<?>> filtered = new ArrayList<>();

        for (ComponentListeners componentListeners : listeners) {
            UI ui = componentListeners.getComponent().getUI().orElse(null);
            if (ui != null && uis.contains(ui)) {
                filtered.addAll(filterComponentListeners(componentListeners, eventType, sourceType));
            }
        }
        return filtered;
    }

    protected Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType,
                                                                              @Nullable Class<?> sourceType) {
        List<ApplicationListener<?>> filtered = new ArrayList<>();

        for (ComponentListeners componentListeners : listeners) {
            filtered.addAll(filterComponentListeners(componentListeners, eventType, sourceType));
        }
        return filtered;
    }

    protected Collection<ApplicationListener<?>> filterComponentListeners(ComponentListeners componentListeners,
                                                                          ResolvableType eventType,
                                                                          @Nullable Class<?> sourceType) {
        return componentListeners.getListeners()
                .stream()
                .filter(listener -> supportsEvent(listener, eventType, sourceType))
                .collect(Collectors.toList());
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType,
                                    @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
                (GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
        return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
    }

    protected static class ComponentListeners {

        protected Component component;
        protected List<ApplicationListener<?>> listeners;

        public ComponentListeners(Component component, Consumer<ComponentListeners> uiDetachHandler) {
            this.component = component;

            listeners = new ArrayList<>();

            component.getUI().ifPresent(ui ->
                    ui.addDetachListener(event -> uiDetachHandler.accept(ComponentListeners.this)));
        }

        public Component getComponent() {
            return component;
        }

        public boolean isSameComponent(Component component) {
            return this.component.equals(component);
        }

        public void removeListener(ApplicationListener<?> listener) {
            listeners.remove(listener);
        }

        public void addListener(ApplicationListener<?> listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        public boolean contains(ApplicationListener<?> listener) {
            return listeners.contains(listener);
        }

        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        public List<ApplicationListener<?>> getListeners() {
            return listeners;
        }

        public boolean isEnabled() {
            return component.getUI().isPresent();
        }
    }
}
