package io.jmix.flowui.binder.externalbinder.converters;

import io.jmix.core.common.util.Preconditions;
import io.jmix.core.metamodel.datatype.Datatype;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.binder.data.ConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.text.ParseException;

@Component("flowiu_DatatypeToStringConverter")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DatatypeToStringConverter<M> implements ValueConverter {

    @Autowired
    protected CurrentAuthentication authentication;

    protected Datatype<M> datatype;

    public DatatypeToStringConverter(Datatype<M> datatype) {
        Preconditions.checkNotNullArgument(datatype);

        this.datatype = datatype;
    }

    public String convertToPresentation(M modelValue) throws ConversionException {
        return datatype.format(modelValue, authentication.getLocale());
    }

    public M convertToModel(@Nullable String presentationValue) throws ConversionException {
        try {
            return datatype.parse(presentationValue, authentication.getLocale());
        } catch (ParseException e) {
            throw new ConversionException(getConversionErrorMessage(), e);
        }
    }

    public String getConversionErrorMessage() {
        return "Conversion error message"; // todo rp ext-binder use messages
    }
}
