package io.jmix.flowui.binder.externalbinder;

import com.vaadin.flow.component.HasValue;
import io.jmix.core.common.util.Preconditions;
import io.jmix.flowui.component.validation.Validator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BinderContext<M, P> {

    protected HasValue component;
    protected String property;

    protected List<Validator<M>> validators;

    protected Function<M, P> toPresentationConverter;
    protected Function<P, M> toModelConverter;

    public BinderContext(HasValue component, String property) {
        Preconditions.checkNotNullArgument(component);
        Preconditions.checkNotEmptyString(property);

        this.component = component;
        this.property = property;
    }

    public static <M, P> BinderContext<M, P> create(HasValue<?, P> component, String property) {
        return new BinderContext<>(component, property);
    }

    public BinderContext<M, P> withValidator(Validator<M> modelValidator) {
        if (validators == null) {
            validators = new ArrayList<>(2);
        }
        validators.add(modelValidator);
        return this;
    }

    public BinderContext<M, P> withValidators(Validator<M>... modelValidators) {
        if (this.validators == null) {
            this.validators = new ArrayList<>(2);
        }
        this.validators.addAll(Arrays.asList(modelValidators));
        return this;
    }

    public BinderContext<M, P> withToPresentationConverter(@Nullable Function<M, P> converter) {
        toPresentationConverter = converter;
        return this;
    }

    public BinderContext<M, P> withToModelConverter(@Nullable Function<P, M> converter) {
        toModelConverter = converter;
        return this;
    }

    public HasValue getComponent() {
        return component;
    }

    public String getProperty() {
        return property;
    }

    public List<Validator<M>> getValidators() {
        return validators;
    }

    @Nullable
    public Function<M, P> getToPresentationConverter() {
        return toPresentationConverter;
    }

    @Nullable
    public Function<P, M> getToModelConverter() {
        return toModelConverter;
    }
}
