package com.minerva.shareart.ui.favourites.tab.grid;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minerva.likebutton.LikeButton;
import com.minerva.likebutton.LikeButtonWithCounter;
import com.minerva.likebutton.OnLikeListener;
import com.minerva.shareart.R;
import com.minerva.shareart.post.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    String TAG = "SA";

    List<String> postIds;
    LayoutInflater inflater;

    String additionalFilters;

    List<Post> posts = new ArrayList<>();
    List<String> ids = new ArrayList<>();

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference _users_ref = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference _posts_ref = FirebaseDatabase.getInstance().getReference("posts");
    StorageReference _images_ref = FirebaseStorage.getInstance("gs://shareart-826c6.appspot.com").getReference();

    public GridAdapter(Context ctx, List<String> postIds, String filter){
        this.postIds = postIds;
        this.inflater = LayoutInflater.from(ctx);
        additionalFilters = filter;
        ids.clear();
        posts.clear();
        for (String id : postIds){
            Log.d(TAG, "ListAdapter: " + id);
            _posts_ref.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    Log.d(TAG, "onComplete: " + task.getResult().child("category").getValue().toString().contains(additionalFilters));
                    if (task.getResult().child("category").getValue().toString().contains(additionalFilters)){
                        posts.add(task.getResult().getValue(Post.class));
                        ids.add(id);
                    }
                }
            });
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GridAdapter.ViewHolder holder, int position) {
        Post current = posts.get(position);
        holder.likeButton.setText(String.valueOf(current.likes));

        _users_ref.child(mUser.getUid()).child("favourites").child(ids.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() == null){
                    holder.likeButton.setLiked(false);
                } else {
                    holder.likeButton.setLiked(true);
                }
            }
        });

        holder.likeButton.addOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                _posts_ref.child(ids.get(position)).child("likes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        _posts_ref.child(ids.get(position)).child("likes").setValue((long)task.getResult().getValue() + 1);
                        holder.likeButton.setText(String.valueOf((long)task.getResult().getValue() + 1));
                        _users_ref.child(mUser.getUid()).child("favourites").child(postIds.get(position)).child("post_time").setValue(System.currentTimeMillis() / 1000);
                    }
                });
            }

            @Override
            public void unliked(LikeButton likeButton) {
                _posts_ref.child(ids.get(position)).child("likes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        _posts_ref.child(ids.get(position)).child("likes").setValue((long)task.getResult().getValue() - 1);
                        holder.likeButton.setText(String.valueOf((long)task.getResult().getValue() - 1));
                        _users_ref.child(mUser.getUid()).child("favourites").child(postIds.get(position)).child("post_time").setValue(null);
                    }
                });
            }
        });

        Glide
                .with(holder.contentImage.getContext())
                .load(_images_ref.child(current.image_url))
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .into(holder.contentImage);

        /*try {

        } catch (Exception e){
            e.printStackTrace();
            Glide
                    .with(holder.contentImage.getContext())
                    .load(R.drawable.ic_baseline_broken_image_24)
                    .into(holder.contentImage);
        }*/
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void notifyDataChange(){
        this.notifyDataSetChanged();
    }

    private void addId(String id){
        ids.add(id);
    }

    public void setPostIds(List<String> ids, String filter){
        this.postIds = ids;
        additionalFilters = filter;
        posts.clear();
        this.ids.clear();
        for (String id : postIds){
            Log.d(TAG, "ListAdapta: " + id);
            _posts_ref.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    Log.d(TAG, "onComplete: " + filter);
                    Log.d(TAG, "onComplete: " + task.getResult().child("category").getValue().toString() + " " + additionalFilters + " " + task.getResult().child("category").getValue().toString().contains(additionalFilters));
                    if (task.getResult().child("category").getValue().toString().contains(additionalFilters)){
                        Log.d(TAG, "onComplete: added post. " + task.getResult().getValue(Post.class).toString());
                        posts.add(task.getResult().getValue(Post.class));
                        addId(id);
                        Log.d(TAG, "onComplete: " + posts.size());
                        notifyDataChange();
                    }
                }
            });
        }

        this.notifyDataSetChanged();
        Log.d(TAG, "setPostIds: " + posts.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView contentImage;
        LikeButtonWithCounter likeButton;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.grid_content_image);
            likeButton = itemView.findViewById(R.id.like_button_grid);
        }
    }
}
