package utils;

import org.apache.log4j.Logger;

public class StringSwitchUtil {
    private static Logger log=Logger.getLogger(StringSwitchUtil.class);

    /**
     * 将16进制串转为字符串
     * @param s  hexString
     * @return String
     */
    public static String hexStringToString(String s){
        log.info("hexStringToString...");
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace("\\u", "");
        log.info("处理后的字符串为["+s+"]...");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "unicode");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 字符转转16进制
     * @param s  String
     * @return hexString
     */
    public static String StringToHexString(String s){
        log.info("StringToHexString...");
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + "\\u" + s4;
        }
        return str;
    }
}
