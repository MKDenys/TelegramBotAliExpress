package com.mk;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    static boolean isUrl(String text){
        try {
            new URL(text);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }
}
