package bdd.utils;

import bdd.model.CrmData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PerformChange {
    List<CrmData> inputData;

    public PerformChange(List<CrmData> inputData) {
        this.inputData = inputData;
    }

    public List<CrmData> setCheckInDate(LocalDate date, String dateFormat) {
        String inputDate = String.valueOf(date.format(DateTimeFormatter.ofPattern(dateFormat)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        for (int i = 1; i < inputData.size(); i++) {
            LocalDate dateInCsv = LocalDate.parse(inputData.get(i).getCheckInDate(), formatter);
            if (date.isAfter(dateInCsv)) {
                inputData.get(i).setCheckInDate(inputDate);
            }
        }
        return inputData;
    }

    public List<CrmData> setCheckOutDate(LocalDate date, String dateFormat) {
        String inputDate = String.valueOf(date.format(DateTimeFormatter.ofPattern(dateFormat)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        for (int i = 1; i < inputData.size(); i++) {
            LocalDate dateInCsv = LocalDate.parse(inputData.get(i).getCheckOutDate(), formatter);
            if (date.isAfter(dateInCsv)) {
                inputData.get(i).setCheckOutDate(inputDate);
            }
        }
        return inputData;
    }
}





