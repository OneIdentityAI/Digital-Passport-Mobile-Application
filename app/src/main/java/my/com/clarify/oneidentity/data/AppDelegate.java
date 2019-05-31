package my.com.clarify.oneidentity.data;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;

public class AppDelegate extends MultiDexApplication {
    public static int BARCODE_REQUEST_CODE = 0;
    public static String SharedPreferencesTag = "one_identity";

    public static String appPath = "/OneIdentity";
    public static String tempFolder = "/tempFolder";
    public static String apikey = "9c24abc8797a4554a54f3c6c26c705d9";
    public static String credDefIdOneIdentity = "Th7MpTaRZVRYnPiabds81Y:3:CL:6:TAG1";

    public AppDelegate() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void createDirectoryIfNotExist(String directoryPath) {
        try {
            File dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void alert(Activity activity, String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String fileNameGenerator(Context context)
    {
        Calendar now = Calendar.getInstance();
        Date date = now.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String formattedDate = format.format(date);

        return getDeviceId(context) + "-" + formattedDate;
    }

    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getResizedImagePathFromOriginalPath(Context context, String imagePath, int desiredWidth, int desiredHeight)
    {
        String resizedFilePath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + fileNameGenerator(context) + ".jpg";
        Bitmap scaledBitmap = loadResizedBitmap(imagePath, desiredWidth, desiredHeight, true);
        File file = new File(resizedFilePath);
        FileOutputStream fOut;
        try
        {
            fOut = new FileOutputStream(file);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e)
        { // TODO
            e.printStackTrace();
            //scaledBitmap.recycle();
        }
        if(scaledBitmap != null)
            scaledBitmap.recycle();

        return resizedFilePath;
    }

    public static Bitmap loadResizedBitmap(String filename, int width, int height, boolean exact ) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        if (options.outHeight > 0 && options.outWidth > 0 ) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;

            int oriWidth = options.outWidth;
            int oriHeight = options.outHeight;
            while (options.outWidth  / options.inSampleSize > width &&
                    options.outHeight / options.inSampleSize > height ) {
                options.inSampleSize++;
            }
            options.inSampleSize--;
            bitmap = BitmapFactory.decodeFile(filename, options);
            if ( bitmap != null && exact )
            {
                int bWidth = bitmap.getWidth();
                int bHeight = bitmap.getHeight();
                int nWidth = width;
                int nHeight = (int) Math.round(width/( (1.0*bWidth) / bHeight ));

                //int nHeight = Math.round(height * parentRatio);
                bitmap = Bitmap.createScaledBitmap( bitmap, nWidth, nHeight, false );

                try {
                    ExifInterface exif = new ExifInterface(filename);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    }
                    else if (orientation == 3) {
                        matrix.postRotate(180);
                    }
                    else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    Bitmap myBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                    return myBitmap;
                }
                catch (Exception e) {

                }
            }
        }
        return bitmap;
    }

    public static String formatString(String text)
    {
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}