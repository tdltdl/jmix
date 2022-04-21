package io.jmix.flowui.binder.externalbinder;

import io.jmix.core.MetadataTools;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.flowui.component.validation.bean.BeanPropertyValidator;
import io.jmix.flowui.model.InstanceContainer;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.util.ArrayList;
import java.util.List;

@Component("flowui_ValueBinder")
public class EntityBinder implements ApplicationContextAware {

    @Autowired
    protected Validator validator;

    @Autowired
    protected MetadataTools metadataTools;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <E> List<Binding> bindAll(InstanceContainer<E> container, BinderContext... contexts) {
        List<Binding> bindings = new ArrayList<>(contexts.length);
        for (BinderContext context : contexts) {
            bindings.add(bind(container, context));
        }
        return bindings;
    }

    public <E, M, P> Binding<M, P> bind(InstanceContainer<E> container, BinderContext<M, P> context) {
        Binding<M, P> binding = new BindingImpl<>(container, context.getProperty(), context.getComponent());
        ((ApplicationContextAware) binding).setApplicationContext(applicationContext);

        if (context.getToPresentationConverter() != null) {
            binding.setToPresentationConverter(context.getToPresentationConverter());
        }
        if (context.getToModelConverter() != null) {
            binding.setToModelConverter(context.getToModelConverter());
        }

        MetaPropertyPath mpp = container.getEntityMetaClass().getPropertyPath(context.getProperty());
        initValidators(binding, context, mpp);

        // todo rp ext-binder init default required

        // if property is ENUM try to set options
        // if (context.getComponent() instanceof HasDataListView) { //... }

        binding.bind();

        return binding;
    }

    protected <M, P> void initValidators(Binding<M, P> binding, BinderContext<M, P> context, MetaPropertyPath mpp) {
        MetaProperty metaProperty = mpp.getMetaProperty();

        MetaClass propertyEnclosingMetaClass = metadataTools.getPropertyEnclosingMetaClass(mpp);
        Class enclosingJavaClass = propertyEnclosingMetaClass.getJavaClass();

        if (enclosingJavaClass != KeyValueEntity.class) {
            BeanDescriptor beanDescriptor = validator.getConstraintsForClass(enclosingJavaClass);

            if (beanDescriptor.isBeanConstrained()) {
                binding.addValidator(applicationContext.getBean(BeanPropertyValidator.class, enclosingJavaClass, metaProperty.getName()));
            }
        }

        if (CollectionUtils.isNotEmpty(context.getValidators())) {
            context.getValidators()
                    .forEach(binding::addValidator);
        }
    }
}
