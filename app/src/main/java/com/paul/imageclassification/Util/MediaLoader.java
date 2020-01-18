package com.paul.imageclassification.Util;


import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.paul.imageclassification.R;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import java.net.URI;

public class MediaLoader implements AlbumLoader {
    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(ImageUtil.getBitmapByPath(imageView.getContext(), url))
                .error(R.drawable.icon_error_loading)
                .placeholder(R.drawable.icon_loading_image)
                .into(imageView);
    }
}
