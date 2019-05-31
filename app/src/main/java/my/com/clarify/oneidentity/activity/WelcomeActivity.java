package my.com.clarify.oneidentity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import my.com.clarify.oneidentity.R;

public class WelcomeActivity extends AppCompatActivity {
    public AppCompatImageView imageCreateYourIdentity;
    //public TextView textRestoreYourIdentity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        imageCreateYourIdentity = findViewById(R.id.image_create_your_identity);
        imageCreateYourIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, IdentityCreationActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        textRestoreYourIdentity = findViewById(R.id.text_restore_your_identity);
//        textRestoreYourIdentity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WelcomeActivity.this, SigninActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
}
