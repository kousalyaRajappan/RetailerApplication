package com.za.toptitup.loginlibrary.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import com.za.toptitup.loginlibrary.R;

import com.za.toptitup.loginlibrary.model.MyApiEndpointInterface;
import com.za.toptitup.loginlibrary.model.service_provider_item_settings;
import com.za.toptitup.loginlibrary.model.service_provider_settings;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {
    String[] _items = null;
    boolean[] mSelection = null;
    List<String> unselection;
    int isadmin = 0;
    int isproduct = 0;
    ArrayAdapter<String> simple_adapter;
    Realm realm;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    RealmResults<service_provider_settings> service_provider_settings;
    RealmResults<service_provider_item_settings> service_provider_item_settings;

    public MultiSelectionSpinner(Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
        // Initialize Realm

    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public void setisAdmin(int isAdmin) {
        isadmin = isAdmin;
    }

    public void setisProduct(int isProduct) {
        isproduct = isProduct;
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            simple_adapter.clear();
            //  simple_adapter.add(buildSelectedItemString());  uncomment
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);

        Realm.init(getContext());
        realm = Realm.getDefaultInstance();

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Log.e("perform click","............click");

                try{
                    showCustomDialog();

                    List<String> m = getSelectedStrings();

//                    callApiUnselected(unselection);
                    if (isproduct == 1) {
                        handler.postDelayed(runnable, 2000);
                        service_provider_settings = realm.where(service_provider_settings.class).findAll();

                        int size = service_provider_settings.size();
                        for (int j = 0; j < size; j++) {
                            service_provider_settings product = service_provider_settings.get(j);
                            realm.beginTransaction();

                            if (isadmin == 1)
                                product.setVisible_admin(false);
                            else
                                product.setVisible_cashier(false);
                            realm.commitTransaction();
                        }


                        for (int i = 0; i < m.size(); i++) {
                            service_provider_settings = realm.where(service_provider_settings.class).equalTo("provider_desc", m.get(i)).findAll();
                            //int size = service_provider.size();
                            size = service_provider_settings.size();
                            for (int k = 0; k < size; k++) {

                                service_provider_settings sp = service_provider_settings.get(k);
                                Log.e("service", "customized..selected...k.         " + sp.provider_id);

                                realm.beginTransaction();
                                if (isadmin == 1) {
                                    sp.setVisible_admin(true);
                                } else {
                                    sp.setVisible_cashier(true);
                                }
                                realm.commitTransaction();
                            }
                        }

                    }
                    else {
                        handler.postDelayed(runnable, 2000);
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).findAll();

                        int size = service_provider_item_settings.size();

                        for (int j = 0; j < size; j++) {
                            service_provider_item_settings product = service_provider_item_settings.get(j);
                            realm.beginTransaction();
                            if (isadmin == 1)
                                product.setVisible_admin(false);
                            else
                                product.setVisible_cashier(false);
                            realm.commitTransaction();
                        }


                        for (int i = 0; i < m.size(); i++) {
                            service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("item_desc", m.get(i)).findAll();

                            size = service_provider_item_settings.size();
//                          Toasty.info(getContext(), "size" + size, 8000, true).show();
                            /**/
                            for (int k = 0; k < size; k++) {
                                service_provider_item_settings sp = service_provider_item_settings.get(k);
                                //   Toasty.info(getContext(), "isadmin" +  isadmin, 8000, true).show();
                                realm.beginTransaction();
                                if (isadmin == 1) {
                                    sp.setVisible_admin(true);

                                } else {
                                    sp.setVisible_cashier(true);

                                }
                                realm.commitTransaction();


                            }
                        }
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }


            }

        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                // dialog.dismiss();
            }
        });

        builder.show();
        return true;
    }

    private void callApiUnselected(List<String> unselection) {
        ArrayList<Integer> idsList = new ArrayList<>();

        for (int m = 0; m < unselection.size(); m++) {
            if (unselection.get(m)==null) {
                unselection.remove(m);
            }
        }

        for (int i = 0; i < unselection.size(); i++) {
            if (activity_product_settings.filterSelectedItem.equals("product")) {
                service_provider_settings = realm.where(service_provider_settings.class).equalTo("provider_desc", unselection.get(i)).findAll();

                int size = service_provider_settings.size();
                if(unselection.size()==size) {
                    Log.e("service", "unselected..selected...before..         " + size + ",id" + service_provider_settings.get(i).provider_id);
                    int id = service_provider_settings.get(i).getProvider_id();
                    Log.e("ids unselected", "........unselected......." + id);
                    idsList.add(id);
                }
            } else {
                service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("item_desc", unselection.get(i)).findAll();

                int size = service_provider_item_settings.size();
                if(unselection.size()==size) {
                    Log.e("service", "unselected..selected...before..         " + size + ",id" + service_provider_item_settings.get(i).getService_provider_id());
                    int id = service_provider_item_settings.get(i).getService_provider_item_id();
                    Log.e("ids unselected", "........unselected......." + id);
                    idsList.add(id);
                }
            }
//            service_provider_settings = realm.where(service_provider_settings.class).equalTo("provider_desc", unselection.get(i)).findAll();
            //int size = service_provider.size();


            /*for (int k = 0;k<size;k++) {
                Log.e("service","unselected..selected...k.         "+k);
                int id = service_provider_settings.get(k).getProvider_id();
                Log.e("ids unselected","........unselected......."+id);
                idsList.add(id);
            }*/
        }

        String csv = idsList.toString().replace("[", "").replace("]", "");
//                .replace(", ", ",");

        String s = TextUtils.join(", ", idsList);
        Log.e("unselected" + idsList.size(), s + ".....comma separate..size....." + csv);


        Call<ResponseBody> call = apiService.setUn_selected_Items(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(getContext(), "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                //Toasty.error(mContext, "Could not log in", 3000, true).show();
                String res = "";
                try {
                    res = response.body().string();
                } catch (Exception ex) {
                    if (response.body() != null)
                    response.body().close();
                }

                if (res.trim().length() == 0 || res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res;
                    if (res.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:", "");

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK

                    PrinterTopitup.print_data(res);

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toasty.error(getContext(), t.getMessage(), 100000, true).show();
                Timber.e("GetUpdateAll error: " + t.getMessage());


            }
        });


    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j] != null) {
                    if (_items[j].equals(cell)) {
                        mSelection[j] = true;
                    }
                }
            }
        }
    }


    public void clearSelection() {

        for (int j = 0; j < _items.length; ++j) {

            mSelection[j] = false;

        }

    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        unselection = new LinkedList<String>();

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            } else {
                Log.e("unselected", "....unselected......." + _items[i]);
                unselection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();

        // return "Uncheck Product to Disable";
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }


    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();

        }
    };


    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;

    private void showCustomDialog() {

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = dialog.findViewById(R.id.pageLoadingWrapper);
        pop_title = dialog.findViewById(R.id.pop_title);
        pop_content = dialog.findViewById(R.id.pop_content);

        pop_title.setText("Updating");
        pop_content.setText("please wait...");

        bt_close = dialog.findViewById(R.id.bt_close);
        bt_close.setVisibility(View.GONE);

        bt_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }


}

