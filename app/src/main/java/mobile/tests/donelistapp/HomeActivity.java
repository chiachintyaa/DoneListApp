package mobile.tests.donelistapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    RecyclerView rvActivity;
    ActivityAdapter activityAdapter;
    ArrayList<ActivityModel> listActivity;
    String email,name;
    View progress;
    TextView txtName;
    Button btnKeluar;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            rvActivity.setVisibility(show ? View.GONE : View.VISIBLE);
            rvActivity.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rvActivity.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            rvActivity.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class ActivityListTask extends AsyncTask<Void, Void, Integer> {

        private final String t_email;

        public ActivityListTask(String email) {
            this.t_email = email;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                OkHttpClient client = new OkHttpClient();
                MediaType mediatype = MediaType.parse("application/json; charset=utf-8");

                String url = "https://allpurpose.000webhostapp.com/api/listActivity";
                JSONObject object = new JSONObject();
                object.put("email",t_email);

                RequestBody body = RequestBody.create(mediatype, object.toString());
                Request request = new Request.Builder().url(url).post(body).build();
                Response response = client.newCall(request).execute();
//                Log.e("HEHE",response.code()+"");
                if(response.code() == 200){
                    String res = response.body().string();
                    JSONObject result = new JSONObject(res);
                    JSONArray objarray = result.getJSONArray("result");
                    for(int i=0; i<objarray.length(); i++) {
                        JSONObject objAct = objarray.getJSONObject(i);
                        ActivityModel m = new ActivityModel();
                        m.setId(objAct.getString("id"));
                        m.setActivity(objAct.getString("activity"));
                        listActivity.add(m);
                    }

                }

                return response.code();
            }catch (Exception e){
                Log.e(HomeActivity.class.getSimpleName(),e.getMessage());
            }

            return 500;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            showProgress(false);

            if(integer != 200){
                Toast.makeText(HomeActivity.this, "Tidak ada data aktivitas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecyclerView(){
        listActivity = new ArrayList<>();
        new ActivityListTask(email).execute();

        rvActivity.setLayoutManager(new LinearLayoutManager(this));

        activityAdapter = new ActivityAdapter(this);
        activityAdapter.setListActivity(listActivity);
        rvActivity.setAdapter(activityAdapter);
    }

    private void attemptLogout(){
        AlertDialog.Builder hehe = new AlertDialog.Builder(this);
        hehe.setTitle("Are you sure?");
        hehe.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences settings = getApplicationContext().getSharedPreferences("DoneList", Context.MODE_PRIVATE);
                Boolean hapus = settings.edit().clear().commit();

                if(hapus){
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        hehe.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog quit = hehe.create();
        quit.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences loginpref = this.getSharedPreferences("DoneList",MODE_PRIVATE);
        email = loginpref.getString("sp_email",null);
        name = loginpref.getString("sp_name",null);
        txtName.setText("Hi "+name+" !");
        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogout();
            }
        });
        startRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),AddActivity.class);
                startActivity(i);
                finish();
            }
        });

        progress = findViewById(R.id.progress);
        rvActivity = (RecyclerView)findViewById(R.id.listActivity);
        rvActivity.setHasFixedSize(true);
        txtName = (TextView)findViewById(R.id.user_name);
        btnKeluar = (Button)findViewById(R.id.btnKeluar);
    }

}
