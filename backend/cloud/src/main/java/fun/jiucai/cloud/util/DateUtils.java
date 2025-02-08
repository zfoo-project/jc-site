package fun.jiucai.cloud.util;

import io.netty.util.concurrent.FastThreadLocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author godotg
 */
public abstract class DateUtils {

    public static final String DATE_FORMAT_TEMPLATE = "MM/dd HH:mm";


    private static final FastThreadLocal<SimpleDateFormat> DATE_FORMAT = new FastThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_TEMPLATE);
        }
    };


    public static Date stringToDate(String dateString) throws ParseException {
        return DATE_FORMAT.get().parse(dateString);
    }

    public static String dateFormatForDayTimeString(long time) {
        return DATE_FORMAT.get().format(new Date(time));
    }

}
