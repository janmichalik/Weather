package pl.michalikjan.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class ForecastActivity extends AppCompatActivity {

    public String city;
    DbHelper dbHelper;
    long epoch;
    LineGraphSeries<DataPoint> series;
    int[] temp = new int[16];

    public int max(int[] array) {
        int max = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public int min(int[] array) {
        int min = array[0];

        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadForecast extends AsyncTask<Void, Void, Void> {

        String[][] weatherLong = new String[16][4];
        String[] weatherNow = new String[4];
        String weatherCondition;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String[] TITLE = {"", "Temperatura: ", "Ciśnienie: ", "Wiatr: "};
        final String[] END = {". ", " °C. ", " hPa. ", " km/h. "};
        final int DAYS = 15;
        final int SOURCE = 0;


        String[][] images = new String[][]{
                {"weather_none_available", "Bezchmurnie", "weather_clear_night", "Częściowo słonecznie", "weather_clouds_night", "Przeważnie słonecznie", "weather_few_clouds_night",
                        "weather_freezing_rain", "weather_hail", "Pochmurno", "weather_mist", "Deszcz", "weather_showers_day", "weather_showers_night",
                        "weather_showers_scattered", "Przelotne opady", "weather_showers_scattered_night", "weather_snow", "weather_snow_rain", "weather_snow_scattered",
                        "weather_snow_scattered_day", "weather_snow_scattered_night", "Burze z piorunami", "Częściowo słonecznie i burze", "weather_storm_night", "Słonecznie", "Zachmurzenie duże", "Przejściowe zachmurzenie", "Zachmurzenie umiarkowane"},

                {"weather_none_available", "weather_clear", "weather_clear_night", "weather_clouds", "weather_clouds_night", "weather_few_clouds", "weather_few_clouds_night",
                        "weather_freezing_rain", "weather_hail", "weather_many_clouds", "weather_mist", "weather_showers", "weather_showers_day", "weather_showers_night",
                        "weather_showers_scattered", "weather_showers_scattered_day", "weather_showers_scattered_night", "weather_snow", "weather_snow_rain", "weather_snow_scattered",
                        "weather_snow_scattered_day", "weather_snow_scattered_night", "weather_storm", "weather_storm_day", "weather_storm_night", "weather_clear", "weather_many_clouds", "weather_clouds", "weather_clouds"}
        };

        @Override
        protected void onPreExecute() {
            Toast.makeText(ForecastActivity.this, "Pobieranie danych", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String jsonString, stringData;
                StringBuilder jsonData = new StringBuilder();

                long currentMilli = System.currentTimeMillis();
                long seconds = currentMilli / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                epoch = hours / 24;
                Log.v("qwerty", ("Days since epoch : " + epoch));

                URL[][] urls = new URL[2][15];
                urls[0][0] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-krakow,cId,4970");
                urls[0][1] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-tarnow,cId,35360");
                urls[0][2] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-nowy-sacz,cId,23590");
                urls[0][3] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-oswiecim,cId,24874");
                urls[0][4] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-chrzanow,cId,4482");
                urls[0][5] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-olkusz,cId,24111");
                urls[0][6] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-nowy-targ,cId,23614");
                urls[0][7] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-bochnia,cId,1812");
                urls[0][8] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-gorlice,cId,9011");
                urls[0][9] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-zakopane,cId,40219");
                urls[0][10] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-skawina,cId,31447");
                urls[0][11] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-wieliczka,cId,37388");
                urls[0][12] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-andrychow,cId,145");
                urls[0][13] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-trzebina,cId,35971");
                urls[0][14] = new URL("https://pogoda.interia.pl/prognoza-dlugoterminowa-wadowice,cId,36720");

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urls[SOURCE][Integer.parseInt(city)].openConnection();
                InputStream in = httpsURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((jsonString = reader.readLine()) != null) {
                    jsonData.append(jsonString);
                }
                stringData = jsonData.toString().trim();

                if (jsonData.length() > 2) {
                    String[] regex = new String[8];

                    for (int i = 0; i < DAYS; i++) {
                        regex[0] = "entry-forecast-phrase\">.*?<span>(.*?)</span>.*?" + regex[0];
                        regex[1] = "entry-forecast-temp\">.*?(\\d+).*?" + regex[1];
                        regex[2] = "entry-pressure-value\">(.*?\\d+)</span>.*?" + regex[2];
                        regex[3] = "entry-wind-value\">.*?(\\d+).*?" + regex[3];
                    }

                    String[] regex1 = {
                            "weather-currently-icon-description\">\\s+(.*?)\\s+</li>",
                            "weather-currently-temp-strict\">(\\d+)",
                            "weather-currently-details-value\\s\\w+\">(\\d+)",
                            "weather-currently-details-value\">(\\d+)\\s"
                    };

                    String[] TITLE2 = {"<small><b>", "<big><big><b>", "<small>Ciśnienie:</small><br><b>", "<small>Wiatr:</small><br><b>"};
                    String[] END2 = {"</b></small>", "</b> °C</big></big>", "</b> hPa", "</b> km/h"};

                    for (int i = 0; i < weatherNow.length; i++) {
                        Pattern pattern1 = Pattern.compile(regex1[i]);
                        Matcher matcher1 = pattern1.matcher(stringData);
                        if (matcher1.find())
                            weatherNow[i] = TITLE2[i] + matcher1.group(1) + END2[i];
                        if (i == 0) {
                            weatherCondition = matcher1.group(1);
                        }
                    }

                    for (int h = 0; h < 16; h++) {
                        for (int i = 0; i < 4; i++) {
                            Pattern pattern = Pattern.compile(regex[i]);
                            Matcher matcher = pattern.matcher(stringData);
                            if (matcher.find()) {
                                if (i == 1) {
                                    temp[h] = Integer.valueOf(matcher.group(h + 1));
                                }
                                weatherLong[h][i] = TITLE[i] + matcher.group(h + 1) + END[i];
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (weatherNow[0] != null) {
                Toast.makeText(ForecastActivity.this, "Pobrano", Toast.LENGTH_SHORT).show();
                LinearLayout viewWeatherNow = findViewById(R.id.viewWeatherNow);
                viewWeatherNow.setVisibility(View.VISIBLE);
                LinearLayout chart = findViewById(R.id.chartLayout);
                chart.setVisibility(View.VISIBLE);
                LinearLayout viewWeatherLong = findViewById(R.id.viewWeatherLong);
                viewWeatherLong.setVisibility(View.VISIBLE);
                Button refresh = findViewById(R.id.refresh);
                refresh.setVisibility(View.GONE);

                GraphView graph = findViewById(R.id.chart);
                graph.getGridLabelRenderer().setTextSize(32f);
                graph.getGridLabelRenderer().reloadStyles();
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(1);
                graph.getViewport().setMaxX(14);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(min(temp));
                graph.getViewport().setMaxY(max(temp));
                graph.setTitle("Temperatura 14-dniowa");

                series = new LineGraphSeries<>();
                for (int i = 0; i < temp.length; i++) {
                    series.appendData(new DataPoint(i, temp[i]), true, 15);
                }
                graph.addSeries(series);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                for (int i = 0; i <= 7; i++)
                    dbHelper.insertNewTask(timeStamp, String.valueOf(epoch), city, weatherLong[7 - i][0], weatherLong[7 - i][1], weatherLong[7 - i][2], weatherLong[7 - i][3]);

                Log.v("elo320", dbHelper.getOldWeather("18060", "0"));

                String imageOfWeather = "weather_none_available";
                for (int i = 0; i < images[1].length; i++) {
                    if (images[0][i].equals(weatherCondition)) imageOfWeather = images[1][i];
                }
                Log.v("qwerty", ("|" + weatherCondition + "|"));
                String htmlText = "<img src=\"" + imageOfWeather + "\">"; // [zawsze 1][...]
                TextView t0 = findViewById(R.id.textView1);
                t0.setText(Html.fromHtml(htmlText, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        int resourceId = getResources().getIdentifier(source, "drawable", getPackageName());
                        Drawable drawable = getResources().getDrawable(resourceId);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        return drawable;
                    }
                }, null));

                TextView t1 = findViewById(R.id.textView3);
                t1.setText(Html.fromHtml(weatherNow[0]));
                TextView t2 = findViewById(R.id.textView4);
                t2.setText(Html.fromHtml(weatherNow[1]));
                TextView t3 = findViewById(R.id.textView5);
                t3.setText(Html.fromHtml(weatherNow[2]));
                TextView t4 = findViewById(R.id.textView6);
                t4.setText(Html.fromHtml(weatherNow[3]));

                StringBuilder builder2 = new StringBuilder();
                builder2.append("<b><u><font color='#781B47'>Archiwalna pogoda na dziś wg. Interia.pl:</font></u></b><br><br>");
                for (int i = 7; i >= 0; i--) {
                    String a = dbHelper.getOldWeather(Long.toString(epoch - i), city);
                    if (a.length() > 2) {
                        if (i == 0) {
                            builder2.append("<b>Dzisiaj:</b><br>");
                        } else {
                            builder2.append("<b>");
                            builder2.append(i);
                            if (i == 1) builder2.append(" dzień temu:</b><br>");
                            else builder2.append(" dni temu:</b><br>");
                        }
                        builder2.append(a);
                        builder2.append("<br><br>");
                    }
                }
                builder2.append("" +
                        "<b><u><font color='#1B4778'>Pogoda długoterminowa wg. Interia.pl:</font></u></b><br>");
                for (int i = 1; i < DAYS; i++) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DATE, i);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    String[] day = {"", "Niedziela", "Ponidziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};
                    String date = sdf.format(c.getTime());
                    builder2.append("<br>");
                    builder2.append("<b>");
                    builder2.append(day[dayOfWeek]);
                    builder2.append(", ");
                    builder2.append(date);
                    builder2.append(" (za ");
                    builder2.append(i);
                    if (i == 1) builder2.append(" dzień");
                    else builder2.append(" dni");
                    builder2.append("):</b><br>");
                    for (String value : weatherLong[i]) {
                        builder2.append(value);
                    }
                    builder2.append("<br>");
                }

                String forecastText = builder2.toString();
                TextView tt = findViewById(R.id.textView2);
                tt.setMovementMethod(new ScrollingMovementMethod());
                tt.setText(Html.fromHtml(forecastText));
            } else {
                Toast.makeText(ForecastActivity.this, "Błąd połączenia z Internetem", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAccent));
        }
        dbHelper = new DbHelper(this);
        //this.deleteDatabase("forecast"); // usuwanie tabeli z bazy danych
        Button refresh = findViewById(R.id.refresh);
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        String cityName = intent.getStringExtra("cityName");
        setTitle("Pogoda dla miasta " + cityName);
        new DownloadForecast().execute();
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DownloadForecast().execute();
            }
        });
    }
}
