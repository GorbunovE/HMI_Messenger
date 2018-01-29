package com.firebase_info.firebaseandroidchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // Иное
    // 1. Пользовательский интерфейс (UI)
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    private ListView mMessageListView;
    private EditText mMessageEditText;
    private Button mSendButton;
    private MessageAdapter mMessageAdapter;


    // 2. Firebase
    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mMessageDatabaseReference,mUserDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String test = "ТЕСТ";
    String[] str_weather;
    String[] str_bot;
    public static String LOG_TAG = "my_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // 3. Связываем UI
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);


        // 4. Инициализируем Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        // 5. Создаем слушатель базы данных

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s){

                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.getUser().equals(user.getEmail()) || message.getTo().equals(user.getEmail()) || message.getTo().equals("all")) {
                        mMessageAdapter.add(message);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            // 6. Устанавливаем слушатель базы данных
            mMessageDatabaseReference.addChildEventListener(mChildEventListener);
        }

        // 7. Создаем лист где будем хранить сообщения
        List<Message> messages = new ArrayList<>();

        // 8. Создаем и устанавливаем Адаптер для сообщений
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);

        // 9. Устанавливаем слушатель клика на кнопку, создаем сообщение, отправляем сообщение в базу, удаляем текст
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMessageEditText.getText().toString().startsWith("/to")){
                    String[] str =mMessageEditText.getText().toString().split(" ");
                mMessageDatabaseReference.push().setValue(new Message(mMessageEditText.getText().toString().replace("/to "+str[1]+" ",""),user.getEmail(),str[1]));
                mMessageEditText.setText("");
                }

                else if(mMessageEditText.getText().toString().startsWith("/справка")){
                    String info = "________________________\n" +
                            "ИНФОРМАЦИЯ \n" +
                            "________________________\n\n" +
                            "Для отправки сообщения нажмите кнопку 'Send'\n" +
                            "Для отправки личного сообщения введите '/to <email получателя> <Текст сообщения>'\n" +
                            "Данное приложение было разработанно в ознакомительных целях\n" +
                            "________________________";
                    mMessageEditText.setText("");
                }
                else{
                    mMessageDatabaseReference.push().setValue(new Message(mMessageEditText.getText().toString(),user.getEmail(),"all"));
                    mMessageEditText.setText("");
                    new GetTok().execute();
                }
            }
        });

        // * устанавливаем слушатель текста
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    // 10. Удаляем слушатель базы данных
    @Override
    protected void onDestroy() {
        if (mChildEventListener != null) {
            mMessageDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
            super.onDestroy();
        }
    }


    private class GetTok extends AsyncTask<Void, Void, String> {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://belindn.ddns.net/test.php?mytokken="+ refreshedToken);

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
