package com.za.toptitup.loginlibrary;



import android.content.Context;
import android.content.Intent;


public final class LoginLibraryLauncher {

    private LoginLibraryLauncher() {
        // prevent instantiation
    }

    /**
     * Entry point to Login Library
     */
    public static void launch(Context context) {
        Intent intent = new Intent(context, com.za.toptitup.loginlibrary.activitySplashScreen.class);

        // Required when launching from Application / non-Activity context
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}

