package com.example.a777;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity  {


    private String url1;

    public void setUrl1(String url1) {
        this.url1 = url1;
    }
    public String getUrl1() {
        return url1;
    }

    public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            Button button = findViewById(R.id.button);
            Button buttonMain = findViewById(R.id.buttonMain);
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
                            JSONTokener tokener1 = new JSONTokener(responseStr);
                            JSONObject json1 = null;
                            try {
                                json1 = new JSONObject(tokener1);
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

                            int commentsnumber = commentsPages * 42;

                            //печать количества комментариев
                            commentsCount1.setText(String.valueOf(commentsnumber));

                            Double commentsvolume = commentsPages * 0.045;

                            @SuppressLint("DefaultLocale") String commentsvolumeToString = String.format("%.3f", commentsvolume);

                            //печать объема для скачивания
                            commentsValue1.setText(commentsvolumeToString);

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
                            String end = "Написал " + user1 + formattedDate + " ,Рейтинг " + rating;

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




                   ArrayList< ArrayList<String>> arrayList2 = new ArrayList<>();


                    OkHttpClient client1 = new OkHttpClient();

                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    int myIntValue = sp.getInt("your_int_key", -1);

                    SharedPreferences sp1 = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    String myUserName = sp1.getString("your_Username", String.valueOf(-1));

                    String finalFileName = "jsonComments_" + myUserName + ".json";

                    progressBar.setMax(myIntValue-2);

                    if (myIntValue>100) {

                        int part = myIntValue/100;

                        int finalpart = myIntValue%100;



                        for (int j=0; j<=part; j++) {


                            arrayList2.add(new ArrayList<>());
                            arrayList2.get(j).add(null);




                            for (int i = 0; i <= 100; i++) {

                                progressBar.incrementProgressBy(1);

                                Request request1 = new Request
                                        .Builder()
                                        .get()
                                        //.addHeader("Accept-Encoding", "gzip,deflate, br")
                                        .url(getUrl1() + "?page=" + (j + i + 1))
                                        .build();
                                Call call1 = client1.newCall(request1);

                                int finalI = i;
                                int finalJ = j;
                                call1.enqueue(new Callback() {

                                    @SuppressWarnings("NullableProblems")
                                    @Override
                                    public void onFailure(Call call1, IOException e) {
                                        Toast.makeText(MainActivity.this, R.string.no_user_input1, Toast.LENGTH_LONG).show();
                                    }


                                    @SuppressWarnings("NullableProblems")
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onResponse(Call call1, Response response1) throws IOException {

                                        assert response1.body() != null;
                                        String responseStr1 = response1.body().string();


                                        runOnUiThread(() -> {

                                            arrayList2.get(finalJ).add(responseStr1);


                                            String ee = arrayList2.get(finalJ).toString();

                                            FileOutputStream fos2 = null;
                                            try {
                                                fos2 = openFileOutput(finalFileName + finalJ, Context.MODE_PRIVATE);
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


                                            progressBar.setProgress(finalJ+finalI);
                                            control.setText("Пасим страница№" + finalJ+finalI);


                                        });

                                    }
                                });

                            }

                        }


                                int end = part + 1;

                        ArrayList<String>arrayList3;
                        arrayList3 = new ArrayList<>();



                                for (int i = 0; i <= finalpart; i++) {

                                    progressBar.incrementProgressBy(1);

                                    Request request1 = new Request
                                            .Builder()
                                            .get()
                                            //.addHeader("Accept-Encoding", "gzip,deflate, br")
                                            .url(getUrl1() + "?page=" + (i + 1))
                                            .build();
                                    Call call1 = client1.newCall(request1);

                                    int finalI = i;

                                    call1.enqueue(new Callback() {

                                        @SuppressWarnings("NullableProblems")
                                        @Override
                                        public void onFailure(Call call1, IOException e) {
                                            Toast.makeText(MainActivity.this, R.string.no_user_input1, Toast.LENGTH_LONG).show();
                                        }


                                        @SuppressWarnings("NullableProblems")
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onResponse(Call call1, Response response1) throws IOException {

                                            assert response1.body() != null;
                                            String responseStr1 = response1.body().string();


                                            runOnUiThread(() -> {

                                                arrayList3.add(responseStr1);

                                                String ee;
                                                ee = arrayList3.toString();

                                                FileOutputStream fos2 = null;
                                                try {
                                                    fos2 = openFileOutput(finalFileName + end, Context.MODE_PRIVATE);
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


                                                progressBar.setProgress(end+finalI);
                                                control.setText("Пасим страница№" + end+finalI);


                                            });

                                        }
                                    });

                                }


                            }













                    else {

                         ArrayList<String>arrayList;
                        arrayList = new ArrayList<>();

                        for (int i = 0; i <= myIntValue; i++) {

                            progressBar.incrementProgressBy(1);

                            Request request1 = new Request
                                    .Builder()
                                    .get()
                                    //.addHeader("Accept-Encoding", "gzip,deflate, br")
                                    .url(getUrl1() + "?page=" + (i + 1))
                                    .build();
                            Call call1 = client1.newCall(request1);

                            int finalI = i;
                            call1.enqueue(new Callback() {

                                @SuppressWarnings("NullableProblems")
                                @Override
                                public void onFailure(Call call1, IOException e) {
                                    Toast.makeText(MainActivity.this, R.string.no_user_input1, Toast.LENGTH_LONG).show();
                                }


                                @SuppressWarnings("NullableProblems")
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(Call call1, Response response1) throws IOException {

                                    assert response1.body() != null;
                                    String responseStr1 = response1.body().string();


                                    runOnUiThread(() -> {

                                        arrayList.add(responseStr1);

                                        String ee;
                                        ee = arrayList.toString();

                                        FileOutputStream fos2 = null;
                                        try {
                                            fos2 = openFileOutput(finalFileName, Context.MODE_PRIVATE);
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


                                        progressBar.setProgress(finalI);
                                        control.setText("Пасим страница№" + finalI);


                                    });

                                }
                            });

                        }


                    }


                }

            });

        }

}
