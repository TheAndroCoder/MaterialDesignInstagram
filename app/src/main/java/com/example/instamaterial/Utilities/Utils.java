package com.example.instamaterial.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {
    private static int screenWidth=0;
    private static int screenHeight=0;
    public  static int getScreenHeight(Context c){
        if(screenHeight==0){
            WindowManager manager=(WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display=manager.getDefaultDisplay();
            Point size=new Point();
            display.getSize(size);
            screenHeight=size.y;
        }
        return screenHeight;
    }
    public  static int getScreenWidth(Context c){
        if(screenHeight==0){
            WindowManager manager=(WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display=manager.getDefaultDisplay();
            Point size=new Point();
            display.getSize(size);
            screenWidth=size.x;
        }
        return screenWidth;
    }
    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static String getRandomkey(){
        StringBuffer sb = new StringBuffer();
        byte array[]=new byte[256];
        new Random().nextBytes(array);
        String randomString = new String(array, Charset.forName("UTF-8"));
        String alphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");
        int n=10;
        for(int k=0;k<alphaNumericString.length();k++){
            if (Character.isLetter(alphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(alphaNumericString.charAt(k))
                    && (n > 0)) {

                sb.append(alphaNumericString.charAt(k));
                n--;
            }
        }
        return sb.toString();
    }
    public static String getDateString(){
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        Date d=new Date();
        return date.format(d)+" at "+time.format(d);
    }
    public static Bitmap getBitmapFromUrl(String s){
        //new Thread(new Runn)
        try {
            URL url=new URL(s);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("sachin","Bitmap returned is null");
        return null;
    }
    public static void blurBitmapWithRenderscript(
            RenderScript rs, Bitmap bitmap2) {
        // this will blur the bitmapOriginal with a radius of 25
        // and save it in bitmapOriginal
        // use this constructor for best performance, because it uses
        // USAGE_SHARED mode which reuses memory
        final Allocation input =
                Allocation.createFromBitmap(rs, bitmap2);
        final Allocation output = Allocation.createTyped(rs,
                input.getType());
        final ScriptIntrinsicBlur script =
                ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // must be >0 and <= 25
        script.setRadius(15f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap2);
    }
}
