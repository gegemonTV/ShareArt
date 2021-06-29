package com.minerva.likebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

public class LikeButtonWithCounter extends FrameLayout {

    private TextView counterText;
    private LikeButton likeButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonWithCounter(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonWithCounter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonWithCounter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.like_text_button, this, true);

//        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LikeButtonWithCounter, defStyle, 0);

        counterText = findViewById(R.id.likes_counter_text);
        likeButton = findViewById(R.id.like_button);

//        counterText.setText(array.getString(R.styleable.LikeButtonWithCounter_text));
    }

    public void setText(String text){
        counterText.setText(text);
    }

    public void setText(@StringRes int text){
        counterText.setText(text);
    }

    public void addOnLikeListener(OnLikeListener onLikeListener){
//        this.setOnClickListener(onClickListener);
        likeButton.setOnLikeListener(onLikeListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setLikeDrawable(Drawable likeDrawable){
        likeButton.setLikeDrawable(likeDrawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setUnlikeDrawableRes(@DrawableRes int resId) {
        likeButton.setUnlikeDrawableRes(resId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setUnlikeDrawable(Drawable unLikeDrawable) {
        likeButton.setUnlikeDrawable(unLikeDrawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setIcon(IconType currentIconType) {
        likeButton.setIcon(currentIconType);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setIcon() {
        likeButton.setIcon();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setIconSizeDp(int iconSize) {
        likeButton.setIconSizeDp(iconSize);
    }

    public void setLiked(boolean liked){
        likeButton.setLiked(liked);
    }
}
