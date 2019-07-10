package bdd.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CrmData {
    //  @CsvBindByName(column = "email", required = true)
    @CsvBindByPosition(position = 0)
    private String email;

    @CsvBindByPosition(position = 1)
    private String bookingNumber;

    @CsvBindByPosition(position = 2)
    private String customerName;

    @CsvBindByPosition(position = 3)
    private String hotel;

    @CsvBindByPosition(position = 4)
    private String checkInDate;

    @CsvBindByPosition(position = 5)
    private String checkOutDate;

    @CsvBindByPosition(position = 6)
    private String roomType1;

    @CsvBindByPosition(position = 7)
    private String roomType2;

    @CsvBindByPosition(position = 8)
    private String roomType3;

    @CsvBindByPosition(position = 9)
    private String roomType4;

    @CsvBindByPosition(position = 10)
    private String roomType5;

    @CsvBindByPosition(position = 11)
    private String roomType6;

    @CsvBindByPosition(position = 12)
    private String roomType7;

    @CsvBindByPosition(position = 13)
    private String roomType8;

    @CsvBindByPosition(position = 14)
    private String roomType9;

    @CsvBindByPosition(position = 15)
    private String sourceMarket;

    @CsvBindByPosition(position = 16)
    private String additionalName1;

    @CsvBindByPosition(position = 17)
    private String additionalName2;

    @CsvBindByPosition(position = 18)
    private String additionalName3;

    @CsvBindByPosition(position = 19)
    private String additionalName4;

    @CsvBindByPosition(position = 20)
    private String additionalName5;

    @CsvBindByPosition(position = 21)
    private String additionalName6;

    @CsvBindByPosition(position = 22)
    private String additionalName7;

    @CsvBindByPosition(position = 23)
    private String additionalName8;

    @CsvBindByPosition(position = 24)
    private String adult;

    @CsvBindByPosition(position = 25)
    private String child;

    @CsvBindByPosition(position = 26)
    private String infant;

    @CsvBindByPosition(position = 27)
    private String flightNumberOutbound;

    @CsvBindByPosition(position = 28)
    private String flightTimeOutbound;

    @CsvBindByPosition(position = 29)
    private String flightNumberInbound;

    @CsvBindByPosition(position = 30)
    private String flightTimeInbound;

    @CsvBindByPosition(position = 31)
    private String marketingOptOut;

    @CsvBindByPosition(position = 32)
    private String sourceMarketId;

    @CsvBindByPosition(position = 33)
    private String customerSegment;
}


