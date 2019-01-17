package com.sticker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.sticker.model.Sticker;
import com.sticker.model.StickerPack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Activity context = MainActivity.this;
    String TAG = getClass().getSimpleName();

    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    String path;

    ArrayList<StickerPack> stickerPacks = new ArrayList<>();
    List<Sticker> Stickers;

    ProgressDialog progressDialog;
    Button download;
    Button addToWhatsapp;

    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    public static final String EXTRA_STICKERPACK = "stickerpack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(context);

        download = findViewById(R.id.download);
        addToWhatsapp = findViewById(R.id.addToWhatsapp);

        path = getFilesDir() + "/" + "stickers_asset";
        getPermissions();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trayImage();
                dowlnloadImages();
            }
        });

        addToWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStickerNameToWhatsapp();
            }
        });


    }

    void trayImage() {
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        String url = "https://raw.githubusercontent.com/WhatsApp/stickers/master/Android/app/src/main/assets/1/tray_Cuppy.png";
        downloadImages downloadImages = new downloadImages(url, new BitmapListener() {
            @Override
            public void OnBitmapLoad(Bitmap bitmap) {
                SaveTryImage(bitmap, "chandresh", "c1");
            }
        });
        downloadImages.execute();
    }

    void dowlnloadImages() {
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        String[] images = {"https://na.cx/i/NzHLcMc.png", "http://icons-for-free.com/free-icons/png/512/2481087.png", "https://images-na.ssl-images-amazon.com/images/I/71mPwewBIaL.png"};
        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            downloadImages downloadImages = new downloadImages(images[i], new BitmapListener() {
                @Override
                public void OnBitmapLoad(Bitmap bitmap) {
                    SaveImage(bitmap, "chandresh" + finalI, "c1");
                }
            });
            downloadImages.execute();
        }
    }

    private void getPermissions() {
        int perm = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (perm != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    1
            );
        }
    }

    void SaveTryImage(Bitmap finalBitmap, String name, String identifier) {

        String root = path + "/" + identifier;
        File myDir = new File(root + "/" + "try");
        myDir.mkdirs();
        String fname = name.replace(".png", "").replace(" ", "_") + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 40, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveImage(Bitmap finalBitmap, String name, String identifier) {

        String root = path + "/" + identifier;
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = name.replace(".png", "").replace(" ", "_") + ".webp";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class downloadImages extends AsyncTask {
        String src;
        BitmapListener bitmapListener;

        public downloadImages(String src, BitmapListener bitmapListener) {
            this.src = src;
            this.bitmapListener = bitmapListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                bitmapListener.OnBitmapLoad(myBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    interface BitmapListener {
        void OnBitmapLoad(Bitmap bitmap);
    }

    void addStickerNameToWhatsapp(){
        ArrayList<String> emoji = new ArrayList<>();
        emoji.add("");
        Sticker sticker = new Sticker("chandresh0.webp",emoji);
        Sticker sticker1 = new Sticker("chandresh1.webp",emoji);
        Sticker sticke2 = new Sticker("chandresh2.webp",emoji);
        StickerPack stickerPack = new StickerPack("c1","chandresh","vithani","chandresh.png","","abc@gmail.com","","");
        ArrayList<Sticker> stickerList = new ArrayList<>();
        stickerList.add(sticker);
        stickerList.add(sticker1);
        stickerList.add(sticke2);

        stickerPack.setStickers(stickerList);
        stickerPacks.add(stickerPack);

        Hawk.put("c1", stickerList);
        Hawk.put("sticker_packs", stickerPacks);

        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, stickerPack.identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPack.name);
        try {
            startActivityForResult(intent, 255);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
        }
    }
}
