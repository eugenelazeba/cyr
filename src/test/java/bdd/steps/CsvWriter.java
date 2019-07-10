package bdd.steps;

import bdd.model.CrmData;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CsvWriter {

    public void writeCsv(Path path, List<CrmData> outputFile) throws CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException, IOException {

        path = Paths.get(String.valueOf(path));
        Writer writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));

        StatefulBeanToCsv<CrmData> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .build();

        beanToCsv.write(outputFile);
        writer.flush();
    }
}


