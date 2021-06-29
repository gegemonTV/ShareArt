package com.minerva.shareart.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.minerva.shareart.R;
import com.minerva.shareart.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;


public class LogInFragment extends Fragment {


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    AuthViewModel authViewModel;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance() {
        LogInFragment fragment = new LogInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        View root = inflater.inflate(R.layout.fragment_log_in, container, false);

        EditText emailEditText, passwordEditText;

        emailEditText = root.findViewById(R.id.email_input_login);
        passwordEditText = root.findViewById(R.id.password_input_login);

        Button logInButton = root.findViewById(R.id.log_in_proceed);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString(), password = passwordEditText.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()){
                                authViewModel.setIsAuthorized(true);
                                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new HomeFragment()).commit();
                                Snackbar.make(getView(), String.format("Welcome back, %s!", task.getResult().getUser().getDisplayName()), Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(getView(), String.format("Error: %s Try later.", task.getException().getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception e){
                            Snackbar.make(getView(), String.format("Error: %s Try later.", e.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return root;
    }


}