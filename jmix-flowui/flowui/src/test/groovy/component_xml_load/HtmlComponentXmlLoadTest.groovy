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

package component_xml_load

import com.vaadin.flow.component.HasText
import com.vaadin.flow.component.HtmlContainer
import com.vaadin.flow.component.html.AnchorTarget
import com.vaadin.flow.component.html.OrderedList
import component_xml_load.screen.html.HtmlView
import io.jmix.core.DataManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import test_support.spec.FlowuiTestSpecification

@SpringBootTest
class HtmlComponentXmlLoadTest extends FlowuiTestSpecification {

    @Autowired
    DataManager dataManager

    @Autowired
    JdbcTemplate jdbcTemplate

    void setup() {
        registerScreenBasePackages("component_xml_load.screen.html")
    }

    def "Load #container from XML"() {
        when: "Open the HtmlView"
        def activeRouterTargetsChain = getRouterChain(HtmlView.class)

        then: "Html container attributes will be loaded"
        activeRouterTargetsChain.get(0) instanceof HtmlView

        def htmlView = activeRouterTargetsChain.get(0) as HtmlView
        def htmlContainer = htmlView."${container}Id" as HtmlContainer

        verifyAll(htmlContainer) {
            id.get() == "${container}Id"
            classNames.containsAll(["cssClassName1", "cssClassName2"])
            enabled
            height == "50px"
            width == "100px"
            maxHeight == "55px"
            maxWidth == "120px"
            minHeight == "40px"
            minWidth == "80px"
            getElement().getThemeList().containsAll(["badge", "primary", "small"])
            title.get() == "${container}Title"
            visible
            whiteSpace == HasText.WhiteSpace.PRE
            (getChildren().findAny().get() as HtmlContainer).getText() == "${container}Child"
        }

        where:
        container << ["article", "aside", "descriptionList", "term", "description", "div", "emphasis", "footer", "h1",
                      "h2", "h3", "h4", "h5", "h6", "header", "listItem", "p", "pre", "section", "span",
                      "unorderedList", "anchor", "htmlObject", "image", "main", "nav", "orderedList"]
    }

    def "Load additional anchor attributes from XML"() {
        when: "Open the HtmlView"
        def activeRouterTargetsChain = getRouterChain(HtmlView.class)

        then: "Additional anchor attributes will be loaded"
        activeRouterTargetsChain.get(0) instanceof HtmlView

        def htmlView = activeRouterTargetsChain.get(0) as HtmlView
        def anchor = htmlView.anchorId

        verifyAll(anchor) {
            href == "anchorHref"
            target.get() == AnchorTarget.PARENT.value
        }
    }

    def "Load additional htmlObject attributes from XML"() {
        when: "Open the HtmlView"
        def activeRouterTargetsChain = getRouterChain(HtmlView.class)

        then: "Additional htmlObject attributes will be loaded"
        activeRouterTargetsChain.get(0) instanceof HtmlView

        def htmlView = activeRouterTargetsChain.get(0) as HtmlView
        def htmlObject = htmlView.htmlObjectId

        verifyAll(htmlObject) {
            data == "data"
            type.get() == "type"
        }
    }

    def "Load additional #container attributes from XML"() {
        when: "Open the HtmlView"
        def activeRouterTargetsChain = getRouterChain(HtmlView.class)

        then: "Additional #container attributes will be loaded"
        activeRouterTargetsChain.get(0) instanceof HtmlView

        def htmlView = activeRouterTargetsChain.get(0) as HtmlView

        htmlView."${container}Id".ariaLabel.get() == "ariaLabelString"

        where:
        container << ["main", "nav"]
    }

    def "Load additional orderedList attributes from XML"() {
        when: "Open the HtmlView"
        def activeRouterTargetsChain = getRouterChain(HtmlView.class)

        then: "Additional orderedList attributes will be loaded"
        activeRouterTargetsChain.get(0) instanceof HtmlView

        def htmlView = activeRouterTargetsChain.get(0) as HtmlView

        htmlView.orderedListId.type == OrderedList.NumberingType.UPPERCASE_LETTER
    }

}
