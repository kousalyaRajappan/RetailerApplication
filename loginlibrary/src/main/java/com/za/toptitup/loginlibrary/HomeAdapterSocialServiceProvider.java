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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import com.za.toptitup.loginlibrary.model.ItemSPI;
import com.za.toptitup.loginlibrary.utils.Topitup;

public class HomeAdapterSocialServiceProvider extends RecyclerView.Adapter<HomeAdapterSocialServiceProvider.ViewHolder> {

    private final ArrayList<ItemSPI> mValues;
    private static Context mContext;
    protected static ItemListener mListener;
    private static SharedPreferences settings;
    private static String enable_airtime = "", enable_blue = "", enable_one_for = "", enable_ott = "", enable_flexipin = "";
    private static boolean isEnable = false;

    enum SOCIAL_KEY {
        //0=whatsapp,1=youtube,2=twitter,3=facebook,4=instagram,5=tiktok
        WHATSAPP("0"), YOUTUBE("1"),
        TWITTER("2"), FACEBOOK("3"), INSTAGRAM("4"),
        TIKTOK("5");
        public final String KEY;

        SOCIAL_KEY(String key) {
            this.KEY = key;
        }
    }

    public enum PeriodLabel {

        Default("-1"),
        Standard("0"), Daily("1"),
        Monthly("2"), Hourly("3"), Weekly("4"),
        Special("5"), Social("6");
        private final String abbreviation;

        // Reverse-lookup map for getting a day from an abbreviation
        private static final Map<String, PeriodLabel> lookup = new HashMap<String, PeriodLabel>();

        static {
            for (PeriodLabel d : PeriodLabel.values()) {
                lookup.put(d.getAbbreviation(), d);
            }
        }

        PeriodLabel(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public static PeriodLabel get(String abbreviation) {
            return lookup.get(abbreviation);
        }
    }

    public enum Social {

        Default("-1"),
        WhatsApp("0"),
        YouTube("1"),
        Twitter("2"), Facebook("3"), Instagram("4"),
        TikTok("5");
        private final String abbreviation;

        // Reverse-lookup map for getting a day from an abbreviation
        private static final Map<String, Social> lookup = new HashMap<String, Social>();

        static {
            for (Social d : Social.values()) {
                lookup.put(d.getAbbreviation(), d);
            }
        }

        Social(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public static Social get(String abbreviation) {
            return lookup.get(abbreviation);
        }
    }


    public HomeAdapterSocialServiceProvider(Context context, ArrayList<ItemSPI> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private final TextView textViewcat;
        private final TextView textDeno;
        private final ImageView imageView;
        private final LinearLayout relativeLayout;
        private ItemSPI item;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = v.findViewById(R.id.textView);
            textView.setSelected(true);
            textViewcat = v.findViewById(R.id.textViewcat);
            textDeno = v.findViewById(R.id.textDeno);
            imageView = v.findViewById(R.id.imageView);
            relativeLayout = v.findViewById(R.id.relativeLayout);

        }

        public void setData(ItemSPI item, int position) {

            this.item = item;
            String imageName = mContext.getResources().getResourceName(item.drawable);
            settings = mContext.getSharedPreferences("TIUPREF", 0);

            isEnable = false;

            enable_airtime = settings.getString("enable_airtime", "0");
            enable_one_for = settings.getString("enable_oneforu", "0");
            enable_blue = settings.getString("enable_blue", "0");
            enable_ott = settings.getString("enable_ott", "0");
            if (imageName.contains("prov1") || imageName.contains("prov2") || imageName.contains("prov3") || imageName.contains("prov_worldcall") || imageName.contains("prov10") || imageName.contains("prov_blue") || imageName.contains("prov_oneforyou") || imageName.contains("prov_oneforyou") || imageName.contains("ringas") || imageName.contains("prov_ott")) {

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
                            isEnable = true;
                        } else if (imageName.contains("prov_oneforyou") && enable_one_for.equals("2")) {
                            isEnable = true;

                        } else if (imageName.contains("prov_blue") && enable_blue.equals("2")) {
                            isEnable = true;

                        } else if (imageName.contains("prov_ott") && enable_ott.equals("2")) {
                            isEnable = true;

                        } else {
                            isEnable = false;

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
                } else {
                    textView.setText(item.text);
                    imageView.setImageResource(item.drawable);
                }
            } else {
                if (!item.social_type.equalsIgnoreCase("")) {
                    textView.setText(Social.get(item.social_type).name());
                }
                imageView.setImageResource(item.drawable);
            }

            if (item.text.toLowerCase().contains("whatsapp")) {
                textView.setText(item.text.replace("Whatsapp", ""));
            } else if (item.text.toLowerCase().contains("facebook")) {
                textView.setText(item.text.replace("Facebook", ""));

            } else if (item.text.toLowerCase().contains("youtube")) {
                textView.setText(item.text.replace("Youtube", ""));

            } else if (item.text.toLowerCase().contains("tiktok")) {
                textView.setText(item.text.replace("Tiktok", ""));
            } else if (item.text.toLowerCase().contains("twitter")) {
                textView.setText(item.text.replace("Twitter", ""));
            } else if (item.text.toLowerCase().contains("instagram")) {
                textView.setText(item.text.replace("Instagram", ""));
            } else {
                textView.setText(item.text);
            }
            Log.i("dataText", item.text);
            //  textView.setText(Social.get(item.social_type).name());
            if (!item.period_label.equalsIgnoreCase("")) {
                textViewcat.setText(PeriodLabel.get(item.period_label).name());
            }
            textViewcat.setAllCaps(false);

         /*   if (item.text.toLowerCase().contains("whatsapp")) {
                imageView.setImageResource(R.drawable.whatsapp);
            } else if (item.text.toLowerCase().contains("facebook")) {
                imageView.setImageResource(R.drawable.facebook);
            } else if (item.text.toLowerCase().contains("youtube")) {
                imageView.setImageResource(R.drawable.youtube);
            } else if (item.text.toLowerCase().contains("tik tok")) {
                imageView.setImageResource(R.drawable.tiktok) ;
            } else if (item.text.toLowerCase().contains("twitter")) {
                imageView.setImageResource(R.drawable.twitter);
            } else {
                imageView.setImageResource(0);
            }*/
            //   ll   Default("-1"),
            //                    WhatsApp("0"),
            //                    YouTube("1"),
            //                    Twitter("2"), Facebook("3"), Instagram("4"),
            //                    TikTok("5");
            if (item.text.toLowerCase().contains("whatsapp")) {
                imageView.setImageResource(R.drawable.whatsapp_icon);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("0")) {
                imageView.setImageResource(R.drawable.whatsapp_icon);
            } else if (item.text.toLowerCase().contains("facebook")) {
                imageView.setImageResource(R.drawable.facebook);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("3")) {
                imageView.setImageResource(R.drawable.facebook);
            } else if (item.text.toLowerCase().contains("youtube")) {
                imageView.setImageResource(R.drawable.youtube);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("1")) {
                imageView.setImageResource(R.drawable.youtube);
            } else if (item.text.toLowerCase().contains("tik tok")) {
                imageView.setImageResource(R.drawable.tiktok);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("5")) {
                imageView.setImageResource(R.drawable.tiktok);
            } else if (item.text.toLowerCase().contains("twitter")) {
                imageView.setImageResource(R.drawable.twitter);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("2")) {
                imageView.setImageResource(R.drawable.twitter);
            } else if (item.text.toLowerCase().contains("instagram")) {
                imageView.setImageResource(R.drawable.ic_insta);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("4")) {
                imageView.setImageResource(R.drawable.ic_insta);
            } else if (item.social_type.toLowerCase().equalsIgnoreCase("-1")) {
                imageView.setImageResource(0);
            }


            //Toasty.error(mContext, textViewcat.getText(), Toast.LENGTH_LONG).show();
            textDeno.setVisibility(View.VISIBLE);
            textDeno.setBackgroundColor(Color.parseColor("#bf2035"));
            textView.setTextSize(12);
           /* if (textViewcat.getText().equals("Data") || textViewcat.getText().equals("Social+")) {
                textDeno.setBackgroundColor(Color.parseColor("#bf2035"));
                textView.setTextSize(12);
            } else if (textViewcat.getText().equals("IKEJA")) {
                textDeno.setVisibility(View.GONE);
                imageView.getLayoutParams().width = 475;
            } else {
                textDeno.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textView.setTextSize(24);

            }*/


            textDeno.setText(item.deno);
           /* textDeno.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            textViewcat.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.GONE);*/
            //imageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {

            String product_admin_disable = mContext.getString(R.string.product_admin_disable);
            String product_cashier_disable = mContext.getString(R.string.product_cashier_disable);

            if (mListener != null) {

                if (Topitup.IS_ADMIN.equals("1")) {
                    if (item.visible_admin) {
                        mListener.onItemClick(item, isEnable);
                    } else
                        Toasty.error(mContext, product_admin_disable, 8000, true).show();
                } else {

                    if (item.visible_admin && item.visible_cashier) {
                        mListener.onItemClick(item, isEnable);
                    } else
                        Toasty.error(mContext, product_cashier_disable, 8000, true).show();

                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_social_service_provider, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setData(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(ItemSPI item, Boolean isEnable);
    }
}