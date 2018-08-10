package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class PhotoHelper {

    private File photoFile;
    public String photoFileName = "photo";
    private Context context;
    Uri fileProvider;
    String imagePath;
    File resizedFile;
    int SOME_WIDTH = 480;

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    public PhotoHelper(Context c) {
        context = c;
    }

    public void getDefaultPropic() {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_group_default);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        writeStreamToFile(bytes);
    }

    public Intent takePhoto() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        photoFileName = "JPEG_" + timeStamp;
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher

        fileProvider = FileProvider.getUriForFile(context, "com.antisocialmedia.fileprovider", photoFile);
        if (Build.MODEL.equals("Pixel 2")) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        }

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            return intent;
        } else {
            Log.d("PhotoHelper", "Photo wasn't taken");
        }
        return null;
    }

    public Intent uploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            return intent;
        } else {
            Log.d("PhotoHelper", "Photo wasn't uploaded");
        }
        return null;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GroupFeedFragment");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("PhotoHelper", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    public Bitmap handleUploadedImage(Uri photoUri) {
        Bitmap selectedImage = null;
        Bitmap resizedBitmap = null;
        try {
            selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            resizedBitmap = scaleToFitWidth(selectedImage, SOME_WIDTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 40, stream);

        Toast.makeText(context, "Picture uploaded!", Toast.LENGTH_SHORT).show();

        writeStreamToFile(stream);

        return resizedBitmap;
    }

    private void writeStreamToFile(ByteArrayOutputStream stream) {
        File resizedUri = getPhotoFileUri(photoFileName + "_resized.jpg");
        imagePath = resizedUri.getPath();
        resizedFile = new File(imagePath);

        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Write the bytes of the bitmap to file
        try {
            fos.write(stream.toByteArray());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Bitmap handleTakenPhoto(Intent data) {
        // by this point we have the camera photo on disk
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        if (takenImage == null) {
            if (data.getData() != null) {
                try {
                    takenImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                takenImage = (Bitmap) data.getExtras().get("data");
            }
        } else {
            takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
        }

        // RESIZE BITMAP, see section below
        // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
        Bitmap resizedBitmap = scaleToFitWidth(takenImage, SOME_WIDTH);
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)

        writeStreamToFile(bytes);

        Log.d("CameraActivity", "loading successful");
        // Load the taken image into a preview
        return resizedBitmap;
    }

    public ParseFile grabImage() {
        if (resizedFile != null) {
            final ParseFile parseFile = new ParseFile(resizedFile);
            return parseFile;
        }
        return null;
    }

    public static void displayPropic(ParseUser user, ImageView imageView, Context context) {
        ParseFile propic = user.getParseFile("profileImage");
        if (propic != null) {
            Glide.with(context)
                    .load(propic.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        } else {
            String propicUrl = user.getString("propicUrl");
            if (propicUrl != null) {
                Glide.with(context)
                        .load(propicUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_prof_default);
            }
        }
    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}
