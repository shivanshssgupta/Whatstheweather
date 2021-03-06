package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    String weatherDescription;
    TextView weather;
    EditText cityName;
    public void findWeather(View view)
    {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //task= new DownloadTask();
        weather= findViewById(R.id.weather);
        cityName= findViewById(R.id.cityName);
    }
    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection;
            int data;
            try{
                url= new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                data= reader.read();
                while (data!=-1)
                {
                    char current=(char) data;
                    result+= current;
                    data= reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            weatherDescription="";
            try {
                JSONObject jsonObject= new JSONObject(s);
                String weatherInfo= jsonObject.getString("weather");
                JSONArray jsonArray= new JSONArray(weatherInfo);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject part= jsonArray.getJSONObject(i);
                    weatherDescription+=part.getString("main");
                    weatherDescription+=": ";
                    weatherDescription+=part.getString("description");
                    weatherDescription+="\n";
                }
                Log.i("Info",weatherDescription);
                weather.setText(weatherDescription);
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
