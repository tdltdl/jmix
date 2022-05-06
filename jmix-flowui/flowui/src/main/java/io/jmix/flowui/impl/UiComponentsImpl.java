package io.jmix.flowui.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.di.Instantiator;
import io.jmix.core.metamodel.datatype.DatatypeRegistry;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.SupportsDatatype;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.component.datetimepicker.TypedDateTimePicker;
import io.jmix.flowui.component.grid.JmixGrid;
import io.jmix.flowui.component.grid.JmixGridContextMenu;
import io.jmix.flowui.component.grid.JmixTreeGrid;
import io.jmix.flowui.component.textfield.JmixBigDecimalField;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.component.timepicker.TypedTimePicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component("flowui_UiComponents")
public class UiComponentsImpl implements UiComponents {
    private static final Logger log = LoggerFactory.getLogger(UiComponentsImpl.class);

    protected DatatypeRegistry datatypeRegistry;

    public UiComponentsImpl(DatatypeRegistry datatypeRegistry) {
        this.datatypeRegistry = datatypeRegistry;
    }

    protected Map<Class<? extends Component>, Class<? extends Component>> classes = new ConcurrentHashMap<>();
    protected Map<Class<? extends Component>, Class<? extends Component>> replacedClasses = new ConcurrentHashMap<>();

    {
        classes.put(Grid.class, JmixGrid.class);
        classes.put(Button.class, JmixButton.class);
        classes.put(TextField.class, TypedTextField.class);
        classes.put(BigDecimalField.class, JmixBigDecimalField.class);
        classes.put(ComboBox.class, JmixComboBox.class);
        classes.put(TimePicker.class, TypedTimePicker.class);
        classes.put(DateTimePicker.class, TypedDateTimePicker.class);
        classes.put(DatePicker.class, TypedDatePicker.class);
        classes.put(GridContextMenu.class, JmixGridContextMenu.class);
        classes.put(TreeGrid.class, JmixTreeGrid.class);

        replacedClasses.put(JmixGrid.class, Grid.class);
        replacedClasses.put(JmixTreeGrid.class, TreeGrid.class);
        replacedClasses.put(JmixGridContextMenu.class, GridContextMenu.class);
        replacedClasses.put(JmixButton.class, Button.class);
        replacedClasses.put(JmixComboBox.class, ComboBox.class);
        replacedClasses.put(TypedTextField.class, TextField.class);
        replacedClasses.put(TypedTimePicker.class, TimePicker.class);
        replacedClasses.put(TypedDateTimePicker.class, DateTimePicker.class);
        replacedClasses.put(TypedDatePicker.class, DatePicker.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T create(Class<T> type) {
        if (classes.containsKey(type)) {
            Class<? extends Component> actualType = classes.get(type);

            log.trace("Creating {} as it overrides {} component", actualType.getName(), type.getName());

            return (T) Instantiator.get(UI.getCurrent()).getOrCreate(actualType);
        }

        if (replacedClasses.containsKey(type)) {
            Class<? extends Component> overridden = replacedClasses.get(type);
            Class<? extends Component> actualType = classes.get(overridden);

            log.trace("Creating {} as it replaces overriding of {} by {} component",
                    actualType.getName(), overridden.getName(), type.getName());

            return (T) Instantiator.get(UI.getCurrent()).getOrCreate(actualType);
        }

        log.trace("Creating {} component", type.getName());

        return Instantiator.get(UI.getCurrent()).getOrCreate(type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T extends Component> T create(ParameterizedTypeReference<T> typeReference) {
        ParameterizedType type = (ParameterizedType) typeReference.getType();
        T component = create((Class<T>) type.getRawType());
        if (component instanceof SupportsDatatype) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                Class actualTypeArgument = (Class) actualTypeArguments[0];

                ((SupportsDatatype<?>) component).setDatatype(datatypeRegistry.find(actualTypeArgument));
            }
        }
        return component;
    }

    public void replaceComponent(Class<? extends Component> component, Class<? extends Component> replacedComponent) {
        if (replacedClasses.containsKey(replacedComponent)) {
            replacedComponent = replacedClasses.get(replacedComponent);
        }
        classes.put(replacedComponent, component);
        replacedClasses.put(component, replacedComponent);
    }
}
