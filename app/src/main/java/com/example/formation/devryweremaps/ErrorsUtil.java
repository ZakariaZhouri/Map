package com.example.formation.devryweremaps;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by formation on 09/11/15.
 */
public class ErrorsUtil {

    public static void poupUpError(Context context, final String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.show();
    }

}
