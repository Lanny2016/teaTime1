package com.example.teatime;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    EditText edtTitle;
    EditText edtMessage;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAABcOkxb4:APA91bGJZQvH6UATYJzzjv36jpmSWDGZOrxrCvvyFyX-pr633k1oUwiEBc_-4lZO9fhZCGKZI03rnPN3l28m957CTDE3pGfl-3icq8baqYAgMYssT-cKiPM_SxmM4p4a1W_eIpWXzi1h";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private FirebaseAuth mAuth;
     String user_email;
     String start_time;

    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtTitle = findViewById(R.id.edtTitle);
        edtMessage = findViewById(R.id.edtMessage);
        Button btnSend = findViewById(R.id.btnSend);

        edtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to

                String madeTime = edtTitle.getText().toString();
                String readyTime = edtMessage.getText().toString();
                // receiving SharedPreference data from Login activity
                SharedPreferences sharedPref = getSharedPreferences("inputInfo",MODE_PRIVATE);
                String user_email = sharedPref.getString("userName","");
                /////////////////////////////////////////////////////////////////////////////////
                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();

                try {
                    notifcationBody.put("title", "Çay yapılma zamanı: " + madeTime + "\n");
                    notifcationBody.put("message1", "Hazır olma zamanı: " + readyTime + "\n" + "Yapan: " + user_email);
                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }
                sendNotification(notification);
                edtTitle.setEnabled(false);
                edtMessage.setEnabled(false);
                edtTitle.setTextColor(Color.GRAY);
            }

        });

    }

    public void processTimePickerResult(int hourOfDay, int minute) {
        // Convert time elements into strings.
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);
// Assign the concatenated strings to timeMessage.
        start_time = (String.format("%02d:%02d", hourOfDay, minute));
        edtTitle.setText(start_time);
        edtMessage.setText(start_time + " + " + "30 dakika sonra\n");
    }
//    public void setAlarm(){
//
//        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent(this,AlertReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,alarmIntent,0);
//        alarmManager.setTime(600000);
//    }

    private void sendNotification(JSONObject notification) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

//    public void clearInput() {
//        edtTitle.getText().clear();
//        edtMessage.getText().clear();
//    }

}
