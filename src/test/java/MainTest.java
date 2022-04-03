import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {
    @ParameterizedTest
    @CsvSource({
            "2022-03-21T16:12:05, true, 2022-03-21T16:12:06, true, 0.05",
            "2022-03-21T16:12:05, false, 2022-03-22T16:12:05, false, 143.00"
    })
    void testBill(String startTime,
                  boolean inDayLightSavingOfStartTime,
                  String endTime,
                  boolean inDayLightSavingOfEndTime,
                  String bill) {
        long timeSpan = TelephoneBill.calculateTimeSpan(LocalDateTime.parse(startTime),
                inDayLightSavingOfStartTime,
                LocalDateTime.parse(endTime),
                inDayLightSavingOfEndTime);
        assertEquals(new BigDecimal(bill), TelephoneBill.calculateFee(timeSpan));
    }

    @ParameterizedTest
    @CsvSource({
            "2022-03-21T16:12:05, true, 2022-03-21T16:11:05, true",
            "2022-03-21T16:12:05, false, 2023-03-21T16:12:05, false"
    })
    void testIllegalTimeSpan(String startTime,
                         boolean inDayLightSavingOfStartTime,
                         String endTime,
                         boolean inDayLightSavingOfEndTime) {
        assertThrows(IllegalArgumentException.class, () -> {
            long timeSpan = TelephoneBill.calculateTimeSpan(LocalDateTime.parse(startTime),
                    inDayLightSavingOfStartTime,
                    LocalDateTime.parse(endTime),
                    inDayLightSavingOfEndTime);
            TelephoneBill.calculateFee(timeSpan);
        });
    }
}