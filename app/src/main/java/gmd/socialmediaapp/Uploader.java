package gmd.socialmediaapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class Uploader extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_LOAD_VIDEO = 2;
    String imgDecodableString;
    VideoView videoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);
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
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                String fileType = imgDecodableString.substring(imgDecodableString.lastIndexOf("."));
                if(fileType.equals(".jpg") || fileType.equals(".jpeg") || fileType.equals("png")) {
                    cursor.close();
                    ImageView imgView = (ImageView) findViewById(R.id.imgView);
                    // Set the Image in ImageView after decoding the String
                    imgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    Log.v("URL: ", selectedImage.getEncodedPath());

                 buttonToogle();
                } else if(fileType.equals(".mp4")) {
                    ImageView imgView = (ImageView) findViewById(R.id.imgView);
                    imgView.setVisibility(View.GONE);
                    VideoView videoView = (VideoView) findViewById(R.id.videoView);
                    videoView.setVisibility(View.VISIBLE);
                    Uri video = data.getData();
                    videoView.setVideoURI(video);
                    videoView.start();
                    buttonToogle();

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

}
