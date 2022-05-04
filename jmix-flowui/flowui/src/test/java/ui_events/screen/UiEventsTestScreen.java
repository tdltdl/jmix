package ui_events.screen;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.Screen;
import io.jmix.flowui.screen.UiController;
import org.springframework.context.event.EventListener;
import ui_events.TestUiEvent;

@Route("ui-events-test-screen")
@UiController
public class UiEventsTestScreen extends Screen {

    public String eventMessage = "noop";

    @EventListener
    public void testUiEventHandler(TestUiEvent event) {
        eventMessage = event.getMessage();
    }
}
