package com.example.oleg.physiognomist;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView mImageView;
    private ImageButton    mBtnTakePicture;
    private Button  mBtnPrevious;
    private Button  mBtnNext;
    private LinearLayout mTemplatesLayout;

    private Uri fileUri; // file url to store image/video
    private int loadedTemplateSet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBtnTakePicture = (ImageButton)findViewById(R.id.make_picture);
        mImageView = (ImageView)findViewById(R.id.imagePicture);

        mBtnNext = (Button) findViewById(R.id.next_template);
        mBtnPrevious = (Button) findViewById(R.id.prev_template);
        mTemplatesLayout = (LinearLayout)findViewById(R.id.templates_layout);

        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler_view);
        if( recycler != null ) {
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recycler.setItemAnimator((new DefaultItemAnimator()));
            recycler.setNestedScrollingEnabled(false);

            List<FacePartTemplate> templates = new ArrayList<>();
            FacePartTemplate t1 = new FacePartTemplate("Nose 1", R.drawable.nose_1);
            templates.add(t1);
            FacePartTemplate t2 = new FacePartTemplate("Nose 2", R.drawable.nose_2);
            templates.add(t2);
            FacePartTemplate t3 = new FacePartTemplate("Nose 3", R.drawable.nose_3);
            templates.add(t3);
            FacePartTemplate t4 = new FacePartTemplate("Nose 4", R.drawable.nose_4);
            templates.add(t4);
            FacePartTemplate t5 = new FacePartTemplate("Nose 5", R.drawable.nose_5);
            templates.add(t5);
            FacePartTemplate t6 = new FacePartTemplate("Nose 6", R.drawable.nose_6);
            templates.add(t6);
            loadedTemplateSet = 0;

            TemplatesAdapter adapter = new TemplatesAdapter(this, templates);
            recycler.setAdapter(adapter);
        }

        if( fileUri != null )
            previewImage(fileUri);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if( id == R.id.menu_picture) {
            takePicture(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
        outState.putInt("template_set", loadedTemplateSet);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        if( fileUri != null )
            previewImage(fileUri);

        savedInstanceState.getInt("template_set", loadedTemplateSet);
        if( loadedTemplateSet == 0 )
            mBtnPrevious.setEnabled(false);
        else
            mBtnPrevious.setEnabled(true);

    }

    public void takePicture(View view) {

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();

            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void loadNextTemplateSet(View v){
        loadedTemplateSet++;
    }

    public void loadPreviousTemplateSet(View v){
        loadedTemplateSet--;
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final String IMAGE_DIRECTORY_NAME = "PhysignomistCamera";

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void previewImage(Uri fileUri) {

        mImageView.setVisibility(View.VISIBLE);
        mBtnTakePicture.setVisibility(View.GONE);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                options);
        if( bitmap != null )
            bitmap = rotateImage(bitmap, 90);
//        try {
//            ExifInterface ei = new ExifInterface(fileUri.toString());
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//            switch(orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotateImage(bitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotateImage(bitmap, 180);
//                    break;
//                // etc.
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mImageView.setImageBitmap(bitmap);

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                mTemplatesLayout.setVisibility(View.VISIBLE);
                previewImage(fileUri);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }
}
