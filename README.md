# Android-ImageClipper
简单、高效、灵活的Android图片裁剪框架。

# 使用方式1：使用ImageClipperActivity

`Java
Intent intent = new Intent(getApplication(), ImageClipperActivity.class);
//imgUri是你从其它应用获取到的图片的Uri
intent.putExtra(ImageClipperActivity.IMAGE_URI, imgUri);
startActivityForResult(intent, 222);
`

# 使用方式2：使用ImageClipperView
