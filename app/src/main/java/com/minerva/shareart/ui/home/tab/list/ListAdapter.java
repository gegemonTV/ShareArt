package com.minerva.shareart.ui.home.tab.list;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    String TAG = "SA";

    FirebaseDatabase _db = FirebaseDatabase.getInstance();
    DatabaseReference _ref_posts = _db.getReference("posts");
    DatabaseReference _ref_users = _db.getReference("users");

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    LayoutInflater inflater;
    List<String> postIds;
    List<Post> posts = new ArrayList<>();

    StorageReference _images_ref = FirebaseStorage.getInstance("gs://shareart-826c6.appspot.com").getReference();

    public ListAdapter(Context ctx, List<String> postIds) {
        inflater = LayoutInflater.from(ctx);
        this.postIds = postIds;
        posts.clear();
        for (String postId : postIds){
            _ref_posts.child(postId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    Log.d("SA", "onComplete: " + task.getResult().getValue(Post.class).toString());
                    posts.add(task.getResult().getValue(Post.class));
                }
            });
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        _ref_posts.child(postIds.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    Post current = task.getResult().getValue(Post.class);
                    Log.d("SA", "onComplete: " + current.image_url);

                    _ref_users.child(mUser.getUid()).child("favourites").child(postIds.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (task.getResult().getValue() == null){
                                holder.likeButton.setLiked(false);
                            } else {
                                holder.likeButton.setLiked(true);
                            }
                        }
                    });

                    holder.descriptionText.setText(current.description);
                    holder.likeButton.setText(String.valueOf(current.likes));

                    holder.likeButton.addOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            _ref_posts.child(postIds.get(position)).child("likes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    _ref_posts.child(postIds.get(position)).child("likes").setValue((long)task.getResult().getValue() + 1);
                                    holder.likeButton.setText(String.valueOf((long)task.getResult().getValue() + 1));
                                    _ref_users.child(mUser.getUid()).child("favourites").child(postIds.get(position)).child("post_time").setValue(System.currentTimeMillis() / 1000);
                                }
                            });
                        }

                        @Override
                        public void unliked(LikeButton likeButton) {
                            _ref_posts.child(postIds.get(position)).child("likes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    _ref_posts.child(postIds.get(position)).child("likes").setValue((long)task.getResult().getValue() - 1);
                                    holder.likeButton.setText(String.valueOf((long)task.getResult().getValue() - 1));
                                    _ref_users.child(mUser.getUid()).child("favourites").child(postIds.get(position)).child("post_time").setValue(null);
                                }
                            });
                        }
                    });

                    try {
                        Glide
                                .with(holder.contentImage.getContext())
                                .load(_images_ref.child(current.image_url))
                                .placeholder(R.drawable.ic_baseline_broken_image_24)
                                .into(holder.contentImage);
                    } catch (Exception e){
                        e.printStackTrace();
                        Glide
                                .with(holder.contentImage.getContext())
                                .load(R.drawable.ic_baseline_broken_image_24)
                                .into(holder.contentImage);
                    }

                    _ref_users.child(current.user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            Log.d("SA", "onComplete: " + task.getResult().toString());
                            holder.usernameText.setText(task.getResult().child("nickname").getValue().toString());
                            try {
                                Glide
                                        .with(holder.userImage.getContext())
                                        .load(task.getResult().child("user_image").getValue() == null ? null : _images_ref.child(task.getResult().child("user_image").getValue().toString()))
                                        .circleCrop()
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .into(holder.userImage);
                            } catch (Exception e){
                                e.printStackTrace();
                                Glide
                                        .with(holder.userImage.getContext())
                                        .load(R.drawable.ic_baseline_account_circle_24)
                                        .circleCrop()
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .into(holder.userImage);
                            }

                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postIds.size();
    }

    public void setPostIds(List<String> Ids){
        postIds = Ids;
        posts.clear();
        for (String postId : postIds){
            _ref_posts.child(postId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    Log.d("SA", "onComplete: " + task.getResult().getValue(Post.class).toString());
                    posts.add(task.getResult().getValue(Post.class));
                }
            });
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView userImage;
        ImageView contentImage;
        TextView usernameText, descriptionText;
        LikeButtonWithCounter likeButton;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_icon_list);
            contentImage = itemView.findViewById(R.id.image_content_list);
            usernameText = itemView.findViewById(R.id.nickname_text_list);
            descriptionText = itemView.findViewById(R.id.post_description_list);
            likeButton = itemView.findViewById(R.id.like_button_list);
        }
    }
}
