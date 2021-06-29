package com.minerva.shareart.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minerva.shareart.R;
import com.minerva.shareart.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    AuthViewModel authViewModel;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
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

        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        TextInputEditText editNickname, editEmail, editPassword;

        editNickname = root.findViewById(R.id.nickname_input_signup);
        editEmail = root.findViewById(R.id.email_input_signup);
        editPassword = root.findViewById(R.id.password_input_signup);

        Button signUpButton = root.findViewById(R.id.sign_up_proceed);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseDatabase _db = FirebaseDatabase.getInstance();

                DatabaseReference _refUsers = _db.getReference("users");

                String nickname, email, password;
                nickname = editNickname.getText().toString();
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                try {
                                    if (task.isSuccessful()){
                                        _refUsers.child(task.getResult().getUser().getUid()).child("nickname").setValue(nickname);
                                        task.getResult().getUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nickname).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    authViewModel.setIsAuthorized(true);
                                                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new HomeFragment()).commit();
                                                    Snackbar.make(getView(), String.format("Welcome, %s!", nickname), Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Snackbar.make(getView(), "Authentication failed.", Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Snackbar.make(getView(), "Authentication failed." + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        return root;
    }
}