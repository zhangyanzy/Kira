package com.moemoe.lalala.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import io.rong.imkit.RongContext;
import io.rong.imkit.utils.RongDateUtils;

/**
 * 字符串工具类
 * Created by yi on 2016/11/28.
 */

public class StringUtils {
    private static final String TAG = "StringUtils";

    public static final long TIME_ONE_DAY = 24 * 3600 * 1000;
    public static final long TIME_ONE_HOUR = 3600 * 1000;
    public static final long TIME_ONE_MIN = 60 * 1000;

    private static SimpleDateFormat sServerDate = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sServerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat sYearMthDay = new SimpleDateFormat("yyyy年M月d日");

    private static SimpleDateFormat sMthDay = new SimpleDateFormat("M月d日");

    private static SimpleDateFormat sMthDayUS = new SimpleDateFormat("M.d");

    private static SimpleDateFormat sYearMthDayUS = new SimpleDateFormat("yyyy.M.d");

    private static SimpleDateFormat sNormal = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    private static Pattern sEmailPattern = Pattern.compile("[\\w\\.\\-\\+]+@([\\w\\-\\+]+\\.)+[\\w\\-\\+]+", Pattern.CASE_INSENSITIVE);

    private static Pattern sNickNamePattern;

    private static Pattern sPasswordPattern;

    private static Pattern sWebUrlPattern;

    public static String toServerTimeString(long time) {
        return sServerTime.format(new Date(time));
    }

    /**
     * 带位数限制的数字: 1000, 3 -> 999+
     *
     * @param num
     * @param limit
     * @return
     */
    public static String getNumberInLengthLimit(int num, int limit) {
        String res = num + "";
        int rl = (int) Math.pow(10, limit);
        if (num > rl) {
            res = (rl - 1) + "+";
        }
        return res;
    }


    /**
     * 判断是否是邮箱，支持“+”,"-"号
     *
     * @param email
     * @return
     */
    public static boolean isEmailFormated(String email) {
        Matcher matcher = sEmailPattern.matcher(email);
        boolean res = matcher.matches();
        return res;
    }

    /**
     * 昵称中不能包含特殊字符：`~!@#$%^&*()+=|{}':;',[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？
     *
     * @param nickName
     * @return
     */
    public static boolean isLeagleNickName(String nickName) {
        if (sNickNamePattern == null) {
            sNickNamePattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\n\\s]");
            //sNickNamePattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");
        }
        if (sNickNamePattern.matcher(nickName).find()) {    // email
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLeagleVCode(String vCode) {
        boolean res = false;
        if (vCode.length() == 6) {
            res = true;
            for (int i = 0; i < vCode.length(); i++) {
                if (vCode.charAt(i) < '0' && vCode.charAt(i) > '9') {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 是否是合法的密码,规则是英文数字，常规特殊符号，不限制长度
     *
     * @param password
     * @return
     */
    public static boolean isLegalPassword(String password) {
        boolean isLegal = false;
        if (sPasswordPattern == null) {
            sPasswordPattern = Pattern.compile("\\w{6,15}$");
        }
        if (!TextUtils.isEmpty(password)) {
            isLegal = sPasswordPattern.matcher(password).matches();
        }
        return isLegal;

    }

    /**
     * 获取表示文件打下的字符串：
     * 10240 = 10kb
     * 1024 * 1024 * 3 = 3mb
     *
     * @param fileSize
     * @return
     */
    public static String getFileSizeString(long fileSize) {
        int level = 0;
        float nfs = fileSize;
        while (nfs / 1024 >= 1 && level < 4) {
            nfs = nfs / 1024;
            level++;
        }
        String ret = String.format("%.2f", nfs);
        if (level == 0) {
            ret = ret + "b";
        } else if (level == 1) {
            ret = ret + "kb";
        } else if (level == 2) {
            ret = ret + "mb";
        } else if (level == 3) {
            ret = ret + "gb";
        }
        return ret;
    }

    public static long parseSentenceTime(String timeStr) {
        long time = 0;
        if (!TextUtils.isEmpty(timeStr)) {
            String[] part = timeStr.split(":");
            int h = Integer.valueOf(part[0]);
            int m = Integer.valueOf(part[1]);
            time = h * 3600 + m * 60;
        }
        return time;
    }

    public static long parseSentenceTimeSec(String timeStr) {
        long time = 0;
        if (!TextUtils.isEmpty(timeStr)) {
            String[] part = timeStr.split(":");
            int h = Integer.valueOf(part[0]);
            int m = Integer.valueOf(part[1]);
            int s = Integer.valueOf(part[2]);
            time = h * 3600 + m * 60 + s;
        }
        return time;
    }

    public static boolean matchCurrentTime(long start, long end) {
        boolean res = false;
        Calendar time = Calendar.getInstance();
        int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60;
        if (t < end && t >= start) {
            res = true;
        }
        return res;
    }

    public static boolean issyougo() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("09:00"), parseSentenceTime("14:00"));
        if (a) {
            res = true;
        }
        return res;
    }

    public static boolean isasa() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("05:00"), parseSentenceTime("10:00"));
        if (a) {
            res = true;
        }
        return res;
    }

    public static boolean isgogo() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("14:00"), parseSentenceTime("17:00"));
        if (a) {
            res = true;
        }
        return res;
    }

    public static boolean istasogare() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("17:00"), parseSentenceTime("19:00"));
        if (a) {
            res = true;
        }
        return res;
    }

    public static boolean isyoru2() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("19:00"), parseSentenceTime("24:00"));
        boolean b = matchCurrentTime(parseSentenceTime("00:00"), parseSentenceTime("01:00"));
        if (a || b) {
            res = true;
        }
        return res;
    }

    public static boolean ismayonaka() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("01:00"), parseSentenceTime("05:00"));
        if (a) {
            res = true;
        }
        return res;
    }

    public static boolean isyoru() {
        boolean res = false;
        boolean a = matchCurrentTime(parseSentenceTime("22:00"), parseSentenceTime("24:00"));
        boolean b = matchCurrentTime(parseSentenceTime("00:00"), parseSentenceTime("06:00"));
        if (a || b) {
            res = true;
        }
        return res;
    }

    public static boolean isBackSchool() {
        return matchCurrentTime(parseSentenceTime("22:00"), parseSentenceTime("23:00"));
    }

    public static boolean isKillEvent() {
        return matchCurrentTime(parseSentenceTime("00:00"), parseSentenceTime("01:00"));
    }

    public static boolean isDayEvent() {
        return matchCurrentTime(parseSentenceTime("06:00"), parseSentenceTime("10:00"));
    }

    public static boolean matchCurrentTime1(String startTime, String endTime) {
        long start = parseSentenceTime(startTime);
        long end = parseSentenceTime(endTime);
        boolean res = false;
        Calendar time = Calendar.getInstance();
        int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60 + time.get(Calendar.SECOND);
        if (t < end && t >= start) {
            res = true;
        }
        return res;
    }

    public static boolean matchCurrentTime(String startTime, String endTime) {
        long start = parseSentenceTimeSec(startTime);
        long end = parseSentenceTimeSec(endTime);
        boolean res = false;
        Calendar time = Calendar.getInstance();
        int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60 + time.get(Calendar.SECOND);
        if (t < end && t >= start) {
            res = true;
        }
        return res;
    }

    public static boolean matchCurrentTime(Calendar time, Calendar vipTime) {
        boolean res = false;
        if (time.getTimeInMillis() <= vipTime.getTimeInMillis()) {
            res = true;
        }
        return res;
    }

    public static boolean matchCurrentTime(Calendar time, String startTime, String endTime) {
        long start = parseSentenceTimeSec(startTime);
        long end = parseSentenceTimeSec(endTime);
        boolean res = false;
        int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60 + time.get(Calendar.SECOND);
        if (t < end && t >= start) {
            res = true;
        }
        return res;
    }

    public static boolean matchYear(int start, int end) {
        boolean res = false;
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        if (year < end && year > start) {
            res = true;
        }
        return res;
    }

    public static boolean matchDate(Calendar time, String start, String end) {
        boolean res = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date parseStart = null;
        try {
            parseStart = sdf.parse(start);
            Date parseEnd = sdf.parse(start);
            Date serviceTime = sdf.parse(sdf.format(time.getTime()));
            if (serviceTime.getTime() >= parseStart.getTime() && serviceTime.getTime() <= parseEnd.getTime()) {
                res = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean matchYear(Calendar time, int start, int end) {
        boolean res = false;
        int year = time.get(Calendar.YEAR);
        if (year < end && year > start) {
            res = true;
        }
        return res;
    }


    /**
     * 文本增加网址监听
     *
     * @param context
     * @param text
     * @return
     */
    public static SpannableString getUrlClickableText(Context context, String text) {
        if (sWebUrlPattern == null) {
            //sWebUrlPattern = Pattern.compile("((https://|http://)([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)|www.(([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)");
            sWebUrlPattern = Pattern.compile("(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
        }
        if (!TextUtils.isEmpty(text)) {
            SpannableString ss = new SpannableString(text);
            Matcher m = sWebUrlPattern.matcher(text);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                CustomUrlSpan span = new CustomUrlSpan(context, null, text.substring(start, end));
                ss.setSpan(span, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return ss;
        } else {
            return null;
        }
    }


    /**
     * 文本增加网址监听
     *
     * @param context
     * @param text
     * @return
     */
    public static SpannableStringBuilder getUrlClickableText(Context context, SpannableStringBuilder text) {
        if (sWebUrlPattern == null) {
            //sWebUrlPattern = Pattern.compile("((https://|http://)([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)|www.(([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)");
            sWebUrlPattern = Pattern.compile("(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
        }
        if (!TextUtils.isEmpty(text)) {
            Matcher m = sWebUrlPattern.matcher(text);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                CustomUrlSpan span = new CustomUrlSpan(context, null, text.subSequence(start, end).toString());
                text.setSpan(span, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return text;
        } else {
            return null;
        }
    }

    /**
     * 高亮关键字
     *
     * @param value
     * @param keyWord
     * @return
     * @author Haru
     */
    @SuppressLint("DefaultLocale")
    public static SpannableString highLightKeyWord(Context ctx, String value, String keyWord) {
        if (!TextUtils.isEmpty(keyWord) && !TextUtils.isEmpty(value)) {
            SpannableString s = new SpannableString(value);
            Pattern p = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(s);
            int color = ctx.getResources().getColor(R.color.main_cyan);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return s;
        }
        return null;
    }


    public static int getHashOfString(String text, int limit) {
        int ret = 0;
        if (!TextUtils.isEmpty(text)) {
            ret = text.charAt(0) % limit;
        }
        return ret;
    }


    /**
     * AutoCompleteTextView 邮箱自动补全的{@link TextWatcher#afterTextChanged(android.text.Editable)} 方法响应事件
     * 在其中设置邮箱列表，如果不符合邮箱列表，则加载预置补全项
     *
     * @param context
     * @param actv           AutoCompleteTextView
     * @param newText        当前text in AutoCompleteTextView
     * @param needSetDefault 当newText不符合邮箱格式时，是否需要设置默认补全adapter
     * @param defaultAdapter 默认补全adapter
     * @return true if 成功设置了email adpater， false if设置了defaut adpater；如果没有设置adpater，返回值与needSetDefault相同
     */
    public static boolean onEmailAutoCompleteTvTextChanged(Context context, AutoCompleteTextView actv, String newText,
                                                           boolean needSetDefault, ArrayAdapter<String> defaultAdapter) {
        boolean enable = needSetDefault;
        if (newText != null) {
            int len = newText.length();
            if (len > 0) {
                if (newText.contains("@")) {
                    // 邮箱后缀补全
                    if ("@".equals(newText.subSequence(len - 1, len))) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                R.layout.simple_dropdown_item_1line, EmailSufix.getFormatedEmails(newText));
                        actv.setAdapter(adapter);
                    }
                    enable = true;
                } else if (needSetDefault && defaultAdapter != null) {
                    actv.setAdapter(defaultAdapter);
                    enable = false;
                }
                ListAdapter apdapter = actv.getAdapter();
                if (apdapter != null && apdapter.getCount() == 1 && newText.equals(apdapter.getItem(0))) {
                    actv.dismissDropDown();
                }
            }
        }
        return enable;
    }

    private static String getFormatDate(String year, String month, String day) {
        StringBuffer sBuffer = new StringBuffer();
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        sBuffer.append(year).append(month).append(day);
        return sBuffer.toString();
    }

    private static String getFormateTime(Date time, String pares) {
        return new SimpleDateFormat(pares, Locale.getDefault()).format(time);
    }

    public static String timeFormat(long time) {
        if (time > 0L) {
            try {
                long second = (System.currentTimeMillis() - time) / 1000;
                String res = "";
                if (second == 0) {
                    res = "刚刚";
                } else if (second < 30) {
                    res = second + "秒以前";
                } else if (second >= 30 && second < 60) {
                    res = "半分钟前";
                } else if (second >= 60 && second < 60 * 60) {
                    long minute = second / 60;
                    res = minute + "分钟前";
                } else if (second >= 60 * 60 && second < 60 * 60 * 24) {
                    long hour = (second / 60) / 60;
                    res = hour + "小时前";
                } else {
                    Date date1 = new Date(time);
                    res = getFormateTime(date1, "yyyy-MM-dd");
                }
                return res;
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static String timeFormat(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(str));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long second = (System.currentTimeMillis() - c.getTimeInMillis()) / 1000;
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(str);
                String res = "";
                if (second == 0) {
                    res = "刚刚";
                } else if (second < 30) {
                    res = second + "秒以前";
                } else if (second >= 30 && second < 60) {
                    res = "半分钟前";
                } else if (second >= 60 && second < 60 * 60) {
                    long minute = second / 60;
                    res = minute + "分钟前";
                } else if (second >= 60 * 60 && second < 60 * 60 * 24) {
                    long hour = (second / 60) / 60;
                    res = hour + "小时前";
                } else {
                    res = getFormateTime(date, "yyyy-MM-dd");
                }
                return res;
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static String getUrl(String path) {
        String res;
        if (path.startsWith("http") || path.startsWith("https")) {
            res = path;
        } else {
            res = ApiService.URL_QINIU + path;
        }
        return res;
    }

    public static String getUrl(Context context, String path, int width, int height, boolean isDocDetail, boolean isDoc) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        String res;
        if (path.startsWith("http") || path.startsWith("https")) {
            res = path;
        } else {
            res = ApiService.URL_QINIU + path;
        }
        boolean isLow = !NetworkUtils.isWifi(context) && !AppSetting.IS_DOWNLOAD_LOW_IN_3G;
        if (width > 0) {
            if (isLow) {
                width = width / 2;
                if (width > 600) width = 600;
            }
            if (isDoc && !isDocDetail) {
                res = res + "?imageView2/1/w/" + width + "/h/" + height;// + "/format/jpg";
            } else if (isDocDetail && isDoc) {
                res = res + "?imageView2/2/w/" + width + (isLow ? "/q/60" : "");
            } else {
                res = res + "?imageView2/0/w/" + width + "/h/" + height;
            }
        } else {
            res = res + "?imageView2/2/w/" + width + (isLow ? "/q/60" : "");
        }
        return res;
    }

    /**
     * format seconds to HH:mm:ss String
     *
     * @param seconds seconds
     * @return String of formatted in HH:mm:ss
     */
    public static String seconds2HH_mm_ss(long seconds) {

        long h = 0;
        long m = 0;
        long s = 0;
        long temp = seconds % 3600;

        if (seconds > 3600) {
            h = seconds / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            m = seconds / 60;
            if (seconds % 60 != 0) {
                s = seconds % 60;
            }
        }

        String dh = h < 10 ? "0" + h : h + "";
        String dm = m < 10 ? "0" + m : m + "";
        String ds = s < 10 ? "0" + s : s + "";

        return dh + ":" + dm + ":" + ds;
    }

    public static String createImageFile(boolean isGif) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName;
        if (isGif) {
            imageFileName = "neta_" + timeStamp + ".gif";
        } else {
            imageFileName = "neta_" + timeStamp + ".jpg";
        }
        return imageFileName;
    }

    /**
     * 解析16进制颜色字符串
     *
     * @param str
     * @param defaultColor
     * @return
     * @author Ben
     */
    public static int readColorStr(String str, int defaultColor) {
        int color = defaultColor;
        if (!TextUtils.isEmpty(str)) {
            try {
                if (!str.startsWith("#")) {
                    str = "#" + str;
                }
                color = Color.parseColor(str);
            } catch (Exception e) {
            }
        }
        return color;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String res = bigInt.toString(16);
        if (res.length() < 32) {
            int n = 32 - res.length();
            for (int i = 0; i < n; i++) {
                res = "0" + res;
            }
        }
        return res;
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild ;true递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory() && listChild) {
                map.putAll(getDirMD5(f, listChild));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }

    public static String buildDataAtUser(CharSequence sequence) {
        String res;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        CustomUrlSpan[] spen = stringBuilder.getSpans(0, stringBuilder.length(), CustomUrlSpan.class);
        if (spen.length > 0) {
            res = stringBuilder.toString();
            int step = 0;
            for (CustomUrlSpan span : spen) {
                int before = res.length();
                String beginStr = res.substring(0, stringBuilder.getSpanStart(span) + step);
                String str = res.substring(stringBuilder.getSpanStart(span) + step, stringBuilder.getSpanEnd(span) + step);
                String endStr = res.substring(stringBuilder.getSpanEnd(span) + step);
                str = "<at_user user_id=" + span.getmUrl() + ">" + str + "</at_user>";
                res = beginStr + str + endStr;
                step += res.length() - before;
            }
        } else {
            res = stringBuilder.toString();
        }
        return res;
    }

    public static Set<String> getAtUserIds(CharSequence sequence) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        CustomUrlSpan[] spen = stringBuilder.getSpans(0, stringBuilder.length(), CustomUrlSpan.class);
        Set<String> ids = new HashSet<>();
        for (CustomUrlSpan span : spen) {
            ids.add(span.getmUrl());
        }
        return ids;
    }

    /**
     * 解析自定协议显示
     *
     * @param s
     * @return
     */
    public static SpannableStringBuilder buildAtUserToShow(Context context, String s) {
        s = s.replace(" ", "&nbsp;").replace("<at_user&nbsp;", "<at_user ");
        Document document = Jsoup.parse(s);
        Elements elements = document.select("at_user");
        DoubleKeyValueMap<String, Integer, Integer> map = new DoubleKeyValueMap<>();
        for (Element element : elements) {
            String text = element.text();
            String id = element.attr("user_id");
            String all = element.toString().replace("\n", "")
                    .replace("\"" + id + "\"", id)
                    .replace(" ", "")
                    .replace("&nbsp;", " ");
            if (!all.contains("<at_user>")) {
                all = all.replace("<at_user", "<at_user ");
            }
            s = s.replace("&nbsp;", " ");
            String beginStr = s.substring(0, s.indexOf(all));
            String endStr = s.substring(s.indexOf(all) + all.length());
            map.put(id, s.indexOf(all), s.indexOf(all) + text.length());
            s = beginStr + text + endStr;
            s = s.replace(" ", "&nbsp;").replace("<at_user&nbsp;", "<at_user ");
        }
        s = s.replace("&nbsp;", " ");
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(s);
        for (String id : map.getFirstKeys()) {
            ConcurrentHashMap<Integer, Integer> concurrentHashMap = map.get(id);
            for (Integer begin : concurrentHashMap.keySet()) {
                int end = concurrentHashMap.get(begin);
                CustomUrlSpan span = new CustomUrlSpan(context, null, id);
                stringBuilder.setSpan(span, begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return stringBuilder;
    }

    /**
     * 解析自定协议显示
     *
     * @param s
     * @return
     */
    public static SpannableStringBuilder buildAtUserToEdit(Context context, String s) {
        s = s.replace(" ", "&nbsp;").replace("<at_user&nbsp;", "<at_user ");
        Document document = Jsoup.parse(s);
        Elements elements = document.select("at_user");
        DoubleKeyValueMap<String, Integer, Integer> map = new DoubleKeyValueMap<>();
        for (Element element : elements) {
            String text = element.text();
            String id = element.attr("user_id");
            String all = element.toString().replace("\n", "")
                    .replace("\"" + id + "\"", id)
                    .replace(" ", "")
                    .replace("&nbsp;", " ");
            if (!all.contains("<at_user>")) {
                all = all.replace("<at_user", "<at_user ");
            }
            s = s.replace("&nbsp;", " ");
            String beginStr = s.substring(0, s.indexOf(all));
            String endStr = s.substring(s.indexOf(all) + all.length());
            map.put(id, s.indexOf(all), s.indexOf(all) + text.length());
            s = beginStr + text + endStr;
            s = s.replace(" ", "&nbsp;").replace("<at_user&nbsp;", "<at_user ");
        }
        s = s.replace("&nbsp;", " ");
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(s);
        for (String id : map.getFirstKeys()) {
            ConcurrentHashMap<Integer, Integer> concurrentHashMap = map.get(id);
            for (Integer begin : concurrentHashMap.keySet()) {
                int end = concurrentHashMap.get(begin);
                CustomUrlSpan span = new CustomUrlSpan(context, "", id);
                stringBuilder.setSpan(span, begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return stringBuilder;
    }


    public static boolean isThirdParty(String platform) {
        return platform != null && (cn.sharesdk.tencent.qq.QQ.NAME.equals(platform)
                || Wechat.NAME.equals(platform)
                || SinaWeibo.NAME.equals(platform)
                || Constant.THIRD_LOGIN_QQ.equals(platform)
                || Constant.THIRD_LOGIN_SINA.equals(platform)
                || Constant.THIRD_LOGIN_WECHAT.equals(platform));
    }

    public static String convertPlatform(String platform) {
        if (cn.sharesdk.tencent.qq.QQ.NAME.equals(platform)) {
            return Constant.THIRD_LOGIN_QQ;
        }
        if (Wechat.NAME.equals(platform)) {
            return Constant.THIRD_LOGIN_WECHAT;
        }
        if (SinaWeibo.NAME.equals(platform)) {
            return Constant.THIRD_LOGIN_SINA;
        }
        return platform;
    }

    public static String getMinute(int time) {
        int h = time / (1000 * 60 * 60);
        String minute;
        int sec = (time % (1000 * 60)) / 1000;
        int min = time % (1000 * 60 * 60) / (1000 * 60);
        String hS = h < 10 ? "0" + h : "" + h;
        String secS = sec < 10 ? "0" + sec : "" + sec;
        String minS = min < 10 ? "0" + min : "" + min;
        if (h == 0) {
            minute = minS + ":" + secS;
        } else {
            minute = hS + ":" + minS + ":" + secS;
        }
        return minute;
//        if (time < 10) {
//            return "00:0" + time;
//        }
//        if (time < 60) {
//            return "00:" + time;
//        }
//        if (time < 3600) {
//            int minute = time / 60;
//            time = time - minute * 60;
//            if (minute < 10) {
//                if (time < 10) {
//                    return "0" + minute + ":0" + time;
//                }
//                return "0" + minute + ":" + time;
//            }
//            if (time < 10) {
//                return minute + ":0" + time;
//            }
//            return minute + ":" + time;
//        }
//        int hour = time / 3600;
//        int minute = (time - hour * 3600) / 60;
//        time = time - hour * 3600 - minute * 60;
//        if (hour < 10) {
//            if (minute < 10) {
//                if (time < 10) {
//                    return "0" + hour + ":0" + minute + ":0" + time;
//                }
//                return "0" + hour + ":0" + minute + ":" + time;
//            }
//            if (time < 10) {
//                return "0" + hour + minute + ":0" + time;
//            }
//            return "0" + hour + minute + ":" + time;
//        }
//        if (minute < 10) {
//            if (time < 10) {
//                return hour + ":0" + minute + ":0" + time;
//            }
//            return hour + ":0" + minute + ":" + time;
//        }
//        if (time < 10) {
//            return hour + minute + ":0" + time;
//        }
//        return hour + minute + ":" + time;
    }


    /**
     * 格式化时间
     *
     * @param hour   小时
     * @param minute 分钟
     * @return 格式化后的时间:[xx:xx]
     */
    public static String formatTime(int hour, int minute) {
        return addZero(hour) + ":" + addZero(minute);
    }

    /**
     * 时间补零
     *
     * @param time 需要补零的时间
     * @return 补零后的时间
     */
    public static String addZero(int time) {
        if (String.valueOf(time).length() == 1) {
            return "0" + time;
        }

        return String.valueOf(time);
    }

    /**
     * 从时间(毫秒)中提取出时间(分:秒)
     * 时间格式:  分:秒
     *
     * @param millisecond
     * @return
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//        Date date = new Date(millisecond);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = simpleDateFormat.format(millisecond);
        return hms.substring(1);
    }

    /**
     * format seconds to HH:mm:ss String
     *
     * @param seconds seconds
     * @return String of formatted in HH:mm:ss
     */
    public static String seconds2HH_mm(long seconds) {

        long h = 0;
        long m = 0;
        long s = 0;
        long temp = seconds % 3600;

        if (seconds > 3600) {
            h = seconds / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            m = seconds / 60;
            if (seconds % 60 != 0) {
                s = seconds % 60;
            }
        }

        String dh = h < 10 ? "0" + h : h + "";
        String dm = m < 10 ? "0" + m : m + "";
        String ds = s < 10 ? "0" + s : s + "";

        return dh + ":" + dm;
    }
}
