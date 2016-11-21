package me.sunyfusion.bdsp.fields;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.adapter.UniqueAdapter;

/**
 * Created by deisingj1 on 11/14/2016.
 */

public class Camera implements Field {

    public static final int containerId = R.id.cameraView;
    public static final int labelId = R.id.cameraLabel;
    public static final int valueId = R.id.cameraValue;

    Context context;
    private String label = "";
    private View thisView;
    public static Uri photoURI;
    public static String photoLabel;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public Camera(Context c, String l) {
        context = c;
        label = l;
    }

    public String getLabel() { return label; }

    public View getView() {
        return thisView;
    }

    public void clearField() {
        ((ImageView) thisView.findViewById(valueId)).setImageResource(R.drawable.ic_add_a_photo_black_24dp);
    }

    public boolean makeField(UniqueAdapter.ViewHolder holder) {
        thisView = holder.mView.findViewById(containerId);
        holder.mView.findViewById(containerId).setVisibility(View.VISIBLE);
        TextView t = (TextView) holder.mView.findViewById(labelId);
        t.setText(label);
        ImageView p = (ImageView) holder.mView.findViewById(valueId);
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        return false;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = Utils.getDateString("yyyyMMdd_HHmmss");
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        BdspRow.getInstance().put(label,
                //TODO Make assetTest set dynamically
                "http://sunyfusion.me/projects/" + "assetTest" + "/" + image.getName()
        );
        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(context,
                        "me.sunyfusion.bdsp.fileprovider",
                        photoFile);
                photoLabel = this.label;
                Log.d("photoURI", photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
