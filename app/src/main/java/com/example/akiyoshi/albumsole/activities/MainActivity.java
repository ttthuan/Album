package com.example.akiyoshi.albumsole.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.Screen;
import com.example.akiyoshi.albumsole.models.ThumbsManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context context;
    public static DrawerLayout drawer_layout;
    NavigationView nav_view;
    public static List<PictureAreaItem> listPictureItem;
    public static List<PictureAreaItem> listFolderItem;
    public static List<String> listMonthly;
    public static int widthScreen, heightScreen;
    public static int edgeMargin = 0, itemMargin = 1; //dp
    int rowsInScreen = 7; //rows
    int heightRow, widthRow, widthRowPercent, percentAllow = 40, widthAllowWrap;
    float ratioScreen, ratioRow;
    public static int rowInGrid = 7;
    public static int whItem, whToHItem = 5;
    private int currentFunctionNeedExcuse = 0;
    public static Screen nowScreen = Screen.HOME;
    public static Screen preScreen = Screen.HOME;
    boolean isNavOpened = false;
    boolean isNavChangeFragment = false;

    Fragment  fragmentPictureArea;
    Fragment folderManagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;
        currentFunctionNeedExcuse = 1;
        ThumbsManager.Init(this);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                isNavOpened = true;
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorHFShadowWhite));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isNavOpened = false;
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(ContextCompat.getColor(context, android.R.color.transparent));
                if (isNavChangeFragment) {
                    isNavChangeFragment = false;
                    FragmentManager fm = getSupportFragmentManager();
                    switch (nowScreen) {
                        case HOME:
                            currentFunctionNeedExcuse = 1; // month
                            if (isStoragePermissionGranted()) {
                                currentFunctionNeedExcuse = 0;
                                // open fragment month
                                LoadData loadDataMonth = new LoadData(1);
                                loadDataMonth.execute();
                            }
                            break;
                        case MAP:
                            currentFunctionNeedExcuse = 2; // map
                            if (isStoragePermissionGranted()) {
                                currentFunctionNeedExcuse = 0;
                                LoadData loadDataMap = new LoadData(2);
                                loadDataMap.execute();
                            }
                            break;
                        case ALBUMS:
                            break;
                        case FOLDERS:
                            if (isStoragePermissionGranted()) {
                                currentFunctionNeedExcuse = 3; // folder
                                // open fragment folder
                                LoadData loadDataAlbum = new LoadData(3);
                                loadDataAlbum.execute();
                            }
                            break;
                        case MEMORY:
                            if (isStoragePermissionGranted()) {
                                currentFunctionNeedExcuse = 4; // Memroy
                                // open fragment memory
                                LoadData loadDataAlbum = new LoadData(4);
                                loadDataAlbum.execute();
                                nowScreen = preScreen;
                            }
                            break;
                    }
                }

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_memory:
                                if (nowScreen != Screen.MEMORY) {
                                    Log.d("Memory", "Memory click");
                                    preScreen = nowScreen;
                                    nowScreen = Screen.MEMORY;
                                    isNavChangeFragment = true;
                                    menuItem.setChecked(false);
                                }
                                break;
                            case R.id.nav_map:
                                if (nowScreen != Screen.MAP) {
                                    Log.d("Map", "Map click");
                                    nowScreen = Screen.MAP;
                                    isNavChangeFragment = true;
                                    menuItem.setChecked(true);
                                }

                                break;
                            case R.id.nav_month:
                                if (nowScreen != Screen.HOME) {
                                    Log.d("Home", "Home click");
                                    nowScreen = Screen.HOME;
                                    isNavChangeFragment = true;
                                    menuItem.setChecked(true);
                                }

                                break;
                            case R.id.nav_folder:
                                if (nowScreen != Screen.FOLDERS) {
                                    Log.d("Folders", "Folders click");
                                    nowScreen = Screen.FOLDERS;
                                    isNavChangeFragment = true;
                                    menuItem.setChecked(true);
                                }

                                break;
                        }

                        drawer_layout.closeDrawers();
                        return false;
                    }
                });
        nav_view.setCheckedItem(R.id.nav_month);

        if (isStoragePermissionGranted()) {
            currentFunctionNeedExcuse = 0;
            // open fragment month
            LoadData loadDataMonth = new LoadData(1);
            loadDataMonth.execute();
        }
    }


    public void InitPictureAreaItem() {
        try {
            getDimensionScreen();
            PictureLab.getInstance(this).LoadImage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDimensionScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heightScreen = displayMetrics.heightPixels;
        ratioScreen = widthScreen * 1.0f / heightScreen * 1.0f;
        widthRow = (widthScreen - dpToPx(edgeMargin, this));
        widthRowPercent = widthRow * percentAllow / 100;
        widthAllowWrap = widthRow + widthRowPercent;
        heightRow = (heightScreen / rowsInScreen);
        ratioRow = widthRow * 1.0f / heightRow;
        whItem = (int) Math.ceil((widthRow - ((rowInGrid - 1) * dpToPx(itemMargin, this))) * 1.0f / rowInGrid) + whToHItem;
    }

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int n = permissions.length;
        for (int i = 0; i < n; i++) {
            if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("MainActivity", "Permission: " + permissions[i] + "was " + grantResults[i]);
                    if (currentFunctionNeedExcuse == 1) {
                        currentFunctionNeedExcuse = 0;
                        // open fragment month
                        LoadData loadDataMonth = new LoadData(1);
                        loadDataMonth.execute();
                    } else if (currentFunctionNeedExcuse == 2) {
                        currentFunctionNeedExcuse = 0;
                        LoadData loadDataMap = new LoadData(2);
                        loadDataMap.execute();
                    } else if (currentFunctionNeedExcuse == 3) {
                        currentFunctionNeedExcuse = 0;
                        LoadData loadDataMemory = new LoadData(3);
                        loadDataMemory.execute();
                    } else if (currentFunctionNeedExcuse == 4) {
                        currentFunctionNeedExcuse = 0;
                        LoadData loadDataMemory = new LoadData(4);
                        loadDataMemory.execute();
                    }
                }
                break;
            } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DELETE", "MAIN DELETE");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PictureAreaFragment.REQUEST_CODE_IN_VIEW_DETAIL) {
            PictureAreaAdapter.isOpenDetail = false;
            ((PictureAreaFragment) fragmentPictureArea).deleteImageFrontUI();
        }else if(requestCode == FolderItemFragment.REQUEST_CODE_IN_VIEW_DETAIL){
            PictureAreaAdapter.isOpenDetail = false;
            ((FolderManagerFragment)folderManagerFragment).changeDataForFolderFragment();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MainActivity", "Permission read is granted");
                return true;
            } else {
                Log.v("MainActivity", "Permission read is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("MainActivity", "Permission read is granted");
            return true;
        }
    }

    public class LoadData extends AsyncTask<Object, Object, Object> {
        private int TypeOfFragment;

        public LoadData(int type) {
            this.TypeOfFragment = type;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            InitPictureAreaItem();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (this.TypeOfFragment == 1) { // month
                OpenFragmentMonth();
            } else if (this.TypeOfFragment == 2) { // map
                OpenFragmentMap();
            } else if (this.TypeOfFragment == 3) { // folder
                OpenFragmentFolder();
            } else if (this.TypeOfFragment == 4) { // memory
                OpenFragmentMemory();
            }
        }
    }

    public void OpenFragmentMonth() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentPictureArea = fragmentManager.findFragmentByTag("MONTH");
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        if (fragmentPictureArea == null) {
            fragmentPictureArea = new PictureAreaFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //transaction.setCustomAnimations(R.anim.open_main_content, R.anim.close_main_content, R.anim.open_main_content, R.anim.close_main_content);
            transaction.replace(R.id.fragment_container, fragmentPictureArea, "MONTH");
            //transaction.addToBackStack("MONTH");
            transaction.commit();
        } else {
            fragmentManager.popBackStackImmediate("MONTH", 0);
        }
    }

    public void ReturnOpenFragmentMonth() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }

    public void OpenFragmentFolder() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        folderManagerFragment = fragmentManager.findFragmentByTag("FOLDERMANAGER");
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        if (folderManagerFragment == null) {
            folderManagerFragment = new FolderManagerFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //transaction.setCustomAnimations(R.anim.open_main_content, R.anim.close_main_content, R.anim.open_main_content, R.anim.close_main_content);
            transaction.replace(R.id.fragment_container, folderManagerFragment, "FOLDERMANAGER");
//            transaction.add(R.id.FOLDERMANAGER, fragment, "FOLDERMANAGER");
            transaction.addToBackStack("FOLDERMANAGER");
            transaction.commit();
        } else {
            fragmentManager.popBackStackImmediate("FOLDERMANAGER", 0);
        }
    }

    public void OpenFragmentMemory() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentByTag("MEMORY");
//        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//            fragmentManager.popBackStack();
//        }
//        if (fragment == null) {
//            fragment = new MemoryFragment();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            //transaction.setCustomAnimations(R.anim.open_main_content, R.anim.close_main_content, R.anim.open_main_content, R.anim.close_main_content);
//            transaction.replace(R.id.fragment_container, fragment, "MEMORY");
////            transaction.add(R.id.fragment_container_memory, fragment, "MEMORY");
//            transaction.addToBackStack("MEMORY");
//            transaction.commit();
//        } else {
//            fragmentManager.popBackStackImmediate("MEMORY", 0);
//        }

        Intent intent = new Intent(this, MemoryActivity.class);
        startActivity(intent);
    }

    public void OpenFragmentMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("GOOGLE_MAP");
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        if (fragment == null) {
            fragment = new GoogleMapFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.fragment_container, fragment, "GOOGLE_MAP");
            transaction.addToBackStack("GOOGLE_MAP");
            transaction.commit();
        } else {
            fragmentManager.popBackStackImmediate("GOOGLE_MAP", 0);
        }
    }

    public void showDrawerNav() {
        drawer_layout.openDrawer(Gravity.START);
    }


    @Override
    public void onBackPressed() {
        if (isNavOpened) {
            drawer_layout.closeDrawer(Gravity.START);
        } else {
            switch (nowScreen) {
                case FOLDERS:
                    nowScreen = Screen.HOME;
                    nav_view.setCheckedItem(R.id.nav_month);
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                    break;
                case FOLDER_ITEM:
                    nowScreen = Screen.FOLDERS;
                    setTitle("Folders");
                    FolderManagerFragment.folderToolbar.setNavigationIcon(R.drawable.ic_menu);
                    getSupportFragmentManager().popBackStackImmediate("FOLDERS", 0);
                    break;
                default:
                    nowScreen = Screen.HOME;
                    nav_view.setCheckedItem(R.id.nav_month);

                    super.onBackPressed();
                    break;
            }
        }
    }

    //    public void InitPictureAreaItem() {
//        try {
//            getDimensionScreen();
//            PictureLab.getInstance(this).LoadImage();
//            listPictureItem = new ArrayList<>();
//            listPicTempToCrop = new ArrayList<>();
//            listMonthly = PictureLab.getInstance(this).getPictureMonth().getListKey();
//            String monthly;
//            for (int i = 0; i < listMonthly.size(); i++) {
//                monthly = listMonthly.get(i);
//                listPictureItem.add(new PictureAreaItem(monthly, null, 0, 0, true));
//                listPictureInMonth = PictureLab.getInstance(this).getPictureMonth().getListPictureInMonth(monthly);
//                int totalWidthCrop = 0;
//                float percentCropItem = -1;
//                for (int j = 0; j < listPictureInMonth.size(); j++) {
//                    picTemp = listPictureInMonth.get(j);
//                    Log.i("path", "" + picTemp.getName());
//                    int wPic = picTemp.getWidth();
//                    int hPic = picTemp.getHeight();
//                    int lastCrop = 0;
//                    float ratio = wPic * 1.0f / hPic * 1.0f;
//                    int countListTemp = listPicTempToCrop.size();
//                    int wPicCrop, hPicCrop, totalMargin;
//
//                    if (countListTemp > 0) {
//                        totalMargin = dpToPx(itemMargin, this) * (countListTemp);
//                        totalWidthItem += Math.ceil(heightRow * ratio);
//                        int totalWidth = totalMargin + totalWidthItem;
//                        //tính toán crop hình
//                        if (totalWidth < widthRow) {
//                            listPicTempToCrop.add(new PictureAreaItem("", picTemp, wPic, hPic, false));
//                            Log.i("If", "1");
//                        } else if (totalWidth >= widthAllowWrap) {
//                            totalMargin = dpToPx(itemMargin, this) * (countListTemp - 1);
//                            totalWidthItem -= Math.ceil(heightRow * ratio);
//                            if (percentCropItem < 0)
//                                percentCropItem = (widthRow) * 1.0f / totalWidth;
//                            for (int u = 0; u < listPicTempToCrop.size(); u++) {
//                                picTemp = listPicTempToCrop.get(u).getPicture();
//                                wPic = picTemp.getWidth();
//                                hPic = picTemp.getHeight();
//                                ratio = wPic * 1.0f / hPic * 1.0f;
//                                hPicCrop = heightRow;
//                                wPicCrop = (int) Math.round(hPicCrop * ratio);
//                                wPicCrop = (int) Math.round(wPicCrop * percentCropItem) - (int) Math.ceil(dpToPx(itemMargin, this) * percentCropItem);
//                                totalWidthCrop += wPicCrop + dpToPx(itemMargin, this);
//                                if (u == listPicTempToCrop.size() - 1) {
//                                    lastCrop = widthRow - totalWidthCrop - 2;
//                                    wPicCrop = wPicCrop + lastCrop;
//                                }
//                                listPictureItem.add(new PictureAreaItem(monthly, picTemp, wPicCrop, hPicCrop, false));
//                            }
//                            listPicTempToCrop.clear();
//                            totalWidthItem = 0;
//                            totalWidthCrop = 0;
//                            percentCropItem = -1;
//                            j--;
//                            Log.i("If", "2");
//                        } else if (totalWidth < widthAllowWrap && totalWidth >= widthRow) {
//                            if (percentCropItem < 0)
//                                percentCropItem = (widthRow) * 1.0f / totalWidth;
//                            listPicTempToCrop.add(new PictureAreaItem("", picTemp, wPic, hPic, false));
//                            for (int u = 0; u < listPicTempToCrop.size(); u++) {
//                                picTemp = listPicTempToCrop.get(u).getPicture();
//                                Log.i("path", "" + picTemp.getName());
//                                wPic = picTemp.getWidth();
//                                hPic = picTemp.getHeight();
//                                ratio = wPic * 1.0f / hPic * 1.0f;
//                                hPicCrop = heightRow;
//                                wPicCrop = (int) Math.round(hPicCrop * ratio);
//                                wPicCrop = (int) Math.round(wPicCrop * percentCropItem) - (int) Math.ceil(dpToPx(itemMargin, this) * percentCropItem);
//                                totalWidthCrop += wPicCrop + dpToPx(itemMargin, this);
//                                if (u == listPicTempToCrop.size() - 1) {
//                                    lastCrop = widthRow - totalWidthCrop - 2;
//                                    wPicCrop = wPicCrop + lastCrop;
//                                }
//                                listPictureItem.add(new PictureAreaItem(monthly, picTemp, wPicCrop - 1, hPicCrop, false));
//
//                            }
//                            listPicTempToCrop.clear();
//                            totalWidthItem = 0;
//                            totalWidthCrop = 0;
//                            percentCropItem = -1;
//                            Log.i("If", "3");
//                        }
//                    } else {
//                        hPicCrop = heightRow;
//                        wPicCrop = (int) Math.ceil(widthRow - dpToPx(itemMargin, this));
//                        int wScale = (int) Math.ceil(heightRow * ratio);
//                        if (wScale >= widthRow / 2) {
//                            listPictureItem.add(new PictureAreaItem(monthly, picTemp, wPicCrop, hPicCrop, false));
//                            totalWidthItem = 0;
//                        } else {
//                            listPicTempToCrop.add(new PictureAreaItem("", picTemp, wPic, hPic, false));
//                            totalWidthItem += heightRow * ratio;
//                            //Log.i("Total width", "" + totalWidthItem);
//                        }
//                    }
//
//                }
//            }
//
//        } catch (
//                IOException e)
//
//        {
//            e.printStackTrace();
//        }
//
//    }
}
