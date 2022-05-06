package io.jmix.flowui.sys;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import io.jmix.core.Messages;
import io.jmix.flowui.screen.UiControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component("flowui_ScreensHelper")
public class ScreensHelper {

    protected Messages messages;

    @Autowired
    public ScreensHelper(Messages messages) {
        this.messages = messages;
    }

    /**
     * Gets localized page title from the component.
     *
     * @param component component to get localized page title
     * @return localized page title or message key if not found
     * @see PageTitle
     * @see HasDynamicTitle
     */
    public String getLocalizedPageTitle(Component component) {
        String titleKey = UiControllerUtils.getPageTitle(component);

        if (Strings.isNullOrEmpty(titleKey)) {
            return "";
        }

        if (titleKey.contains("/")) {
            return messages.getMessage(titleKey);
        }

        return messages.getMessage(component.getClass(), titleKey);
    }
}
