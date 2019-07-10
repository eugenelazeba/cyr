package bdd.steps;

import bdd.model.CrmData;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import cucumber.api.java.en.Then;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


public class CsvReader {

    public CsvReader() throws IOException {
    }

    @Then("read CSV (\\S+)$")
    public List<CrmData> readCsv(String file) throws IOException, CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException {

        InputStream dataFiles = getClass().getResourceAsStream(file);

        try (
                Reader reader = new InputStreamReader(dataFiles, StandardCharsets.UTF_8);
        ) {
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(CrmData.class);

            String[] memberFieldsToBindTo = {"Email", "bookingNumber", "customerName", "hotel", "checkInDate",
                    "checkOutDate", "roomType1", "roomType2", "roomType3", "roomType4", "roomType5", "roomType6",
                    "roomType7", "roomType8", "roomType9", "sourceMarket", "additionalName1", "additionalName2",
                    "additionalName3", "additionalName4", "additionalName5", "additionalName6", "additionalName7",
                    "additionalName8", "adult", "child", "infant", "flightNumberOutbount", "flightTimeOutbound",
                    "flightNumberInbound","flightTimeInbound", "marketingOptOut", "sourceMarketId", "customerSegment"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}


