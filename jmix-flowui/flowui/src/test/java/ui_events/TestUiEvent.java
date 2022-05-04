package ui_events;

import org.springframework.context.ApplicationEvent;

public class TestUiEvent extends ApplicationEvent {

    protected String message;

    public TestUiEvent(Object source, String message) {
        super(source);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
