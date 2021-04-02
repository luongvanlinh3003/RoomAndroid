package com.vanlinh.roomandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vanlinh.roomandroid.database.UserDatabase;

public class UpdateActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editAddress;
    private Button btnUpdateUser;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editUsername = findViewById(R.id.edt_username);
        editAddress = findViewById(R.id.edt_address);
        btnUpdateUser = findViewById(R.id.btn_update_user);

        user = (User) getIntent().getExtras().get("object_user");
        if (user!=null){
            editUsername.setText(user.getUsername());
            editAddress.setText(user.getAddress());
        }

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    private void updateUser() {
        String strUsername = editUsername.getText().toString().trim();
        String strAddress =  editAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)){
            return ;
        }

        // Update User trong database
        user.setUsername(strUsername);
        user.setAddress(strAddress);

        UserDatabase.getInstance(this).userDao().updateUser(user);
        Toast.makeText(this,"Edit user thành công",Toast.LENGTH_SHORT).show();

        Intent intentResult = new Intent();
        setResult(Activity.RESULT_OK,intentResult);
        finish();
    }

}