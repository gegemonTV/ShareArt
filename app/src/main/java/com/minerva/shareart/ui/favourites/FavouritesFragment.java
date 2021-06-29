package com.minerva.shareart.ui.favourites;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minerva.shareart.R;
import com.minerva.shareart.ui.auth.AuthViewModel;
import com.minerva.shareart.ui.auth.LogInFragment;
import com.minerva.shareart.ui.auth.SignUpFragment;
import com.minerva.shareart.ui.favourites.tab.PageAdapter;

import org.jetbrains.annotations.NotNull;

public class FavouritesFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    AuthViewModel authViewModel;

    boolean isAuthorized = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        isAuthorized = authViewModel.getIsAuthorized().getValue();
    }

    public static FavouritesFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FavouritesFragment fragment = new FavouritesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        Log.d("SA", ""+isAuthorized);
        if (isAuthorized){
            Log.d("SA", "Inflating logged in");
            root = inflater.inflate(R.layout.fragment_favourites_logged_in, container, false);
        }else{
            root = inflater.inflate(R.layout.fragment_favourites_required_login, container, false);
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAuthorized){
            TabLayout tabLayout = view.findViewById(R.id.tab_layout_favourites);
            ViewPager2 viewPager = view.findViewById(R.id.view_pager_favourites);

            TabItem listTabItem = view.findViewById(R.id.tab_list_favourites),
                    gridTabItem = view.findViewById(R.id.tab_grid_favourites);

            String [] tabLayoutTitles = {"List", "Grid"};

            PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager(), getLifecycle(), tabLayout.getTabCount());

            viewPager.setAdapter(pageAdapter);

            new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                    tab.setText(tabLayoutTitles[position]);
                }
            }).attach();
        }else {
            Button logInButton = view.findViewById(R.id.log_in_button_favourites);
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new LogInFragment()).commit();
                }
            });

            Button signUpButton = view.findViewById(R.id.sign_up_button_favourites);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_home, new SignUpFragment()).commit();
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}