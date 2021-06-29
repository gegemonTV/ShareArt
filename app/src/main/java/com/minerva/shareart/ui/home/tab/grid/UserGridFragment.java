package com.minerva.shareart.ui.home.tab.grid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minerva.shareart.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserGridFragment extends Fragment {

    FirebaseDatabase _db = FirebaseDatabase.getInstance();
    DatabaseReference _ref = _db.getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    ArrayList<String> postIds = new ArrayList<>();

    public UserGridFragment() {
        // Required empty public constructor
    }

    public static UserGridFragment newInstance() {
        UserGridFragment fragment = new UserGridFragment();
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
        return inflater.inflate(R.layout.fragment_user_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.grid_recycler_view_home);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        GridAdapter gridAdapter = new GridAdapter(getContext(), postIds);

        recyclerView.setAdapter(gridAdapter);

        _ref.child("users").child(mUser.getUid()).child("posts").orderByChild("post_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()){
                    postIds.add(snap.getKey());
                    Log.d("SA", "onDataChange: " + snap.getKey());
                }
                Collections.reverse(postIds);
                gridAdapter.setPostIds(postIds);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Snackbar.make(getView(), String.format("Error: %s", error.getMessage()), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}