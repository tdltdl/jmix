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

package component_xml_load.screen.html;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.ComponentId;
import io.jmix.flowui.screen.Screen;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = "test-project-view")
@UiController("HtmlView")
@UiDescriptor("html-view.xml")
public class HtmlView extends Screen {

    @ComponentId
    public Article articleId;

    @ComponentId
    public Aside asideId;

    @ComponentId
    public DescriptionList descriptionListId;

    @ComponentId
    public DescriptionList.Term termId;

    @ComponentId
    public DescriptionList.Description descriptionId;

    @ComponentId
    public Div divId;

    @ComponentId
    public Emphasis emphasisId;

    @ComponentId
    public Footer footerId;

    @ComponentId
    public H1 h1Id;

    @ComponentId
    public H2 h2Id;

    @ComponentId
    public H3 h3Id;

    @ComponentId
    public H4 h4Id;

    @ComponentId
    public H5 h5Id;

    @ComponentId
    public H6 h6Id;

    @ComponentId
    public Header headerId;

    @ComponentId
    public ListItem listItemId;

    @ComponentId
    public Paragraph pId;

    @ComponentId
    public Pre preId;

    @ComponentId
    public Section sectionId;

    @ComponentId
    public Span spanId;

    @ComponentId
    public UnorderedList unorderedListId;

    @ComponentId
    public Anchor anchorId;

    @ComponentId
    public HtmlObject htmlObjectId;

    @ComponentId
    public Image imageId;

    @ComponentId
    public Main mainId;

    @ComponentId
    public Nav navId;

    @ComponentId
    public OrderedList orderedListId;
}
