package com.uninorte.transdigital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText ced, pass;
    private Button mSubmit;

    private ProgressDialog pDialog;

    // Clase JSONParser
    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "https://transitodigital-asalcedod.c9users.io/login.php";

    // La respuesta del JSON es
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // setup input fields
        ced = (EditText) findViewById(R.id.cedula);
        pass = (EditText) findViewById(R.id.password);
        // setup buttons
        mSubmit = (Button) findViewById(R.id.login);
        // register listeners
        mSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                //Toast.makeText(this, "WTF??", Toast.LENGTH_LONG).show();
                String cedula = ced.getText().toString();
                String password = pass.getText().toString();
                if (TextUtils.isEmpty(cedula)&& TextUtils.isEmpty(password)){
                    ced.setError("No Puede Estar Vacio");
                    pass.setError("No Puede Estar Vacio");
                }
                else{
                    if (TextUtils.isEmpty(cedula)) {
                        ced.setError("No Puede Estar Vacio");
                    }
                    else
                    if (TextUtils.isEmpty(password)) {
                        pass.setError("No Puede Estar Vacio");
                    } else {
                        new AttemptLogin().execute(cedula,password);
                        break;
                    }
                }
            default:
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String cedula = args[0];
            String password = args[1];

            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("cedula", cedula));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("cedula", cedula);
                    edit.commit();
                    // Intent i = new Intent(Login.this, ReadComments.class);
                    Intent i = new Intent(Login.this, MainActivity.class);
                    // Intent i = new Intent(Login.this, Cond_Vehi_Prop.class);


                    finish();
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}