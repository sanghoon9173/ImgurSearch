package com.example.sanghoonlee.imgursearch.Util;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class Util {
    public static void hideKeyboardIfOpen(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
