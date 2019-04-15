package ru.crew4dev.celllogger;

import java.text.SimpleDateFormat;

public class Constants {
    public final static String WORK_DONE = "WORK_DONE";
    public final static String UPDATE_DATA = "UPDATE_DATA";
    public final static String PLACE_ID = "PLACE_ID";

    public static final SimpleDateFormat dateFullFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
}
