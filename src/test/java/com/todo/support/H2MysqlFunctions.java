package com.todo.support;

import org.h2.api.Interval;
import org.h2.api.IntervalQualifier;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class H2MysqlFunctions {
    private H2MysqlFunctions() {
    }

    public static Timestamp dateSub(Timestamp timestamp, Interval interval) {
        LocalDateTime value = timestamp.toLocalDateTime();
        long sign = interval.isNegative() ? -1 : 1;
        long leading = interval.getLeading() * sign;
        long remaining = interval.getRemaining() * sign;
        IntervalQualifier qualifier = interval.getQualifier();

        LocalDateTime result = switch (qualifier) {
            case YEAR -> value.minusYears(leading);
            case MONTH -> value.minusMonths(leading);
            case DAY -> value.minusDays(leading);
            case HOUR -> value.minusHours(leading);
            case MINUTE -> value.minusMinutes(leading);
            case SECOND -> value.minusSeconds(leading).minusNanos(remaining);
            case DAY_TO_HOUR -> value.minusDays(leading).minusHours(remaining);
            case DAY_TO_MINUTE -> value.minusDays(leading).minusHours(interval.getHours() * sign).minusMinutes(interval.getMinutes() * sign);
            case DAY_TO_SECOND -> value.minusDays(leading).minusHours(interval.getHours() * sign).minusMinutes(interval.getMinutes() * sign).minusSeconds(interval.getSeconds() * sign).minusNanos(interval.getNanosOfSecond() * sign);
            case HOUR_TO_MINUTE -> value.minusHours(leading).minusMinutes(remaining);
            case HOUR_TO_SECOND -> value.minusHours(leading).minusMinutes(interval.getMinutes() * sign).minusSeconds(interval.getSeconds() * sign).minusNanos(interval.getNanosOfSecond() * sign);
            case MINUTE_TO_SECOND -> value.minusMinutes(leading).minusSeconds(interval.getSeconds() * sign).minusNanos(interval.getNanosOfSecond() * sign);
            case YEAR_TO_MONTH -> value.minusYears(leading).minusMonths(remaining);
        };
        return Timestamp.valueOf(result);
    }
}
