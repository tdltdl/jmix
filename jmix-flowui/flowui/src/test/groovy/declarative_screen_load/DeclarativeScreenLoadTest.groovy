package declarative_screen_load

import com.vaadin.flow.component.HasElement
import declarative_screen_load.screen.CustomerView
import io.jmix.core.DataManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import test_support.entity.sales.Customer
import test_support.spec.FlowuiTestSpecification

@SpringBootTest
@Deprecated
class DeclarativeScreenLoadTest extends FlowuiTestSpecification {

    @Autowired
    DataManager dataManager

    @Autowired
    JdbcTemplate jdbcTemplate;

    void setup() {
        registerScreenBasePackages("declarative_screen_load.screen")

        def customer = dataManager.create(Customer)
        dataManager.save(customer)
    }

    void cleanup() {
        jdbcTemplate.execute("delete from TEST_CUSTOMER")
    }

    def "Navigate to CustomerView"() {
        when: "Open the CustomerView"
        List<HasElement> activeRouterTargetsChain = getRouterChain(CustomerView.class)

        then: "Data container should be injected"
        activeRouterTargetsChain.get(0) instanceof CustomerView

        CustomerView projectView = (CustomerView) activeRouterTargetsChain.get(0)

        projectView.customersDc != null
    }

    def "Click button in CustomerView"() {
        when: "Open the CustomerView"
        List<HasElement> activeRouterTargetsChain = getRouterChain(CustomerView.class)

        then: "The button will be clickable"
        activeRouterTargetsChain.get(0) instanceof CustomerView

        CustomerView projectView = (CustomerView) activeRouterTargetsChain.get(0)

        projectView.doBtn.click()
    }
}
