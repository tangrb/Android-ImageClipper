# Android-ImageClipper
简单、高效、灵活的Android图片裁剪框架。

# 使用方式1：使用ImageClipperActivity

## 调用ImageClipperActivity
```Java
Intent intent = new Intent(getApplication(), ImageClipperActivity.class);
//imgUri是你从其它应用获取到的图片的Uri，需要将此Uri示例传给ImageClipperActivity
intent.putExtra(ImageClipperActivity.IMAGE_URI, imgUri);
startActivityForResult(intent, 222);
```
## 处理ImageClipperActivity返回的结果
```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == 222 && resultCode == RESULT_OK && data != null) {
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
```

# 使用方式2：使用ImageClipperView
