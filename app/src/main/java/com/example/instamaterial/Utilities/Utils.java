package com.example.instamaterial.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

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
}
