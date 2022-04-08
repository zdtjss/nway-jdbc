package com.nway.spring.jdbc.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Timestamp date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDateTime();
    }

    public static LocalTime toLocalTime(Time time) {
        if (time == null) {
            return null;
        }
        return time.toLocalTime();
    }
}
