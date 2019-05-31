package my.com.clarify.oneidentity.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;

public class ProfileDobActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public EditText inputDob;
    public AppCompatImageView imageSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dob);
        delegate = (AppDelegate)getApplication();

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputDob = findViewById(R.id.input_dob);
        inputDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar now = Calendar.getInstance();
                try {
                    now.setTime(simpleDateFormat.parse(inputDob.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ProfileDobActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        final String dob = preferences.getString(getResources().getString(R.string.param_dob), "");
        inputDob.setText(dob);

        imageSave = findViewById(R.id.image_save);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputDob.getText().toString().equals(""))
                    inputDob.setError(getString(R.string.error_required_field));

                if(!inputDob.getText().toString().equals(""))
                {
                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.param_dob), inputDob.getText().toString());
                    editor.apply();
                    finish();
                }
            }
        });

        if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
            inputDob.setFocusable(false);
            imageSave.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        String monthString = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = year + "-" + monthString + "-" + dayString;
        inputDob.setText(date);
    }
}