package com.mk;

import java.util.prefs.Preferences;

public class PreferenceSettingsManager {

    private static final String SELECTED_CHAT_ID = "selected_chat_id";
    private static final String DATE_CURRENCY_UPDATE = "date_currency_update";
    private static final String USD_RATE = "usd_rate";
    private static final String RUB_RATE = "rub_rate";
    private static final String AUTO_POSTING_KEY = "auto_posting";
    private static final String SCHEDULED_POSTING_KEY = "scheduled_posting";
    private static Preferences prefs;

    public static void init(){
        prefs = Preferences.userNodeForPackage(com.mk.Bot.class);
    }

    public static void setSelectedChannelId(long chatId){
        prefs.putLong(SELECTED_CHAT_ID, chatId);
    }

    public static long getSelectedChannelId(){
        long chatId = 0;
        chatId = prefs.getLong(SELECTED_CHAT_ID, chatId);
        return chatId;
    }

    public static void removeSelectedChannelId() {
        prefs.remove(SELECTED_CHAT_ID);
    }

    public static void setDateCurrencyUpdate(long date) {
        prefs.putLong(DATE_CURRENCY_UPDATE, date);
    }

    public static long getDateCurrencyUpdate() {
        long date = 0;
        date = prefs.getLong(DATE_CURRENCY_UPDATE, date);
        return date;
    }

    public static void setUsdRate(double rate) {
        prefs.putDouble(USD_RATE, rate);
    }

    public static double getUsdRate() {
        double rate = 0;
        rate = prefs.getDouble(USD_RATE, rate);
        return rate;
    }

    public static void setRubRate(double rate) {
        prefs.putDouble(RUB_RATE, rate);
    }

    public static double getRubRate() {
        double rate = 0;
        rate = prefs.getDouble(RUB_RATE, rate);
        return rate;
    }

    public static boolean getAutoPosting() {
        boolean autoPosting = false;
        autoPosting = prefs.getBoolean(AUTO_POSTING_KEY, autoPosting);
        return autoPosting;
    }

    public static void setAutoPosting(boolean value) {
        prefs.putBoolean(AUTO_POSTING_KEY, value);
    }

    public static boolean getScheduledPosting() {
        boolean scheduledPosting = false;
        scheduledPosting = prefs.getBoolean(SCHEDULED_POSTING_KEY, scheduledPosting);
        return scheduledPosting;
    }

    public static void setScheduledPosting(boolean value) {
        prefs.putBoolean(SCHEDULED_POSTING_KEY, value);
    }
}
