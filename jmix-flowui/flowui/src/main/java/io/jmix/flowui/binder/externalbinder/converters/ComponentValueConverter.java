package io.jmix.flowui.binder.externalbinder.converters;


import io.jmix.flowui.binder.data.ConversionException;

import javax.annotation.Nullable;

public interface ComponentValueConverter<M, P> extends ValueConverter {

    P convertToPresentation(M modelValue) throws ConversionException;

    M convertToModel(@Nullable P presentationValue) throws ConversionException;

    Class<M> getModelType();

    Class<P> getPresentationType();
}
