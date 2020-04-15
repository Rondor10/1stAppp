package com.example.a1stapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ConstantSettings extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener date;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText firstName_editText, lastName_editText, userDOBView;
    private RadioGroup myGender_radio;
    private String gender = "";
    private Button btn_save;
    private User host = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constant_settings);

        host.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
        host.setTel(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        findViews();
        initViews();
        date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int  dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }

    private void save() {
        String check = "";
        String firstName = firstName_editText.getText().toString();
        String lastName = lastName_editText.getText().toString();
        if(firstName.isEmpty())
            check += "You must enter your first name. ";
        if(lastName.isEmpty())
            check += "You must enter your last name. ";

        if(myGender_radio.getCheckedRadioButtonId() == -1)
            check += "You must select your gender. ";

        String birthDay = userDOBView.getText().toString();

        if(birthDay.isEmpty())
            check += "You must enter your birthday. ";
        else {
            String month = birthDay.substring(0,2);
            String day = birthDay.substring(3, 5);
            String year = birthDay.substring(6);

            int m;
            if(month.indexOf(0) == 0)
                m = Integer.parseInt(month.substring(1));
            else
                m = Integer.parseInt(month);

            int d;
            if(day.indexOf(0) == 0)
                d = Integer.parseInt(day.substring(1));
            else
                d = Integer.parseInt(day);

            int y = Integer.parseInt(year);

            int age = getAge(y, m, d);
            if(age < 0)
                check += "You cannot select a date of the future. ";
            else if(age < 18)
                check += "Using this app requiring being 18+. ";
        }

        if(check.isEmpty()) {
            host.setFirstName(firstName);
            host.setLastName(lastName);
            host.setGender(gender);
            host.setBirthday(userDOBView.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("users").child(host.getKey()).setValue(host);
            Intent intent = new Intent(ConstantSettings.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), check, Toast.LENGTH_LONG).show();
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        // edittext.setText(sdf.format(myCalendar.getTime()));
        userDOBView.setText(sdf.format(myCalendar.getTime()));
    }

    public void onSelectGender(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked) {
                    gender = "Male";
                    break;
                }
            case R.id.radio_female:
                if (checked) {
                    gender = "Female";
                    break;
                }
            case R.id.radio_other:
                if (checked) {
                    gender = "Other";
                    break;
                }
        }
    }

    private void findViews() {
        userDOBView  = (EditText)findViewById(R.id.etDOB);
        firstName_editText = findViewById(R.id.firstname_editText);
        lastName_editText = findViewById(R.id.lastname_editText);
        myGender_radio = findViewById(R.id.myGender_radio);
        btn_save = findViewById(R.id.btn_save);
    }

    private void initViews() {

        userDOBView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    new DatePickerDialog(ConstantSettings.this, date,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public int getAge (int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        return a;
    }
}
