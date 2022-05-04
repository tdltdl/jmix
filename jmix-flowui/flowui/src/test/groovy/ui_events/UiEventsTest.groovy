package ui_events

import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.UI
import io.jmix.flowui.ScreenNavigators
import io.jmix.flowui.UiEventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import test_support.spec.FlowuiTestSpecification
import ui_events.screen.UiEventsTestScreen

@SpringBootTest
class UiEventsTest extends FlowuiTestSpecification {

    @Autowired
    ScreenNavigators screenNavigators

    @Autowired
    UiEventPublisher uiEventPublisher

    void setup() {
        registerScreenBasePackages("ui_events.screen")
    }

    def "screen receives an event"() {
        screenNavigators.screen(UiEventsTestScreen)
                .navigate()

        when: "Fire application event"
        def event = new TestUiEvent(this, "eventMessage")
        uiEventPublisher.publishEvent(event)

        then: "Screen's application listener receives the event"

        List<HasElement> activeRouterTargetsChain = UI.getCurrent().getInternals().getActiveRouterTargetsChain()
        activeRouterTargetsChain.get(0) instanceof UiEventsTestScreen

        ((UiEventsTestScreen) activeRouterTargetsChain.get(0)).eventMessage == event.getMessage()
    }
}
