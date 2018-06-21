package com.example.akiyoshi.albumsole.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.List;
public class ResultAfterEditPicureActivity extends AppCompatActivity {
    private static final String TAG = "ResultAfterEditPicureActivity";
    private static final int DOWNLOAD_NOTIFICATION_ID_DONE = 911;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private static final String EXTRA_PATH = "ExtraPathParent";
    private static final String CHANNEL_ID = "3000";
    private String pathParent;

    public static void startWithUri(Context context, Uri uri, String path) {
        Intent intent = new Intent(context, ResultAfterEditPicureActivity.class);
        intent.setData(uri);
        intent.putExtra(EXTRA_PATH, path);
        context.startActivity(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_after_edit);
        Uri uri = getIntent().getData();
        pathParent = getIntent().getStringExtra(EXTRA_PATH);
        Log.d("PATH", "parent " + pathParent);
        if (uri != null) {
            try {
                UCropView uCropView = findViewById(R.id.ucrop);
                uCropView.getCropImageView().setImageUri(uri, null);
                uCropView.getOverlayView().setShowCropFrame(false);
                uCropView.getOverlayView().setShowCropGrid(false);
                uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            } catch (Exception e) {
                Log.e(TAG, "setImageUri", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(getIntent().getData().getPath()).getAbsolutePath(), options);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.format_crop_result_d_d, options.outWidth, options.outHeight));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_download) {
            if(isStoragePermissionGranted()){
                saveCroppedImage();
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveCroppedImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("PATH", "Permission read is granted");
                return true;
            } else {
                Log.v("PATH", "Permission read is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PATH", "Permission read is granted");
            return true;
        }
    }

    @SuppressLint("LongLogTag")
    private void saveCroppedImage() {
        Uri imageUri = getIntent().getData();
        if (imageUri != null && imageUri.getScheme().equals("file")) {
            try {
                copyFileToDownloads(getIntent().getData());
            } catch (Exception e) {
                Toast.makeText(ResultAfterEditPicureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, imageUri.toString(), e);
            }
        } else {
            Toast.makeText(ResultAfterEditPicureActivity.this, getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath = pathParent;
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());

        File saveFile = new File(downloadsDirectoryPath, filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        showNotification(saveFile);
        //UpdateMediaStore(saveFile, this);

        //Log.d("PATH", "path " + path + " uir " + uri);

        Log.d("PATH", "size before add " + PictureLab.getInstance(this).getPictureMonth().getListAllPicture().size());
        // add vào tree map
        try {
            PictureLab.getInstance(this).LoadAgain();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("PATH", "size after add " + PictureLab.getInstance(this).getPictureMonth().getListAllPicture().size());
        // notifi adapter change
        Log.d("PATH", "file new " + saveFile.getPath());

        Picture tem = FindPictureWithPath(saveFile.getPath());

        if(tem != null){
            PictureDetailViewActivity.addPictureInAdapter.addPicture(tem);
        }

        Toast.makeText(this, R.string.notification_image_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void UpdateMediaStore(File saveFile, Context context){
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

            public void onScanCompleted(String path, Uri uri) {
                Log.d("PATH", "path " + path + " uir " + uri);

                Log.d("PATH", "size before add " + PictureLab.getInstance(context).getPictureMonth().getListAllPicture().size());
                // add vào tree map
                try {
                    PictureLab.getInstance(context).LoadAgain();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("PATH", "size after add " + PictureLab.getInstance(context).getPictureMonth().getListAllPicture().size());
                // notifi adapter change
                Log.d("PATH", "file new " + saveFile.getPath());

                Picture tem = FindPictureWithPath(saveFile.getPath());

                if(tem != null){
                    PictureDetailViewActivity.addPictureInAdapter.addPicture(tem);
                }
            }
        });
    }

    public Picture FindPictureWithPath(String path){
        Picture picture = null;

        List<Picture> list = PictureLab.getInstance(this).getPictureAlbum().getListAllPicture();
        int n = list.size();

        for(int i = 0; i < n; i++){
            if(list.get(i).getPath().equals(path)){
                picture = list.get(i);
                break;
            }
        }

        return picture;
    }

    private void showNotification(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri fileUri = FileProvider.getUriForFile(
                this,
                getString(R.string.file_provider_authorities),
                file);

        intent.setDataAndType(fileUri, "image/*");

        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resInfoList) {
            grantUriPermission(
                    info.activityInfo.packageName,
                    fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(createChannel());
            }
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_image_saved_click_to_preview))
                .setTicker(getString(R.string.notification_image_saved))
                .setSmallIcon(R.drawable.check)
                .setOngoing(false)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true);
        if (notificationManager != null) {
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID_DONE, notificationBuilder.build());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationChannel createChannel() {
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), importance);
        channel.setDescription(getString(R.string.channel_description));
        channel.enableLights(true);
        channel.setLightColor(Color.YELLOW);
        return channel;
    }

}
