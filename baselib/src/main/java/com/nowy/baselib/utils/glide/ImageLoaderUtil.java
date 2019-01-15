package com.nowy.baselib.utils.glide;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoaderUtil {
    public static int IMG_PRE_DEF = 0xFFFFFF;
    public static int IMG_ERROR_DEF = 0xFFFFFF;


    public static RequestOptions getOriginalOption() {
        return new RequestOptions();
    }

    public static RequestOptions getDefOption() {
        return new RequestOptions()
                .placeholder(IMG_PRE_DEF)
                .error(IMG_ERROR_DEF)
                .priority(Priority.NORMAL).diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    public static void display(ImageView iv, String url) {
        Glide.with(iv.getContext())
                .load(url)
                .apply(getDefOption())
                .into(iv);
    }

}