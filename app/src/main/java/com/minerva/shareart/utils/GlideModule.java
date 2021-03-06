package com.minerva.shareart.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull @NotNull Context context, @NonNull @NotNull Glide glide, @NonNull @NotNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }
}
