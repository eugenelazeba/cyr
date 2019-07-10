package bdd.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cucumber.api.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.tika.io.IOUtils;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bdd.steps.TestData.outputData;
import static org.junit.Assert.assertTrue;

@Slf4j
public class Kibana {

    static final Config config = ConfigFactory.load();

    private ObjectMapper objectMapper = new ObjectMapper();


    private List<String> getCorrelationId(String file, List<String> listEntityId, int retries, long pollIntervalMs) throws IOException,
            InterruptedException, UnirestException {

        String requestBody = IOUtils.toString(getClass().getResourceAsStream(file), "UTF-8");
        JsonNode requestBodyNode = objectMapper.readTree(requestBody);
        List listCorId = new ArrayList<>();

        for (int elementEntity = 0; elementEntity < listEntityId.size(); elementEntity++) {
            for (int k = 0; k < retries; k++) {
                ((ObjectNode) requestBodyNode.path("query").path("bool").path("must").path(1).path("match"))
                        .put("entityId", listEntityId.get(elementEntity));
                String publishMessage = String.valueOf(requestBodyNode);

                HttpResponse<String> response = Unirest.post(config.getString("kibana.url") + ":" +
                        config.getString("kibana.port") +
                        config.getString("kibana.action"))
                        .header(HttpHeaders.AUTHORIZATION, config.getString("kibana.authorization"))
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .body(publishMessage).asString();

                Assert.assertEquals("Unexpected response: " + response.getStatus(), HttpStatus.SC_OK, response.getStatus());

                JsonNode responseBodyKibana = objectMapper.readTree(response.getBody());
                JsonNode correlationId = responseBodyKibana.path("hits").path("hits").findPath("correlationId");

                if (!correlationId.isMissingNode() && !correlationId.isNull()) {
                    listCorId.add(correlationId.textValue());
                    break;
                }
                Thread.sleep(pollIntervalMs);
            }
        }

        assertTrue(listEntityId.size() == listCorId.size());
        return listCorId;
    }


    @And("execute (\\S+) to receive correlataionId for each record in test data")
    public void getCorrelationId(String requestBodyKibana) throws Exception {
        List listCorrelationId = new ArrayList<>();
        List<String> listEntityId = new ArrayList<>();

        for (int i = 1; i < outputData.size(); i++) {
            String bookingNumber = outputData.get(i).getBookingNumber();
            listEntityId.add(bookingNumber);
        }

        listCorrelationId.add(getCorrelationId(requestBodyKibana, listEntityId, 6, 30000));
        log.info("correlationIds :" + listCorrelationId.get(0));
    }
}




