package com.goldsikka.goldsikka;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.goldsikka.goldsikka.Fragments.Sell_Fragment;
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
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import javax.net.ssl.HttpsURLConnection;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBankDetailsForSellModule extends AppCompatActivity implements View.OnClickListener {


    EditText et_accountname,et_accountnumber,et_bankname,et_bankbranch,et_ifsc;
    Button add_bankdetails;
    TextView tv_accountname,tv_accountnumber,tv_bankname,tv_branch,tv_ifsc;
    String  st_accountname,st_accountnumber,st_bankname,st_bankbranch,st_ifsc;
    ApiDao apiDao;

    GifImageView loading_gif;

    TextView uidTv, unameTv, titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_details_for_sell_module);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titleTv = findViewById(R.id.title);
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText("Add your Bank Details");
//        setTitle("Add your Bank Details");
        //  toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        intilizeviews();
    }
    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
        // finish();
    }
    public void intilizeviews(){

        unameTv = findViewById(R.id.uname);
        uidTv = findViewById(R.id.uid);

        unameTv.setText(AccountUtils.getName(this));
        uidTv.setText(AccountUtils.getCustomerID(this));

        et_accountname = findViewById(R.id.et_accountname);
        et_accountnumber = findViewById(R.id.et_accountnumber);
        et_bankname = findViewById(R.id.et_bankname);
        et_bankbranch = findViewById(R.id.et_bankbranch);
        et_ifsc = findViewById(R.id.et_ifsccode);

        loading_gif = findViewById(R.id.loading_gif);

        tv_accountname = findViewById(R.id.tv_accountname);
        tv_accountnumber = findViewById(R.id.tv_accountnumber);
        tv_bankname = findViewById(R.id.tv_bankname);
        tv_branch = findViewById(R.id.tv_bankbranch);
        tv_ifsc = findViewById(R.id.tv_ifsc);

        add_bankdetails = findViewById(R.id.btn_add);
        add_bankdetails.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                init_validation();
                break;
        }
    }
    public void init_validation(){

        tv_accountname.setVisibility(View.GONE);
        tv_accountnumber.setVisibility(View.GONE);
        tv_bankname.setVisibility(View.GONE);
        tv_branch.setVisibility(View.GONE);
        tv_ifsc.setVisibility(View.GONE);

        add_bankdetails.setVisibility(View.GONE);
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

                AddBankDetailsForSellModule.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validation();
                        add_bankdetails.setVisibility(View.VISIBLE);
                        loading_gif.setVisibility(View.GONE);
                    }
                });
            }
        }, 500);
    }
    public void validation(){
        st_accountname = et_accountname.getText().toString().trim();
        st_accountnumber = et_accountnumber.getText().toString().trim();
        st_bankname = et_bankname.getText().toString().trim();
        st_bankbranch = et_bankbranch.getText().toString().trim();
        st_ifsc = et_ifsc.getText().toString().trim();

        tv_accountname.setVisibility(View.GONE);
        tv_accountnumber.setVisibility(View.GONE);
        tv_bankname.setVisibility(View.GONE);
        tv_branch.setVisibility(View.GONE);
        tv_ifsc.setVisibility(View.GONE);

        if (st_accountname.isEmpty()){
            tv_accountname.setVisibility(View.VISIBLE);
            tv_accountname.setText("Please Enter Account Holder Name");
        }
        else if (st_accountnumber.isEmpty()){
            tv_accountnumber.setVisibility(View.VISIBLE);
            tv_accountnumber.setText("Please Enter Account Number");
        }
        else if (st_bankname.isEmpty()){
            tv_bankname.setVisibility(View.VISIBLE);
            tv_bankname.setText("Please Enter Bank Name");
        }
        else if (st_bankbranch.isEmpty()){
            tv_branch.setVisibility(View.VISIBLE);
            tv_branch.setText("Please Enter Branch Name");
        }
        else if (st_ifsc.isEmpty()){
            tv_ifsc.setVisibility(View.VISIBLE);
            tv_ifsc.setText("Please Enter IFSC Code");
        }
        else {
            openaddbankdetails();
        }
    }

    private void openaddbankdetails() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();

        if (!NetworkUtils.isConnected(this)) {
            ToastMessage.onToast(this,getString(R.string.error_no_internet_connection),ToastMessage.ERROR);
            dialog.dismiss();
            //ToastMessage.onToast(this,getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
           // ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(this)).create(ApiDao.class);
            Call<Listmodel> getbankdetails = apiDao.get_bankdetails("Bearer " + AccountUtils.getAccessToken(this), st_accountnumber, st_accountname, st_bankname, st_bankbranch, st_ifsc);
            getbankdetails.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode = response.code();
                    Log.e("code", String.valueOf(statuscode));
                    if (statuscode == HttpsURLConnection.HTTP_OK || statuscode == HttpsURLConnection.HTTP_CREATED || statuscode == HttpsURLConnection.HTTP_ACCEPTED) {
                        List<Listmodel> list = Collections.singletonList(response.body());
                        for (Listmodel listmodel : list) {
                            dialog.dismiss();
                            onsuccess();
                            ToastMessage.onToast(AddBankDetailsForSellModule.this, "successfully added", ToastMessage.SUCCESS);
                            // ToastMessage.onToast( AddBankDetailsForSellModule.this,"successfully added",ToastMessage.SUCCESS);
                        }
                    } else {
                        try {
                            tv_accountname.setVisibility(View.GONE);
                            tv_accountnumber.setVisibility(View.GONE);
                            tv_bankname.setVisibility(View.GONE);
                            tv_branch.setVisibility(View.GONE);
                            tv_ifsc.setVisibility(View.GONE);

                            // response.errorBody() != null;
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String st = jObjError.getString("message");
//                            ToastMessage.onToast(AddBankDetailsForSellModule.this, st, ToastMessage.ERROR);
                            // ToastMessage.onToast( AddBankDetailsForSellModule.this, st, ToastMessage.ERROR);
                            JSONObject er = jObjError.getJSONObject("errors");

                            try {
                                JSONArray array_title = er.getJSONArray("name_on_account");
                                for (int i = 0; i < array_title.length(); i++) {
                                    tv_accountname.setVisibility(View.VISIBLE);
                                    tv_accountname.setText(array_title.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONArray array_address = er.getJSONArray("account_number");
                                for (int i = 0; i < array_address.length(); i++) {
                                    tv_accountnumber.setVisibility(View.VISIBLE);
                                    tv_accountnumber.setText(array_address.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONArray arry_city = er.getJSONArray("bank_name");
                                for (int i = 0; i < arry_city.length(); i++) {
                                    tv_bankname.setVisibility(View.VISIBLE);
                                    tv_bankname.setText(arry_city.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray array_state = er.getJSONArray("bank_branch");
                                for (int i = 0; i < array_state.length(); i++) {
                                    tv_branch.setVisibility(View.VISIBLE);
                                    tv_branch.setText(array_state.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray array_pin = er.getJSONArray("ifsc_code");
                                for (int i = 0; i < array_pin.length(); i++) {
                                    tv_ifsc.setVisibility(View.VISIBLE);
                                    tv_ifsc.setText(array_pin.getString(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        } catch (JSONException | IOException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    Log.e("saaf", t.toString());
                }
            });
        }

    }

    private void onsuccess() {
       Intent intent = new Intent(AddBankDetailsForSellModule.this, Sell_Fragment.class);
       startActivity(intent);
    }




//    Call<Listmodel> get_bankdetails = apiDao.get_bankdetails("Bearer "+AccountUtils.getAccessToken(this),
//            st_accountnumber,st_accountname,st_bankname,st_bankbranch,st_ifsc);
//        get_bankdetails.enqueue(new Callback<Listmodel>() {
//        @Override
//        public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
//            int statuscode = response.code();
//            List<Listmodel> list = Collections.singletonList(response.body());
//            if (statuscode == HttpsURLConnection.HTTP_CREATED||statuscode == HttpsURLConnection.HTTP_OK||
//                    statuscode ==HttpsURLConnection.HTTP_ACCEPTED){
//                for (Listmodel listmodel : list){
//
//
//                    dialog.dismiss();
//                    ToastMessage.onToast( AddBankDetailsForSellModule.this,"successfully added",ToastMessage.SUCCESS);
//                    onsuccess();
//                }
//            }
//            else {

//            }
//        }
//
//        @Override
//        public void onFailure(Call<Listmodel> call, Throwable t) {
//            ToastMessage.onToast( AddBankDetailsForSellModule.this,"We Have Some Issues",ToastMessage.ERROR);
//            dialog.dismiss();
//        }
//    });
}