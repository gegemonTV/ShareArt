package com.minerva.shareart.ui.home.tab.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minerva.shareart.R;
import com.minerva.shareart.post.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserListFragment extends Fragment {

    FirebaseDatabase _db = FirebaseDatabase.getInstance();
    DatabaseReference _ref = _db.getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    public UserListFragment() {
        // Required empty public constructor
    }


    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
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
        View root = inflater.inflate(R.layout.fragment_user_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.user_list_recycler_view);

        ArrayList<String> postIds = new ArrayList<>();

        ListAdapter listAdapter = new ListAdapter(getContext(), postIds);

        recyclerView.setAdapter(listAdapter);

        _ref.child("users").child(mUser.getUid()).child("posts").orderByChild("post_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()){
                    postIds.add(snap.getKey());
                    Log.d("SA", "onDataChange: " + snap.getKey());
                }
                Collections.reverse(postIds);
//                listAdapter.notifyDataSetChanged();
                listAdapter.setPostIds(postIds);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Snackbar.make(getView(), String.format("Error: %s", error.getMessage()), Snackbar.LENGTH_LONG).show();
            }
        });

        return root;
    }
}