# Android-ImageClipper
简单、高效、灵活的Android图片裁剪框架。

# 使用参考
可以参考 app 模块中的 MainActivity 和 imageclipper 模块中的 ImageClipperActivity。


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

# 使用方式2：使用ImageClipView

## 初始化ImageClipView
```Java
//构建输入条件
ImageClipView.InputCondition condition = new ImageClipView.InputCondition.Builder()
    //裁剪框的类型，此处未矩形
    .setClipBorderType(ImageClipView.ClipBorderType.Rectangle)
    //裁剪框的颜色
    .setClipBorderColor(Color.WHITE)
    //裁剪框的边线宽度，单位为像素
    .setClipBorderWidth(20)
    //裁剪框边线的触摸宽度，实际触摸宽度为 边线宽度 + 此处设置的宽度，单位为像素
    .setClipBorderAppendWidth(20)
    //裁剪框的宽度（外边框），单位像素
    .setClipBorderLayoutMinWidth(200)
    //裁剪框的高度（外边框），单位像素
    .setClipBorderLayoutMinHeight(200)
    //是否显示裁剪框的宽高值
    .setShowWidthHeightValue(true)
    //设置原始的Bitmap，imgPath是将要裁剪图片的绝对路径
    .setRawBitmap(rawBitmap = BitmapFactory.decodeFile(imgPath))
    .build();

//imageClipView是使用ImageClipView的实例，ImageClipView是一个视图控件，直接继承自View
imageClipView.onCreate(condition, 0);
```

## 获取裁剪后的Bitmap
```Java
imageClipView.getClippedBitmap(true);
```

## 销毁输入条件和缓存
```Java
imageClipView.onDestroy();
```
