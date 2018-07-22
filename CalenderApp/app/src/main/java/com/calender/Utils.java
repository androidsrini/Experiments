package com.calender;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.calender.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hoffensoft on 16-07-2018.
 */

public class Utils {

    public static Utils utils;
    private ProgressDialog progressDialog;

    private Utils() {
    }

    public static Utils GetInstance() {
        if (utils == null)
            utils = new Utils();
        return utils;
    }

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final Canvas sCanvas = new Canvas();

    public static int dp2Px(int dp) {
        return Math.round(dp * DENSITY);
    }

    public static Bitmap createBitmapFromView(View view) {
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            synchronized (sCanvas) {
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }

    public String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT);
        return sdf.format(new Date());
    }

    public String convertDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT);
        return sdf.format(calendar.getTime());
    }

    public void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public String getUserID() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return null;
    }

    public String getPhoneNumber() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getPhoneNumber();
        }
        return null;
    }

    public String getUserName() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getDisplayName();
        }
        return null;
    }

    public String getEmail() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getEmail();
        }
        return null;
    }

    public User getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            User user = new User();
            user.setId(getUserID());
            user.setEmail(getEmail());
            user.setName(getUserName());
            user.setPhoneNumber(getPhoneNumber());
            return user;
        }
        return null;
    }

    public void showProgressDialog(Context context) {
        progressDialog = ProgressDialog.show(context, null, "Loading...", false, false);
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
