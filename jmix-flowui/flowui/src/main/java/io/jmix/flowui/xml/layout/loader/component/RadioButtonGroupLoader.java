/*
 * Copyright 2022 Haulmont.
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

package io.jmix.flowui.xml.layout.loader.component;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import io.jmix.flowui.xml.layout.loader.AbstractComponentLoader;

//TODO: kremnevda, replace RadioButtonGroup to JmixRadioButtonGroup 26.04.2022
public class RadioButtonGroupLoader extends AbstractComponentLoader<RadioButtonGroup<?>> {

    @Override
    protected RadioButtonGroup<?> createComponent() {
        return factory.create(RadioButtonGroup.class);
    }

    @Override
    public void loadComponent() {
        loadString(element, "label", resultComponent::setLabel);
        loadBoolean(element, "invalid", resultComponent::setInvalid);
        loadBoolean(element, "required", resultComponent::setRequired);
        loadResourceString("errorMessage", context.getMessageGroup(), resultComponent::setErrorMessage);

        componentLoader().loadEnabled(resultComponent, element);
        componentLoader().loadClassName(resultComponent, element);
        componentLoader().loadThemeName(resultComponent, element);
        componentLoader().loadHelperText(resultComponent, element);
        componentLoader().loadSizeAttributes(resultComponent, element);
        componentLoader().loadValueAndElementAttributes(resultComponent, element);
    }
}
