package com.brother.ptouch.sdk.FeedingSandiego;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.FeedingSandiego.common.Common;
import com.brother.ptouch.sdk.FeedingSandiego.printprocess.ImagePrint;
import com.brother.ptouch.sdk.FeedingSandiego.printprocess.PrinterModelInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.brother.ptouch.sdk.FeedingSandiego.BaseActivity;

public class ScrollingActivity extends BaseActivity  {

    // Contains every component IDs inside content_scrolling.xml
    private static ArrayList<View> components = new ArrayList<View>();
    private final int PERMISSION_WRITE_EXTERNAL_STORAGE = 10001;
    private final ArrayList<String> mFiles = new ArrayList<String>();
    Bitmap bitmap;
    File file;
    Canvas cv;
    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        file = new File(directory, "templet" + ".jpg");
        if (!file.exists()) {
            Drawable drawable = getResources().getDrawable(R.drawable.templet1);
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        ViewGroup main = findViewById(R.id.lay_main);
        components = getChildren(main);
        init();
        getDataFromIntent();
        // initialize the SharedPreferences
        setPreferences();
        isPermitWriteStorage();
    }
    public void printTemplet(final String text){ final String captionString = "Hello World!";
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(100);
        paintText.setStyle(Paint.Style.FILL);
//                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

        Rect rectText = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);


        cv.drawText(captionString,
                (cv.getWidth() / 2) - (rectText.width() / 2), rectText.height() + (rectText.height() / 3), paintText);

        cv.drawText(captionString,
                (cv.getWidth() / 2) - (rectText.width() / 2), cv.getHeight() / 2, paintText);

        cv.drawText(captionString,
                (cv.getWidth() / 2) - (rectText.width() / 2), cv.getHeight() - (rectText.height() / 3), paintText);

        cv.drawText(captionString,
                0, rectText.height(), paintText);}
    private void init() {
        /*
         * copy .bin file (RJ paper size info.) to
         * /mnt/sdcard/customPaperFileSet/ .bin file is made by Printer Setting
         * Tool which can be downloaded from the Brother Net Site
         */
        try {

            raw2file("TD4100N_102mm.bin", R.raw.td4100n_102mm);
            raw2file("TD4100N_102mm152mm.bin", R.raw.td4100n_102mmx152mm);
            raw2file("TD4000_102mm.bin", R.raw.td4000_102mm);
            raw2file("TD4000_102mm152mm.bin", R.raw.td4000_102mmx152mm);

            raw2file("RJ4230B_58mm.bin", R.raw.rj4230b_58mm);
            raw2file("RJ4230B_102mm.bin", R.raw.rj4230b_102mm);
            raw2file("RJ4230B_102mm152mm.bin", R.raw.rj4230b_102mm152mm);
            raw2file("RJ4250WB_58mm.bin", R.raw.rj4250wb_58mm);
            raw2file("RJ4250WB_102mm.bin", R.raw.rj4250wb_102mm);
            raw2file("RJ4250WB_102mm152mm.bin", R.raw.rj4250wb_102mm152mm);
        } catch (Exception ignored) {
        }


    }
    private boolean isPermitWriteStorage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if (!isPermitWriteStorage()) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.unable_access),
                            Toast.LENGTH_SHORT).show();
                } else {
                    init();
                }
            }

        }
    }

    private void setPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        // initialization for print
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();

        if (printerInfo == null) {
            printerInfo = new PrinterInfo();
            printer.setPrinterInfo(printerInfo);

        }
        if (sharedPreferences.getString("printerModel", "").equals("")) {
            String printerModel = printerInfo.printerModel.toString();
            PrinterModelInfo.Model model = PrinterModelInfo.Model.valueOf(printerModel);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("printerModel", printerModel);
            editor.putString("port", printerInfo.port.toString());
            editor.putString("address", printerInfo.ipAddress);
            editor.putString("macAddress", printerInfo.macAddress);
            editor.putString("localName", printerInfo.getLocalName());

            // Override SDK default paper size
            editor.putString("paperSize", model.getDefaultPaperSize());

            editor.putString("orientation", "PORTRAIT");
            editor.putString("numberOfCopies","1");
            editor.putString("halftone", "PATTERNDITHER");
            editor.putString("printMode", "FIT_TO_PAGE");
            editor.putString("leftMargin","0");
            editor.putString("topMargin","0");
            // editor.putString("customSetting",
            //    sharedPreferences.getString("customSetting", ""));
            editor.putString("rjDensity", "5");
            editor.putString("dashLine", "TRUE");

            editor.putString("printQuality","NORMAL");
            editor.putString("skipStatusCheck", "TRUE");
            editor.putString("checkPrintEnd", "CPE_NO_CHECK");
            editor.putString("trimTapeAfterData", "FALSE");



            editor.putString("overwrite",
                    Boolean.toString(printerInfo.overwrite));
            editor.putString("savePrnPath", printerInfo.savePrnPath);
            editor.putString("softFocusing",
                    Boolean.toString(printerInfo.softFocusing));
            editor.putString("rawMode",
                    Boolean.toString(printerInfo.rawMode));
            editor.putString("workPath", printerInfo.workPath);
            editor.putString("useLegacyHalftoneEngine",
                    Boolean.toString(printerInfo.useLegacyHalftoneEngine));
            editor.apply();
        }

    }
    private String parseFileName(String fileName) {
        if (fileName.contains("content://")) {
            if (getIntent().getExtras() == null) {
                return "";
            }
            final Uri imageUri = Uri.parse(
                    getIntent().getExtras().get("android.intent.extra.STREAM").toString());
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
            if (cursor == null) {
                return "";
            }
            int columnIndex = 0;
            try {
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            } catch (IllegalArgumentException e) {
                return "";
            }
            cursor.moveToFirst();
            fileName = cursor.getString(columnIndex);
            cursor.close();
        } else if (fileName.contains("file://")) {
            fileName = Uri.decode(fileName);
            fileName = fileName.substring(7);
        }
        return fileName;
    }

    private String saveDataFromIntent(Intent intent) {
        String fileName = "";

        try {
            Uri uri;
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } else {
                uri = getIntent().getData();
            }
            if (uri == null) {
                return "";
            }
            Cursor cursor = getContentResolver().query(uri, new String[]{
                    MediaStore.MediaColumns.DISPLAY_NAME
            }, null, null, null);
            if (cursor == null) {
                return "";
            }
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);

            String folder = Environment.getExternalStorageDirectory()
                    .toString() + "/com.brother.ptouch.sdk/";
            File newdir = new File(folder);
            if (!newdir.exists()) {
                newdir.mkdir();
            }

            if (nameIndex >= 0) {
                fileName = folder + cursor.getString(nameIndex);
            }
            cursor.close();
            File dstFile = new File(fileName);
            OutputStream output = new FileOutputStream(dstFile);
            InputStream input = new BufferedInputStream(getContentResolver().openInputStream(uri));

            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            input.close();
            output.close();
        } catch (IOException e) {
            fileName = "";
        }


        return fileName;
    }

    /**
     * launch by intent sending
     */
    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        // Get file path from intent
        if (Intent.ACTION_SEND.equals(intent.getAction())
                || Intent.ACTION_VIEW.equals(intent.getAction())) {

            String fileName = "";
            // get the data of Intent.ACTION_SEND from other application
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                if (intent.getExtras() == null) {
                    return;
                }
                fileName = intent.getExtras()
                        .get("android.intent.extra.STREAM").toString();
                fileName = parseFileName(fileName);

            } else {

                Uri uri = intent.getData();
                if (uri == null) {
                    return;
                }
                fileName = uri.toString();
                if (fileName == null) {
                    return;
                }
                fileName = parseFileName(fileName);
            }
            if (fileName == null || "".equals(fileName)) {
                fileName = saveDataFromIntent(intent);
            }

            if (fileName == null || fileName.equals("")) {
                return;
            }
            // launch the PrintImage Activity when it is a image file or prn
            // file
            if (Common.isImageFile(fileName) || Common.isPrnFile(fileName)) {
                Intent printerList = new Intent(this, Activity_PrintImage.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
            // launch the PrintPdf Activity when it is a pdf file
            else if (Common.isPdfFile(fileName)) {
                Intent printerList = new Intent(this, Activity_PrintPdf.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
            // launch the TransferPdz Activity when it is a pdz file
            else if (Common.isTemplateFile(fileName)) {
                Intent printerList = new Intent(this,
                        Activity_TransferPdz.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
        }
    }
    private void raw2file(String fileName, int fileID) {

        File newdir = new File(Common.CUSTOM_PAPER_FOLDER);
        if (!newdir.exists()) {
            newdir.mkdir();
        }
        File dstFile = new File(Common.CUSTOM_PAPER_FOLDER + fileName);
        if (!dstFile.exists()) {
            try {
                InputStream input;
                OutputStream output;
                input = this.getResources().openRawResource(fileID);
                output = new FileOutputStream(dstFile);
                int DEFAULT_BUFFER_SIZE = 1024 * 4;
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                input.close();
                output.close();
            } catch (IOException ignored) {
            }
        }
    }

    private ArrayList<View> getChildren(View v) {

        // Anything with child(s) is a ViewGroup, end recursion if not
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        // Loop inside current group, and compile results from every child
        ViewGroup vg = (ViewGroup) v;
        Log.i("ChildCount", String.valueOf(vg.getChildCount()));
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getChildren(child));   // Recursion

            result.addAll(viewArrayList);
        }
Log.d("result bef", result.toString());
        // Return to parent
        return result;
    }

    private Map<String, String> getData() {
        // Collect all input data
        Map<String, String> result = new HashMap<>();

        for (View comp : components) {
            // Get every EditText's tag & text.
            if (comp instanceof EditText) {
                result.put(comp.getTag().toString(), ((EditText) comp).getText().toString());
            }
        }
        Log.d("result aft", result.toString());

        return result;
    }
    public void printme(final View view){
        Log.d("init printing",file.getPath());
        startActivity(new Intent(this, Activity_PrinterSettings.class));

    }

    // Assigned to the fab (floating action button) onClick parameter.
    public void postSheet(final View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://script.google.com/macros/s/AKfycbzajpQfSuy8UCRVdsZswdAyI6spkwuBae9t8DgrUExEQwAZJwo/exec";

        // Collect all data to send
        final Map<String, String> allData = getData();
       // allData.putAll(allRadio);  // Combine with radio data
       // allData.putAll(allCheck);  // Combine with check data
Log.d("alldata",allData.toString());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response is",response.toString());
                Snackbar.make(view, "Response: " + response, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, "No response", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("error",error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.d("data",allData.toString());

//                Map<String, String> params = new HashMap<>();
                return allData;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
    public void printit(final View view){
       // startActivity(new Intent(this, Activity_PrinterPreference.class));

    }

    @Override
    public void selectFileButtonOnClick() {

    }

    @Override
    public void printButtonOnClick() {
        Log.d("init printing",file.getPath());
        mFiles.add(file.toString());
Log.d("mfiles",mFiles.toString());
        myPrint.getPrinterStatus();

        ((ImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        // call function to print
        myPrint.print();
        Log.d("yipee","Done printing");
    }
}
