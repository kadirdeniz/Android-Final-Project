package com.example.mobilefinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView sabah,ogle,ikindi,aksam,yatsi,secilenSehir,dateStrings;
    private Spinner mainSpinner;
    private RequestQueue mQueue;
    private int selectedCity,saat,dakika,saniye;
    private String[] sehirler;
    private String spinnerSehir;
    private ArrayAdapter<String> dataAdapter;

    NotificationHelper notificationHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sabah = findViewById(R.id.sabah);
        ogle = findViewById(R.id.ogle);
        ikindi = findViewById(R.id.ikindi);
        aksam = findViewById(R.id.aksam);
        yatsi = findViewById(R.id.yatsi);
        secilenSehir = findViewById(R.id.secilenSehir);
        mQueue = Volley.newRequestQueue(this);
        mainSpinner = (Spinner) findViewById(R.id.spinner);
        mainSpinner.setOnItemSelectedListener(this);
        dateStrings = findViewById(R.id.dateString);

        sehirler = new String[]{"Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
                "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
                "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum ", "Eskişehir",
                "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
                "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya ", "Malatya",
                "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya",
                "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", "Trabzon  ", "Tunceli", "Şanlıurfa", "Uşak",
                "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt ", "Karaman", "Kırıkkale", "Batman", "Şırnak",
                "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük ", "Kilis", "Osmaniye ", "Düzce"};

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sehirler);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mainSpinner.setAdapter(dataAdapter);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dateformatted = dateFormat.format(date);
        String[] total = dateformatted.split(":");
        saat  = Integer.parseInt(total[0]);
        dakika  = Integer.parseInt(total[1]);
        saniye  = Integer.parseInt(total[2]);

        Thread t = new Thread(){
          @Override
            public void run(){
              while(!isInterrupted()){
                  try {
                      Thread.sleep(1000);

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              saniye++;

                              if(saniye == 60) {
                                  saniye = 00;
                                  dakika++;
                              }

                              if(dakika == 60){
                                  dakika = 00;
                                  saat++;
                              }

                              if(saat == 24){
                                  saat=00;
                                  dakika=00;
                                  saniye=00;
                              }

                              String stringTime = "Saat : "+saat+" Dakika : "+dakika+" Saniye : "+saniye;
                              dateStrings.setText(stringTime);
                          }
                      });

                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }
        };

        t.start();

        notificationHelper = new NotificationHelper(this);
    }


    private void jsonParse(Integer sehirPlaka){
        String sehir = String.valueOf(sehirPlaka);
        String url = "https://www.my-api.co/namaz.php?il="+sehir;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String restSehir = response.getString("city");
                    secilenSehir.setText(restSehir);

                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject object = jsonArray.getJSONObject(0);

                    String restImsak = object.getString("Imsak");
                    String[] seperatedRestImsak = restImsak.split(" ");
                    sabah.setText("Sabah : "+seperatedRestImsak[1]);

                    String restOgle = object.getString("Ogle");
                    String[] seperatedRestOgle = restOgle.split(" ");
                    ogle.setText("Öğle : "+seperatedRestOgle[1]);

                    String restIkindi = object.getString("Ikindi");
                    String[] seperatedRestIkindi = restIkindi.split(" ");
                    ikindi.setText("İkindi : "+seperatedRestIkindi[1]);

                    String restAksam = object.getString("Aksam");
                    String[] seperatedRestAksam = restAksam.split(" ");
                    aksam.setText("Akşam : "+seperatedRestAksam[1]);

                    String restYatsi = object.getString("Yatsi");
                    String[] seperatedRestYatsi = restYatsi.split(" ");
                    yatsi.setText("Yatsı : "+seperatedRestYatsi[1]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        spinnerSehir = mainSpinner.getSelectedItem().toString();
        if(spinnerSehir == "Adana")
            selectedCity=01;
        else if(spinnerSehir == "Adıyaman")
            selectedCity = 02;
        else if(spinnerSehir == "Afyon")
            selectedCity=03;
        else if(spinnerSehir == "Ağrı")
            selectedCity=04;
        else if(spinnerSehir == "Amasya")
            selectedCity=05;
        else if(spinnerSehir == "Ankara")
            selectedCity=06;
        else if(spinnerSehir == "Antalya")
            selectedCity=07;
        else if(spinnerSehir == "Artvin")
            selectedCity=8;
        else if(spinnerSehir == "Aydın")
            selectedCity=9;
        else if(spinnerSehir == "Balıkesir")
            selectedCity=10;
        else if(spinnerSehir == "Bilecik")
            selectedCity=11;
        else if(spinnerSehir == "Bingöl")
            selectedCity = 12;
        else if(spinnerSehir == "Bitlis")
            selectedCity=13;
        else if(spinnerSehir == "Bolu")
            selectedCity=14;
        else if(spinnerSehir == "Burdur")
            selectedCity=15;
        else if(spinnerSehir == "Bursa")
            selectedCity=16;
        else if(spinnerSehir == "Çanakkale")
            selectedCity=17;
        else if(spinnerSehir == "Çankırı")
            selectedCity=18;
        else if(spinnerSehir == "Çorum")
            selectedCity=19;
        else if(spinnerSehir == "Denizli")
            selectedCity=20;
        else if(spinnerSehir == "Diyarbakır")
            selectedCity=21;
        else if(spinnerSehir == "Edirne")
            selectedCity = 22;
        else if(spinnerSehir == "Elazığ")
            selectedCity=23;
        else if(spinnerSehir == "Erzincan")
            selectedCity=24;
        else if(spinnerSehir == "Erzurum")
            selectedCity=25;
        else if(spinnerSehir == "Eskişehir")
            selectedCity=26;
        else if(spinnerSehir == "Gaziantep")
            selectedCity=27;
        else if(spinnerSehir == "Giresun")
            selectedCity=28;
        else if(spinnerSehir == "Gümüşhane")
            selectedCity=29;
        else if(spinnerSehir == "Hakkari")
            selectedCity=30;
        else if(spinnerSehir == "Hatay")
            selectedCity=31;
        else if(spinnerSehir == "Isparta")
            selectedCity = 32;
        else if(spinnerSehir == "Mersin")
            selectedCity=33;
        else if(spinnerSehir == "İstanbul")
            selectedCity=34;
        else if(spinnerSehir == "İzmir")
            selectedCity=35;
        else if(spinnerSehir == "Kars")
            selectedCity=36;
        else if(spinnerSehir == "Kastamonu")
            selectedCity=37;
        else if(spinnerSehir == "Kayseri")
            selectedCity=38;
        else if(spinnerSehir == "Kırklareli")
            selectedCity=39;
        else if(spinnerSehir == "Kırşehir")
            selectedCity=40;
        else if(spinnerSehir == "Kocaeli")
            selectedCity=41;
        else if(spinnerSehir == "Konya")
            selectedCity = 42;
        else if(spinnerSehir == "Kütahya")
            selectedCity=43;
        else if(spinnerSehir == "Malatya")
            selectedCity=44;
        else if(spinnerSehir == "Manisa")
            selectedCity=45;
        else if(spinnerSehir == "Kahramanmaraş")
            selectedCity=46;
        else if(spinnerSehir == "Mardin")
            selectedCity=47;
        else if(spinnerSehir == "Muğla")
            selectedCity=48;
        else if(spinnerSehir == "Muş")
            selectedCity=49;
        else if(spinnerSehir == "Nevşehir")
            selectedCity=50;
        else if(spinnerSehir == "Niğde")
            selectedCity=51;
        else if(spinnerSehir == "Ordu")
            selectedCity = 52;
        else if(spinnerSehir == "Rize")
            selectedCity=53;
        else if(spinnerSehir == "Sakarya")
            selectedCity=54;
        else if(spinnerSehir == "Samsun")
            selectedCity=55;
        else if(spinnerSehir == "Siirt")
            selectedCity=56;
        else if(spinnerSehir == "Sinop")
            selectedCity=57;
        else if(spinnerSehir == "Sivas")
            selectedCity=58;
        else if(spinnerSehir == "Tekirdağ")
            selectedCity=59;
        else if(spinnerSehir == "Tokat")
            selectedCity=60;
        else if(spinnerSehir == "Trabzon")
            selectedCity=61;
        else if(spinnerSehir == "Tunceli")
            selectedCity = 62;
        else if(spinnerSehir == "Şanlıurfa")
            selectedCity=63;
        else if(spinnerSehir == "Uşak")
            selectedCity=64;
        else if(spinnerSehir == "Van")
            selectedCity=65;
        else if(spinnerSehir == "Yozgat")
            selectedCity=66;
        else if(spinnerSehir == "Zonguldak")
            selectedCity=67;
        else if(spinnerSehir == "Aksaray")
            selectedCity=68;
        else if(spinnerSehir == "Bayburt")
            selectedCity=69;
        else if(spinnerSehir == "Karaman")
            selectedCity=70;
        else if(spinnerSehir == "Kırıkkale")
            selectedCity=71;
        else if(spinnerSehir == "Batman")
            selectedCity = 72;
        else if(spinnerSehir == "Şırnak")
            selectedCity=73;
        else if(spinnerSehir == "Bartın")
            selectedCity=74;
        else if(spinnerSehir == "Ardahan")
            selectedCity=75;
        else if(spinnerSehir == "Iğdır")
            selectedCity=76;
        else if(spinnerSehir == "Yalova")
            selectedCity=77;
        else if(spinnerSehir == "Karabük")
            selectedCity=78;
        else if(spinnerSehir == "Kilis")
            selectedCity=79;
        else if(spinnerSehir == "Osmaniye")
            selectedCity=80;
        else if(spinnerSehir == "Düzce")
            selectedCity=81;

        jsonParse(selectedCity);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Seçilen Sehir : " + spinnerSehir, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void sendNotification(View view) {
        notificationHelper.sendHighPriorityNotification(secilenSehir.getText()+" için Namaz Vakitleri ", "Sabah : "+sabah.getText().toString()+"\nÖğle : "+ogle.getText().toString()+"\nİkindi : "+ikindi.getText().toString()+"\nAkşam : "+aksam.getText().toString()+"\nYatsi : "+yatsi.getText().toString(), HomeActivity.class);
    }
}