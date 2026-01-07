package com.za.toptitup.loginlibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import com.za.toptitup.loginlibrary.model.ItemSPI;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class HomeAdapterServiceProvider extends RecyclerView.Adapter<HomeAdapterServiceProvider.ViewHolder> {

    private final ArrayList<ItemSPI> mValues;
    private final Context mContext;
    protected ItemListener mListener;
    SharedPreferences settings;
    String enable_airtime = "", enable_blue = "", enable_one_for = "", enable_ott = "", enable_flexipin = "";
    private boolean isEnable = false;

    public HomeAdapterServiceProvider(Context context, ArrayList<ItemSPI> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_service_provider, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position), position);
        viewHolder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(ItemSPI item,Boolean isEnable);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private final TextView textViewcat;
        private final TextView textDeno;
        private final ImageView imageView;
        private final ImageView imageView1;
        private final RelativeLayout relativeLayout;
        public final RelativeLayout rl_select;
        private ItemSPI item;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
            textViewcat = v.findViewById(R.id.textViewcat);
            textDeno = v.findViewById(R.id.textDeno);
            imageView = v.findViewById(R.id.imageView);
            imageView1 = v.findViewById(R.id.imageView1);
            relativeLayout = v.findViewById(R.id.relativeLayout);
            rl_select = v.findViewById(R.id.rl_select);
        }

        public void setData(ItemSPI item, int position) {
            this.item = item;
            String imageName = mContext.getResources().getResourceName(item.drawable);
            settings = mContext.getSharedPreferences("TIUPREF", 0);


            enable_airtime = settings.getString("enable_airtime", "0");
            enable_one_for = settings.getString("enable_oneforu", "0");
            enable_blue = settings.getString("enable_blue", "0");
            enable_ott = settings.getString("enable_ott", "0");
            enable_flexipin = settings.getString("enable_flexepin", "0");
            for (int i = 0; i < activity_spi.selectedProviderIds.size(); i++) {
                if (activity_spi.selectedProviderIds.get(i).pos == item.pos) {
                    rl_select.setVisibility(View.VISIBLE);

                }
            }
            isEnable = false;

            if (imageName.contains("prov1") || imageName.contains("prov2") || imageName.contains("prov3") || imageName.contains("prov_worldcall") || imageName.contains("prov10") || imageName.contains("prov_blue") || imageName.contains("prov_oneforyou") || imageName.contains("prov_oneforyou") || imageName.contains("ringas") || imageName.contains("prov_ott")||imageName.contains("prov12_small")) {

                Log.e("item text", "text............." + item.text);
                if (item.text != null) {
                    if (position == 0) {

                        if (item.text.equalsIgnoreCase("input amount")) {
                            if (imageName.contains("prov10")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.telkom_input));

                            } else if (imageName.contains("prov1")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.vodacom_input));

                            } else if (imageName.contains("prov2")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.mtn_input));

                            } else if (imageName.contains("prov3")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.cell_input));

                            } else if (imageName.contains("prov_worldcall")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.telkom_input));

                            } else if (imageName.contains("prov10")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.telkom_input));

                            } else if (imageName.contains("prov_blue")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.bluvlohcer_inputamoun));

                            } else if (imageName.contains("prov_oneforyou")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.one_voucher_input));

                            } else if (imageName.contains("ringas")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.ringas_input));

                            } else if (imageName.contains("prov_ott")) {
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.prov_ott_nnew_input));

                            }

                            if ((imageName.contains("prov1") || imageName.contains("prov2") || imageName.contains("prov3") || imageName.contains("prov10") || imageName.contains("ringas")) && enable_airtime.equals("2")) {
                            } else if(imageName.contains("prov_oneforyou") && enable_one_for.equals("2")){

                            }else if(imageName.contains("prov_blue") && enable_blue.equals("2")){

                            }else if(imageName.contains("prov_ott") &&  enable_ott.equals("2")){

                            }else {

                                View overlay = new View(mContext);
                                overlay.setBackgroundColor(Color.parseColor("#80FFFFFF")); // Light translucent white
                                RelativeLayout.LayoutParams overlayParams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT
                                );
                                relativeLayout.addView(overlay, overlayParams);

                                // Add a TextView programmatically
                                TextView textView = new TextView(mContext);
                                textView.setText("Disabled");
                                textView.setTextColor(Color.RED); // Text color


                                textView.setTypeface(null, Typeface.BOLD); // Make text bold
                                textView.setTextSize(23); // Text size in sp
                                textView.setGravity(Gravity.CENTER);

                                // Set layout parameters for the TextView
                                RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                textParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); // Center horizontally
                                textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); // Align to the top
                                textParams.setMargins(0, 10, 0, 0); // Add margin to position it above the middle (150dp from the top)

                                // Add the TextView to the RelativeLayout
                                relativeLayout.addView(textView, textParams);
                            }

                        }
                        else {
                            textView.setText(item.text);
                            textView.setTextSize(12);
                            imageView.setImageResource(item.drawable);

                        }

                    } else {
                        textView.setText(item.text);
                        textView.setTextSize(12);
                        imageView.setImageResource(item.drawable);

                    }
                }
            } else {
                textView.setText(item.text);
                imageView.setImageResource(item.drawable);

            }
            textViewcat.setText(item.bmenu);
            if (item.text != null) {
                if (item.text.toLowerCase().contains("whatsapp")) {
                    imageView1.setImageResource(R.drawable.whatsapp_icon);
                } else if (item.text.toLowerCase().contains("facebook")) {
                    imageView1.setImageResource(R.drawable.facebook);
                } else if (item.text.toLowerCase().contains("youtube")) {
                    imageView1.setImageResource(R.drawable.youtube);
                } else if (item.text.toLowerCase().contains("tik tok")) {
                    imageView1.setImageResource(R.drawable.tiktok);
                } else if (item.text.toLowerCase().contains("twitter")) {
                    imageView1.setImageResource(R.drawable.twitter);
                } else {
                    imageView1.setImageResource(0);
                }
            }

            //Toasty.error(mContext, textViewcat.getText(), Toast.LENGTH_LONG).show();
            textDeno.setVisibility(View.VISIBLE);
            if (textViewcat.getText().equals("Data") || textViewcat.getText().equals("Social+")) {
                textDeno.setBackgroundColor(Color.parseColor("#bf2035"));
                textView.setTextSize(18);
            } else if (textViewcat.getText().equals("IKEJA")) {
                textDeno.setVisibility(View.GONE);
                imageView.getLayoutParams().width = 475;
            } else {
                textDeno.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textView.setTextSize(24);

            }


            textDeno.setText(item.deno);
        }

        @Override
        public void onClick(View view) {


            if (mListener != null) {
                String imageName = mContext.getResources().getResourceName(item.drawable);

                if ((imageName.contains("prov1") || imageName.contains("prov2") || imageName.contains("prov3") || imageName.contains("prov10") || imageName.contains("ringas")) && enable_airtime.equals("2")) {
                isEnable = true;
                } else if(imageName.contains("prov_oneforyou") && enable_one_for.equals("2")){
                    isEnable = true;

                }else if(imageName.contains("prov_blue") && enable_blue.equals("2")){
                    isEnable = true;

                }else if(imageName.contains("prov_ott") &&  enable_ott.equals("2")){
                    isEnable = true;

                }else {
                    isEnable = false;

                }
                String product_admin_disable = mContext.getString(R.string.product_admin_disable);
                String product_cashier_disable = mContext.getString(R.string.product_cashier_disable);
                if (Topitup.IS_ADMIN.equals("1")) {
                    if (item.visible_admin) {
                        if (activity_main.isMultiVoucherSelected) {
                            performClick(item, rl_select);

                        } else {
                            mListener.onItemClick(item,isEnable);

                        }

                    } else
                        Toasty.error(mContext, product_admin_disable, 8000, true).show();
                } else {

                    if (item.visible_admin && item.visible_cashier) {
                        if (activity_main.isMultiVoucherSelected) {

                        } else {
                            mListener.onItemClick(item,isEnable);

                        }

                    } else
                        Toasty.error(mContext, product_cashier_disable, 8000, true).show();

                }
            }
        }
    }

    public void performClick(ItemSPI item, RelativeLayout rlSelect) {
        if (activity_spi.selectedProviderIds.size() > 0) {
            Log.e("multi voucheer selected", ".......pos.." + item.pos);

            for (int i = 0; i < activity_spi.selectedProviderIds.size(); i++) {
                if (activity_spi.selectedProviderIds.get(i).pos == item.pos) {
                    activity_spi.selectedProviderIds.remove(i);
                    rlSelect.setVisibility(View.GONE);
                    activity_spi.isSelected = false;
                    Log.e("multi voucheer selected", ".......111111111110");

                    return;

                } else {
                    Log.e("multi voucheer selected", ".......222222222");
                    activity_spi.isSelected = true;
                    rlSelect.setVisibility(View.VISIBLE);

                }
            }
            mListener.onItemClick(item,isEnable);


        } else {
            Log.e("multi voucheer selected", ".......3333333333");
            activity_spi.isSelected = true;
            rlSelect.setVisibility(View.VISIBLE);
            mListener.onItemClick(item,isEnable);
        }

    }
}