package com.superaligator.konferencja.utils;


import android.content.Context;

import com.superaligator.konferencja.definitions.EventType;
import com.superaligator.konferencja.network.Comunicator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OtherUtils {

    public static boolean checkPlayServices(Context ctx) {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(ctx);
//        if (result != ConnectionResult.SUCCESS) {
//
//            return false;
//        }
        return true;
    }

    public static EventType eventTypeFromString(String str) {
        try {
            return EventType.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String prepareNotIn(List<String> arr){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        String s = null;
        for(String el: arr){
            if(s != null){
                sb.append(s);
            }else{
                s = ",";
            }
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    public static String listJoin(List<String> arr, String glue) {
        StringBuilder sb = new StringBuilder();
        String s = null;
        for(String el: arr){
            if(s != null){
                sb.append(s);
            }else{
                s = glue;
            }
            sb.append(el);
        }
        return sb.toString();
    }

    public static int randomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static String getXApp(String userAgent) {
        if (Comunicator.getInstance().APP_KEY == null)
            return null;

        String tmpUserAgent = userAgent.substring(2, userAgent.length());
        String phrase = userAgent + Comunicator.getInstance().APP_KEY + tmpUserAgent;
        String tk = md5(phrase);
        //2 znak zamien miejscami z 4
        String res = zamianaMiejscami(tk, 2, 4);
        return res;
    }

    public static String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Zamienia miejscami 2 znaki
     * Przykład ("krowa" 0, 2) -> orkwa
     */
    public static String zamianaMiejscami(String txt, int pos1, int pos2) {
        if (txt == null)
            return null;
        if (pos1 > pos2)
            throw new IllegalArgumentException("Bad parameters");

        char[] txtChars = txt.toCharArray();
        char z1 = txtChars[pos1];
        txtChars[pos1] = txtChars[pos2];
        txtChars[pos2] = z1;
        return String.valueOf(txtChars);
    }


    public static String getGluePass(int[] arr) {
        String ret = "";
        for (int i = 0; i < arr.length; i++) {
            ret += (char) arr[i];
        }
        return ret;
    }
}
