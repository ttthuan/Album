package com.example.akiyoshi.albumsole.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.StoreCluster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;


public class GoogleMapFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap googleMap;
    private List<Picture> listPicture;
    private MapView mapView;
    public static Bitmap marker_buble;
    private ClusterManager<StoreCluster> mClusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listPicture = PictureLab.getInstance(getActivity()).getPictureMonth().getListAllPicture();
        marker_buble = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_marker_blue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Log.d("MAP", "create map");

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        MapFragment mapFragment = (MapFragment)(getActivity().getFragmentManager().findFragmentById(R.id.view_map));
//        mapFragment.getMapAsync(this);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        Log.d("MAP", "create map2");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        mClusterManager = new ClusterManager<StoreCluster>(getActivity(), googleMap);
        CustomeClusterRender renderer = new CustomeClusterRender(getActivity(), googleMap, mClusterManager);

        mClusterManager.setRenderer(renderer);

        mClusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomeInforViewAdapter(LayoutInflater.from(getActivity()), getContext()));

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<StoreCluster>() {
            @Override
            public void onClusterItemInfoWindowClick(StoreCluster storeCluster) {
                Intent intent = PictureDetailViewActivity.newIntent(getContext(), Integer.parseInt(storeCluster.getTitle()));

                startActivity(intent);
            }
        });

        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);

        LatLng khtn = new LatLng(10.762775, 106.681169);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(khtn, 1.0f));

        AddCluster();
        Log.d("MAP", "create map3");

    }

    public void AddCluster() {
        int n = listPicture.size();
        float kinhTuyenHDKHTN, viTuyenDHKHTN;
        kinhTuyenHDKHTN = 10.762775f;
        viTuyenDHKHTN = 106.681169f;

        for (int i = 0; i < n; i++) {
            if (listPicture.get(i).getLat() != null && listPicture.get(i).getLng() != null) {
                Picture picture = listPicture.get(i);
                LatLng latLng = new LatLng(listPicture.get(i).getLat(), listPicture.get(i).getLng());
                mClusterManager.addItem(new StoreCluster(latLng, picture.getPath(), picture.getId()));
            }
        }

        // test nhiều image có location
//        for (int i = 0; i < n; i++) {
//            Picture picture = listPicture.get(i);
//            LatLng latLng = new LatLng(kt + i, vt+i);
//            mClusterManager.addItem(new StoreCluster(latLng, picture.getPath(), picture.getId()));
//        }

        mClusterManager.cluster();
    }

    public void removeCluster(){

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
