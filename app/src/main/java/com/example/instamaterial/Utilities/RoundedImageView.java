package com.example.instamaterial.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.nio.file.Path;

@SuppressLint("AppCompatCustomView")
public class RoundedImageView extends ImageView {
    private float radius=18.0f;
    private Path path;
    private RectF rectF;
    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        BitmapDrawable drawable=(BitmapDrawable)getDrawable();
        if(drawable==null)
            return;
        if(getWidth()==0 || getHeight()==0)
            return;
        Bitmap fullSizeBitmap = drawable.getBitmap();
        int scaledwidth = getMeasuredWidth();
        int scaledheight = getMeasuredHeight();
        Bitmap mScaledBitmap;
        if(scaledwidth==fullSizeBitmap.getWidth() && scaledheight==fullSizeBitmap.getHeight())
            mScaledBitmap=fullSizeBitmap;
        else
            mScaledBitmap=Bitmap.createScaledBitmap(fullSizeBitmap,scaledwidth,scaledheight,true);

    }
}
