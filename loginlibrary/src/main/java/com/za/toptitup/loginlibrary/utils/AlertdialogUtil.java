package com.za.toptitup.loginlibrary.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import com.za.toptitup.loginlibrary.BaseActivity;

public class AlertdialogUtil extends BaseActivity {
    private final Context context;
    private AlertDialog.Builder alertDialog;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;

    public AlertdialogUtil(Context context) {
        this.context = context;
    }

    public void setOkayAlert(String setTitle, String setMessage, String okay, DialogInterface.OnClickListener listener) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(setTitle);
        alertDialog.setMessage(setMessage);
        alertDialog.setPositiveButton(okay, listener);
        dialog = alertDialog.create();
        dialog.show();
    }

    public void setYesNoAlert(String setTitle, String setMessage,String yes,String no,DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listener2) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(setTitle);
        alertDialog.setMessage(setMessage);
        alertDialog.setPositiveButton(yes, listener);
        alertDialog.setNegativeButton(no, listener2);
        dialog = alertDialog.create();
        dialog.show();

    }

    public void customOkAlertDialog(Drawable icon, String title, String message, String okay, DialogInterface.OnClickListener listener) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(icon);
        alertDialog.setPositiveButton(okay, listener);
        dialog = alertDialog.create();
        dialog.show();
    }

    public void customYesOrNoAlert(Drawable icon, String title, String message, String yes,String no ,DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listener2) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(icon);
        alertDialog.setPositiveButton("Yes", listener);
        alertDialog.setNegativeButton("No", listener2);
        dialog = alertDialog.create();
        dialog.show();
    }

    public void setProgressDialog(String title, String message, int delay) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCancelable(false);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        }, delay);

    }

    public void setIconProgressDialog(Drawable icon, String title, String message, int delay) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIcon(icon);
        progressDialog.show();
        progressDialog.setCancelable(false);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        }, delay);

    }

    public void setCustomLayoutAlert(View customLayout,String okay,String title ,DialogInterface.OnClickListener listener) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(customLayout);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(okay, listener);
        dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }



}
