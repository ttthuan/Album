package com.example.akiyoshi.albumsole.models;

import android.content.Context;
import android.graphics.Bitmap;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import java.util.ArrayList;
import java.util.List;

public class ThumbsManager {
    public static List<Filter> filters = null;
    public static List<Bitmap> bitmaps = null;

    public static void Init(Context context){
        if(filters == null){
            filters = new ArrayList<>();

            filters.add(FilterPack.getAdeleFilter(context));
            filters.add(FilterPack.getAmazonFilter(context));
            filters.add(FilterPack.getAprilFilter(context));
            filters.add(FilterPack.getAudreyFilter(context));

            filters.add(FilterPack.getAweStruckVibeFilter(context));
            filters.add(FilterPack.getBlueMessFilter(context));
            filters.add(FilterPack.getClarendon(context));
            filters.add(FilterPack.getCruzFilter(context));

            filters.add(FilterPack.getHaanFilter(context));
            filters.add(FilterPack.getLimeStutterFilter(context));
            filters.add(FilterPack.getMarsFilter(context));
            filters.add(FilterPack.getOldManFilter(context));

            filters.add(FilterPack.getMetropolis(context));
            filters.add(FilterPack.getNightWhisperFilter(context));
            filters.add(FilterPack.getRiseFilter(context));
            filters.add(FilterPack.getStarLitFilter(context));
        }

        if(bitmaps == null){
            bitmaps = new ArrayList<>();
        }
    }

    public static void addBitmap(Bitmap bitmap){
        bitmaps.add(bitmap);
    }

    public static List<Bitmap> processBitmap(){
        int n = bitmaps.size();
        for(int i = 0; i < n; i++){
            bitmaps.set(i, filters.get(i).processFilter(bitmaps.get(i)));
        }
        return bitmaps;
    }

    public static void clearBitmap(){
        bitmaps.clear();
    }
}
