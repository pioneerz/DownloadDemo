package com.timcoder.downloaddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DownloadManager";

    // 大文件 url="https://b6.market.xiaomi.com/download/AppChannel/054785162b6d8ff4aac8c902c2b57bdb9b741addb/com.hegu.dnl.mi.apk"
    // https://b6.market.xiaomi.com/download/AppStore/0bf455300eabf0f939bdbaa3b158e0f3c5a41d2dc/com.tencent.weishi.apk
    private static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final String mUrl = "https://b6.market.xiaomi.com/download/AppStore/0bf455300eabf0f939bdbaa3b158e0f3c5a41d2dc/com.tencent.weishi.apk";

    private long mDownloadId;
    private DownloadManager mDownloadManager;
    private DownloadReceiver mDownloadReceiver;

    private String mSavePath;
    private ProgressBar mProgressBar;

    private ImageView mImageView;
    private byte[] favicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        initConfig();
        initView();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progress_bar);
//        mProgressBar.setProgress(60);

        mImageView = findViewById(R.id.image_view);
    }

    private void initConfig() {
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(mDownloadReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                favicon = baos.toByteArray();
                Log.e(TAG, "load success");
            }
        }).start();

    }

    private void checkPermissions() {
        boolean isRequest = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                STORAGE_PERMISSION) == PackageManager.PERMISSION_DENIED;
        if (isRequest) {
            ActivityCompat.requestPermissions(this, new String[]{STORAGE_PERMISSION}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "permission already request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission request success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "permission request deny", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void startDownload(View view) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUrl));
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/download/", "games_download.apk");
        if (mDownloadManager != null) {
            mDownloadId = mDownloadManager.enqueue(request);
        }

//        DownloadManager.Request requestTest = new DownloadManager.Request(Uri.parse(mUrl));
//        requestTest.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        requestTest.setDestinationInExternalPublicDir("/download/", "download.apk");
//        if (mDownloadManager != null) {
//            mDownloadManager.enqueue(requestTest);
//        }

    }

    public void removeDownload(View view) {
        if (mDownloadManager != null) {
            mDownloadManager.remove(mDownloadId);
        }
    }

    public void restartDownload(View view) {
        if (mDownloadManager != null) {
        }
    }

    public void updateProgress(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DownloadManager.Query query = new DownloadManager.Query();
                    final Cursor cursor = mDownloadManager.query(query.setFilterById(mDownloadId));
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                        Log.e(TAG, "bytes_downloaded=" + bytes_downloaded + "  bytes_total=" + bytes_total);
                        int progress = (int) (bytes_downloaded * 1.0f / bytes_total * 100);
                        Log.e(TAG, "progress=" + progress + "   path=" + path);
                        mProgressBar.setProgress(progress);
                        if (progress == 100) {
                            break;
                        }
                    }
                }
            }
        }).start();
    }

    public void loadPhoto(View view) {
        try {
            long start = System.currentTimeMillis();
            Bitmap bitmap = BitmapFactory.decodeByteArray(favicon, 0, favicon.length);
            mImageView.setImageBitmap(bitmap);
            Log.e(TAG, "time1=" + (System.currentTimeMillis() - start));
        } catch (Exception e) {

        }
    }

    public void loadGlidePhoto(View view) {
        long start = System.currentTimeMillis();
        Glide.with(getApplicationContext())
                .load(favicon)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);
        Log.e(TAG, "time2=" + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownloadReceiver);
    }

    public void openRxjava(View view) {
        try {
            Intent intent = new Intent(this, RxjavaActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    Log.e(TAG, "Download complete or stop the download");
                    openDownloadFile();
                    break;
                case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                    Log.e(TAG, "Notification Click");
                    break;
                default:
                    break;
            }
        }
    }

    private void openDownloadFile() {
        DownloadManager.Query query = new DownloadManager.Query();
        final Cursor cursor = mDownloadManager.query(query.setFilterById(mDownloadId));
        if (cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Can not find the application for opening this file");
            }
        }
    }

}
