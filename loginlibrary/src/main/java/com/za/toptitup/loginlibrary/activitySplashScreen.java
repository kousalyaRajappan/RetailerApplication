package com.za.toptitup.loginlibrary;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.za.toptitup.loginlibrary.utils.NetworkUtils.isNetworkAvailable;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.model.GetUpdateAll;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.fin_balance;
import com.za.toptitup.loginlibrary.model.pos_users;
import com.za.toptitup.loginlibrary.model.service_provider;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.utils.Topitup;
import com.za.toptitup.loginlibrary.utils.UserException;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;


public class activitySplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private static final int REQUEST_EXTERNALRESULT = 100;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    Realm realm;
    Context mContext;
    String enable_vas;
    String noticeid;
    String customer_id;
    String enable_rpt_monthly_deposit = "0", enable_rpt_comm_statement = "0";
    String spiver;
    SharedPreferences settings;
    //***
    RealmResults<service_provider_item_settings> service_provider_item_settings;
    int size;
    com.za.toptitup.loginlibrary.model.service_provider_item_settings service_provider_item_setting;
    RealmResults<service_provider_item> service_provider_items;
    boolean complete_step_1 = false;
    boolean complete_step_2 = false;
    boolean complete_step_3 = false;
    boolean complete_step_4 = false;
    String advertId = "";
    private ImageView mLogo;
    private boolean busy_loading = false;
    private int[][] u_infor;

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        FullscreenCall();

        setContentView(R.layout.activity_splash_screen);


        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        mLogo = findViewById(R.id.logo_assetforce_splash);

        animation1();
        settings = getSharedPreferences("TIUPREF", 0);
     /*   SharedPreferences.Editor editor = settings.edit();
        editor.putString("printer", "inner");
        editor.commit();*/
        advertId = settings.getString("advert_id", "");
      //  settings.getString("printer", "inner");

        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //AAUpdaterController.init(this,Topitup.BASE_URL_UPDATE + "api/get_android_update");


        if (Topitup.TIU_LICENSE.equals("")) {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(activitySplashScreen.this, activity_login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    overridePendingTransition(0, R.anim.splashfadeout2);
                }
            }, SPLASH_TIME_OUT);

        } else {


            get_update_all();


            get_status_full();

            get_update_users();

            get_balance();
            if (!Topitup.TIU_LICENSE.equals("")) {
                if (checkPermission()) {
                    ///method to get Images
                    getAddslogin();
                } else {
                    requestPermission();
                }
                //  displayAddDialog();

            }


        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, CAMERA}, REQUEST_EXTERNALRESULT);
        }
    }

    private boolean checkPermission() {
        int result4 = ContextCompat.checkSelfPermission(this, CAMERA);

        int result = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);


        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }



    private void getAddslogin() {

        Call<JsonObject> call = apiService.get_adds_login(Topitup.TIU_LICENSE);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();

                        return;
                    }
                    try {

                        if (!response.headers().get("Server").equals("TIU")) {

                            throw new UserException("Please check your internet connection!");
                        }
                        if (response.body().toString().toLowerCase().contains("ad_path = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                        } else {
                            if (response.body().toString().contains("ad_path")) {
                                String advert_id = response.body().get("advert_id").toString().replace("\"", "");
                                String ad_path = response.body().get("ad_path").toString().replace("\"", "");

                                Log.e("ad path ","............"+ad_path);
                                if (advertId.equals("")) {
                                    doAdvertAdd(ad_path, advert_id);
                                } else {
                                    if (!advert_id.equals(advertId)) {
                                        Log.i("filepath", "file.......1111111111....");

                                        doAdvertAdd(ad_path, advert_id);
                                    } else {
                                        Log.i("filepath", "file.......2222222222....");

                                        settings = getSharedPreferences("TIUPREF", 0);
                                        String filepath = settings.getString("file_path", "");

                                        if (filepath.equals("")) {
                                            Log.i("filepath", "file.......333333333....");

                                            doAdvertAdd(ad_path, advert_id);

                                        } else {
                                            Log.i("filepath", "file.......4444444444444....");

                                            File file = new File(filepath);
                                            DisplayMetrics displayMetrics = new DisplayMetrics();
                                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                            int height = displayMetrics.heightPixels;
                                            int width = displayMetrics.widthPixels;

                                            Log.i("filepath" + pxToDp(width), filepath);
                                            Log.i("files" + height, file.getAbsolutePath());


                                            if (file.exists()) {
                                                Log.i("files" + height, "if.........." + file.getAbsolutePath());
                                                String data;
                                                if(Topitup.DEVICE_TYPE.equals("TABLET")){
                                                    data = "<body> <img   src = \"" + file.getAbsolutePath() + "\"  width=\"280\" height=\"200\"/></body>";
                                                } else {
                                                    data = "<body> <img   src = \"" + file.getAbsolutePath() + "\"  width=\"360\" height=\"100\"/></body>";

                                                }

                                                SharedPreferences.Editor editor = settings.edit();
                                                editor.putString("get_advs", data);
                                                editor.commit();
                                                Log.i("kousi", data);
//                                            String data = "<body> <img   src = \"" + file.getAbsolutePath() + "\"  width=\"360\" height=\"100\"/></body>";
                                         /*   img_gif.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", null);
                                            img_gif.setBackgroundColor(Color.TRANSPARENT);*/

                                            } else {
                                                Log.i("files" + height, "else..." + file.getAbsolutePath());
                                                doAdvertAdd(ad_path, advert_id);
                                            }
                                        }

                                    }
                                }

                            }
                        }
                    } catch (Exception ex) {
                       /* if (response.raw() != null)
                            response.raw().close();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void doAdvertAdd(String ad_path, String advert_id) {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("advert_id", advert_id);
        editor.commit();
        byte[] decodedString = Base64.decode(ad_path, Base64.DEFAULT);
        String img_path = new String(decodedString);

        if(isValidUrl(img_path)){
            downloadBitmap(img_path);

        }else{
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putString("get_advs", "local");
            editor1.commit();
        }
    }
    public boolean isValidUrl(String url) {
        String urlPattern = "^(http|https)://.*$";
        return url != null && url.matches(urlPattern);
    }
    private void downloadBitmap(String url) {
        Glide.with(this)
                .download(url)
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
//                        progressDailog.dismiss();
//                        Toast.makeText(activity_login.this, "Error saving", Toast.LENGTH_SHORT).show();
                        Log.i("filepath", "file.......14141414...." + isFirstResource);
                        SharedPreferences.Editor editor1 = settings.edit();
                        editor1.putString("get_advs", "local");
                        editor1.commit();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
//                        progressDailog.dismiss();
                        try {
                            Log.i("filepath", "file.......12121212212....");
                            saveGifImage(activitySplashScreen.this, getBytesFromFile(resource), createName(url));
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    public String createName(String url) {

        String name = url.substring(url.lastIndexOf('/') + 1);
        String NoExt = name.substring(0, name.lastIndexOf('.'));

        /*if(!ext.equals(".gif")){
            name = NoExt + ".jpg";
        }*/
        return name;
    }

    public void saveGifImage(Context context, byte[] bytes, String imgName) {
        FileOutputStream fos = null;
        try {

            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File customDownloadDirectory = new File(externalStoragePublicDirectory, "topitup");
            if (!customDownloadDirectory.exists()) {
                boolean isFileMade = customDownloadDirectory.mkdirs();
            }
            if (customDownloadDirectory.exists()) {
                File file = new File(customDownloadDirectory, imgName);
                fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
                fos.close();
                if (file != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, file.getName());
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
                    values.put(MediaStore.Images.Media.DESCRIPTION, "");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("file_path", file.getAbsolutePath());
                    editor.commit();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // will results in a much smaller image than the original
                    Log.i("filepathh", file.getAbsoluteFile().toString());
                    final Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                    String data = "";

                    if(Topitup.DEVICE_TYPE.equals("TABLET")){
                        data = "<body> <img   src = \"" + file.getAbsolutePath() + "\"  width=\"100\" height=\"360\"/></body>";
                    } else {
                        data = "<body> <img   src = \"" + file.getAbsolutePath() + "\"  width=\"360\" height=\"100\"/></body>";

                    }
                    SharedPreferences.Editor editor1 = settings.edit();
                    editor1.putString("get_advs", data);
                    editor1.commit();
                   /* img_gif.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", null);
                    img_gif.setBackgroundColor(Color.TRANSPARENT);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get_status_full() {
     /*   realm.beginTransaction();
        realm.where(service_provider_item_settings.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();*/

        busy_loading = true;

        //Timber.i("APP VER: " + Topitup.APP_VERSION);

        Call<GetUpdateAll> call = apiService.get_status_full(Topitup.TIU_LICENSE, "", Topitup.APP_VERSION, spiver, "");
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {


                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    try {

                        GetUpdateAll res = response.body();

                    } catch (Exception ex) {
                        if (response.raw()!= null)
                            response.raw().close();
                        //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();

                    }
                }
                complete_step_4 = true;
                check_complete();


            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 3000, true).show();
                //Timber.e("GetUpdateAll error: " + t.getMessage());

                complete_step_4 = true;
                check_complete();

            }
        });


    }

    private void get_update_users() {

        // Timber.i("SPLASH get_update_users");

        busy_loading = true;


        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>() {

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Timber.e(response.body());
                //Toasty.error(mContext, response.toString(), 8000, true).show();


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {

                    List<pos_users> result = response.body();

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(result);
                    realm.commitTransaction();

                    // res = response.body().string();

                } catch (Exception ex) {
                    //
                    response.body().clear();
                }

                if (res.contains("<error><err>")) {

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK


                }

                complete_step_2 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();
                Log.e("result response","posuser.......444444444444444.......");

                Log.e("error",".....11111111111111111......."+t.getMessage());

                complete_step_2 = true;
                check_complete();

            }

        });


    }

    private void get_update_all() {
        //Timber.i("result-manoj="+"called");
        busy_loading = true;

//        RealmResults<GetUpdateAll> result2 = realm.where(GetUpdateAll.class)
//                .equalTo("customer_id", Topitup.CUSTOMER_ID)
//                .findFirst();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();


        if (tiu_settings == null) {
            spiver = "0";
        } else {
            spiver = tiu_settings.spi_ver;
        }
        Call<GetUpdateAll> call = apiService.get_update_all(Topitup.TIU_LICENSE, spiver, "", Topitup.APP_VERSION);
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {

                try {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }

                    //Toasty.error(mContext, "Could not log in", 3000, true).show();


                    //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                    if (!response.headers().get("Server").equals("TIU")) {
                        try {
                            throw new UserException("Please check your internet connection!");
                        } catch (UserException e) {

                        }
                    }

                    if (response.body().toString().toLowerCase().contains("customer_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                        //

                    } else {

                        GetUpdateAll result = response.body();

                        realm.beginTransaction();

                        String service_provider_data = "";

                        byte[] bytes = Base64.decode(result.published_data, Base64.DEFAULT);
                        service_provider_data = new String(bytes);

                        Log.i("serviceProviderdata", service_provider_data);

                        result.service_provider_data = service_provider_data;

                        customer_id = result.customer_id;

                        //Toasty.error(mContext,"enable_rpt_comm_statement="+result.enable_rpt_comm_statement, 12000, true).show();

                        realm.copyToRealmOrUpdate(result);
                        realm.commitTransaction();

                        update_spi(service_provider_data);


                    }

                } catch (Exception ex) {


                    if (response != null && response.body() != null)
                        response.body();
                    Toasty.error(mContext, "ERR : from upgra " + ex.getMessage(), 3000, true).show();
                }

              /*  activity_product_settings product_settings = new activity_product_settings();
                product_settings.updateSPIsettings();*/
                complete_step_1 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {
                if (isNetworkAvailable(activitySplashScreen.this)) {

                    Toasty.error(mContext, t.getMessage(), 100000, true).show();
                }else {
                    Toasty.error(mContext,"Please check your internet connection! ", 100000, true).show();

                }
                Timber.e("GetUpdateAll error: " + t.getMessage());

                complete_step_1 = true;
                check_complete();

            }
        });


    }

    private void check_complete() {
        Log.e("check complete", complete_step_1 + "..." + complete_step_2 + ".." + complete_step_3 + "....." + complete_step_4);

        if (complete_step_1 && complete_step_2 && complete_step_3 && complete_step_4) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // Toasty.info(mContext, "Saved! Logged out after 3 minutes on inactivity", 20000, true).show();

//                    call_endpoints_config();
                 //   printmethod();


                    Intent i = new Intent(activitySplashScreen.this, activity_login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    overridePendingTransition(0, R.anim.splashfadeout2);
                }
            }, SPLASH_TIME_OUT);
        }


    }


    public void get_balance() {

        final Call<fin_balance> call = apiService.get_balance_new(Topitup.TIU_LICENSE, "1");

        call.enqueue(new Callback<fin_balance>() {

            @Override
            public void onResponse(Call<fin_balance> call, Response<fin_balance> response) {


                if (response.isSuccessful()) {
                    if (!response.headers().get("Server").equals("TIU")) {
                        Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                        return;
                    }
                    try {

                        fin_balance res = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(res);
                        realm.commitTransaction();

                    } catch (Exception ex) {
                      /*  if (response.raw() != null)
                            response.raw().close();*/
                    }
                    complete_step_3 = true;
                    check_complete();
                }


            }

            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {
                Log.i("filepath", "file....balance...1111111111...."+t.getMessage());

                complete_step_3 = true;
                check_complete();

            }

        });


    }


    private void update_spi(String service_provider_data) {


        realm.beginTransaction();
        realm.where(service_provider.class).findAll().deleteAllFromRealm();
        realm.where(service_provider_item.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();


        BufferedReader bufReader = new BufferedReader(new StringReader(service_provider_data));

        try {

            String line = null;
            Integer last_sp_id = 0;

            while ((line = bufReader.readLine()) != null) {


                // Timber.i(line);

                //System.out.println(line);

                String[] myData = line.split("\\^");
                //for (String s: myData) {

                //if (s.length() > 0) {

                if (myData[0].equals("p")) {

                    service_provider new_sp = new service_provider();
                    new_sp.provider_id = Integer.parseInt(myData[1]);

                    last_sp_id = Integer.parseInt(myData[1]);

                    // Timber.i("SPI : " + line );

                    if (myData.length > 2) new_sp.provider_desc = myData[2];
                    if (myData.length > 3) new_sp.provider_message = myData[3];

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_sp);
                    realm.commitTransaction();

                } else {

                    service_provider_item new_spi = new service_provider_item();
                    new_spi.service_provider_item_id = Integer.parseInt(myData[0]);
                    new_spi.service_provider_id = last_sp_id;

//                        Timber.i("SPI : SPI -  " + myData[0] );
//                        if (last_sp_id == 19) {
//                            Timber.i("SPI :  " + String.valueOf(new_spi.service_provider_item_id));
//                        }
//

                    boolean item_show_value = Integer.parseInt(myData[7]) == 1;

                    new_spi.item_desc = myData[1];
                    new_spi.item_btn_desc = myData[2];
                    //new_spi.item_print_desc = myData[3];
                    new_spi.item_print_desc = myData[4];
                    new_spi.item_value = myData[5];
                    new_spi.item_type = Integer.parseInt(myData[6]);
                      /*  if(Integer.parseInt(myData[6])==1)
                        Log.i("NAMEMANOJ="+myData[4], myData[5]+"size of the data....splash......"+Integer.parseInt(myData[6]));*/
                    new_spi.item_show_value = item_show_value;
                    new_spi.item_position = Integer.parseInt(myData[8]);
                    // new_spi.item_barcode = myData[9];
                  /*  new_spi.item_barcode = myData[9];
                    new_spi.period_label = myData[10];
                    new_spi.social_type = myData[11];*/
                    new_spi.item_barcode = myData[9];
                    new_spi.social_type = myData[10];
                    new_spi.period_label = myData[11];
                 /*   if (myData.length == 10) {
                        new_spi.item_barcode = myData[9];
                    } else {
                        if (myData.length == 12) {
                            new_spi.item_barcode = myData[9];
                            new_spi.period_label = myData[10];
                            new_spi.social_type = myData[11];
                        }

                    }*/
                   /* Log.e("TestInfo Service", myData[1] + "," + myData[2] + "," +
                            myData[3] + "," + myData[4] + "," +
                            myData[5] + "," + myData[6] + "," + myData[7] + "," + myData[8] + "," + myData[9] + "," + myData[10] + "," + myData[11]
                    );*/
                    new_spi.item_value_int = Double.parseDouble(myData[5]);

                    // Timber.i("SPI :  " + String.valueOf( Integer.parseInt(myData[5])));

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new_spi);
                    realm.commitTransaction();

                }
                //  System.out.println(s);

                // }

                // }


            }

        } catch (Exception ex) {

            Timber.i("SPI Error: " + ex.getMessage());

        }


/**
 *
 * Service provider items
 *
 */
    /*
        service_provider_items = realm.where(service_provider_item.class).findAll();

        size = service_provider_items.size();

        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("enable", 1).findAll();
        realm.beginTransaction();
        for (int i = 0; i < service_provider_item_settings.size(); i++) {
            service_provider_item_settings.get(i).setEnable(0);
        }

        realm.commitTransaction();
        for (int k = 0;k<size;k++) {
            service_provider_item sp_item = service_provider_items.get(k);
            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", sp_item.service_provider_item_id).findFirst();
            realm.beginTransaction();
            if(service_provider_item_setting==null) {
                //Toasty.info(mContext, "if loop ", 8000, true).show();
                service_provider_item_settings sp_settings=new service_provider_item_settings();
                sp_settings.setService_provider_id(sp_item.service_provider_id);
                sp_settings.setService_provider_item_id(sp_item.service_provider_item_id);
                sp_settings.setItem_desc(sp_item.item_desc);
                sp_settings.setItem_position(sp_item.item_position);
                sp_settings.setVisible_admin(true);
                sp_settings.setVisible_cashier(true);
                realm.insertOrUpdate(sp_settings);

            }else {
                service_provider_item_setting.setEnable(1);
                service_provider_item_setting.setItem_desc(sp_item.item_desc);
            }
            realm.commitTransaction();
        }

*/

//        RealmResults<service_provider_item> service_provider_items;
//        service_provider_items = realm.where(service_provider_item.class)
//                .equalTo("service_provider_id", 19)
//                .findAll();

        //Timber.i("TOTAL STUFF : " + String.valueOf(service_provider_items.size()));


    }


    public void onDestroy() {

        super.onDestroy();

    }


    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(600);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(600);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void FullscreenCall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
