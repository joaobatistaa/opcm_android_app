package com.android.opcmpt.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.opcmpt.Config;
import com.android.opcmpt.R;
import com.android.opcmpt.utils.Constant;
import com.android.opcmpt.utils.NetworkCheck;
import com.android.opcmpt.utils.validation.Rule;
import com.android.opcmpt.utils.validation.Validator;
import com.android.opcmpt.utils.validation.annotation.Email;
import com.android.opcmpt.utils.validation.annotation.Password;
import com.android.opcmpt.utils.validation.annotation.Required;
import com.android.opcmpt.utils.validation.annotation.TextRule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityUserLogin extends AppCompatActivity implements Validator.ValidationListener {

    String strEmail, strPassword, strMessage, strName, strPassengerId, strImage;
    @Required(order = 1)
    @Email(order = 2, message = "Introduza um email válido!")
    EditText edtEmail;

    @Required(order = 3)
    @Password(order = 4, message = "Insira uma password válida")
    @TextRule(order = 5, minLength = 6, message = "Insira a password corretamente")
    EditText edtPassword;
    private Validator validator;
    Button btnSingIn, btnSignUp;
    MyApplication MyApp;
    TextView txt_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        setupToolbar();

        MyApp = MyApplication.getInstance();
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSingIn = findViewById(R.id.btn_update);
        btnSignUp = findViewById(R.id.btn_create);
        txt_forgot = findViewById(R.id.txt_forgot);

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validateAsync();
                MyApp.saveType("normal");

            }
        });

        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.LOST_PASSWORD_URL)));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityUserRegister.class));
                finish();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setTitle("");
        }

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }
    }

    @Override
    public void onValidationSucceeded() {
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();
        if (NetworkCheck.isNetworkAvailable(ActivityUserLogin.this)) {
            new MyTaskLoginNormal().execute(Constant.NORMAL_LOGIN_URL + strEmail + "&user_pass=" + strPassword);
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, "Record Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyTaskLoginNormal extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityUserLogin.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.login_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return NetworkCheck.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strEmail = objJson.getString(Constant.USER_EMAIL);
                            strPassengerId = objJson.getString(Constant.USER_ID);
                            strImage = objJson.getString(Constant.USER_IMAGE);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != progressDialog && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        setResult();
                    }
                }, Constant.DELAY_PROGRESS_DIALOG);
            }

        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.whops);
            dialog.setMessage(R.string.login_failed);
            dialog.setPositiveButton(R.string.dialog_ok, null);
            dialog.setCancelable(false);
            dialog.show();

        } else if (Constant.GET_SUCCESS_MSG == 2) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.whops);
            dialog.setMessage(R.string.login_disabled);
            dialog.setPositiveButton(R.string.dialog_ok, null);
            dialog.setCancelable(false);
            dialog.show();

        } else {
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strPassengerId, strName, strEmail, strImage);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.login_title);
            dialog.setMessage(R.string.login_success);
            dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

}
