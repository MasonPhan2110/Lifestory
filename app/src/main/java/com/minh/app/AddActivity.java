package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    Intent intent;
    String userid, case_add;
    EditText edit_text;
    Button save;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        intent = getIntent();
        userid =intent.getStringExtra("userid");
        case_add = intent.getStringExtra("case");
        Toolbar toolbar = findViewById(R.id.toolbar);

        edit_text = findViewById(R.id.edit_text);
        save = findViewById(R.id.save_btn);
        save.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        switch (case_add){
            case "add work":
                getSupportActionBar().setTitle("Add Workplace");
                edit_text.setHint("Workplace Name");
                break;
            case "add edu":
                getSupportActionBar().setTitle("Add Education");
                edit_text.setHint("School Name");
                edit_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String value = s.toString();
                        if(value.equals("")){
                            save.setVisibility(View.GONE);
                        }else{
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("education",edit_text.getText().toString());
                        setResult(Activity.RESULT_OK,intent1);
                        finish();
                    }
                });
                break;
            case "add current city":
                getSupportActionBar().setTitle("Add Current City");
                edit_text.setHint("City Name");
                edit_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String value = s.toString();
                        if(value.equals("")){
                            save.setVisibility(View.GONE);
                        }else{
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("city",edit_text.getText().toString());
                        setResult(Activity.RESULT_OK,intent1);
                        finish();
                    }
                });
                break;
            case "add hometown":
                getSupportActionBar().setTitle("Add Hometown");
                edit_text.setHint("Hometown Name");
                edit_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String value = s.toString();
                        if(value.equals("")){
                            save.setVisibility(View.GONE);
                        }else{
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("hometown",edit_text.getText().toString());
                        setResult(Activity.RESULT_OK,intent1);
                        finish();
                    }
                });
                break;
            case "add relationship":
                getSupportActionBar().setTitle("Add Relationship");
                edit_text.setVisibility(View.GONE);
                break;
        }
    }
}