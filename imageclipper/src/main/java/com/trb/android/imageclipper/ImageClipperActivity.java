package com.trb.android.imageclipper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageClipperActivity extends AppCompatActivity {
    private static final String TAG = ImageClipperActivity.class.getSimpleName();

    public static final String TITLE_TEXT = "TITLE_TEXT";
    public static final String SURE_TEXT = "SURE_TEXT";
    public static final String CANCEL_TEXT = "CANCEL_TEXT";
    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String CLIPPED_IMG_URI_RESULT = "CLIPPED_IMG_URI_RESULT";
    public static final String CLIPPED_IMG_PATH_RESULT = "CLIPPED_IMG_PATH_RESULT";

    private ImageClipView imageClipView;
    private Bitmap rawBitmap;

    @Override
    public void onBackPressed() {
        //disable
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_clipper);

        TextView titleView = findViewById(R.id.title_TextView_ImageClipperActivity);
        imageClipView = findViewById(R.id.imageClipView_ImageClipperActivity);
        Button cancelBtn = findViewById(R.id.cancel_Button_ImageClipperActivity);
        Button sureBtn = findViewById(R.id.sure_Button_ImageClipperActivity);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplication(), "intent is null", Toast.LENGTH_SHORT).show();
            return;
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeCancel();
            }
        });

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSure();
            }
        });

        String titleText = intent.getStringExtra(TITLE_TEXT);
        if (!TextUtils.isEmpty(titleText)) titleView.setText(titleText);

        String cancelText = intent.getStringExtra(CANCEL_TEXT);
        if (!TextUtils.isEmpty(cancelText)) titleView.setText(cancelText);

        String sureText = intent.getStringExtra(SURE_TEXT);
        if (!TextUtils.isEmpty(sureText)) titleView.setText(sureText);

        final Uri imageUri = intent.getParcelableExtra(IMAGE_URI);
        final String imgPath = LocalUri2PathUtils.getRealPathFromUri(getApplication(), imageUri);
        Log.d(TAG, "onCreate: imgPath=" + imgPath);

        //use demo
        ImageClipView.InputCondition condition = new ImageClipView.InputCondition.Builder()
                .setClipBorderType(ImageClipView.ClipBorderType.Rectangle)
                .setClipBorderColor(Color.WHITE)
                .setClipBorderWidth(20)
                .setClipBorderAppendWidth(20)
                .setClipBorderLayoutMinWidth(200)
                .setClipBorderLayoutMinHeight(200)
                .setShowWidthHeightValue(true)
                .setRawBitmap(rawBitmap = BitmapFactory.decodeFile(imgPath))
                .build();
        imageClipView.onCreate(condition, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveClippedImageThread != null) saveClippedImageThread.interrupt();
        imageClipView.onDestroy();
        freeBitmap(rawBitmap);
    }

    private void freeBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void executeCancel() {
        finish();
    }

    private SaveClippedImageThread saveClippedImageThread = null;

    private void executeSure() {
        if (saveClippedImageThread != null) {
            Toast.makeText(getApplication(), "clipping, please waiting", Toast.LENGTH_SHORT).show();
            return;
        }

        saveClippedImageThread = new SaveClippedImageThread();
        saveClippedImageThread.context = this;
        saveClippedImageThread.start();
    }

    private static class SaveClippedImageThread extends Thread {
        private final String TAG = getClass().getSimpleName();

        private Bitmap bitmap;
        private ImageClipperActivity context;

        private void free() {
            if (bitmap != null) bitmap.recycle();
            context = null;
        }

        @Override
        public void run() {
            File cacheDir = context.getExternalCacheDir();

            if (cacheDir == null) {
                Log.e(TAG, "run: ", new Exception("Call Context.getExternalCacheDir() failed"));
                free();
                return;
            }

            File dir = new File(cacheDir.getAbsolutePath() + "/img_clip_cache");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String imgPath = dir.getAbsolutePath() + "/clipped_img_" + System.currentTimeMillis() + ".png";

            long time = System.currentTimeMillis();
            bitmap = context.imageClipView.getClippedBitmap(true);
            Log.d(TAG, "run: clip time: " + (System.currentTimeMillis() - time));

            try (OutputStream outs = new FileOutputStream(new File(imgPath))) {
                time = System.currentTimeMillis();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outs);
                outs.flush();
                Log.d(TAG, "run: save success: time=" + (System.currentTimeMillis() - time));

                Intent intent = new Intent();
                intent.putExtra(CLIPPED_IMG_PATH_RESULT, imgPath);
                intent.putExtra(CLIPPED_IMG_URI_RESULT, Uri.parse(imgPath));
                context.setResult(RESULT_OK, intent);
                context.finish();
            } catch (Exception e) {
                Log.e(TAG, "run: save failed", e);
            } finally {
                free();
            }
        }
    }
}
