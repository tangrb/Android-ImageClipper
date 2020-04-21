package com.trb.android.imageclipperapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trb.android.imageclipper.ImageClipperActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView_MainActivity);

        if (!checkSDCardReadWritePermission(this)) {
            requestSDCardReadWritePermission(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            Uri imgUri = data.getData();
            Log.d(TAG, "onActivityResult: imgUri=" + imgUri);

            if (imgUri != null) {
                Intent intent = new Intent(getApplication(), ImageClipperActivity.class);
                intent.putExtra(ImageClipperActivity.IMAGE_URI, imgUri);
                startActivityForResult(intent, 222);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        //
        else if (requestCode == 222 && resultCode == RESULT_OK && data != null) {
            final String path = data.getStringExtra(ImageClipperActivity.CLIPPED_IMG_PATH_RESULT);
            Log.d(TAG, "onActivityResult: clipped image path >> " + path);

            final Uri uri = data.getParcelableExtra(ImageClipperActivity.CLIPPED_IMG_URI_RESULT);
            Log.d(TAG, "onActivityResult: clipped image uri >> " + uri);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showImageFromImageClipper(path, uri);
                }
            });
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void selectPicture_Button_OnClick_MainActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 111);
    }

    public boolean checkSDCardReadWritePermission(Activity activity) {
        Log.d(TAG, "checkSDCardReadWritePermission: ");
        return activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //请求SD卡读写权限
    public void requestSDCardReadWritePermission(Activity activity) {
        Log.d(TAG, "requestSDCardReadWritePermission: ");
        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /**
     * @param path 裁剪后的图片的绝对路径
     * @param uri  裁剪后的图片的Uri
     */
    private void showImageFromImageClipper(String path, Uri uri) {
        if (!TextUtils.isEmpty(path)) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            return;
        }

        if (uri != null) {
            imageView.setImageURI(uri);
            return;
        }

        Toast.makeText(getApplication(), "裁剪失败", Toast.LENGTH_SHORT).show();
    }
}
