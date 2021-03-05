package pl.michalikjan.weather;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String[] dataArray = {"Kraków", "Tarnów", "Nowy Sącz", "Oświęcim", "Chrzanów", "Olkusz", "Nowy Targ",
            "Bochnia", "Gorlice", "Zakopane", "Skawina", "Wieliczka", "Andrychów", "Trzebina", "Wadowice"};
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAccent));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText theFilter = findViewById(R.id.searchFilter);
        ListView listView = findViewById(R.id.cities);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataArray);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
                String clickedCity = adapterView.getItemAtPosition(i).toString();
                intent.putExtra("city", String.valueOf(Arrays.asList(dataArray).indexOf(clickedCity)));
                intent.putExtra("cityName", clickedCity);
                startActivity(intent);
            }
        });

        theFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (MainActivity.this).adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
