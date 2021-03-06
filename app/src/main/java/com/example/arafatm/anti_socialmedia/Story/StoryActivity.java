package com.example.arafatm.anti_socialmedia.Story;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.OnSwipeTouchListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StoryActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_PERMISSION_RESULT = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY = 100;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private TextureView mTextureView;
    private int mCaptureState = STATE_PREVIEW;
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mVideoSize;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private ImageButton upload;
    private ImageButton switchCamera;
    private ImageView captureButton;
    private ImageButton closeTView;
    private boolean mIsRecording = false;
    private boolean mIsTimeLapsed = false;
    private File mVideoFolder;
    private String mVideoFileName;
    private File mImageFolder;
    private String mImageFileName;
    private boolean frontCameraEnabled = false;
    private int mTotalRotation;
   private ProgressBar progressBar;
    private MediaRecorder mMediaRecorder;
    private Chronometer mChronometer;
    private CameraCaptureSession mPreviewCaptureSession;
    private CameraDevice mCameraDevice;
    private Size mImageSize;
    private ImageReader mImageReader;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    public static byte[] compressedImageByte;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    /*initializes the textureView*/
    private TextureView.SurfaceTextureListener msurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    /*Initializes the camera device*/
    private CameraDevice.StateCallback mCameraDeviceStateCallBack = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice cameraDevice) { // this gets called when the camera is on
            mCameraDevice = cameraDevice;
            if (mIsRecording) {
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.start(); //starts the timer
            } else {
                startPreview();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    /*this methods helps to determine the current state of the camera*/
    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void process(CaptureResult captureResult) {
            switch (mCaptureState) {
                case STATE_PREVIEW:
                    //Do nothing
                    break;
                case STATE_WAIT_LOCK:
                    mCaptureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        startStillCaptureRequest();
                    }
                    break;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            mBackgroundHandler.post(new ImageSaver(imageReader.acquireLatestImage()));
        }
    };

    /*This class creates a savable images from the camera*/
    private class ImageSaver implements Runnable {
        private final Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(mImageFileName);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();

                Bitmap _bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String imageFilePath = tempFileImage(getApplicationContext(), _bitmap, "name");

                compressedImageByte = convertImageToBytes(_bitmap);
                //naviagtes to the preview page
                Intent intent = new Intent(StoryActivity.this, PreviewStoryActivity.class);
                //send the just taken picture
                intent.putExtra("imagePath", imageFilePath);
                intent.putExtra("dataType", "picture");
                startActivity(intent);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private byte[] convertImageToBytes(Bitmap bitmap) {
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();
        return data;
    }

    //creates a temporary file and return the absolute file path
    public static String tempFileImage(Context context, Bitmap bitmap, String name) {
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");
        OutputStream os;

        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }

    /*compares the various screen sizes*/
    private static class CompareSizeArea implements Comparator<Size> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(Size rhs, Size lhs) {
            return Long.signum((long) rhs.getWidth() * lhs.getHeight());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        createVideoFolder();
        createImageFolder();
        mMediaRecorder = new MediaRecorder();
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mTextureView = (TextureView) findViewById(R.id.textureView);
        upload = (ImageButton) findViewById(R.id.iv_upload);
        captureButton = (ImageView) findViewById(R.id.iv_take);
        switchCamera = (ImageButton) findViewById(R.id.ib_switch_cam);
        closeTView = (ImageButton) findViewById(R.id.iv_close);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);

        closeTView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                closeCamera();
                stopBackgroundThread();
                Intent intent = new Intent(StoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        mTextureView.setOnTouchListener(new OnSwipeTouchListener(StoryActivity.this) {
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                switchCamera();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() { //when the gallery icon is clicked
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        captureButton.setOnLongClickListener(new View.OnLongClickListener() { //when the camera icon is clicked to record a video
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onLongClick(View view) {
                mIsTimeLapsed = true;
                captureButton.setImageResource(R.drawable.recorder_mode); //switches the icon to recording mode
                checkWriteStoragePermission();
                return true;
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() { //when the camera icon is clicked to take pictures
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (mIsRecording || mIsTimeLapsed) {
                    mChronometer.stop();
                    mChronometer.setVisibility(View.INVISIBLE);
                    mIsRecording = false;
                    mIsTimeLapsed = false;
                    captureButton.setImageResource(R.drawable.record);
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();

                    Uri videoUri = Uri.fromFile(videoFile);
                    byte[] videoByte = new byte[0];
                    try {
                        videoByte = convertToByte();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //navigates to preview activity
                    Intent intent = new Intent(StoryActivity.this, PreviewStoryActivity.class);
                    //pass the video uri and byte to preview activity
                    intent.putExtra("dataType", "video");
                    intent.putExtra("videoPath", videoUri.toString());
                    intent.putExtra("byteData", videoByte);
                    startActivity(intent);
                } else {
                    lockFocus();
                }
            }
        });
    }

    public byte[] convertToByte() throws IOException {
        byte[] bytesArray = new byte[(int) videoFile.length()];
        FileInputStream fis = new FileInputStream(videoFile);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();
        return bytesArray;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void switchCamera() {
        closeCamera();
        stopBackgroundThread();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            if (frontCameraEnabled) {
                frontCameraEnabled = false;
                setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                frontCameraEnabled = true;
                setupFrontCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }

            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(msurfaceTextureListener);
        }
    }

    /*when the story pages loads*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();

        if (mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(msurfaceTextureListener);
        }
    }

    /*Enables full screen display*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("myVideo");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler((mBackgroundHandlerThread.getLooper()));
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        closeCamera();
        startBackgroundThread();
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotationWidth = width;
                int rotationHeight = height;
                if (swapRotation) {
                    rotationWidth = width;
                    rotationHeight = height;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotationWidth, rotationHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotationWidth, rotationHeight);
                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotationWidth, rotationHeight);
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupFrontCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != cameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotationWidth = width;
                int rotationHeight = height;
                if (swapRotation) {
                    rotationWidth = width;
                    rotationHeight = height;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotationWidth, rotationHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotationWidth, rotationHeight);
                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotationWidth, rotationHeight);
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallBack, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Video app required acces to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallBack, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecord() {
        try {
            setupMeidaRecorder();
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.setRepeatingRequest(
                                mCaptureRequestBuilder.build(), null, null);

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Captures the picture upon request*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startStillCaptureRequest() {
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mTotalRotation);

            CameraCaptureSession.CaptureCallback stillCaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                    try {
                        createImageFileName();

                        File im = imageFile;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    mPreviewCaptureSession = cameraCaptureSession;
                    try {
                        mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "Unable to seturp camera preview", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }

            if (bigEnough.size() > 0) {
                return Collections.min(bigEnough, new CompareSizeArea());
            } else {
                return choices[0];
            }
        }
        return null;
    }

    /*takes care of approving various permissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera cannot run without permission", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera cannot have audio without permission", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_WRITE_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile, "myVideo");
        if (!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
    }

    private File videoFile;

    private File createVideoFileName() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" + timeStamp + "_";
        videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        mVideoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageFile, "myImages");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
    }

    private File imageFile;

    private File createImageFileName() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timeStamp + "_";
        imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        mImageFileName = imageFile.getAbsolutePath();
        return imageFile;
    }

    /*checks for permission and starts the video recording*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mMediaRecorder.start();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.start();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "app needs permission to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION_RESULT);
            }
        } else {

            try {
                createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startRecord();
            mMediaRecorder.start();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupMeidaRecorder() throws IOException {
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mTotalRotation);
        mMediaRecorder.prepare();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void lockFocus() {
        mCaptureState = STATE_WAIT_LOCK;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);

        try {
            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), mPreviewCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void uploadImage() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY);
        }
    }

    /*gets the uploaded image and send it to preview page for preview*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    // Do something with the photo based on Uri
                    Bitmap selectedImage = null;
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //creates the temporary file and gets the path
                    String filePath = tempFileImage(getApplicationContext(), selectedImage, "name");
                    //naviagtes to the preview page
                    Intent intent = new Intent(StoryActivity.this, PreviewStoryActivity.class);
                    //send the just taken picture
                    intent.putExtra("imagePath", filePath);
                    intent.putExtra("result", "picture");
                    startActivity(intent);
                }
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
