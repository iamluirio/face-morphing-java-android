package com.example.dlibandroidfacelandmark;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat6;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraReal extends AppCompatActivity {
    DLibResult dLibResult;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    Mat firstImage = null;
    Mat secondImage = null;

    MatOfKeyPoint firstKeyPoints = null;
    List<KeyPoint> keyPointList1 = null;
    List<KeyPoint> extendedKeyPoints1 = null;
    MatOfKeyPoint extendedKeyPointsMat1 = null;
    KeyPoint[] FkeyPoints = null;
    ArrayList<Point> FarrayOfPoints = null;
    MatOfPoint2f firstMatOfPoint2f = null;
    org.opencv.core.Point[] firstM2F = null;
    MatOfKeyPoint secondKeyPoints = null;
    List<KeyPoint> keyPointList2 = null;
    List<KeyPoint> extendedKeyPoints2 = null;
    MatOfKeyPoint extendedKeyPointsMat2 = null;
    KeyPoint[] SkeyPoints = null;
    ArrayList<org.opencv.core.Point> SarrayOfPoints = null;
    MatOfPoint2f secondMatOfPoint2f = null;
    org.opencv.core.Point[] secondM2F = null;

    MatOfKeyPoint mediaKeyPoints = null;
    KeyPoint[] FSkeyPoints = null;
    ArrayList<org.opencv.core.Point> FSarrayOfPoints = null;
    MatOfPoint2f fsMatOfPoint2f = null;
    MatOfFloat6 triangleList = null;
    float[] triangles;

    Rect rect = null;

    ArrayList<org.opencv.core.Point> TarrayOfPoints = null;
    MatOfPoint2f thirdMatOfPoint2f = null;
    org.opencv.core.Point[] thirdM2F = null;

    Mat img1Rect = null;
    Mat img2Rect = null;

    Mat imgRect = null;

    Mat warpImage1 = null;
    Mat warpImage2 = null;
    Mat imgMorph = null;

    MatOfInt vertices = null;

    Bitmap img1;
    Bitmap img2;


    ImageView imageViewRealCamera1;
    ImageView imageViewRealCamera2;

    Button buttonRealCamera;

    private TextureView textureView;

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realcamera_activity);

        // Inizializza la tua classe DLibResult con il nome del file del modello
        dLibResult = new DLibResult(this, "shape_predictor_68_face_landmarks_GTX.dat");

        imageViewRealCamera1 = findViewById(R.id.imageViewRealCamera1);
        imageViewRealCamera2 = findViewById(R.id.imageViewRealCamera2);

        textureView = findViewById(R.id.textureViewReal);

        buttonRealCamera = findViewById(R.id.buttonRealCameraMorphing);
        buttonRealCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewRealCamera1.setDrawingCacheEnabled(true);
                imageViewRealCamera1.buildDrawingCache();
                img1 = imageViewRealCamera1.getDrawingCache();

                firstImage = new Mat();
                secondImage = new Mat();

                initCamera();
                loadFirstImage();
            }
        });
    }

    private void initCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Ottieni l'ID della fotocamera posteriore
            String cameraId = cameraManager.getCameraIdList()[0];

            // Apri la fotocamera
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    cameraDevice.close();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            // Configura l'ImageReader per ottenere i frame della fotocamera
            imageReader = ImageReader.newInstance(textureView.getWidth(), textureView.getHeight(),
                    ImageFormat.YUV_420_888, 2);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

            // Configura la richiesta di cattura per visualizzare l'anteprima
            final CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            captureRequestBuilder.addTarget(imageReader.getSurface());

            // Crea la sessione di cattura
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (cameraDevice == null) {
                                return;
                            }

                            cameraCaptureSession = session;
                            try {
                                // Avvia la visualizzazione dell'anteprima
                                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                session.setRepeatingRequest(captureRequestBuilder.build(),
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // Ottieni l'immagine dalla fotocamera
                    Image image = reader.acquireLatestImage();
                    if (image != null) {
                        // Converti l'immagine in un array di byte e visualizzala nell'ImageView
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);

                        // Puoi ora manipolare l'array di byte o visualizzarlo nell'ImageView
                        // (esempio: imageView.setImageBitmap(...) )

                        // Rilascia l'immagine per evitare memory leaks
                        image.close();
                    }
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            initCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            initCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void loadFirstImage() {
        firstKeyPoints = new MatOfKeyPoint();

        // Processa la prima immagine
        dLibResult.processFrame(img1);
        ArrayList<Face> faces = dLibResult.getFaces();

        // Itera attraverso le facce
        for (Face face : faces) {
            // Ottieni le posizioni facciali per ogni faccia
            ArrayList<Position> facePositions = face.getPositions();

            // Creare un array di keypoints per la faccia corrente
            KeyPoint[] keypointsArray = new KeyPoint[facePositions.size()];

            // Creare i KeyPoints e aggiungerli all'array
            for (int i = 0; i < facePositions.size(); i++) {
                Position position = facePositions.get(i);
                keypointsArray[i] = new KeyPoint((float) position.getX(), (float) position.getY(), 5);
            }
            // Aggiungere gli array di KeyPoints alla MatOfKeyPoint
            firstKeyPoints.fromArray(keypointsArray);
        }
    }

    private void loadSecondImage() {
        secondKeyPoints = new MatOfKeyPoint();

        // Processa la prima immagine
        dLibResult.processFrame(img1);
        ArrayList<Face> faces = dLibResult.getFaces();

        // Itera attraverso le facce
        for (Face face : faces) {
            // Ottieni le posizioni facciali per ogni faccia
            ArrayList<Position> facePositions = face.getPositions();

            // Creare un array di keypoints per la faccia corrente
            KeyPoint[] keypointsArray = new KeyPoint[facePositions.size()];

            // Creare i KeyPoints e aggiungerli all'array
            for (int i = 0; i < facePositions.size(); i++) {
                Position position = facePositions.get(i);
                keypointsArray[i] = new KeyPoint((float) position.getX(), (float) position.getY(), 5);
            }
            // Aggiungere gli array di KeyPoints alla MatOfKeyPoint
            firstKeyPoints.fromArray(keypointsArray);
        }
    }




}
