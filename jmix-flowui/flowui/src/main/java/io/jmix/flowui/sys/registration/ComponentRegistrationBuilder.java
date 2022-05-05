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
import io.jmix.flowui.xml.layout.ComponentLoader;

/**
 * Builds registration object that is used for adding new UI component loader or overriding UI components in the
 * framework.
 * <p>
 * For instance:
 * <pre>
 * &#64;Configuration
 * public class ComponentConfiguration {
 *
 *     &#64;Bean
 *     public ComponentRegistration extButton() {
 *         return ComponentRegistrationBuilder.create(ExtJmixButton.class)
 *                 .withComponentClass(ExtWebButton.class)
 *                 .withComponentLoaderClass(ExtButtonLoader.class)
 *                 .build();
 *     }
 * }
 * </pre>
 *
 * @see ComponentRegistration
 * @see CustomComponentsRegistry
 */
public class ComponentRegistrationBuilder {

    protected Class<? extends Component> componentType;
    protected String tag;
    protected Class<? extends Component> overriddenComponentType;
    protected Class<? extends ComponentLoader> componentClassLoader;

    /**
     * @param componentType component name
     */
    public ComponentRegistrationBuilder(Class<? extends Component> componentType) {
        this.componentType = componentType;
    }

    /**
     * @param componentType component type
     * @return builder instance
     */
    public static ComponentRegistrationBuilder create(Class<? extends Component> componentType) {
        return new ComponentRegistrationBuilder(componentType);
    }

    // todo rp javadoc
    /**
     * Sets component class.
     *
     * @param componentType component class
     * @return builder instance
     */
    public ComponentRegistrationBuilder overrideComponent(Class<? extends Component> componentType) {
        overriddenComponentType = componentType;
        return this;
    }

    /**
     * Sets component loader class.
     *
     * @param tag                  component name in the screen descriptor
     * @param componentClassLoader component loader class
     * @return builder instance
     */
    public ComponentRegistrationBuilder withComponentLoader(String tag,
                                                            Class<? extends ComponentLoader> componentClassLoader) {
        this.tag = tag;
        this.componentClassLoader = componentClassLoader;
        return this;
    }

    /**
     * @return instance of registration object
     */
    public ComponentRegistration build() {
        return new ComponentRegistrationImpl(componentType, tag, overriddenComponentType, componentClassLoader);
    }
}
