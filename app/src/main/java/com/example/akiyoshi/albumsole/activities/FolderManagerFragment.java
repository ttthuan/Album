package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Screen;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FolderManagerFragment extends Fragment {
    //ParallaxRecyclerView memoryRecycleView;

    private int widthScreen;
    private int heigthScreen;

    //MemoryAdapter adapter = null;
    public static Toolbar folderToolbar;

    Fragment foldersFragment = null;

    public void getWithAndHeightOfScreen(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heigthScreen = displayMetrics.heightPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_manager, container, false);
        folderToolbar = (Toolbar) view.findViewById(R.id.toolbarFolder);
        ((MainActivity) getActivity()).setSupportActionBar(folderToolbar);
        ((MainActivity) getActivity()).setTitle("Folders");
        folderToolbar.setNavigationIcon(R.drawable.ic_menu);
        folderToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MainActivity.nowScreen)
                {
                    case FOLDER_ITEM:
                        MainActivity.nowScreen = Screen.FOLDERS;
                        getActivity().getSupportFragmentManager().popBackStackImmediate("FOLDERS", 0);
                        ((MainActivity) getActivity()).setTitle("Folders");
                        folderToolbar.setNavigationIcon(R.drawable.ic_menu);
                        break;
                    case FOLDERS:
                        ((MainActivity) getActivity()).showDrawerNav();
                        break;
                }
            }
        });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        foldersFragment = new FoldersFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.folders_container, foldersFragment, "FOLDERS");
        transaction.addToBackStack("FOLDERS");
        transaction.commit();
        return view;
    }

    public void changeDataForFolderFragment(){
        if(foldersFragment != null){
            String[] arrStr = ((FoldersFragment) foldersFragment).folderItemFragment.deleteImageFrontUI();
            if(!arrStr[0].equals("-1")){
                int size = Integer.parseInt(arrStr[0]);
                String nameFolder = arrStr[1];
                ((FoldersFragment) foldersFragment).changeUiAfterRemove(size, nameFolder);
            }
        }
    }

}
