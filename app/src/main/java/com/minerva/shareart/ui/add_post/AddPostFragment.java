package com.minerva.shareart.ui.add_post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minerva.shareart.R;
import com.minerva.shareart.post.Post;
import com.minerva.shareart.ui.dashboard.DashboardFragment;
import com.minerva.shareart.ui.home.HomeFragment;
import com.minerva.shareart.ui.auth.AuthViewModel;
import com.minerva.shareart.utils.PasswordGenerator;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddPostFragment extends Fragment {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://shareart-826c6.appspot.com");
    StorageReference storageRef = storage.getReference();
    StorageReference imageRef = storageRef.child("images/content_images");

    DatabaseReference _posts_ref = FirebaseDatabase.getInstance().getReference("posts");
    DatabaseReference _users_ref = FirebaseDatabase.getInstance().getReference("users");

    String currentPostID, currentContentImageUrl;

    ActivityResultLauncher<Intent> imagePickerResultLauncher;
    ImageView chooseImage;

    AuthViewModel authViewModel;

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    ProgressBar progressBar;

    public AddPostFragment() {
        // Required empty public constructor
    }

    public static AddPostFragment newInstance() {
        AddPostFragment fragment = new AddPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        imagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            try{
                                assert result.getData() != null;
                                final Uri imageUri = result.getData().getData();

                                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                                StorageReference contentImageRef = imageRef.child(currentPostID + ".jpeg");
                                contentImageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("SA", "onComplete: successfully deleted");
                                        }
                                    }
                                });
                                UploadTask task = contentImageRef.putBytes(baos.toByteArray());
                                progressBar.setVisibility(View.VISIBLE);
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.d("SA", "Can not upload image into storage! " + e.getLocalizedMessage());
                                        Snackbar.make(getView(), "Can not upload image into storage! " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                                progressBar.setVisibility(View.GONE);

                                currentContentImageUrl = contentImageRef.getPath();
                                Glide
                                        .with(getContext())
                                        .load(selectedImage)
                                        .centerCrop()
                                        .into(chooseImage);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PasswordGenerator IDGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .usePunctuation(false)
                .build();

        currentPostID = IDGenerator.generate(16);
        currentContentImageUrl = null;

        Bundle args = getArguments();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_post, container, false);

        progressBar = root.findViewById(R.id.add_progressbar);

        MaterialToolbar toolbar = root.findViewById(R.id.add_post_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                if (args.getInt("prev_fragment", 0) == 0){
                    fragment = new HomeFragment();
                } else {
                    fragment = new DashboardFragment();
                }
                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, fragment).commit();
            }
        });

        Button addPostButton = root.findViewById(R.id.add_post_button_add_post);
        chooseImage = root.findViewById(R.id.choose_image_add_post);
        TextInputEditText descriptionText = root.findViewById(R.id.input_description_add_post);

        Chip artChip = root.findViewById(R.id.art_chip_add_post);
        Chip mITChip = root.findViewById(R.id.it_chip_add_post);
        Chip animalsChip = root.findViewById(R.id.animals_chip_add_post);
        Chip photographyChip = root.findViewById(R.id.photography_chip_add_post);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                imagePickerResultLauncher.launch(i);
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionText.getText().toString();
                String category = "|";
                if (artChip.isChecked()){
                    category += "art";
                    category += "|";
                }
                if (mITChip.isChecked()){
                    category += "it";
                    category += "|";
                }
                if (animalsChip.isChecked()){
                    category += "animals";
                    category += "|";
                }
                if (photographyChip.isChecked()){
                    category += "photography";
                    category += "|";
                }

                String userId = authViewModel.getIsAuthorized().getValue()? mUser.getUid() : null;
                if (currentContentImageUrl == null){
                    Snackbar.make(getView(), "Please, choose content image", Snackbar.LENGTH_LONG).show();
                } else {
                    Post createdPost = new Post(userId, description, category, currentContentImageUrl, 0, System.currentTimeMillis() / 1000);
                    _posts_ref.child(currentPostID).setValue(createdPost);
                    _users_ref.child(userId).child("posts").child(currentPostID).child("post_time").setValue(System.currentTimeMillis()/1000);
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new HomeFragment()).commit();
                }
            }
        });

        return root;
    }
}