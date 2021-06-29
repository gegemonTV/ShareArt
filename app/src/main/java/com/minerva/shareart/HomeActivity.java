package com.minerva.shareart;

import android.os.Bundle;
import android.util.Log;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.minerva.shareart.databinding.ActivityHomeBinding;
import com.minerva.shareart.ui.auth.AuthViewModel;

import org.jetbrains.annotations.NotNull;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    public static BottomNavigationView navView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    AuthViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appodeal.initialize(this, "3de17ce8292f05a11684dd2ce1896eebab7cd66e02990938", Appodeal.BANNER);
        Appodeal.setBannerViewId(R.id.banner_dashboard);

        Appodeal.show(this, Appodeal.BANNER_VIEW);

        boolean isAuthorized = getIntent().getExtras().getBoolean("isAuthorized", false);
        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null){
                    Log.d("SA", "user!=null");
                    mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<GetTokenResult> task) {
                            try {
                                if (task.getResult() != null){
                                    Log.d("SA", "Logged in");
                                    mViewModel.setIsAuthorized(true);
                                } else {
                                    Log.d("SA", "Unauthorized");
                                    mViewModel.setIsAuthorized(false);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.d("SA", "User has been deleted!");
                                mViewModel.setIsAuthorized(false);
                            }
                        }
                    });
                } else {
                    Log.d("SA", "Unauthorized/user=null");
                    mViewModel.setIsAuthorized(false);
                }
            }
        });

        mViewModel.setIsAuthorized(isAuthorized);
        Log.d("SA", "onCreate: "+isAuthorized);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_favourites)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        /*NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}