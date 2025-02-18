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

package io.jmix.flowui.kit.action;

import com.vaadin.flow.component.Component;

import java.util.EventObject;

/**
 * Event sent when the {@link Action} is performed.
 */
public class ActionPerformedEvent extends EventObject {

    protected final Component component;

    /**
     * Constructs an ActionPerformedEvent.
     *
     * @param source    the action on which the Event initially occurred
     * @param component {@link Component} that triggered action
     */
    public ActionPerformedEvent(Action source, Component component) {
        super(source);
        this.component = component;
    }

    /**
     * @return {@link Component} that triggered this action.
     */
    public Component getComponent() {
        return component;
    }

    @Override
    public Action getSource() {
        return (Action) super.getSource();
    }
}
