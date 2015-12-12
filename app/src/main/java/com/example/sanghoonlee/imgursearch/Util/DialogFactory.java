package com.example.sanghoonlee.imgursearch.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class DialogFactory {
    public  static final int NETWORK_ERROR_DIALOG = 0;

    public static Dialog createSimpleDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog;
    }

    public static Dialog createDialog(Context context, int type) {
        switch(type) {
            case NETWORK_ERROR_DIALOG:
            default:
                return createSimpleDialog(context, "Network Error",
                        "Please make sure you are connected to internet");
        }
    }
}
