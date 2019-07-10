package bdd.steps;

import bdd.model.CrmData;
import bdd.utils.PerformChange;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import cucumber.api.java.en.Given;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class TestData {

    public TestData() {
    }

    static List<CrmData> outputData;
    static final String PATH_RESOURCES = "src/test/resources/";

    @Given("Prepare (\\S+) using (\\S+) where (\\S+) was set up for date$")
    public void testData(String outputFile, String inputFile, String dateFormat) throws IOException,
            CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        List<CrmData> inputData = new CsvReader().readCsv(inputFile);

        outputData = new PerformChange(inputData).setCheckInDate(LocalDate.now(), dateFormat);
        outputData = new PerformChange(outputData).setCheckOutDate(LocalDate.now().plusDays(1), dateFormat);

        new CsvWriter().writeCsv(Paths.get(PATH_RESOURCES + outputFile), outputData);
    }
}

