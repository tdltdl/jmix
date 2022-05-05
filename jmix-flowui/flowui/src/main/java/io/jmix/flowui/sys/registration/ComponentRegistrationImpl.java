/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.flowui.sys.registration;

import com.vaadin.flow.component.Component;
import io.jmix.core.annotation.Internal;
import io.jmix.flowui.xml.layout.ComponentLoader;

import javax.annotation.Nullable;

@Internal
public class ComponentRegistrationImpl implements ComponentRegistration {

    protected final Class<? extends Component> component;
    protected final String tag;
    protected final Class<? extends Component> overriddenComponent;
    protected final Class<? extends ComponentLoader> componentLoader;

    public ComponentRegistrationImpl(Class<? extends Component> component,
                                     @Nullable String tag,
                                     @Nullable Class<? extends Component> overriddenComponent,
                                     @Nullable Class<? extends ComponentLoader> componentLoader) {
        this.component = component;
        this.tag = tag;
        this.overriddenComponent = overriddenComponent;
        this.componentLoader = componentLoader;
    }

    @Override
    public Class<? extends Component> getComponent() {
        return component;
    }

    @Nullable
    @Override
    public String getTag() {
        return tag;
    }

    @Nullable
    @Override
    public Class<? extends Component> getOverriddenComponent() {
        return overriddenComponent;
    }

    @Nullable
    @Override
    public Class<? extends ComponentLoader> getComponentLoader() {
        return componentLoader;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        return component.equals(((ComponentRegistrationImpl) obj).component);
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }

    @Override
    public String toString() {
        String overriddenComponent = getOverriddenComponent() == null ? "null" : getOverriddenComponent().getName();
        String componentLoaderClass = getComponentLoader() == null ? "null" : getComponentLoader().getName();

        return "{\"component\": \"" + component + "\", "
                + "\"tag\": \"" + tag + "\", "
                + "\"overriddenComponent\": \"" + overriddenComponent + "\", "
                + "\"componentLoader\": \"" + componentLoaderClass + "\"}";
    }
}
