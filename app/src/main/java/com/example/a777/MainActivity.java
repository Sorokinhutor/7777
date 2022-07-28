package com.example.a777;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity  {


    public String url1;



    public void setUrl1(String url1) {
        this.url1 = url1;
    }
    public String getUrl1() {
        return url1;
    }

    @SuppressLint("SetTextI18n")
    public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

            setContentView(R.layout.activity_main);
            Button button = findViewById(R.id.button);
            Button buttonMain = findViewById(R.id.buttonMain);
            Button buttonNext = findViewById(R.id.buttonNext);
            EditText userName = findViewById(R.id.userName);
            TextView user2 = findViewById(R.id.user2);
            CheckBox checkBox = findViewById(R.id.checkBox);
            TextView control = findViewById(R.id.control);
            TextView commentsCount1 = findViewById(R.id.commentsCount1);
            TextView pageCount1 = findViewById(R.id.pageCount1);
            TextView commentsValue1 = findViewById(R.id.commentsVolume1);
            TextView work = findViewById(R.id.work);
            TextView work1 = findViewById(R.id.work1);
            TextView urlPath1 = findViewById(R.id.urlPath1);
            ProgressBar progressBar = findViewById(R.id.progressBar);



            button.setOnClickListener(view -> {
                // Если ничего не ввели в поле, то выдаем всплывающую подсказку
                if (userName.getText().toString().trim().equals("")) {

                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();

                } else {
                    // Если ввели, то формируем ссылку для получения погоды
                    String user1 = userName.getText().toString();
                    String url1 = "https://d3.ru/api/users/" + user1 + "/comments/";
                    this.setUrl1(url1);

                    SharedPreferences sp1 = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp1.edit();
                    editor.putString("your_Username", user1);
                    editor.apply();

                    user2.setText(user1);
                    urlPath1.setText(url1);

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request
                            .Builder()
                            .get()
                            .url(url1)
                            .build();
                    Call call = client.newCall(request);

                    call.enqueue(new Callback() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(MainActivity.this, R.string.no_user_input1, Toast.LENGTH_LONG).show();

                        }

                        @SuppressWarnings("NullableProblems")
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            assert response.body() != null;
                            String responseStr = response.body().string();
                            runOnUiThread(() -> {
                                // work.setText(responseStr);
                            });
                            //делаем общий массив
                            JSONTokener tokOne = new JSONTokener(responseStr);
                            JSONObject json1 = null;
                            try {
                                json1 = new JSONObject(tokOne);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //парсим количество комментариев и считаем объем

                            String pages = null;
                            try {
                                assert json1 != null;
                                pages = json1.getString("page_count");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                              //печать количества страниц
                            pageCount1.setText(pages);

                            assert pages != null;
                             int commentsPages = Integer.parseInt(pages);

                            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("your_int_key", commentsPages);
                            editor.apply();

                            int commentsNumber = commentsPages * 42;

                            //печать количества комментариев
                            commentsCount1.setText(String.valueOf(commentsNumber));

                            Double commentsVolume = commentsPages * 0.045;

                            @SuppressLint("DefaultLocale") String commentsVolumeToString = String.format("%.3f", commentsVolume);

                            //печать объема для скачивания
                            commentsValue1.setText(commentsVolumeToString);

                            JSONArray data1 = null;
                            try {
                                data1 = json1.getJSONArray("comments");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject comments = null;
                            try {
                                assert data1 != null;
                                comments = data1.getJSONObject(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String body = null;
                            try {
                                assert comments != null;
                                body = comments
                                        .getString("body");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            long created = 0;
                            try {
                                created = comments.getLong("created");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            long unixSeconds = created; // секунды
                            Date date = new Date(unixSeconds * 1000L); // *1000 получаем миллисекунды
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf; // какой формат нужен, выбераем
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // если нужно даем таймзон
                            String formattedDate = sdf.format(date);

                            Integer rating = null;
                            try {
                                rating = comments.getInt("rating");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            assert rating != null;
                            String end = "Написал " + user1 +" "+ formattedDate + ", Рейтинг " + rating;

                            work.setText(body);
                            work1.setText(end);

                        }

                    });

                }

            });

            buttonMain.setOnClickListener(view -> {
                // Если ничего не ввели в поле, то выдаем всплывающую подсказку

                if (!checkBox.isChecked()) {

                    //checkBox.setText("Надо тут нажать");

                    Toast.makeText(MainActivity.this, R.string.no_user_input2, Toast.LENGTH_LONG).show();

                } else if (user2.getText().toString().trim().equals("")) {

                    Toast.makeText(MainActivity.this, R.string.no_user_input1, Toast.LENGTH_LONG).show();

                } else {






                        ArrayList<ArrayList<String>> arrayList2 = new ArrayList<>();


                        OkHttpClient client1 = new OkHttpClient();

                        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        int myIntValue = sp.getInt("your_int_key", -1);

                        SharedPreferences sp1 = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        String myUserName = sp1.getString("your_Username", String.valueOf(-1));

                        //String finalFileName = "jsonComments_" + myUserName + ".json";

                        progressBar.setMax(myIntValue - 2);


                        int part = myIntValue / 100;

                        for (int j = 0; j <= part; j++) {


                            arrayList2.add(new ArrayList<>());

                            //noinspection ResultOfMethodCallIgnored
                            arrayList2.get(j);


                            String finalFileName1 = "jsonComments_" + myUserName + j + ".json";

                            for (int i = 0; i <= 100; i++) {

                                //progressBar.incrementProgressBy(1);

                                int cycle = j * 100 + i;


                                Request request1 = new Request
                                        .Builder()
                                        .get()
                                        .url(getUrl1() + "?page=" + cycle)
                                        .build();


                                Call call1 = client1.newCall(request1);


                                try {
                                    call1.clone().execute();
                                    String responseStr1 = Objects.requireNonNull(call1.execute().body()).string();
                                    arrayList2.get(j).add(responseStr1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                            String ee = arrayList2.get(j).toString();

                            FileOutputStream fos2 = null;
                            try {
                                fos2 = openFileOutput(finalFileName1, Context.MODE_PRIVATE);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                assert fos2 != null;
                                fos2.write(ee.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                fos2.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            progressBar.setProgress(j * 100);
                            control.setText("Пасим страница№" + j * 100);
                            control.setText("готово" + j);
                        }

                    }




            });

        

        buttonNext.setOnClickListener(view -> {






                    Intent intent = new Intent(this, ActivityTwo.class);
                    startActivity(intent);


            });
        
        
        
        
        
    }
}

