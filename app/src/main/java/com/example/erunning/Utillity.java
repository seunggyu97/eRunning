package com.example.erunning;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

public class Utillity {
    public Utillity(){/* */}

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isStorageUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/e-running-735bb.appspot.com/o/post");
    }

    public static String storageUrlToName(String url){
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2f").length -1];
    }
}
