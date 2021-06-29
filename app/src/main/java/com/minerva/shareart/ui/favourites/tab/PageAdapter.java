package com.minerva.shareart.ui.favourites.tab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.minerva.shareart.ui.favourites.tab.grid.FavouritesGridFragment;
import com.minerva.shareart.ui.favourites.tab.list.FavouritesListFragment;

import org.jetbrains.annotations.NotNull;

public class PageAdapter extends FragmentStateAdapter {

    private final int numOfTabs;

    public PageAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle, int numOfTabs) {
        super(fragmentManager, lifecycle);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FavouritesListFragment();
            case 1:
                return new FavouritesGridFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
