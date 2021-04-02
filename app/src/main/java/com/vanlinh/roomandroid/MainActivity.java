package com.vanlinh.roomandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vanlinh.roomandroid.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private EditText editUsername;
    private EditText editAddress;
    private EditText editYear;
    private Button btnAddUser;
    private RecyclerView rcv_User;
    private TextView tvDeleteAll;
    private EditText editSearch;

    private UserAdapter userAdapter;
    private List<User> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }

            @Override
            public void deleteUser(User user) {
                clickDeleteUser(user);
            }
        });

        listUser = new ArrayList<>();
        userAdapter.setData(listUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_User.setLayoutManager(linearLayoutManager);

        rcv_User.setAdapter(userAdapter);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickDeleteAllUser();
            }
        });

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    // search
                    handleSearchUser();
                }
                return false;
            }
        });

        loadData();
    }

    private void initUi(){
        editUsername = findViewById(R.id.edt_username);
        editAddress = findViewById(R.id.edt_address);
        editYear = findViewById(R.id.edt_year);
        btnAddUser = findViewById(R.id.btn_add_user);
        rcv_User = findViewById(R.id.rcv_user);
        tvDeleteAll = findViewById(R.id.tv_delete_all);
        editSearch = findViewById(R.id.edt_search);
    }

    private void addUser() {
        String strUsername = editUsername.getText().toString().trim();
        String strAddress =  editAddress.getText().toString().trim();
        String strYear = editYear.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)){
            return ;
        }

        User user = new User(strUsername,strAddress,strYear);

        if(isUserExist(user)){
            Toast.makeText(this, "User đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDatabase.getInstance(this).userDao().insertUser(user);
        Toast.makeText(this, "Thêm User thành công", Toast.LENGTH_SHORT).show();

        editUsername.setText("");
        editAddress.setText("");
        editYear.setText("");

        hideSoftKeyboard();

        loadData();
    }

    public  void hideSoftKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    private void loadData(){
        listUser = UserDatabase.getInstance(this).userDao().getListUser();
        userAdapter.setData(listUser);
    }

    public boolean isUserExist(User user){
        List<User> list = UserDatabase.getInstance(this).userDao().checkUser(user.getUsername());
        return list !=null && !list.isEmpty();
    }

    public void clickUpdateUser(User user){
        Intent intent  = new Intent(MainActivity.this,UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user",user);
        intent.putExtras(bundle);
        startActivityForResult(intent,MY_REQUEST_CODE);
    }

    private void clickDeleteUser( final User user) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete User")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete user
                        UserDatabase.getInstance(MainActivity.this).userDao().deleteUser(user);
                        Toast.makeText(MainActivity.this,"Delete User thành công",Toast.LENGTH_SHORT).show();

                        loadData();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void ClickDeleteAllUser(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete Alll User")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete user
                        UserDatabase.getInstance(MainActivity.this).userDao().deleteAllUser();
                        Toast.makeText(MainActivity.this,"Delete All User thành công",Toast.LENGTH_SHORT).show();

                        loadData();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void handleSearchUser(){
        String strKeyWord = editSearch.getText().toString().trim();
        listUser = new ArrayList<>();
        listUser =  UserDatabase.getInstance(this).userDao().searchUser(strKeyWord);
        userAdapter.setData(listUser);
        hideSoftKeyboard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            loadData();
        }
    }
}