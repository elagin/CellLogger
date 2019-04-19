package ru.crew4dev.celllogger;

import java.util.Calendar;
import java.util.Date;

import static ru.crew4dev.celllogger.Constants.timeDateFormat;
import static ru.crew4dev.celllogger.Constants.timeFormat;

public class Tools {
    public static String getDate(Date date){
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(calendarNow.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendarNow.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendarNow.get(Calendar.DATE) == calendar.get(Calendar.DATE))
            return timeFormat.format(date);
        else
            return timeDateFormat.format(date);
    }
}
