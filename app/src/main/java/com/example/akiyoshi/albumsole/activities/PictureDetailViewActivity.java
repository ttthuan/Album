package com.example.akiyoshi.albumsole.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.github.chrisbanes.photoview.PhotoView;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PictureDetailViewActivity extends AppCompatActivity implements ThumbCallBack {

    private ViewPager viewPager;
    private List<Picture> listPicture;
    private static String EXTRA_PICTURE_ID = "com.example.acer.album.pictureid";
    private View myToolbar;
    private View bottomNavigationView;
    private View header, footer;
    private boolean isShowToolbar = true;
    private int widthScreen;
    private int heigthScreen;
    private ImageButton btnback;
    private ImageButton btnFilter;
    private ImageButton btnEdit;
    private ImageButton btnDelete;

    private int currentApiVersion;

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private String TAG = "PICTURE_DETAIL_VIEW_ACTIVITY";

    private List<Integer> listIdImageRemoved;
    private final static String EXTRA_RESULT_LIST_ID_IMAGE = "list_id_image";

    private boolean isFilter = false;
    private ConstraintSet layoutOrigin, layoutHasFilter;
    private ConstraintLayout constraintLayout;

    // dialog delete
    private View dialogDeleteBackground;
    private View dialogDelete;
    private Button btnOke;
    private Button btnCancel;

    private int processNeedRun = 0;

    private boolean isDialogOpen = false;

    public static AddPictureInAdapter addPictureInAdapter = null;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private Handler handlerSetBitmap;
    private Handler handlerPopFragment;

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, PictureDetailViewActivity.class);
        intent.putExtra(EXTRA_PICTURE_ID, id);
        return intent;
    }

    public static Intent newIntent(Context context, int id, AddPictureInAdapter add) {
        addPictureInAdapter = add;
        Intent intent = new Intent(context, PictureDetailViewActivity.class);
        intent.putExtra(EXTRA_PICTURE_ID, id);
        return intent;
    }

    public void getWithOfScreen(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heigthScreen = displayMetrics.heightPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail_view);

        constraintLayout = findViewById(R.id.layout_picture_detail_view);
        layoutOrigin = new ConstraintSet();
        layoutHasFilter = new ConstraintSet();
        layoutHasFilter.clone(this, R.layout.activity_picture_detail_view_clone);
        layoutOrigin.clone(constraintLayout);

        getWithOfScreen(this);

        processNeedRun = 0;

        // transpare notificale bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        myToolbar = findViewById(R.id.my_tool_bar);

        bottomNavigationView = findViewById(R.id.bottom_bar);

        header = findViewById(R.id.detailHeader);
        footer = findViewById(R.id.detailFooter);

        viewPager = findViewById(R.id.picture_view_pager);

        listPicture = PictureLab.getInstance(this).getPictureMonth().getListAllPicture();

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Picture picture = listPicture.get(position);
                return PictureDetailFragment.newInstance(listPicture.get(position).getId() + "", listPicture.get(position).getMonth());
            }

            @Override
            public int getCount() {
                return listPicture.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }

//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView((PictureDetailFragment)object);
//            }
        });

        int id = getIntent().getIntExtra(EXTRA_PICTURE_ID, -1);

        int size = listPicture.size();
        for (int i = 0; i < size; i++) {
            if (listPicture.get(i).getId().equals(id)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }

        btnEdit = findViewById(R.id.bottom_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNeedRun = 1; // crop
                if (isStoragePermissionGranted()) {
                    processNeedRun = 0;
                    startCrop();
                }
            }
        });

        btnDelete = findViewById(R.id.bottom_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNeedRun = 2; // delete
                if (isStoragePermissionGranted()) {
                    processNeedRun = 0;
                    if(!isDialogOpen){
                        showDialogDelete();
                    }
                }
            }
        });

        btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNeedRun = 3; // filter
                if (isStoragePermissionGranted()) {
                    processNeedRun = 0;
                    showLayoutFilter();
                }
            }
        });

        // btn back
        btnback = findViewById(R.id.button_back);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilter == true){
                    showLayoutFilter();
                }else{
                    onBackPressed();
                }
            }
        });

        // dialog delete
        dialogDeleteBackground = findViewById(R.id.dialogDeletePhotoBg);
        dialogDelete = findViewById(R.id.dialogDeletePhoto);
        btnOke = findViewById(R.id.btn_delete_oke);
        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDiaglogDelete();
                int idx = viewPager.getCurrentItem();
                Log.d("DELETE", idx + "");
                Picture pictureCurrent = listPicture.get(idx);

                removeItemInListPicture(idx);

                deleteImageInStore(pictureCurrent);
            }
        });

        btnCancel = findViewById(R.id.btn_delete_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDiaglogDelete();
            }
        });

        dialogDeleteBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDiaglogDelete();
            }
        });

        listIdImageRemoved = new ArrayList<>();

        handlerSetBitmap = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap = (Bitmap) msg.obj;

                int idx = viewPager.getCurrentItem();
                Picture pictureCurrent = listPicture.get(idx);

                Log.d("FILTER", "path " + pictureCurrent.getPath());
                PhotoView photoView = viewPager.findViewById(pictureCurrent.getId());

                if (photoView != null && bitmap != null) {
                    photoView.setImageBitmap(bitmap);
                }
            }
        };

        handlerPopFragment = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String fragmentTag = (String) msg.obj;
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate(fragmentTag, 0);
            }
        };
    }

    public void showLayoutFilter(){
        if (!isFilter) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layoutHasFilter.applyTo(constraintLayout);

            Thread threadRunFragment = new Thread(new Runnable() {
                @Override
                public void run() {
                    int idx = viewPager.getCurrentItem();
                    Log.d("FILTER", "Index: " + idx);
                    Picture pictureCurrent = listPicture.get(idx);

                    ShowFragmentFilter(pictureCurrent.getId().toString(), pictureCurrent.getMonth());
                }
            });
            threadRunFragment.start();
        } else {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layoutOrigin.applyTo(constraintLayout);
        }
        isFilter = !isFilter;
    }

    public void showDialogDelete(){
        isDialogOpen = true;
        dialogDeleteBackground.setVisibility(View.VISIBLE);
        dialogDeleteBackground.setClickable(true);
        dialogDelete.animate().scaleX(1).scaleY(1).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void hideDiaglogDelete(){
        isDialogOpen = false;
        dialogDelete.animate().scaleX(0f).scaleY(0f).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialogDeleteBackground.setVisibility(View.INVISIBLE);
                dialogDeleteBackground.setClickable(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void saveListItemDelete(List<Integer> ids) {
        Set<String> listStr = new ArraySet<>();

        int n = ids.size();
        for (int i = 0; i < n; i++) {
            listStr.add(ids.get(i) + "");
        }

        Log.d("DELETE", "BEFORE SAVE " + listStr.size());

        SharedPreferences myPrefs = getSharedPreferences("listItemDelete",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putStringSet("id", listStr);
        editor.commit();

        Log.d("DELETE", "save done");
    }

    public void removeItemInListPicture(int idx) {
        Log.d("DELETE", listPicture.size() + "");
        listPicture.remove(idx);
        Log.d("DELETE", listPicture.size() + "");
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void deleteImageInStore(Picture pictureCurrent) {

        File fdelete = new File(pictureCurrent.getPath());

        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[] { fdelete.getAbsolutePath() };

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        //long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
        Uri deleteUri = MediaStore.Files.getContentUri("external");
        int number = contentResolver.delete(deleteUri, selection, selectionArgs);

        if(number > 0){
            listIdImageRemoved.add(pictureCurrent.getId());
            Log.e("DELETE", "Image Deleted :" + pictureCurrent.getId());
            PictureLab.getInstance(this).getPictureAlbum().deleteItem(pictureCurrent);
            PictureLab.getInstance(this).getPictureMonth().deleteItem(pictureCurrent);
            UpdateMediaStore();
        }

    }

    public void UpdateMediaStore(){
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

            public void onScanCompleted(String path, Uri uri) {
                Log.e("ExternalStorage", "Scanned " + path + ":");
                Log.e("ExternalStorage", "-> uri=" + uri);
            }
        });
    }

    public void setDataForResult() {
        Intent intentResult = new Intent();
        intentResult.putExtra(EXTRA_RESULT_LIST_ID_IMAGE, "data");
        setResult(Activity.RESULT_OK, intentResult);

        saveListItemDelete(listIdImageRemoved);

    }


    @Override
    protected void onPause() {
        super.onPause();

        setDataForResult();
    }

//    public static int[] getResultData(Intent data) {
//        if (data != null) {
//            int[] intArray = data.getIntArrayExtra(EXTRA_RESULT_LIST_ID_IMAGE);
//            if (intArray != null)
//                return intArray;
//        }
//        return null;
//    }

    public void callBroadCast() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            /*
             *   (non-Javadoc)
             * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
             */
            public void onScanCompleted(String path, Uri uri) {
                Log.e("ExternalStorage", "Scanned " + path + ":");
                Log.e("ExternalStorage", "-> uri=" + uri);
            }
        });
    }


    public void startCrop() {
        int idx = viewPager.getCurrentItem();
        Picture pictureCurrent = listPicture.get(idx);

        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(Uri.fromFile(new File(pictureCurrent.getPath())), Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = advancedConfig(uCrop);

        uCrop.start(PictureDetailViewActivity.this);
    }


    private UCrop advancedConfig(UCrop uCrop) {
        uCrop = uCrop.useSourceImageAspectRatio();
        UCrop.Options options = new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setCompressionQuality(90);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        return uCrop.withOptions(options);
    }

    public void ShowFragmentFilter(String id, String month) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragmentFilter = fragmentManager.findFragmentByTag("FILTER");
        if (fragmentFilter == null) {
            fragmentFilter = FilterFragment.newInstance(id, month);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.container_filter, fragmentFilter, "FILTER");
            transaction.commit();
        } else {
            Message message = handlerPopFragment.obtainMessage(1, "FILTER");
            handlerPopFragment.sendMessage(message);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(GetTag(), "Permission write is granted");
                return true;
            } else {
                Log.v(GetTag(), "Permission write is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(GetTag(), "Permission write is granted");
            return true;
        }
    }

    public String GetTag() {
        return "PicturePaperActivity";
    }


    public void ToolBarControl() {
        isShowToolbar = !isShowToolbar;
        if (isShowToolbar == false) {
            hideToolbar();
        } else {
            showToolbar();
        }
    }

    public void hideToolbar() {
        //myToolbar.animate().translationY(-myToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        //bottomNavigationView.animate().translationY(bottomNavigationView.getHeight() + 110).setInterpolator(new AccelerateInterpolator()).start();

        myToolbar.animate().alpha(0f).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                myToolbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        bottomNavigationView.animate().alpha(0f).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bottomNavigationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        header.animate().alpha(0f).setInterpolator(new AccelerateInterpolator()).start();
        footer.animate().alpha(0f).setInterpolator(new AccelerateInterpolator()).start();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

    }

    public void showToolbar() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

//        myToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
//        bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        myToolbar.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        myToolbar.animate().alpha(1f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).setInterpolator(new DecelerateInterpolator()).start();
        bottomNavigationView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).alpha(1f).setInterpolator(new DecelerateInterpolator()).start();
        header.animate().alpha(1f).setInterpolator(new DecelerateInterpolator()).start();
        footer.animate().alpha(1f).setInterpolator(new DecelerateInterpolator()).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int n = permissions.length;
        for (int i = 0; i < n; i++) {
            if (permissions[i].endsWith(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(GetTag(), "Permission: " + permissions[i] + "was " + grantResults[i]);
                    if(processNeedRun == 1){ // crop
                        processNeedRun = 0;
                        startCrop();
                    }else if(processNeedRun == 2){ // delete
                        processNeedRun = 0;
                        showDialogDelete();
                    }else if(processNeedRun == 3){ // filter
                        processNeedRun = 0;
                        showLayoutFilter();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            handleCropResult(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void handleCropResult(Uri resultUri) {
        if (resultUri != null) {
            int idx = viewPager.getCurrentItem();
            Picture pictureCurrent = listPicture.get(idx);

            int indexSlash = pictureCurrent.getPath().lastIndexOf("/");
            String pathParent = pictureCurrent.getPath().substring(0, indexSlash);
            Log.d("PATH", pathParent);
            ResultAfterEditPicureActivity.startWithUri(PictureDetailViewActivity.this, resultUri, pathParent);
        } else {
            Toast.makeText(PictureDetailViewActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(PictureDetailViewActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PictureDetailViewActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onThumbClick(Filter filter) {
        Thread threadChangeImage = new Thread(new Runnable() {
            @Override
            public void run() {
                int idx = viewPager.getCurrentItem();
                Picture pictureCurrent = listPicture.get(idx);

                Bitmap bitmapOrigin = null;

                int arr[] = PictureDetailFragment.CalculatorSizeOfImageInDetailView(pictureCurrent.getWidth(), pictureCurrent.getHeight(), widthScreen, heigthScreen);
                int width, height;
                width = arr[0];
                height = arr[1];

                try {
                    bitmapOrigin = Glide.with(getApplicationContext())
                            .asBitmap()
                            .apply(new RequestOptions().override(width, height).centerCrop().placeholder(R.color.colorBlack))
                            .load(pictureCurrent.getPath())
                            .submit().get();

                    if (bitmapOrigin != null) {
                        Log.d("FILTER", "NOT NULL");
                    } else {
                        Log.d("FILTER", " NULL");
                    }

                    Bitmap bitmap = filter.processFilter(bitmapOrigin);

                    Message message = handlerSetBitmap.obtainMessage(1, bitmap);

                    handlerSetBitmap.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        threadChangeImage.start();
    }
}
