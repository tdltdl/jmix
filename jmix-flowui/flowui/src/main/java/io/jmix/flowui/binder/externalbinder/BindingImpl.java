package io.jmix.flowui.binder.externalbinder;

import com.google.common.base.Joiner;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import io.jmix.core.MetadataTools;
import io.jmix.core.annotation.Internal;
import io.jmix.core.common.event.Subscription;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.datatype.Datatype;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.flowui.binder.data.ConversionException;
import io.jmix.flowui.binder.externalbinder.converters.DatatypeToStringConverter;
import io.jmix.flowui.binder.externalbinder.converters.ValueConverter;
import io.jmix.flowui.component.validation.Validator;
import io.jmix.flowui.model.DataComponents;
import io.jmix.flowui.model.InstanceContainer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Internal
public class BindingImpl<M, P> implements Binding<M, P>, ApplicationContextAware {

    protected static final int VALIDATORS_LIST_INITIAL_CAPACITY = 2;

    protected HasValue<?, P> component;
    protected InstanceContainer<?> container;
    protected String property;

    protected List<Validator<M>> validators;
    protected ValueConverter defaultConverter;
    protected Function<M, P> toPresentationConverter;
    protected Function<P, M> toModelConverter;

    protected Registration valueChangeRegistration;

    protected List<Subscription> containerSubscriptions = new ArrayList<>();

    protected ApplicationContext applicationContext;

    public BindingImpl(InstanceContainer container, String property, HasValue component) {
        this.container = container;
        this.property = property;
        this.component = component;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        initDefaultConverter();
    }

    @Override
    public HasValue getComponent() {
        return component;
    }

    @Override
    public void bind() {
        initContainerListeners();
        valueChangeRegistration = component.addValueChangeListener(this::onComponentValueChange);
    }

    @Override
    public void unbind() {
        if (CollectionUtils.isNotEmpty(containerSubscriptions)) {
            containerSubscriptions.forEach(Subscription::remove);
            containerSubscriptions.clear();
        }
        if (valueChangeRegistration != null) {
            valueChangeRegistration.remove();
        }
    }

    @Override
    public void setValidators(List<Validator<M>> validators) {
        this.validators = new ArrayList<>(validators);
    }

    @Override
    public void addValidator(Validator<M> validator) {
        if (validators == null) {
            validators = new ArrayList<>(VALIDATORS_LIST_INITIAL_CAPACITY);
        }

        if (!validators.contains(validator)) {
            validators.add(validator);
        }
    }

    @Override
    public void removeValidator(Validator<M> validator) {
        if (CollectionUtils.isEmpty(validators)) {
            return;
        }
        validators.remove(validator);
    }

    @Override
    public boolean validate() {
        if (CollectionUtils.isNotEmpty(validators)) {
            try {
                M value = convertToModel(component.getValue());

                // todo rp ext-binder handle ValidationException
                for (Validator<M> validator : validators) {
                    validator.accept(value);
                }
            } catch (ConversionException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setToPresentationConverter(@Nullable Function<M, P> toPresentationConverter) {
        this.toPresentationConverter = toPresentationConverter;
    }

    @Override
    public void setToModelConverter(@Nullable Function<P, M> toModelConverter) {
        this.toModelConverter = toModelConverter;
    }

    protected void initContainerListeners() {
        if (applicationContext == null) {
            return;
        }

        MetaClass metaClass = container.getEntityMetaClass();

        MetadataTools metadataTools = applicationContext.getBean(MetadataTools.class);
        MetaPropertyPath metaPropertyPath = metadataTools.resolveMetaPropertyPath(metaClass, property);


        containerSubscriptions.add(this.container.addItemChangeListener(this::onContainerItemChange));
        containerSubscriptions.add(this.container.addItemPropertyChangeListener(this::onContainerPropertyChange));

        InstanceContainer<?> parentCont = container;

        for (int i = 1; i < metaPropertyPath.length(); i++) {
            MetaPropertyPath intermediatePath = new MetaPropertyPath(metaPropertyPath.getMetaClass(),
                    Arrays.copyOf(metaPropertyPath.getMetaProperties(), i));
            String pathToTarget = Joiner.on('.').join(
                    Arrays.copyOfRange(metaPropertyPath.getPath(), i, metaPropertyPath.length()));

            DataComponents dataComponents = applicationContext.getBean(DataComponents.class);
            @SuppressWarnings("unchecked")
            InstanceContainer<Object> propertyCont = dataComponents.createInstanceContainer(intermediatePath.getRangeJavaClass());

            containerSubscriptions.add(
                    parentCont.addItemChangeListener(event -> {
                        // todo rp ext-binder state?
                   /* if (event.getItem() != null) {
                        setState(BindingState.ACTIVE);
                    } else {
                        setState(BindingState.INACTIVE);
                    }*/
                        propertyCont.setItem(event.getItem() != null
                                ? EntityValues.getValueEx(event.getItem(), intermediatePath.getMetaProperty().getName())
                                : null);
                    }));

            containerSubscriptions.add(
                    parentCont.addItemPropertyChangeListener(event -> {
                        if (Objects.equals(event.getProperty(), intermediatePath.getMetaProperty().getName())) {
                            Object entity = event.getValue();
//                            Object prevEntity = event.getPrevValue();
                            propertyCont.setItem(entity);

                            M value = entity != null ? EntityValues.getValueEx(entity, pathToTarget) : null;
                            setValueToComponent(value);

                            // todo rp ext-binder
                        /*V prevValue = prevEntity != null ? EntityValues.getValueEx(prevEntity, pathToTarget) : null;
                        V value = entity != null ? EntityValues.getValueEx(entity, pathToTarget) : null;
                        events.publish(ValueChangeEvent.class,
                                new ValueChangeEvent<>(this, prevValue, value));*/
                        }
                    }));

            if (i == metaPropertyPath.length() - 1) {
                containerSubscriptions.add(
                        propertyCont.addItemPropertyChangeListener(event -> {
                            if (Objects.equals(event.getProperty(), metaPropertyPath.getMetaProperty().getName())) {
                                setValueToComponent((M) event.getValue());
                                // todo rp ext-binder
                            /*events.publish(ValueChangeEvent.class,
                                    new ValueChangeEvent<>(this, (V) event.getPrevValue(), (V) event.getValue()));*/
                            }
                        }));
            }

            parentCont = propertyCont;
        }
    }

    protected void onContainerItemChange(InstanceContainer.ItemChangeEvent event) {
        setValueToComponent(EntityValues.getValueEx(event.getItem(), property));
    }

    protected void onContainerPropertyChange(InstanceContainer.ItemPropertyChangeEvent event) {
        if (event.getProperty().equals(property)) {
            setValueToComponent((M) event.getValue());
        }
    }

    protected void onComponentValueChange(HasValue.ValueChangeEvent<P> event) {


        M value;
        try {
            value = convertToModel(event.getValue());

        } catch (ConversionException e) {
            setValidationError(e.getLocalizedMessage());
            return;
        }

        Object item = container.getItemOrNull();
        if (item != null) {
            EntityValues.setValueEx(item, property, value);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected M convertToModel(@Nullable P componentRawValue)  throws ConversionException {
        if (toModelConverter != null) {
            return toModelConverter.apply(componentRawValue);
        } else if (defaultConverter instanceof DatatypeToStringConverter) {
            return ((DatatypeToStringConverter<M>) defaultConverter).convertToModel((String) componentRawValue);
        }
        return (M) componentRawValue;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected P convertToPresentation(@Nullable M modelValue) throws ConversionException {
        if (modelValue == null) {
            return component.getEmptyValue();
        }

        P value;
        if (toPresentationConverter != null) {
            value = toPresentationConverter.apply(modelValue);
        } else if (defaultConverter instanceof DatatypeToStringConverter) {
            value = (P) ((DatatypeToStringConverter<M>) defaultConverter).convertToPresentation(modelValue);
        } else {
            value = (P) modelValue;
        }

        // todo rp ext-binder ConversionException ?

        return value == null ? component.getEmptyValue() : value;
    }

    protected void setValueToComponent(@Nullable M modelValue) {
        P value = convertToPresentation(modelValue);

        component.setValue(value);
    }

    protected void setValidationError(@Nullable String message) {
        if (component instanceof HasValidation) {
            ((HasValidation) component).setInvalid(!Strings.isEmpty(message));
            ((HasValidation) component).setErrorMessage(message);
        }
    }

    protected void initDefaultConverter() {
        if (applicationContext == null) {
            return;
        }

        MetaPropertyPath mpp = container.getEntityMetaClass().getPropertyPath(property);
        MetaProperty metaProperty = mpp.getMetaProperty();

        if (metaProperty.getRange().isDatatype()) {
            Datatype datatype = metaProperty.getRange().asDatatype();

            if (component instanceof TextField) {
                defaultConverter = applicationContext.getBean(DatatypeToStringConverter.class, datatype);
            }
        }
    }
}
