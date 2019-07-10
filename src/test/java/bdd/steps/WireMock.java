package bdd.steps;

import bdd.utils.CyrMessageValidator;
import bdd.utils.WireMockClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.core.io.FileSystemResourceLoader;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static bdd.steps.TestData.outputData;
import static org.junit.Assert.assertTrue;

@Slf4j
public class WireMock {
    static final Config config = ConfigFactory.load();

    private WireMockClient wireMockClient;
    private CyrMessageValidator cyrMessageValidator;

    private String baseUrl;
    private String cyrUrl;
    private String proxyBaseUrl;

    String marketingOptOut = "out";
    String outBound = "Outbound";
    String inBound = "Inbound";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        baseUrl = config.getString("wireMock.baseUrl");
        //  proxyBaseUrl = config.getString("wireMock.proxyBaseUrl");
        cyrUrl = config.getString("cyrApi.apiUrl");

        wireMockClient = new WireMockClient(baseUrl);
        //wireMockClient.enableProxy(cyrUrl, HttpMethod.PUT);
        wireMockClient.resetLoggedRequests();

        cyrMessageValidator = new CyrMessageValidator(new FileSystemResourceLoader(), ConfigFactory.load());
    }

    @After
    public void tearDown() throws Exception {
        wireMockClient.resetLoggedRequests();
    }

    private JsonNode toJson(String jsonAsString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(jsonAsString);
        } catch (IOException ioe) {
            return null;
        }
    }


    @Then("validate and verify sent messages from test data according to schema")
    public void validateMessage() throws UnirestException {
        validateMessageFromOutputFile();
    }

    private void validateMessageFromOutputFile() throws UnirestException {

        Optional<JsonNode> actualMessage = null;

        int countOfProcessedMessages = 0;

        for (int j = 1; j < outputData.size(); j++) {
            final int indexEntityId = j;
            List<JsonNode> requests = wireMockClient.findRequestsByUrlPath(config.getString("cyrApi.apiUrl"),
                    HttpMethod.POST);

            actualMessage = requests.stream()
                    .map(r -> toJson(r.path("body").asText()))
                    .filter(b -> !b.path("booking").isMissingNode())
                    .filter(b -> b.path("booking").path("bookingIdentifier").path("bookingNumber").asText().
                            equals(outputData.get(indexEntityId).getBookingNumber()))
                    .findFirst();
            if (actualMessage.isPresent()) {
                // schema validation
                ProcessingReport check = (ProcessingReport) cyrMessageValidator.check(actualMessage.get());
                assertTrue(check.isSuccess());

                // check message
                checkBookingGenaralNode(actualMessage, indexEntityId);
                checkBookingIdentifierNode(actualMessage, indexEntityId);
                checkBookingServicesAccommodationNode(actualMessage, indexEntityId);
                checkBookingServicesTransportNode(actualMessage, indexEntityId);
                checkBookingTravelParticipantNode(actualMessage, indexEntityId);
                checkCustomerNode(actualMessage, indexEntityId);

                countOfProcessedMessages ++;
            }
        }
        assertTrue(outputData.size() - 1 == countOfProcessedMessages);
    }


    private void checkBookingServicesTransportNode(Optional<JsonNode> actualMessage, int indexEntityId) {
        if (!actualMessage.get().path("booking").path("services").path("transport").isMissingNode()) {

            if (!outputData.get(indexEntityId).getFlightNumberOutbound().isEmpty() ||
                    !outputData.get(indexEntityId).getFlightTimeOutbound().isEmpty()) {

                Assert.assertEquals(outBound, actualMessage.get().path("booking").path("services").path("transport")
                        .path(0).path("transferType").asText());
            }

            if (!outputData.get(indexEntityId).getFlightNumberInbound().isEmpty() ||
                    !outputData.get(indexEntityId).getFlightTimeInbound().isEmpty()) {

                Assert.assertEquals(inBound, actualMessage.get().path("booking").path("services").path("transport")
                        .path(1).path("transferType").asText());
            }

            if (!outputData.get(indexEntityId).getFlightNumberInbound().isEmpty() &&
                    !outputData.get(indexEntityId).getFlightTimeInbound().isEmpty() &&
                    (!outputData.get(indexEntityId).getFlightNumberOutbound().isEmpty() &&
                            !outputData.get(indexEntityId).getFlightTimeOutbound().isEmpty())) {

                Assert.assertEquals(outputData.get(indexEntityId).getCheckInDate()
                                + "T" + outputData.get(indexEntityId).getFlightTimeOutbound() + ":00",
                        actualMessage.get().path("booking").path("services").path("transport").path(0).path("startDate").asText());

                Assert.assertEquals(outputData.get(indexEntityId).getCheckOutDate()
                                + "T" + outputData.get(indexEntityId).getFlightTimeInbound() + ":00",
                        actualMessage.get().path("booking").path("services").path("transport").path(1).path("endDate").asText());
            }

            Assert.assertEquals(outputData.get(indexEntityId).getFlightNumberOutbound(),
                    actualMessage.get().path("booking").path("services").path("transport").path(0)
                            .path("transportCode").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getFlightNumberInbound(),
                    actualMessage.get().path("booking").path("services").path("transport").path(1)
                            .path("transportCode").asText());
        }
    }

    private void checkBookingServicesAccommodationNode(Optional<JsonNode> actualMessage, int indexEntityId) {
        Assert.assertEquals(outputData.get(indexEntityId).getHotel(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(0)
                        .path("accommodationCode").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getSourceMarket(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(0)
                        .path("brandCode").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getCheckInDate(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(0)
                        .path("startDate").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getCheckOutDate(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(0)
                        .path("endDate").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType1(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(0)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType2(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(1)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType3(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(2)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType4(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(3)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType5(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(4)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType6(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(5)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType7(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(6)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType8(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(7)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType9(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(8)
                        .path("roomType").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getRoomType9(),
                actualMessage.get().path("booking").path("services").path("accommodation").path(8)
                        .path("roomType").asText());

    }

    private void checkBookingTravelParticipantNode(Optional<JsonNode> actualMessage, int indexEntityId) {
        Assert.assertEquals(outputData.get(indexEntityId).getCustomerName(),
                actualMessage.get().path("booking").path("travelParticipant").path(0).path("fullName").asText());

        if (!actualMessage.get().path("booking").path("travelParticipant").path(1).path("fullName").isMissingNode()) {
            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName1(),
                    actualMessage.get().path("booking").path("travelParticipant").path(1).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName2(),
                    actualMessage.get().path("booking").path("travelParticipant").path(2).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName3(),
                    actualMessage.get().path("booking").path("travelParticipant").path(3).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName4(),
                    actualMessage.get().path("booking").path("travelParticipant").path(4).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName5(),
                    actualMessage.get().path("booking").path("travelParticipant").path(5).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName6(),
                    actualMessage.get().path("booking").path("travelParticipant").path(6).path("fullName").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getAdditionalName7(),
                    actualMessage.get().path("booking").path("travelParticipant").path(7).path("fullName").asText());
        }
    }

    private void checkBookingIdentifierNode(Optional<JsonNode> actualMessage, int indexEntityId) {

        Assert.assertEquals(outputData.get(indexEntityId).getBookingNumber(),
                actualMessage.get().path("booking").path("bookingIdentifier").path("bookingNumber").asText());

        if (!actualMessage.get().path("booking").path("bookingIdentifier").path("bookingSystemCode").isMissingNode()) {
            Assert.assertEquals(outputData.get(indexEntityId).getSourceMarketId(),
                    actualMessage.get().path("booking").path("bookingIdentifier").path("bookingSystemCode").asText());
        }
    }

    private void checkBookingGenaralNode(Optional<JsonNode> actualMessage, int indexEntityId) {
        if (!actualMessage.get().path("booking").path("bookingGeneral").isMissingNode()) {

            Assert.assertEquals(outputData.get(indexEntityId).getAdult(),
                    actualMessage.get().path("booking").path("bookingGeneral").path("numberOfAdults").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getChild(),
                    actualMessage.get().path("booking").path("bookingGeneral").path("numberOfChildren").asText());

            Assert.assertEquals(outputData.get(indexEntityId).getInfant(),
                    actualMessage.get().path("booking").path("bookingGeneral").path("numberOfInfants").asText());
        }
    }

    private void checkCustomerNode(Optional<JsonNode> actualMessage, int indexEntityId) {
        Assert.assertEquals(outputData.get(indexEntityId).getEmail(),
                actualMessage.get().path("customer").path("email").path(0).path("address").asText());

        Assert.assertEquals(outputData.get(indexEntityId).getCustomerName(),
                actualMessage.get().path("customer").path("customerIdentity").path("fullName").asText());

        if (!actualMessage.get().path("customer").path("additional").path("segment").isMissingNode()) {
            if (outputData.get(indexEntityId).getMarketingOptOut().toLowerCase().equals(marketingOptOut))
                Assert.assertTrue(actualMessage.get().path("customer").path("permissions").path("doNotContactInd").asBoolean());
            else
                Assert.assertFalse(actualMessage.get().path("customer").path("permissions").path("doNotContactInd").asBoolean());

            Assert.assertEquals(outputData.get(indexEntityId).getCustomerSegment(),
                    actualMessage.get().path("customer").path("additional").path("segment").asText());
        }
    }
}
