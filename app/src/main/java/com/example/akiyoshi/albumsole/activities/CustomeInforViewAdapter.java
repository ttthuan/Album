package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomeInforViewAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater mInflater;
    private Context mContext;

    public CustomeInforViewAdapter(LayoutInflater inflater, Context context) {
        mInflater = inflater;
        mContext = context;
    }

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View popup = mInflater.inflate(R.layout.info_window_layout, null);

        //((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());
        ImageView imgv = popup.findViewById(R.id.info_window);


        int width = dpToPx(130, mContext);
        int height = dpToPx(130, mContext);
        String mPath = marker.getSnippet();
        imgv.setImageBitmap(getCroppedBitmap(Picture.resizeBitmap(mPath, width, height, 0)));

        return popup;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() - dpToPx(40, mContext), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = mInflater.inflate(R.layout.info_window_layout, null);

        //((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

        return popup;
    }
}
