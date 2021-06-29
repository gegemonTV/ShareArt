package com.minerva.shareart.ui.favourites.tab.list;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
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
import java.util.List;

public class FavouritesListFragment extends Fragment {

    String TAG = "SA";

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    List<String> postIds = new ArrayList<>();
    String filter = "|";

    DatabaseReference _users_ref = FirebaseDatabase.getInstance().getReference("users");

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.favourites_recycler_view_list);

        ListAdapter listAdapter = new ListAdapter(getContext(), postIds, filter);

        recyclerView.setAdapter(listAdapter);

        Chip artChip = root.findViewById(R.id.art_chip_favourites);
        Chip mITChip = root. findViewById(R.id.it_chip_favourites);
        Chip photographyChip = root. findViewById(R.id.photography_chip_favourites);
        Chip animalsChip = root. findViewById(R.id.animals_chip_favourites);

        artChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter = String.format("|%s%s%s%s",
                        artChip.isChecked()? "art" + "|" : "",
                        mITChip.isChecked()? "it" + "|" : "",
                        photographyChip.isChecked()? "photography" + "|" : "",
                        animalsChip.isChecked()? "animals" + "|" : "");

                listAdapter.setPostIds(postIds, filter);
                Log.d(TAG, "onCheckedChanged: " + filter);
            }
        });

        mITChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter = String.format("|%s%s%s%s",
                        artChip.isChecked()? "art" + "|" : "",
                        mITChip.isChecked()? "it" + "|" : "",
                        photographyChip.isChecked()? "photography" + "|" : "",
                        animalsChip.isChecked()? "animals" + "|" : "");

                listAdapter.setPostIds(postIds, filter);
                Log.d(TAG, "onCheckedChanged: " + filter);
            }
        });
        photographyChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter = String.format("|%s%s%s%s",
                        artChip.isChecked()? "art" + "|" : "",
                        mITChip.isChecked()? "it" + "|" : "",
                        animalsChip.isChecked()? "animals" + "|" : "",
                        photographyChip.isChecked()? "photography" + "|" : "");

                listAdapter.setPostIds(postIds, filter);
                Log.d(TAG, "onCheckedChanged: " + filter);
            }
        });
        animalsChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter = String.format("|%s%s%s%s",
                        artChip.isChecked()? "art" + "|" : "",
                        mITChip.isChecked()? "it" + "|" : "",
                        photographyChip.isChecked()? "photography" + "|" : "",
                        animalsChip.isChecked()? "animals" + "|" : "");

                listAdapter.setPostIds(postIds, filter);
                Log.d(TAG, "onCheckedChanged: " + filter);

            }
        });

        _users_ref.child(mUser.getUid()).child("favourites").orderByChild("post_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                postIds.clear();
                Log.d(TAG, "onDataChange: " + snapshot.toString());
                for (DataSnapshot snap: snapshot.getChildren()){
                    postIds.add(snap.getKey());
                }
                Collections.reverse(postIds);
                listAdapter.setPostIds(postIds, filter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
