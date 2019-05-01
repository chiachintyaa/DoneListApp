package mobile.tests.donelistapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {

    SharedPreferences sp;
    TextView txtName;
    EditText txtActivity;
    String email,name,activity;
    Button btnAdd;
    View progress;

    private void attemptAdd(){
        try{
            txtActivity.setError(null);
            boolean cancel = false;
            View focusView = null;
            activity = txtActivity.getText().toString();
            if(TextUtils.isEmpty(activity)){
                txtActivity.setError("Activity cannot be empty");
                focusView = txtActivity;
                cancel = true;
            }

            if(cancel){
                focusView.requestFocus();
            }else{
                new AddActivityTask(email,activity).execute();
            }
        }catch (Exception e){
            Log.e(AddActivity.class.getSimpleName(),e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            txtActivity.setVisibility(show ? View.GONE : View.VISIBLE);
            btnAdd.setVisibility(show ? View.GONE : View.VISIBLE);
            txtActivity.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    txtActivity.setVisibility(show ? View.GONE : View.VISIBLE);
                    btnAdd.setVisibility(show ? View.GONE : View.VISIBLE);
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
            txtActivity.setVisibility(show ? View.GONE : View.VISIBLE);
            btnAdd.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class AddActivityTask extends AsyncTask<Void, Void, Integer> {

        private final String t_email;
        private final String t_activity;

        AddActivityTask(String email, String activity) {
            t_email = email;
            t_activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Integer aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);
            if(aBoolean == 201){
                Toast.makeText(AddActivity.this, "Add Successful!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
                finish();
            }else if(aBoolean == 403){
                Toast.makeText(AddActivity.this, "Check your form and try again", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(AddActivity.this, "Unexpected Error. Please try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                OkHttpClient client = new OkHttpClient();
                MediaType mediatype = MediaType.parse("application/json; charset=utf-8");

                String url = "https://allpurpose.000webhostapp.com/api/activity";
                JSONObject object = new JSONObject();
                object.put("email",t_email);
                object.put("aktivitas",t_activity);

                RequestBody body = RequestBody.create(mediatype, object.toString());
                Request request = new Request.Builder().url(url).post(body).build();
                Response response = client.newCall(request).execute();

                return response.code();

            }catch (Exception e){
                Log.e(AddActivity.class.getSimpleName(),e.getMessage());
            }
            return null;
        }
    }

    private void listeners(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAdd();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("Add Activity");
        txtName.setText(name);
        listeners();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        sp = getSharedPreferences("DoneList",MODE_PRIVATE);
        email = sp.getString("sp_email",null);
        name = sp.getString("sp_name",null);

        txtName = (TextView)findViewById(R.id.user_name);
        progress = findViewById(R.id.progress);
        txtActivity = (EditText)findViewById(R.id.txtAktivitas);
        btnAdd = (Button)findViewById(R.id.btnTambah);
    }
}
