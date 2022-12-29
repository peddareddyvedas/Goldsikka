package com.goldsikka.goldsikka.Activitys.Predict_price;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.goldsikka.goldsikka.MainActivity;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Predict_price_address extends AppCompatActivity implements View.OnClickListener {

    TextView tv_address_type,tv_address,tv_city,tv_state,tv_pin;
    EditText et_address,et_city,et_pincode;
    Button bt_add_adderss;
    String st_address,st_city,st_pincode,st_spinstate,st_spinaddresstype;
    String rs_address,rs_city,rs_pincode,rs_state,rs_spinaddresstype;

    String[] st_address_type = {"Home","Office"};
    Spinner spinner_address_type,spinner_state;
    ArrayList<String> sublist,statelist;
    String subcategory,statesubcatagory,address_type;
    ApiDao apiDao;
    String addressId;
    String state_id;
    List<Listmodel> list;
    GifImageView loading_gif;
    String from,fromtolist,fromtoo;

    TextView unameTv, uidTv, titleTv;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_price_address);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        setTitle("Addpress");
        unameTv = findViewById(R.id.uname);
        uidTv = findViewById(R.id.uid);
        titleTv = findViewById(R.id.title);

        unameTv.setText(AccountUtils.getName(this));
        uidTv.setText(AccountUtils.getCustomerID(this));
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText("Addpress");
       // toolbar.setTitleTextColor(getColor(R.color.colorWhite));

        intilizeviews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void intilizeviews(){
        loading_gif = findViewById(R.id.loading_gif);

        et_address = findViewById(R.id.et_customeraddress);
        et_city = findViewById(R.id.et_customercity);
        et_pincode = findViewById(R.id.et_pincode);

        tv_address = findViewById(R.id.tv_erroraddress);
        tv_address_type = findViewById(R.id.address_type);
        tv_city = findViewById(R.id.tv_city);
        tv_state = findViewById(R.id.tv_state);
        tv_pin = findViewById(R.id.tv_pin);
        spinner_state = findViewById(R.id.spin_state);

        bt_add_adderss = findViewById(R.id.btn_addaddress);
        bt_add_adderss.setOnClickListener(this);

        spinner_address_type = findViewById(R.id.spin_title);
        ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,st_address_type);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_address_type.setAdapter(spinner_adapter);
        spinnerclick();


        spinner_stateclick();
    }
    public void spinner_stateclick(){
        statelist = new ArrayList<>();
        loaddata();
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statesubcatagory = spinner_state.getItemAtPosition(spinner_state.getSelectedItemPosition() -1).toString();
                //    spinner_state.getSelectedView().setTextColor(getResources().getColor(R.color.Blue));
                ((TextView) view).setTextColor(ContextCompat.getColor(Predict_price_address.this, R.color.textcolorprimary));
                // state_id     = spinner_state.getSelectedItemPosition();
                if (!statesubcatagory.equals("Select State")) {
                    Listmodel listmodel = list.get(i);
                    state_id = listmodel.getId();
                    rs_state = listmodel.getName();
                    Log.e("stateid",state_id);
                    //Toast.makeText(activity,String.valueOf(id), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    public void loaddata(){
        if (!NetworkUtils.isConnected(this)){
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        }else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Please Wait....");
            dialog.setCancelable(false);
            dialog.show();
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(this)).create(ApiDao.class);
            Call<List<Listmodel>> get_address = apiDao.get_states("Bearer " + AccountUtils.getAccessToken(this));
            get_address.enqueue(new Callback<List<Listmodel>>() {
                @Override
                public void onResponse(Call<List<Listmodel>> call, Response<List<Listmodel>> response) {
                    int statuscode = response.code();
                    list = response.body();
                    if (statuscode == HttpsURLConnection.HTTP_OK) {
                        for (Listmodel listmodel : list) {
                            dialog.dismiss();
                            statelist.add(listmodel.getName());
                        }
                        spinner_state.setAdapter(new ArrayAdapter<String>(Predict_price_address.this, android.R.layout.simple_spinner_dropdown_item,
                                statelist));

                    }
                }


                @Override
                public void onFailure(Call<List<Listmodel>> call, Throwable t) {
                  //  ToastMessage.onToast(Predict_price_address.this, "We Have Some Issues", ToastMessage.ERROR);
                    dialog.dismiss();
                }
            });
        }
    }
    public void spinnerclick(){

        sublist = new ArrayList<>();
        // spinner_signuptype = findViewById(R.id.sub_category);

        spinner_address_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subcategory = spinner_address_type.getItemAtPosition(spinner_address_type.getSelectedItemPosition() - 1).toString();
                // ((TextView) view).setTextColor(ContextCompat.getColor(RegistationActivity.this, R.color.colorWhite));
                ((TextView) view).setTextColor(ContextCompat.getColor(Predict_price_address.this, R.color.textcolorprimary));

                if (subcategory.equals("Home")) {
                    address_type = "Home";
                }else if (subcategory.equals("Office")){
                    address_type = "Office";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addaddress:
                initvalidation();
        }
    }
    public void initvalidation(){
        tv_address_type.setVisibility(View.GONE);
        tv_address.setVisibility(View.GONE);
        tv_city.setVisibility(View.GONE);
        tv_state.setVisibility(View.GONE);
        tv_pin.setVisibility(View.GONE);

        bt_add_adderss.setVisibility(View.GONE);
        loading_gif.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Predict_price_address.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validation();
                        bt_add_adderss.setVisibility(View.VISIBLE);
                        loading_gif.setVisibility(View.GONE);
                    }
                });
            }
        }, 500);
    }
    public void validation(){

        st_address = et_address.getText().toString().trim();
        st_city = et_city.getText().toString().trim();
        st_pincode = et_pincode.getText().toString().trim();

        tv_address_type.setVisibility(View.GONE);
        tv_address.setVisibility(View.GONE);
        tv_city.setVisibility(View.GONE);
        tv_state.setVisibility(View.GONE);
        tv_pin.setVisibility(View.GONE);

        if(subcategory.equals("Select Address Type")){
            ToastMessage.onToast(Predict_price_address.this, "Please Select Address Type", ToastMessage.ERROR);
        }

        else   if (st_address.isEmpty()){
            tv_address.setVisibility(View.VISIBLE);
            tv_address.setText("Please Enter Address");
        }
        else if (st_city.isEmpty()){
            tv_city.setVisibility(View.VISIBLE);
            tv_city.setText("Please Enter City");
        }
        else if (statesubcatagory.equals("Select State")){
            ToastMessage.onToast(Predict_price_address.this, "Please Select State", ToastMessage.ERROR);
        }
        else if (st_pincode.isEmpty()){
            tv_pin.setVisibility(View.VISIBLE);
            tv_pin.setText("Please Enter Zip Code");
        }
        else {
            openAdd_Address();
        }

    }
    public void openAdd_Address(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();
        if (!NetworkUtils.isConnected(this)){
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            dialog.dismiss();
            return;
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(this)).create(ApiDao.class);
            Call<Listmodel> get_address = apiDao.getcustomeraddress("Bearer " + AccountUtils.getAccessToken(this),
                    address_type, st_address, st_city, state_id, st_pincode);
            get_address.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode = response.code();
                    List<Listmodel> list = Collections.singletonList(response.body());
                    if (statuscode == HttpsURLConnection.HTTP_CREATED || statuscode == HttpsURLConnection.HTTP_OK) {
                        for (Listmodel listmodel : list) {
                            addressId = listmodel.getId();
                            //  AccountUtils.setAddressid(activity,addressId);
                            dialog.dismiss();

                            onsuccess();
                        }
                    } else {

                        try {
                            tv_address_type.setVisibility(View.GONE);
                            tv_address.setVisibility(View.GONE);
                            tv_city.setVisibility(View.GONE);
                            tv_state.setVisibility(View.GONE);
                            tv_pin.setVisibility(View.GONE);

                            assert response.errorBody() != null;
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String st = jObjError.getString("message");
                            ToastMessage.onToast(Predict_price_address.this, st, ToastMessage.ERROR);
                            JSONObject er = jObjError.getJSONObject("errors");

                            try {
                                JSONArray array_title = er.getJSONArray("title");
                                for (int i = 0; i < array_title.length(); i++) {
                                    tv_address_type.setVisibility(View.VISIBLE);
                                    tv_address_type.setText(array_title.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONArray array_address = er.getJSONArray("address");
                                for (int i = 0; i < array_address.length(); i++) {
                                    tv_address.setVisibility(View.VISIBLE);
                                    tv_address.setText(array_address.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONArray arry_city = er.getJSONArray("city");
                                for (int i = 0; i < arry_city.length(); i++) {
                                    tv_city.setVisibility(View.VISIBLE);
                                    tv_city.setText(arry_city.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray array_state = er.getJSONArray("state_id");
                                for (int i = 0; i < array_state.length(); i++) {
                                    tv_state.setVisibility(View.VISIBLE);
                                    tv_state.setText(array_state.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray array_pin = er.getJSONArray("zip");
                                for (int i = 0; i < array_pin.length(); i++) {
                                    tv_pin.setVisibility(View.VISIBLE);
                                    tv_pin.setText(array_pin.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                //    ToastMessage.onToast(Predict_price_address.this, "We Have Some Issues", ToastMessage.ERROR);
                    dialog.dismiss();
                }
            });
        }
    }
    public void onsuccess(){
        ToastMessage.onToast(Predict_price_address.this,"Successfully added",ToastMessage.SUCCESS);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}