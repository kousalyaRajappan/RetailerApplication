package com.za.toptitup.loginlibrary.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.za.toptitup.loginlibrary.R;
import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;

public class AlarmDialog extends AppCompatActivity {


//    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    MyApiEndpointInterface apiService_sync = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        final Dialog dialog = new Dialog(AlarmDialog.this);
//        Dialog dialog=new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(R.layout.dialog_alarm);

        dialog.setCancelable(false);

        ImageView img = dialog.findViewById(R.id.img);
        TextView txt_read = dialog.findViewById(R.id.txt_read);
        get_adds(img);

        txt_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
    private void get_adds(ImageView img) {


        final Call<JsonObject> call = apiService_sync.get_adds_(Topitup.TIU_LICENSE);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                String path = null;

                try {

                    if(response.body().toString().contains("ad_path")) {

                        String ad_path = response.body().get("ad_path").toString().replace("\"", "");
                        byte[] decodedString = Base64.decode(ad_path, Base64.DEFAULT);
                        Log.e("image path",".........."+ad_path);
                        String img_path = new String(decodedString);

                        try {
                            RequestOptions options = new RequestOptions();
                            options.fitCenter();
                            Glide.with(AlarmDialog.this)
                                    .load(img_path)
                                    .apply(RequestOptions.signatureOf(new ObjectKey(String.valueOf(System.currentTimeMillis()))))
                                    .apply(options)
                                    .into(img);
                        } catch (Exception e) {
                        }

                   /* Glide.with(AlarmDialog.this).asGif().load(path).into(img);
                    Glide.with(AlarmDialog.this)
                            .load("https://demo.itopitup.co.za/assets/img/ads/ad1.gif")
                            .apply(options)
                            .into(img);*/
                    }
                } catch (Exception ex) {
                    //
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {



                //Timber.i("SPLASH " +  t.getMessage());
//                Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }

}