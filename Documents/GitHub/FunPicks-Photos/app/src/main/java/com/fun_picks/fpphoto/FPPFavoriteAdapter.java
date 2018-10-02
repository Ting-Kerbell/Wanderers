package com.fun_picks.fpphoto;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import android.widget.*;

public class FPPFavoriteAdapter extends ArrayAdapter<FPPFavorite> {
    public static final String TAG = "FPPFavoriteAdapter";
    int resource;
    private Context mContext;
    PBudDBAdapter pbDBAdapter;

    public FPPFavoriteAdapter(Context _context,
                              int _resource,
                              List<FPPFavorite> _favorite) {
        super(_context, _resource, _favorite);
        resource = _resource;
        mContext = _context;
        pbDBAdapter = new PBudDBAdapter(_context);

        //     Open or create the database
        pbDBAdapter.open();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.i(TAG, "getView: entered. position=" + position);
        ImageView imageView;
        FPPFavorite item = getItem(position);
        String jpgFileName = item.getJpgFileName();
        long seqNum = item.getSequenceNumber();

        float dim = (float) ((float) MainActivity.screenWidth/2.5);

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int)dim, (int)dim));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10, 10, 10);


        } else {
            imageView = (ImageView) convertView;
        }

        byte[] bytes = null;
        int size = 0;

        try {

            /*if (MainActivity.importMode == MainActivity.PB_IMPORT_MODE_SD) {
                File imageFile = new File(jpgFileName);
                size = (int) imageFile.length();
                bytes = new byte[size];
                RandomAccessFile raf = new RandomAccessFile(jpgFileName, "r");
                raf.seek(0);
                int cnt = raf.read(bytes);
            } else {*/
                InputStream is;
                is = mContext.getAssets().open(jpgFileName);
                size = is.available();
                bytes=new byte[size];
                is.read(bytes);
           //}

            if(MainActivity.imageEncoded)
                for (int i = 0; i < 500; i++) {
                    bytes[i] ^= MainActivity.xyz;
                }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, size, options);

        imageView.setImageBitmap(bMap);
        return imageView;
    }


}
