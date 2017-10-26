package com.threemusketeers.healthmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Feedback extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFeedbackName;
    private EditText editTextFeedbackEmail;
    private EditText editTextFeedbackDesc;
    private Button buttonFeedbackSubmit;

    private String insertDataURL = "http://bddroid.com/HealthMaster/feedback.php";
    private static String TAG = Feedback.class.getSimpleName();

    // Progress dialogs
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextFeedbackName = (EditText) findViewById(R.id.editTextFeedbackName);
        editTextFeedbackEmail = (EditText) findViewById(R.id.editTextFeedbackEmail);
        editTextFeedbackDesc = (EditText) findViewById(R.id.editTextFeedbackDesc);
        buttonFeedbackSubmit = (Button) findViewById(R.id.buttonFeedbackSubmit);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);


        buttonFeedbackSubmit.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //User clicked home, do whatever you want
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonFeedbackSubmit) {
            if (isOnline()) {
                submit();
            } else {
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setActionTextColor(Color.GREEN).
                        setAction("TURN ON", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivityForResult(settingsIntent, 9003);
                            }
                        }).show();
            }
        }
    }

    private void submit() {
        final String name = editTextFeedbackName.getText().toString().trim();
        final String email = editTextFeedbackEmail.getText().toString().trim();
        final String desc = editTextFeedbackDesc.getText().toString().trim();

        if (!isEmailValid(email)) {
            Toast.makeText(this, "Not a valid email address", Toast.LENGTH_LONG).show();
            return;
        }

        if (desc.length() == 0) {
            Toast.makeText(this, "Please write a feedback", Toast.LENGTH_LONG).show();
            return;
        }

        showpDialog();

        StringRequest request = new StringRequest(Request.Method.POST, insertDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("name", name);
                parameters.put("email", email);
                parameters.put("description", desc);

                hidepDialog();

                return parameters;

            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
        Snackbar.make(findViewById(R.id.feedback), "Feedback Successfully Sent", Snackbar.LENGTH_LONG).show();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
