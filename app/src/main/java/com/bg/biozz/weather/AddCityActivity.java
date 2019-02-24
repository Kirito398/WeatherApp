package com.bg.biozz.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCityActivity extends AppCompatActivity {

    Button addBtn, cancelBtn;
    EditText cityNameEdit;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);
        getSupportActionBar().hide();

        addBtn = findViewById(R.id.addBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        cityNameEdit = findViewById(R.id.cityNameEdit);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answer = new Intent();
                answer.putExtra("NewCity", cityNameEdit.getText().toString());
                setResult(RESULT_OK, answer);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
