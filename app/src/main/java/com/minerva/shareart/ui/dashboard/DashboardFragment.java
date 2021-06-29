package com.minerva.shareart.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minerva.shareart.R;
import com.minerva.shareart.ui.add_post.AddPostFragment;
import com.minerva.shareart.ui.dashboard.list.ListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class DashboardFragment extends Fragment {

    String TAG = getTag();

    String filter = "|";

    ArrayList<String> postIds = new ArrayList<>();

    DatabaseReference _posts_ref = FirebaseDatabase.getInstance().getReference("posts");

    private int postsCounter = 10;

    private long timeHolder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        timeHolder = System.currentTimeMillis() / 1000;
        FloatingActionButton addPostButton = root.findViewById(R.id.add_post_button_dashboard);
        MaterialToolbar toolbar = root.findViewById(R.id.dashboard_toolbar);

        ListAdapter listAdapter = new ListAdapter(getContext(), postIds, filter);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.update:
                        timeHolder = System.currentTimeMillis() / 1000;
                        postIds.clear();
                        _posts_ref.orderByChild("post_time").endAt((double)timeHolder).limitToFirst(postsCounter).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()){
                                    for (DataSnapshot snap : task.getResult().getChildren()){
                                        Log.d(TAG, "onComplete: " + snap.getKey());
                                        postIds.add(snap.getKey());
                                    }
                                    Collections.reverse(postIds);

                                    listAdapter.setPostIds(postIds, filter);

                                    if (listAdapter.getItemCount() >= postsCounter){
                                        postsCounter += 5;
                                    }
                                }
                            }
                        });
                        return true;
                    default:
                        return false;
                }
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddPostFragment();
                Bundle b = new Bundle();
                b.putInt("prev_fragment", 1);
                fragment.setArguments(b);
                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, fragment).commit();
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.dashboard_recycler_view);

        Chip artChip = root.findViewById(R.id.art_chip_dashboard);
        Chip mITChip = root. findViewById(R.id.it_chip_dashboard);
        Chip photographyChip = root. findViewById(R.id.photography_chip_dashboard);
        Chip animalsChip = root. findViewById(R.id.animals_chip_dashboard);

        recyclerView.setAdapter(listAdapter);

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

        _posts_ref.orderByChild("post_time").endAt((double)timeHolder).limitToFirst(postsCounter).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    postIds.clear();
                    for (DataSnapshot snap : task.getResult().getChildren()){
                        Log.d(TAG, "onComplete: " + snap.getKey());
                        postIds.add(snap.getKey());
                    }
                    Collections.reverse(postIds);
                    listAdapter.setPostIds(postIds, filter);
                }
            }
        });

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    postIds.clear();
                    _posts_ref.orderByChild("post_time").endAt((double)timeHolder).limitToFirst(postsCounter).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                for (DataSnapshot snap : task.getResult().getChildren()){
                                    Log.d(TAG, "onComplete: " + snap.getKey());
                                    postIds.add(snap.getKey());
                                }
                                Collections.reverse(postIds);

                                listAdapter.setPostIds(postIds, filter);

                                if (listAdapter.getItemCount() >= postsCounter){
                                    postsCounter += 5;
                                }
                            }
                        }
                    });
                }
            }
        });
*/
        return root;
    }


}