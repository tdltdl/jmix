package io.jmix.flowui.binder.externalbinder;

import com.vaadin.flow.component.HasValue;
import io.jmix.flowui.component.validation.Validator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public interface Binding<M, P> {

    HasValue getComponent();

    void setValidators(List<Validator<M>> validators);

    void addValidator(Validator<M> validator);

    void removeValidator(Validator<M> validator);

    boolean validate(); // todo rp ext-binder throws ValidationException or return ValidationErrors

    void setToPresentationConverter(@Nullable Function<M, P> toPresentationConverter);

    void setToModelConverter(@Nullable Function<P, M> toModelConverter);

    void bind();

    void unbind();
}
