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

package io.jmix.core.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.net.URISyntaxException;

@Converter(autoApply = true)
public class UriConverter implements AttributeConverter<URI, String> {

    private static final Logger log = LoggerFactory.getLogger(UriConverter.class);

    @Override
    public String convertToDatabaseColumn(URI attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public URI convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : new URI(dbData);
        } catch (URISyntaxException e) {
            log.warn("Cannot convert " + dbData + " to URI", e);
            return null;
        }
    }
}
