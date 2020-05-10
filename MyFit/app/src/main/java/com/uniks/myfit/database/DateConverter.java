package com.uniks.myfit.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * converts Date to Long and Long to Date, so database can interpret Date type
 */
public class DateConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}
