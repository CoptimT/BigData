package com.rtmap.hive.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("deprecation")
public class DateUtils {
    /**
     * get date format
     *
     * @return
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }

    public static String transferDate(String date) {
        String[] fields = date.split("/");
        int month = Integer.parseInt(fields[1]);
        int day = Integer.parseInt(fields[2]);

        String m = null;
        String d = null;
        if (month < 10) {
            m = "0" + month;
        } else {
            m = month + "";
        }
        if (day < 10) {
            d = "0" + day;
        } else {
            d = day + "";
        }
        return fields[0] + "-" + m + "-" + d;

    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date);
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return sdf.format(date);
    }

    public static int getCurrentHour() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        return hour;

    }

    public static int getTimeHour(String dateTime, String format) throws ParseException {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sd = new SimpleDateFormat(format);
        Date d = sd.parse(dateTime);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static Date getDate(String dateTime, String format) throws ParseException {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sd = new SimpleDateFormat(format);
        Date d = sd.parse(dateTime);
        return d;
    }

    public static boolean isToday(String dateTime, String format) throws ParseException {
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sd = new SimpleDateFormat(format);
        Date d = sd.parse(dateTime);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        Calendar cur = Calendar.getInstance();
        return cur.get(Calendar.YEAR) == c.get(Calendar.YEAR)
                && cur.get(Calendar.MONTH) == c.get(Calendar.MONTH)
                && cur.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param date
     * @return
     */
    public static int parseToMonth(String date) {
        String[] fields = date.split("-");
        if (fields.length > 1) {
            return Integer.parseInt(fields[1]);
        }
        return 0;
    }

    public static String parseMonthDate(int year, int month) {
        String date = year + "-";
        if (month < 10) {
            date = date + "0" + month;
        } else {
            date = date + month;
        }
        return date + "-01";
    }

    /**
     * @param date
     * @return
     */
    public static int parseToYear(String date) {
        String[] fields = date.split("-");
        if (fields[0].length() == 4) {
            return Integer.parseInt(fields[0]);
        }
        return 0;
    }

    /**
     * @param days
     * @return
     */
    public static String getDateByCondition(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, days);
        return sdf.format(calendar.getTime());
    }

    public static String getDateByCondition(int days, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(date));
        calendar.add(Calendar.DATE, days);
        return sdf.format(calendar.getTime());
    }

    /**
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseToDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(date);
    }

    /**
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDirByDate(int year, int month, int day) {
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("-");
        if (month < 10) {
            sb.append("0");
        }
        sb.append(month);
        sb.append("-");
        if (month < 10) {
            sb.append("0");
        }
        sb.append("day");
        return sb.toString();
    }

    /**
     * get date
     *
     * @param year
     * @return
     */
	public static int getYear(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = sdf.parse(date);
            return d.getYear() + 1900;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get date
     *
     * @param year
     * @return
     */
    public static int getYear() {
        Date date = new Date();
        return date.getYear() + 1900;
    }

    /**
     * get current month
     *
     * @return
     */
    public static int getMonth() {
        Date date = new Date();
        return date.getMonth() + 1;
    }

    /**
     * get month
     *
     * @param date
     * @return
     */
    public static int getMonth(String date) {
        if (date == null) {
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            return d.getMonth() + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get quarter
     *
     * @param date
     * @return
     */
    public static int getQuarter(String date) {
        if (date == null) {
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            int month = d.getMonth() + 1;
            if (month < 4) {
                return 1;
            } else if (month < 7) {
                return 2;
            } else if (month < 10) {
                return 3;
            } else {
                return 4;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get quarter
     *
     * @param date
     * @return
     */
    public static int getQuarter() {
        Date d = new Date();
        int month = d.getMonth() + 1;
        if (month < 4) {
            return 1;
        } else if (month < 7) {
            return 2;
        } else if (month < 10) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * get day
     *
     * @param date
     * @return
     */
    public static int getDay(String date) {
        if (date == null) {
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            return d.getDay() + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @param month
     * @return
     * @throws ParseException
     */
    public static Date parseDate(int month) throws ParseException {
        if (month == 0) {
            return null;
        }
        Date date = new Date();
        String dateFormat = (date.getYear() + 1900) + "-" + month;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date d = sdf.parse(dateFormat);
        return d;
    }

    /**
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date d = sdf.parse(date);
        return d;
    }

    /**
     * @param time1
     * @param time2
     * @return
     * @throws ParseException
     */
    public static long getTimeDiff(String time1, String time2) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return getTimeDiff(df.parse(time1), df.parse(time2));
    }

    /**
     * 秒
     *
     * @param time1
     * @param time2
     * @return
     * @throws ParseException
     */
    public static long getTimeDiff(Date time1, Date time2) throws ParseException {
        long t1 = time1.getTime();
        long t2 = time2.getTime();
        long diff = (t1 - t2) / 1000;
        if (diff > 0) {
            return diff;
        } else {
            return -diff;
        }
    }

    /**
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static boolean compareDate(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long d1 = sdf.parse(date1).getTime();
        long d2 = sdf.parse(date2).getTime();
        return d1 >= d2;
    }

    public static boolean compareDate(String date1, String date2, String format) throws ParseException {
        SimpleDateFormat sdf = null;
        if (format == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            sdf = new SimpleDateFormat(format);
        }
        long d1 = sdf.parse(date1).getTime();
        long d2 = sdf.parse(date2).getTime();
        return d1 >= d2;
    }

    /**
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static boolean compareDate(Date date1, Date date2) throws ParseException {
        if (date1 != null && date2 != null) {
            long d1 = date1.getTime();
            long d2 = date2.getTime();
            return d1 >= d2;
        }
        return true;
    }

    public static String formatDate(Date date, String format) throws ParseException {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }

    /**
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static boolean isBetween(String beginDate, String endDate, String targetDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long begin = sdf.parse(beginDate).getTime();
        long end = sdf.parse(endDate).getTime();

        long target = sdf.parse(targetDate).getTime();
        if (begin <= target && target <= end) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param date1
     * @param date2
     * @param format
     * @return
     * @throws ParseException
     */
    public static boolean isBetween(String beginDate, String endDate, String targetDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long begin = sdf.parse(beginDate).getTime();
        long end = sdf.parse(endDate).getTime();

        long target = sdf.parse(targetDate).getTime();
        if (begin <= target && target <= end) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static boolean compareTo(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long d1 = sdf.parse(date1).getTime();
        long d2 = sdf.parse(date2).getTime();
        if (d1 > d2) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isCurrentWeek(String date) {
        if (date == null) {
            return false;
        }
        if (DateUtils.getMonth() != DateUtils.getMonth(date)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int weekCurrent = cal.get(Calendar.WEEK_OF_MONTH);

            cal.setTime(new Date(sdf.parse(date).getTime()));
            int weekInput = cal.get(Calendar.WEEK_OF_MONTH);
            return (weekCurrent == weekInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameWeek(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        if (DateUtils.getMonth(date1) != DateUtils.getMonth(date2)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(sdf.parse(date1).getTime()));
            int weekCurrent = cal.get(Calendar.WEEK_OF_MONTH);

            cal.setTime(new Date(sdf.parse(date2).getTime()));
            int weekInput = cal.get(Calendar.WEEK_OF_MONTH);
            return (weekCurrent == weekInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 得到前一天
     *
     * @param date
     * @return
     */
    public static Date getPreDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);//得到前一天
        return calendar.getTime();
    }

    /**
     * 获得两个日期相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDateDiff(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long time1 = simpleDateFormat.parse(date1).getTime();
            long time2 = simpleDateFormat.parse(date2).getTime();
            return Integer.parseInt(String.valueOf(Math.abs(time2 - time1) / (1000 * 3600 * 24)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
