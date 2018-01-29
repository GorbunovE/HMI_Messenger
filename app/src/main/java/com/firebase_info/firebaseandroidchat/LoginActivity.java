package com.firebase_info.firebaseandroidchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;
    public static String LOG_TAG = "my_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);

                } else {
                    // User is signed out

                }

            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        boolean email = checkEmail(ETemail.getText().toString());
        boolean password = checkPassword(ETpassword.getText().toString());
        if (!email) ETemail.setError("Введен некорректный email!");
        if (!password) ETpassword.setError("Введен слишком корокий пароль!");
        if(view.getId() == R.id.btn_sign_in)
        {
            if (email && password) signin(ETemail.getText().toString(),ETpassword.getText().toString());
        }
        else if (view.getId() == R.id.btn_registration)
        {
            if (email && password) registration(ETemail.getText().toString(),ETpassword.getText().toString());
        }
    }

    public boolean checkEmail (String email)  {
        String str = "^([a-zA-Z0-9]+[\\w.-]*)@([a-zA-Z0-9]+[\\w.-])+.([a-z]{2,6})$";
        Pattern check = Pattern.compile(str);
        Matcher ce = check.matcher(email);
        return ce.matches();
    }
    public boolean checkPassword (String password)  {
        String str = "^[\\w]{6,}$";
        Pattern check = Pattern.compile(str);
        Matcher cp = check.matcher(password);
        return cp.matches();
    }

    public void signin(String email , String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    new LoginActivity.RegTok().execute();
                    startActivity(intent);
                }else
                    Toast.makeText(LoginActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void registration (String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    new LoginActivity.RegTok().execute();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);

                }
                else
                    Toast.makeText(LoginActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class RegTok extends AsyncTask<Void, Void, String> {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://belindn.ddns.net/tokken.php?tokken="+refreshedToken);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);
        }
    }
}

