package com.za.toptitup.loginlibrary.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import com.za.toptitup.loginlibrary.BaseAdminActivity;


import za.co.topitup.R;
import com.za.toptitup.loginlibrary.model.Item;
import com.za.toptitup.loginlibrary.model.service_provider_item;
import com.za.toptitup.loginlibrary.model.service_provider_quick_items;
import com.za.toptitup.loginlibrary.model.service_provider_settings;


public class activity_quick_launch extends BaseAdminActivity {

    Context mContext;
    private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6,spinner7,spinner8, spinner1_amount, spinner2_amount, spinner3_amount, spinner4_amount, spinner5_amount, spinner6_amount,spinner7_amount,spinner8_amount;
    Realm realm;
    RealmResults<service_provider_item> service_provider_items, service_provider_items1;
    int size;
    private String[] NAMEQ, NAMEQ_NEW, BARCODE, BARCODE_NEW, DENA, DENA_NEW;
    private int[] SPITEMID, spPos, SPITEMID_NEW, sdPos;
    CheckBox chk_voucher_print, chk_quick_launch;
    LinearLayout quicklaunh;
    public  int spinner1SelectedValue = -1,spinner2SelectedValue = -1,spinner3SelectedValue = -1,spinner4SelectedValue= -1,spinner5SelectedValue= -1,spinner6SelectedValue = -1,spinner7SelectedValue = -1,spinner8SelectedValue = -1;
    public  int spinner1Selected = -1,spinner2Selected = -1,spinner3Selected = -1,spinner4Selected = -1,spinner5Selected = -1,spinner6Selected = -1,spinner7Selected = -1,spinner8Selected = -1;

    RealmResults<service_provider_quick_items> tiu_serviceProviderQuick;

    private service_provider_settings service_provider_setting;
    private ArrayList<Item> arrayList;
    private boolean fromFirst = false;
    List<String> denoList2,denoList1,denoList3,denoList4,denoList5,denoList6,denoList7,denoList8 ;
    List<String> NameList2,NameList1,NameList3,NameList4,NameList5,NameList6,NameList7,NameList8 ;
    List<Integer> itemList2,itemList1,itemList3,itemList4,itemList5,itemList6,itemList7,itemList8 ;
    List<String> barcodeList2,barcodeList1,barcodeList3,barcodeList4,barcodeList5,barcodeList6,barcodeList7,barcodeList8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_launch);
        mContext = this;

        realm = Realm.getDefaultInstance();
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);
        spinner6 = findViewById(R.id.spinner6);
        spinner7 = findViewById(R.id.spinner7);
        spinner8 = findViewById(R.id.spinner8);


        spinner1_amount = findViewById(R.id.spinner1_amount);
        spinner2_amount = findViewById(R.id.spinner2_amount);
        spinner3_amount = findViewById(R.id.spinner3_amount);
        spinner4_amount = findViewById(R.id.spinner4_amount);
        spinner5_amount = findViewById(R.id.spinner5_amount);
        spinner6_amount = findViewById(R.id.spinner6_amount);
        spinner7_amount = findViewById(R.id.spinner7_amount);
        spinner8_amount = findViewById(R.id.spinner8_amount);

        quicklaunh = findViewById(R.id.quicklaunh);

        chk_voucher_print = findViewById(R.id.chk_voucher_print);
        chk_quick_launch = findViewById(R.id.chk_quick_launch);

        NAMEQ = new String[8];
        DENA = new String[8];
        BARCODE = new String[8];
        SPITEMID = new int[8];
        spPos = new int[8];
        sdPos = new int[8];
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);

        if (settings.getString("setting_chk_quick_launch", "0").equals("1")) {
            chk_quick_launch.setChecked(true);

            quicklaunh.setVisibility(View.GONE);
        }

        if (settings.getString("setting_chk_voucher_print", "0").equals("1"))
            chk_voucher_print.setChecked(true);
        addItemsOnSpinner();
        chk_quick_launch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) quicklaunh.setVisibility(View.GONE);
                else quicklaunh.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addItemsOnSpinner() {
        arrayList = new ArrayList<>();
        String[] NAME = new String[]{"CELLC", "MTN", "VODACOM", "TELKOM", "ONEFORYOU", "BUSTICKET",  "OTT"};//,"mamamoney,
        for (int i = 0; i < NAME.length; i++) {

            if (NAME[i].equalsIgnoreCase("VODACOM")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 1).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_voda, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_voda_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("MTN")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 2).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mtn, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_mtn_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("CELLC")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 3).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_cellc, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_cell_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("TELKOM")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 10).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_tel, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_telcom_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("ONEFORYOU")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 24).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_one, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.provider_one_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("BUSTICKET")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 29).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                  /*  if (enable_bluvoucher.equals("0"))
                        arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_bluvoucher_dis, "#FFFFFF", ""));
                    else*/
                    arrayList.add(new Item("BLUE VOUCHER", NAME[i], R.drawable.prov_blue, "#FFFFFF", ""));
                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_blue_dis, "#FFFFFF", ""));

                }

            } else if (NAME[i].equalsIgnoreCase("OTT")) {
                service_provider_setting = realm.where(service_provider_settings.class).equalTo("provider_id", 25).findFirst();
                if (service_provider_setting.isVisible_admin()) {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott, "#FFFFFF", ""));

                } else {
                    arrayList.add(new Item(NAME[i], NAME[i], R.drawable.prov_ott_dis, "#FFFFFF", ""));

                }

            }



        }
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < arrayList.size(); i++) {
//            service_provider_item product = service_provider_items.get(i);
            list.add(arrayList.get(i).getText());
//            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner4.setAdapter(adapter);

        spinner5.setAdapter(adapter);
        spinner6.setAdapter(adapter);
        spinner7.setAdapter(adapter);
        spinner8.setAdapter(adapter);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList1= new ArrayList<>();
                denoList1 = new ArrayList<>();
                barcodeList1 = new ArrayList<>();
                itemList1 = new ArrayList<>();
                int selected_id = getCode(spinner1.getSelectedItem().toString());

                Log.e("onItem selected",".....selected_id..."+selected_id);
                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                    denoList1.add(product.item_value);
                    NameList1.add(product.item_desc);
                    barcodeList1.add(product.item_barcode);
                    itemList1.add(product.service_provider_item_id);

                   /* NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;*/


                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1_amount.setAdapter(dataAdapter);
                if(spinner1Selected == i) {
                    if (spinner1SelectedValue != -1) {
                        spinner1_amount.setSelection(spinner1SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                        NameList2= new ArrayList<>();
                        denoList2 = new ArrayList<>();
                        barcodeList2 = new ArrayList<>();
                        itemList2 = new ArrayList<>();
                int selected_id = getCode(spinner2.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();


                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);
                    list.add("R " + String.format("%.0f", product.item_value_int));
                    denoList2.add(product.item_value);
                    NameList2.add(product.item_desc);
                    barcodeList2.add(product.item_barcode);
                    itemList2.add(product.service_provider_item_id);

                   /* NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;*/
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2_amount.setAdapter(dataAdapter);
                Log.e("spinner2 selection......."+spinner2SelectedValue,spinner2Selected+"...value........"+i+"....item.."+spinner2.getSelectedItem().toString());
                if(spinner2Selected == i) {
                    if (spinner2SelectedValue != -1) {
                        spinner2_amount.setSelection(spinner2SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList3= new ArrayList<>();
                denoList3 = new ArrayList<>();
                barcodeList3 = new ArrayList<>();
                itemList3 = new ArrayList<>();
                int selected_id = getCode(spinner3.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                    denoList3.add(product.item_value);
                    NameList3.add(product.item_desc);
                    barcodeList3.add(product.item_barcode);
                    itemList3.add(product.service_provider_item_id);

                }


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3_amount.setAdapter(dataAdapter);
                if(spinner3Selected == i) {

                    if (spinner3SelectedValue != -1) {
                        spinner3_amount.setSelection(spinner3SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList4= new ArrayList<>();
                denoList4 = new ArrayList<>();
                barcodeList4 = new ArrayList<>();
                itemList4 = new ArrayList<>();
                int selected_id = getCode(spinner4.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));

                    denoList4.add(product.item_value);
                    NameList4.add(product.item_desc);
                    barcodeList4.add(product.item_barcode);
                    itemList4.add(product.service_provider_item_id);
                }
               /* for (int j = 0;j<size;j++) {

                    service_provider_item product = service_provider_items.get(j);
//                    NAME[i] = "R "+String.format("%.0f",product.item_value_int);

                    list.add("R "+String.format("%.0f",product.item_value_int));

                }*/

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner4_amount.setAdapter(dataAdapter);
                if(spinner4Selected == i) {

                    if (spinner4SelectedValue != -1) {
                        spinner4_amount.setSelection(spinner4SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList5= new ArrayList<>();
                denoList5 = new ArrayList<>();
                barcodeList5 = new ArrayList<>();
                itemList5 = new ArrayList<>();
                int selected_id = getCode(spinner5.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

//                size = service_provider_items.size();
                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                  /*  NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;*/
                    denoList5.add(product.item_value);
                    NameList5.add(product.item_desc);
                    barcodeList5.add(product.item_barcode);
                    itemList5.add(product.service_provider_item_id);

                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner5_amount.setAdapter(dataAdapter);
                if(spinner5Selected == i) {

                    if (spinner5SelectedValue != -1) {
                        spinner5_amount.setSelection(spinner5SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList6= new ArrayList<>();
                denoList6 = new ArrayList<>();
                barcodeList6 = new ArrayList<>();
                itemList6 = new ArrayList<>();
                int selected_id = getCode(spinner6.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

//                size = service_provider_items.size();
                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                  /*  NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;
*/

                    denoList6.add(product.item_value);
                    NameList6.add(product.item_desc);
                    barcodeList6.add(product.item_barcode);
                    itemList6.add(product.service_provider_item_id);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner6_amount.setAdapter(dataAdapter);
                if(spinner6Selected == i) {

                    if (spinner6SelectedValue != -1) {
                        spinner6_amount.setSelection(spinner6SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList7= new ArrayList<>();
                denoList7 = new ArrayList<>();
                barcodeList7 = new ArrayList<>();
                itemList7 = new ArrayList<>();
                int selected_id = getCode(spinner7.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

//                size = service_provider_items.size();
                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                    /*NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;*/

                    denoList7.add(product.item_value);
                    NameList7.add(product.item_desc);
                    barcodeList7.add(product.item_barcode);
                    itemList7.add(product.service_provider_item_id);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner7_amount.setAdapter(dataAdapter);
                if(spinner7Selected == i) {

                    if (spinner7SelectedValue != -1) {
                        spinner7_amount.setSelection(spinner7SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<String>();
                NameList8= new ArrayList<>();
                denoList8 = new ArrayList<>();
                barcodeList8 = new ArrayList<>();
                itemList8 = new ArrayList<>();
                int selected_id = getCode(spinner8.getSelectedItem().toString());

                service_provider_items = realm.where(service_provider_item.class).equalTo("service_provider_id", selected_id).equalTo("item_type", 0).sort("item_position", Sort.ASCENDING).findAll();

//                size = service_provider_items.size();
                size = service_provider_items.size();
                NAMEQ_NEW = new String[size];
                BARCODE_NEW = new String[size];
                DENA_NEW = new String[size];
                SPITEMID_NEW = new int[size];

                for (int j = 0; j < size; j++) {

                    service_provider_item product = service_provider_items.get(j);

                    list.add("R " + String.format("%.0f", product.item_value_int));
                   /* NAMEQ_NEW[j] = product.item_desc;
                    DENA_NEW[j] = product.item_value;
                    BARCODE_NEW[j] = product.item_barcode;
                    SPITEMID_NEW[j] = product.service_provider_item_id;*/

                    denoList8.add(product.item_value);
                    NameList8.add(product.item_desc);
                    barcodeList8.add(product.item_barcode);
                    itemList8.add(product.service_provider_item_id);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity_quick_launch.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner8_amount.setAdapter(dataAdapter);
                if(spinner8Selected == i) {

                    if (spinner8SelectedValue != -1) {
                        spinner8_amount.setSelection(spinner8SelectedValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner1_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];

               int respos= spinner1.getSelectedItemPosition();
                    NAMEQ[0] =  NameList1.get(i);
                    DENA[0] = denoList1.get(i);
                    BARCODE[0] =barcodeList1.get(i);
                    SPITEMID[0] = itemList1.get(i);
                    spPos[0] = spinner1.getSelectedItemPosition();
                    sdPos[0] = i;

              /*  NAMEQ[1] = NameList.get(i);
                DENA[1] =denoList.get(i);
                BARCODE[1] = barcodeList.get(i);
                SPITEMID[1] = itemList.get(i);
                spPos[1] = spinner2.getSelectedItemPosition();
                sdPos[1] = i;*/


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];

//                Log.e(spinner2SelectedValue+"spinner2 ......."+NAMEQ_NEW[spinner2SelectedValue],i+"...value........"+NAMEQ_NEW[i]);

                NAMEQ[1] = NameList2.get(i);
                DENA[1] =denoList2.get(i);
                BARCODE[1] = barcodeList2.get(i);
                SPITEMID[1] = itemList2.get(i);
                spPos[1] = spinner2.getSelectedItemPosition();
                sdPos[1] = i;

               /* if(fromFirst){
                    Log.e(denoList.get(i)+"spinner2 ......."+NameList.get(i),i+"...value....if...."+NAMEQ_NEW[i]);

                    int respos= spinner2.getSelectedItemPosition();

                    NAMEQ[1] = NameList.get(i);
                    DENA[1] =denoList.get(i);
                    BARCODE[1] = barcodeList.get(i);
                    SPITEMID[1] = itemList.get(i);
                    spPos[1] = spinner2.getSelectedItemPosition();
                    sdPos[1] = i;
                }else{

                    Log.e(spinner2SelectedValue+"spinner2 ......."+NAMEQ_NEW[spinner2SelectedValue],i+"...value...else....."+NAMEQ_NEW[i]);

                    NAMEQ[1] = NAMEQ_NEW[i];
                    DENA[1] = DENA_NEW[i];
                    BARCODE[1] = BARCODE_NEW[i];
                    SPITEMID[1] = SPITEMID_NEW[i];
                    spPos[1] = spinner2.getSelectedItemPosition();
                    sdPos[1] = i;
                }*/
//                int respos= spinner2.getSelectedItemPosition();
                   /* NAMEQ[1] = NAMEQ_NEW[spinner2Selected];
                    DENA[1] = DENA_NEW[spinner2Selected];
                    BARCODE[1] = BARCODE_NEW[spinner2Selected];
                    SPITEMID[1] = SPITEMID_NEW[spinner2Selected];*/




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner3_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];





                NAMEQ[2] = NameList3.get(i);
                DENA[2] = denoList3.get(i);
                BARCODE[2] =barcodeList3.get(i);
                SPITEMID[2] = itemList3.get(i);
                spPos[2] = spinner3.getSelectedItemPosition();
                sdPos[2] = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner4_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];


                NAMEQ[3] = NameList4.get(i);
                DENA[3] =denoList4.get(i);
                BARCODE[3] = barcodeList4.get(i);
                SPITEMID[3] = itemList4.get(i);
                spPos[3] = spinner4.getSelectedItemPosition();
                sdPos[3] = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner5_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];

                NAMEQ[4] = NameList5.get(i);
                DENA[4] = denoList5.get(i);
                BARCODE[4] =  barcodeList5.get(i);
                SPITEMID[4] = itemList5.get(i);
                spPos[4] = spinner5.getSelectedItemPosition();
                sdPos[4] = i;

               /* NAMEQ[3] = NameList3.get(i);
                DENA[3] =denoList3.get(i);
                BARCODE[3] = barcodeList3.get(i);
                SPITEMID[3] = itemList3.get(i);
                spPos[3] = spinner4.getSelectedItemPosition();
                sdPos[3] = i;*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner6_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];


                NAMEQ[5] = NameList6.get(i);
                DENA[5] = denoList6.get(i);
                BARCODE[5] =  barcodeList6.get(i);
                SPITEMID[5] = itemList6.get(i);
                spPos[5] = spinner6.getSelectedItemPosition();
                sdPos[5] = i;
             /*
                NAMEQ[5] = NAMEQ_NEW[i];
                DENA[5] = DENA_NEW[i];
                BARCODE[5] = BARCODE_NEW[i];
                SPITEMID[5] = SPITEMID_NEW[i];
                spPos[5] = spinner6.getSelectedItemPosition();
                sdPos[5] = i;*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner7_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];


                NAMEQ[6] = NameList7.get(i);
                DENA[6] = denoList7.get(i);
                BARCODE[6] =  barcodeList7.get(i);
                SPITEMID[6] = itemList7.get(i);
                spPos[6] = spinner7.getSelectedItemPosition();
                sdPos[6] = i;
               /* NAMEQ[6] = NAMEQ_NEW[i];
                DENA[6] = DENA_NEW[i];
                BARCODE[6] = BARCODE_NEW[i];
                SPITEMID[6] = SPITEMID_NEW[i];
                spPos[6] = spinner7.getSelectedItemPosition();
                sdPos[6] = i;*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner8_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String barcode = BARCODE[i];

                NAMEQ[7] = NameList8.get(i);
                DENA[7] = denoList8.get(i);
                BARCODE[7] =  barcodeList8.get(i);
                SPITEMID[7] = itemList8.get(i);
                spPos[7] = spinner8.getSelectedItemPosition();
                sdPos[7] = i;
               /*
                NAMEQ[7] = NAMEQ_NEW[i];
                DENA[7] = DENA_NEW[i];
                BARCODE[7] = BARCODE_NEW[i];
                SPITEMID[7] = SPITEMID_NEW[i];
                spPos[7] = spinner8.getSelectedItemPosition();
                sdPos[7] = i;*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tiu_serviceProviderQuick = realm.where(service_provider_quick_items.class).findAll();
        size = tiu_serviceProviderQuick.size();
        for (int i = 0; i < size; i++) {
            service_provider_quick_items product = tiu_serviceProviderQuick.get(i);

            if (i == 0) {
                spinner1SelectedValue = product.sdPos;
                spinner1Selected = product.spPos;

                spinner1.setSelection(product.spPos);


            }

            if (i == 1) {

                fromFirst=true;

                spinner2SelectedValue = product.sdPos;
                spinner2Selected = product.spPos;
                Log.e(product.spName+"spinner2 ......."+product.spItemID,i+"...value........"+product.spDena);
                spinner2.setSelection(product.spPos);

            }


            if (i == 2)
            {

                spinner3SelectedValue = product.sdPos;
                spinner3Selected = product.spPos;

                spinner3.setSelection(product.spPos);

            }


            if (i == 3) {

                spinner4SelectedValue = product.sdPos;
                spinner4Selected = product.spPos;

                spinner4.setSelection(product.spPos);
            }


            if (i == 4) {

                spinner5SelectedValue = product.sdPos;
                spinner5Selected = product.spPos;

                spinner5.setSelection(product.spPos);
            }


            if (i == 5) {

                spinner6SelectedValue = product.sdPos;
                spinner6Selected = product.spPos;

                spinner6.setSelection(product.spPos);

            }

            if (i == 6) {

                spinner7SelectedValue = product.sdPos;
                spinner7Selected = product.spPos;


                spinner7.setSelection(product.spPos);

            }
            if (i == 7) {

                spinner8SelectedValue = product.sdPos;
                spinner8Selected = product.spPos;

                spinner8.setSelection(product.spPos);

            }


        }


    }

    public int getCode(String SelectedValue) {
        if (SelectedValue.equals("VODACOM")) {
            return 1;

        } else if (SelectedValue.equals("MTN")) {
            return 2;
        } else if (SelectedValue.equals("CELLC")) {
            return 3;

        } else if (SelectedValue.equals("TELKOM")) {
            return 10;
        } else if (SelectedValue.equals("BLUE VOUCHER")) {
            return 29;

        }  else if (SelectedValue.equals("ONEFORYOU")) {
            return 24;

        } else if (SelectedValue.equals("OTT")) {
            return 25;

        }

        return 0;
    }

    public void onClickBtnAction(View v) {

      /*  switch (v.getId()) {
            case R.id.btn_button_close: {
                onBackPressed();
                return;
            }

            case R.id.btn_setting_save: {
                String item;
                String str_chk_quick_launch = "0";
                if (chk_quick_launch.isChecked()) str_chk_quick_launch = "1";
                String str_chk_voucher_print = "0";
                if (chk_voucher_print.isChecked()) str_chk_voucher_print = "1";
                SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("setting_chk_voucher_print", str_chk_voucher_print);
                editor.putString("setting_chk_quick_launch", str_chk_quick_launch);
                editor.commit();
                realm = Realm.getDefaultInstance();


                realm.beginTransaction();
                realm.where(service_provider_quick_items.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();
                service_provider_quick_items spQuick = new service_provider_quick_items();

                for (int i = 0; i < NAMEQ.length; i++) {
                    Log.e("length saving ",NAMEQ[1]+"....quick......"+i +".n.."+NAMEQ[i]+".d..."+DENA[i]+"...pos..."+spPos[i]+"....item id..."+SPITEMID[i]+"+...bar+"+BARCODE[i]);
                    spQuick.spqid = i;
                    spQuick.spName = NAMEQ[i];
                    spQuick.spDena = DENA[i];
                    spQuick.spItemID = SPITEMID[i];
                    spQuick.spBarcode = BARCODE[i];
                    spQuick.spPos = spPos[i];
                    spQuick.sdPos = sdPos[i];
                    spQuick.visible_admin = true;
                    spQuick.visible_cashier = true;
                    realm.beginTransaction();
                    realm.insertOrUpdate(spQuick);
                    realm.commitTransaction();

                }

            }
            Toasty.info(mContext, "Saved!", 5000, true).show();
            onBackPressed();
        }*/
        if (v.getId() == R.id.btn_button_close) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_setting_save) {
            String str_chk_quick_launch = chk_quick_launch.isChecked() ? "1" : "0";
            String str_chk_voucher_print = chk_voucher_print.isChecked() ? "1" : "0";

            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("setting_chk_voucher_print", str_chk_voucher_print);
            editor.putString("setting_chk_quick_launch", str_chk_quick_launch);
            editor.commit();

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(service_provider_quick_items.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();

            service_provider_quick_items spQuick = new service_provider_quick_items();

            for (int i = 0; i < NAMEQ.length; i++) {
                Log.e("length saving ", NAMEQ[1] + "....quick......" + i + ".n.." + NAMEQ[i] + ".d..." + DENA[i] + "...pos..." + spPos[i] + "....item id..." + SPITEMID[i] + "+...bar+" + BARCODE[i]);
                spQuick.spqid = i;
                spQuick.spName = NAMEQ[i];
                spQuick.spDena = DENA[i];
                spQuick.spItemID = SPITEMID[i];
                spQuick.spBarcode = BARCODE[i];
                spQuick.spPos = spPos[i];
                spQuick.sdPos = sdPos[i];
                spQuick.visible_admin = true;
                spQuick.visible_cashier = true;

                realm.beginTransaction();
                realm.insertOrUpdate(spQuick);
                realm.commitTransaction();
            }

            Toasty.info(mContext, "Saved!", 5000, true).show();
            onBackPressed();
        }

    }



}
