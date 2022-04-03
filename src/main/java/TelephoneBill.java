import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 电话账单计费。
 *
 * 通话计费时间从被叫方应答开始计算，到呼叫方挂机时结束；
 * 通话时间的秒数向上进位到分钟；
 * 没有超过30个小时的通话。
 */
public final class TelephoneBill {
    private static final int TIME_LEAP_MINUTE = 60;

    private static final int BILL_MAX_HOUR = 30;

    private static final long BILL_THRESHOLD = 20L;

    private static final BigDecimal BILL_LESS_THAN_THRESHOLD_PRE_MIN = new BigDecimal("0.05");
    private static final BigDecimal BILL_MORE_THAN_THRESHOLD_BASE = new BigDecimal("1.00");
    private static final BigDecimal BILL_MORE_THAN_THRESHOLD_PRE_MIN = new BigDecimal("0.10");

    private TelephoneBill() { }

    /**
     * 计算通话时间。通话时间的秒数向上进位到分钟。
     *
     * @param startTime 通话开始时间。
     * @param inDayLightSavingOfStartTime 通话开始时间是否在夏令时中。
     * @param endTime 通话结束时间。
     * @param inDayLightSavingOfEndTime 通话结束时间是否在夏令时中。
     * @return 通话时间（分钟）
     */
    public static long calculateTimeSpan(final LocalDateTime startTime,
                                         final boolean inDayLightSavingOfStartTime,
                                         final LocalDateTime endTime,
                                         final boolean inDayLightSavingOfEndTime) {
        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        if (minutes <= 0) {
            throw new IllegalArgumentException("endTime " + endTime + " should not before startTime "
                    + startTime + ".");
        } else if (duration.toDays() >= BILL_MAX_HOUR) {
            throw new IllegalArgumentException("bill time span should not more than 30 hours.");
        }

        return minutes
                - (inDayLightSavingOfStartTime ? TIME_LEAP_MINUTE : 0)
                + (inDayLightSavingOfEndTime ? TIME_LEAP_MINUTE : 0);
    }

    /**
     * 计算通话费。
     *
     * 通话时间小于等于20分钟时，每分钟收费0.05美元，通话时间不足1分钟按1分钟计算；
     * 通话时间大于20分钟时，收费1.00美元，外加超过20分钟的部分每分钟0.10美元；
     * 不到1分钟按1分钟计算。
     *
     * @param timeSpan 通话时间，单位分钟。
     * @return 通话费（美元）
     */
    public static BigDecimal calculateFee(final long timeSpan) {
        if (timeSpan < 0L) {
            throw new IllegalArgumentException("timeSpan should not less than 0.");
        }

        return timeSpan <= BILL_THRESHOLD
                ? BILL_LESS_THAN_THRESHOLD_PRE_MIN.multiply(new BigDecimal(timeSpan))
                : BILL_MORE_THAN_THRESHOLD_BASE.add(
                        BILL_MORE_THAN_THRESHOLD_PRE_MIN.multiply(new BigDecimal(timeSpan - BILL_THRESHOLD)));
    }
}
