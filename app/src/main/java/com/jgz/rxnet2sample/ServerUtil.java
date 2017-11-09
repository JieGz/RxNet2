package com.jgz.rxnet2sample;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;


/**
 * Created by gdjie on 2017/1/4.
 */

public class ServerUtil {

    private static String app_secret = "f290ba094eb84e03a631c9976bed3990";

    /**
     * 加密算法
     *
     * @param text
     * @return
     */
    private static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static TreeMap<String, Object> sign(String[] keys, Object[] vs) {
        TreeMap<String, Object> tree = new TreeMap<>();
        TreeMap<String, Object> values = new TreeMap<>();
        if (keys != null && vs != null && keys.length == vs.length) {
            for (int i = 0; i < vs.length; ++i) {
                values.put(keys[i], vs[i]);
            }
        }
        String valuesJson = new Gson().toJson(values);
        tree.put("sign", encode(valuesJson + app_secret));
        tree.put("data", values);
        tree.put("os", "ANDROID");
        return tree;
    }

    public static TreeMap<String, Object> sign() {
        TreeMap<String, Object> tree = new TreeMap<>();
        TreeMap<String, Object> values = new TreeMap<>();
        String valuesJson = new Gson().toJson(values);
        tree.put("sign", encode(valuesJson + app_secret));
        tree.put("data", values);
        tree.put("os", "ANDROID");
        return tree;
    }
}
