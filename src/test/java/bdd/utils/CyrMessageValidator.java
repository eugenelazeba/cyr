package bdd.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.typesafe.config.Config;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


import java.io.IOException;

public class CyrMessageValidator {
    private final JsonSchema jsonSchema;

    public CyrMessageValidator(ResourceLoader resourceLoader, Config config) throws IOException, ProcessingException {
        String jsonSchemaPath = config.getString("jsonSchemaPath");

        Resource schemaFile = resourceLoader.getResource(jsonSchemaPath);

        this.jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(
                new JsonNodeReader().fromInputStream(schemaFile.getInputStream())
        );

    }

    public ProcessingReport check(JsonNode json) {
        try {
            return jsonSchema.validate(json, true);
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
