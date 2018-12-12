package gmd.socialmediaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Uploader extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_LOAD_VIDEO = 2;
    private Uri uriToPost;
    private String rightUrl;
    private String uploadedUrl;
    private String type;
    String imgDecodableString;
    VideoView videoField;
    private Handler handler;
    private String serverUrl;
    public FirebaseAuth auth;
    public FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);
        handler = new Handler();
        this.serverUrl = " http://mobv.mcomputing.eu/upload/v/";
        this.auth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

    }

    public void loadImagefromGallery(View view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select picture from gallery",
                "Select video from gallery" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    Intent galleryIntent;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // Start the Intent
                                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                                break;
                            case 1:
                                galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(galleryIntent, RESULT_LOAD_VIDEO);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    void buttonToogle() {
        Button loadButton = (Button) findViewById(R.id.buttonLoadPicture);
        loadButton.setVisibility(View.GONE);
        Button uploadButton = (Button) findViewById(R.id.uploadPictureButton);
        uploadButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if ((requestCode == RESULT_LOAD_IMG || requestCode == RESULT_LOAD_VIDEO)  && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data



                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                this.rightUrl = cursor.getString(column_index);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                String fileType = imgDecodableString.substring(imgDecodableString.lastIndexOf("."));
                if(fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals(".png")) {
                    cursor.close();
                    ImageView imgView = (ImageView) findViewById(R.id.imgView);
                    // Set the Image in ImageView after decoding the String
                    imgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    Log.v("URL: ", selectedImage.getPath());
                    this.uriToPost = selectedImage;
                    this.type = "image";
                    buttonToogle();
                } else if(fileType.equals(".mp4")) {
                    ImageView imgView = (ImageView) findViewById(R.id.imgView);
                    imgView.setVisibility(View.GONE);
                    VideoView videoView = (VideoView) findViewById(R.id.videoView);
                    videoView.setVisibility(View.VISIBLE);
                    Uri video = data.getData();
                    videoView.setVideoURI(video);
                    videoView.start();
                    this.uriToPost = video;
                    this.type = "video";
                    buttonToogle();

                }else {
                    Toast.makeText(this, "Something went wrong",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public static Uri handleImageUri(Uri uri) {
        if (uri.getPath().contains("content")) {
            Pattern pattern = Pattern.compile("(content://media/.*\\d)");
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        }
        return uri;
    }

    public void postFileToServer(View view) {

        final String type = this.type;
        final Context c = this;
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, "http://mobv.mcomputing.eu/upload/index.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Log.v("This is response:", response);
                            doPostToDb(jObj.getString("message"), type );
                            Button uploadButton = (Button) findViewById(R.id.uploadPictureButton);
                            uploadButton.setVisibility(View.GONE);
                            Toast.makeText(c, "Upload successfull",
                                    Toast.LENGTH_LONG).show();

                            Intent i = new Intent(c, MainActivity.class);
                            startActivity(i);

                        } catch (JSONException e) {
                            Log.d("Response", response);
                            Toast.makeText(c, "You haven't picked Image",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error);
            }
        });
        smr.addStringParam("param string", " data text");
        smr.addFile("upfile", this.rightUrl);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr);
    }

    void doPostToDb(String imgUrl, String type) {
        Post p = new Post();
        if(type.equals("image")) {
            p = new Post(type, "", this.prefixServerUrl(imgUrl), this.auth.getCurrentUser().getDisplayName().toString(), Timestamp.now() ,this.auth.getCurrentUser().getUid().toString());
        } else if(type.equals("video")) {
            p = new Post(type, this.prefixServerUrl(imgUrl), "", this.auth.getCurrentUser().getDisplayName().toString(),Timestamp.now() ,this.auth.getCurrentUser().getUid().toString() );

        }
        mFirestore.collection("posts")
                .add(p)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //TODO added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO failed to add
                    }
                });
    }

    private String prefixServerUrl(String url) {
        return  this.serverUrl + url;
    }

}
