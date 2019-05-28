package com.example.loghi.a3o_media_player;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenSaver {
    private final static String SHARED_PREF_NAME = "com.example.loghi.a3o_media_player.SHARED_PREF_NAME";
    private final static String TOKEN_KEY = "com.example.loghi.a3o_media_player.TOKEN_KEY";
    private final static String IP = "com.example.loghi.a3o_media_player.IP";

    public static String getToken(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    public static void setToken(Context c, String token) {
        SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }
    public static void setIP(Context c,String ip) {
        SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IP, ip);editor.apply();
        editor.apply();
    }
    public static String getIP(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(IP, null);
    }
    public static void clear(Context c){
        SharedPreferences pref = c.getSharedPreferences(TOKEN_KEY,0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();

        c.getSharedPreferences("com.example.loghi.a3o_media_player.SHARED_PREF_NAME", 0).edit().clear().commit();

    }

}