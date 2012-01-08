package utils;


public class MiscUtils {

    private MiscUtils(){}
    
    public static String intToHex(int i) {
        String hex = Integer.toHexString(i).toUpperCase();
        String padding = "00000000";
        padding = padding.substring(0, 8 - hex.length());
        hex = padding + hex;
        hex = hex.substring(0, 4) + " " + hex.subSequence(4, 8);
        
        return hex;
    }
}
