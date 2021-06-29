package com.minerva.shareart.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minerva.shareart.R;
import com.minerva.shareart.ui.add_post.AddPostFragment;
import com.minerva.shareart.ui.auth.AuthViewModel;
import com.minerva.shareart.ui.auth.LogInFragment;
import com.minerva.shareart.ui.auth.SignUpFragment;
import com.minerva.shareart.ui.home.tab.PageAdapter;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    AuthViewModel authViewModel;

    boolean isAuthorized = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    private int mPage = 0;
    public static final String ARG_PAGE = "ARG_PAGE";


    public static HomeFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        isAuthorized = authViewModel.getIsAuthorized().getValue();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = null;

        Log.d("SA", ""+isAuthorized);
        if (isAuthorized){
            Log.d("SA", "Inflating logged in");
            root = inflater.inflate(R.layout.fragment_home_logged_in, container, false);
        }else{
            root = inflater.inflate(R.layout.fragment_home_required_login, container, false);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("SA", ""+isAuthorized);
        if (isAuthorized){
            TextView nicknameTextView = view.findViewById(R.id.username_profile);
            ShapeableImageView imageView = view.findViewById(R.id.user_image_profile);
            Button editProfileButton = view.findViewById(R.id.edit_profile_button);

            nicknameTextView.setText(mUser.getDisplayName());

            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = getLayoutInflater().inflate(R.layout.edit_profile_dialog, null);

                    /*Button logOutButton = dialogView.findViewById(R.id.log_out_button);

                    logOutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
*/
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext())
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth.signOut();
                                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new HomeFragment()).commit();
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .setView(dialogView)
                            .setMessage("Edit Profile");

                    builder.create().show();
                }
            });

            FloatingActionButton addPostButton = view.findViewById(R.id.profile_add_post_button);

            addPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new AddPostFragment();
                    Bundle b = new Bundle();
                    b.putInt("prev_fragment", 0);
                    fragment.setArguments(b);
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, fragment).commit();
                }
            });

            TabLayout tabLayout = view.findViewById(R.id.tab_layout_profile);
            ViewPager2 viewPager = view.findViewById(R.id.view_pager_profile);

            TabItem listTabItem = view.findViewById(R.id.tab_list),
                    gridTabItem = view.findViewById(R.id.tab_grid);

            String [] tabLayoutTitles = {"List", "Grid"};

            PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager(), getLifecycle(), tabLayout.getTabCount());

            viewPager.setAdapter(pageAdapter);
            new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                    tab.setText(tabLayoutTitles[position]);
                }
            }).attach();

        } else {
            Button logInButton = view.findViewById(R.id.log_in_button);
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new LogInFragment()).commit();
                }
            });

            Button signUpButton = view.findViewById(R.id.sign_up_button);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new SignUpFragment()).commit();
                }
            });
        }
    }
}