package com.example.krahs.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.krahs.R;
import com.example.krahs.utils.CheckInternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edt_username, edt_fullname, edt_email, edt_password,edt_confirm_password;
    Button register;
    ImageView img_back;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new CheckInternetConnection(this).checkConnection();
        edt_username = findViewById(R.id.su_displayname);
        edt_email = findViewById(R.id.su_email);
        edt_fullname = findViewById(R.id.su_yourname);
        edt_password = findViewById(R.id.su_password);
        register = findViewById(R.id.su_sign_up);
        img_back = findViewById(R.id.su_back);
        edt_confirm_password = findViewById(R.id.su_confirm_pass);

        auth = FirebaseAuth.getInstance();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_username = edt_username.getText().toString();
                String str_fullname = edt_fullname.getText().toString();
                String str_email = edt_email.getText().toString();
                String str_password = edt_password.getText().toString();
                String cf_password = edt_confirm_password.getText().toString();

                /*if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show(); //todo replace alert
                } else if(!str_password.equals(cf_password))
                {
                    Toast.makeText(RegisterActivity.this,"Confirm password incorrect!",Toast.LENGTH_LONG).show();
                }*/

                if (validateUsername(str_email,str_username,str_fullname) || validatePassword(str_password,cf_password)){
                        pd = new ProgressDialog(RegisterActivity.this);
                        pd.setMessage("Please wait...");
                        pd.show();
                        register(str_username, str_fullname, str_email, str_password);

                }
            }
        });
    }

    public void register(final String username, final String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("username", username.toLowerCase());
                            map.put("fullname", fullname);
                            map.put("status","online");
                            map.put("imageurl", "https://preview.redd.it/szvpa1e7li331.png?width=960&crop=smart&auto=webp&s=4792a89d3e0cdd732e6398e6ba1f114b27099e32");
                            map.put("bio", "");

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validatePassword(String pass,String retypePassword) {


        if (pass.length() < 4 || pass.length() > 20 || !pass.equals(retypePassword)) {
            edt_password.setError("Password Must consist of 4 to 20 characters");
            edt_confirm_password.setError("Password and retype password not matched");
            return false;
        }
        return true;
    }

    private boolean validateUsername(String email,String username,String fullname) {

        if (email.length() < 4 || email.length() > 30 || username.length()==0 || fullname.length()==0) {
            edt_email.setError("Email Must consist of 4 to 30 characters");
            edt_username.setError("This field can't empty");
            edt_fullname.setError("This field can't empty");
            return false;
        } else if (!email.matches("^[A-za-z0-9.@]+") || username.matches("/s") ) {
            edt_email.setError("Only . and @ characters allowed");
            edt_username.setError("Display name cannot contains spaces");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            edt_email.setError("Email must contain @ and .");
            return false;
        }
        return true;
    }

    TextWatcher usernameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edt_email.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edt_email.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edt_email.setError("Enter Valid Email");
            }
            if (check.length() == 0) {
                edt_username.setError("This field can't empty");
                edt_email.setError("This field can't empty");
                edt_password.setError("This field can't empty");
                edt_confirm_password.setError("This field can't empty");
            }
        }

    };

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            check = editable.toString();
            if (check.length() < 4 || check.length() > 20) {
                edt_password.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edt_password.setError("Only @ special character allowed");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }
}
