package com.usf.registrationwithgallery;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener{

    private Button upload_button;
    private Button download_button;
    private VideoView stream_video;
    public static final int UPLOAD_REQUEST = 20;
    public static final int DOWNLOAD_REQUEST = 30;
    public static final int VIDEO_GALLERY_REQUEST = 40;
    String global_path, global_uid, unique_name;
    Uri videoUri, specific_video_uri;
    private StorageReference videoRef, storageRef, downloadRef;
    String specific_video_url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toast.makeText(GalleryActivity.this, "Welcome to the Gallery", Toast.LENGTH_SHORT).show();

        global_path = "";
        unique_name = "";
        upload_button = (Button) findViewById(R.id.upload_button);
        download_button = (Button) findViewById(R.id.download_button);
        stream_video =  (VideoView) findViewById(R.id.stream_video);
        specific_video_url = "https://firebasestorage.googleapis.com/v0/b/registrationwithgallery.appspot.com/o/videos%2F9DESbKvht7WS0QgAHjbb6sa3dQV2%2F924943006?alt=media&token=11d8cd1c-4575-47dd-bdbe-1921abdd2ca1";
        specific_video_uri = Uri.parse(specific_video_url);


        global_uid = FirebaseAuth.getInstance().getUid();


        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        }

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://registrationwithgallery.appspot.com");
        downloadRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://registrationwithgallery.appspot.com");


        upload_button.setOnClickListener(this);
        download_button.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view == upload_button){
            upload();
        }

        if(view == download_button){
            download(stream_video);
        }

    }



    public void upload() {
        openGallery();

    }
    //download function, the .gitFile method and the chained method
    // .addOnSuccessListener is mainly taken from the firebase assistant, modified
    public void download(View view) {
        Toast.makeText(GalleryActivity.this, "Download Clicked", Toast.LENGTH_SHORT).show();

        try{
            final File localFile = File.createTempFile("temp", "MOV");
            localFile.deleteOnExit();
            String path = specific_video_uri.getPath();
            //videoRef = downloadRef.child(path);
            //set videoRef - which a storageReference object, with the directory "videos"
            //concatinated to the
            videoRef = storageRef.child("/videos/");
            videoRef = videoRef.child("YphMkrW7wHSCN7EAbf3H01Uhr892/");
            videoRef = videoRef.child("136");
            String video_ref_path = videoRef.getPath();

            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(GalleryActivity.this, "Download Success!!!", Toast.LENGTH_SHORT).show();

                    stream_video.setVideoURI(Uri.fromFile(localFile));
                    stream_video.start();
                    localFile.delete();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(GalleryActivity.this, "Download Failure" + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("In download", "Failure: " + exception.getLocalizedMessage() );
                }
            });
        } catch (Exception e) {
            Toast.makeText(GalleryActivity.this, "Download failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d("in download()", "Failure: " + e.getLocalizedMessage());
        }
    }


    //storage url: gs://registrationwithgallery.appspot.com

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        videoUri = data.getData();
        String filename_string = getId(videoUri);
        //videoRef = storageRef.child(global_uid);
        videoRef = storageRef.child("/videos/" + global_uid + "/" + filename_string);

        if (resultCode == RESULT_OK) {

            if (requestCode == UPLOAD_REQUEST) {
                videoRef.putFile(videoUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("GalleryActivity", "Upload failure:" + e.getLocalizedMessage());
                        Toast.makeText(GalleryActivity.this,
                                "Upload failed: " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("GalleryActivity", "Upload Success");
                                Toast.makeText(GalleryActivity.this, "Upload complete",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            } else if (requestCode == DOWNLOAD_REQUEST){
                Toast.makeText(GalleryActivity.this, "Download starting", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(GalleryActivity.this, "resultCode Not OK", Toast.LENGTH_SHORT).show();
        }
    }

    public void openGallery() {
        Toast.makeText(GalleryActivity.this, "Gallery Clicked", Toast.LENGTH_SHORT).show();
        //Intent creates a specific change to a different activity.
        Intent videoGalleryIntent = new Intent(Intent.ACTION_PICK);

        File videoDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        Uri temp = null;

        videoGalleryIntent.setDataAndType(temp, "video/*");

        Log.d("GalleryActivity" , "******BEFORE storageRef is initialized in openGallery*******");

        //recieve something back from this result
        startActivityForResult(videoGalleryIntent, UPLOAD_REQUEST);
    }

    public String getId(Uri uri) {
        String path = uri.getPath();
        String id = path.substring(path.lastIndexOf("/") + 1);
        return id;
    }




//closing bracket
}
