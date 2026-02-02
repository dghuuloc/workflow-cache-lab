package com.porters.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


@Component
public class XmlToJsonConverter {

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public JsonNode convert(String xml) {
        try {
            JsonNode xmlTree = xmlMapper.readTree(xml.getBytes());
            return jsonMapper.readTree(jsonMapper.writeValueAsBytes(xmlTree));
        } catch (Exception e) {
            throw new RuntimeException("XML to JSON conversion failed", e);
        }
    }

}
