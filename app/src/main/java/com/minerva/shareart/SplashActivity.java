package com.minerva.shareart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.minerva.shareart.ui.auth.AuthViewModel;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        Bundle sharedAuth = new Bundle();

        Intent i = new Intent(getApplicationContext(), HomeActivity.class);

        if (mUser != null){
            Log.d("SA", "user!=null");
            mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<GetTokenResult> task) {
                    try {
                        if (task.getResult() != null){
                            i.putExtra("isAuthorized", true);
                            Log.d("SA", "Logged in");
                            startActivity(i);
                        } else {
                            i.putExtra("isAuthorized", false);
                            Log.d("SA", "Unauthorized");
                            startActivity(i);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.d("SA", "User has been deleted!");
                        i.putExtra("isAuthorized", false);
                        startActivity(i);
                    }
                }
            });
        } else {
            Log.d("SA", "Unauthorized/user=null");
            i.putExtra("isAuthorized", false);
            startActivity(i);
        }
    }
}