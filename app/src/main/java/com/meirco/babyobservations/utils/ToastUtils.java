package com.meirco.babyobservations.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by nitsa_000 on 21-Aug-15.
 */
public class ToastUtils {
    public static void shorterToast(Context context, String text, int duration) {
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }
}
