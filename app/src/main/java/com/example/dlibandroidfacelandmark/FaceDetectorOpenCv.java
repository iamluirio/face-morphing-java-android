package com.example.dlibandroidfacelandmark;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class FaceDetectorOpenCv {
    private Context AppContext;
    private BaseLoaderCallback baseLoaderCallback;
    private CascadeClassifier faceDetector;

    FaceDetectorOpenCv(Context AppContext) throws IOException {
        this.AppContext = AppContext;
        setupOpenCv();
    }

    private void setupOpenCv() throws IOException {
        setupBaseLoader();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(
                    OpenCVLoader.OPENCV_VERSION_3_4_0,
                    this.AppContext,
                    this.baseLoaderCallback
            );
        } else {
            baseLoaderCallback.onManagerConnected(
                    LoaderCallbackInterface.SUCCESS
            );
        }
    }

    private void setupBaseLoader() {
        baseLoaderCallback = new BaseLoaderCallback(AppContext) {
            @Override
            public void onManagerConnected(int status) throws IOException {
                super.onManagerConnected(status);
                if (status == LoaderCallbackInterface.SUCCESS)
                    loadHaarCascade();
            }
        };
    }

    private InputStream getInputStream() {
        return this.AppContext
                .getResources()
                .openRawResource(
                        R.raw.haarcascade_frontalface_alt2
                );
    }

    private File getCascadeDirectory() {
        return this.AppContext
                .getDir(
                        "cascade",
                        Context.MODE_PRIVATE
                );
    }

    private File getCascadeFile(File cascadeDir) {
        String fileName = this.AppContext
                .getString(R.string.haarcascade_file);
        return new File(cascadeDir, fileName);
    }

    private void loadHaarCascade() throws IOException{
        InputStream is   = getInputStream();
        File cascadeDir  = getCascadeDirectory();
        File cascadeFile = getCascadeFile(cascadeDir);
        OutputStream fos = new FileOutputStream(cascadeFile);

        byte[] buffer = new byte[4096]; int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1)
            fos.write(buffer, 0, bytesRead);
        is.close(); fos.close();

        this.faceDetector = new CascadeClassifier(
                cascadeFile.getAbsolutePath()
        );

        if (faceDetector.empty())
            faceDetector = null;
        else
            cascadeDir.delete();
    }

    public static Mat bitmap2Mat(Bitmap image) {
        Mat mat = new Mat();
        Bitmap bmp32 = image.copy(
                Bitmap.Config.ARGB_8888,
                true
        );
        Utils.bitmapToMat(bmp32, mat);
        return mat;
    }

    public static Bitmap mat2Bitmap(Mat mat) {
        Mat tmp = new Mat(mat.size(), CvType.CV_8U, new Scalar(4));
        Imgproc.cvtColor(mat, tmp, Imgproc.COLOR_GRAY2RGBA);
        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }


    public ArrayList<Rect> detectFaces(Bitmap image) {
        MatOfRect faceDetections = new MatOfRect();

        faceDetector.detectMultiScale(
                bitmap2Mat(image),
                faceDetections
        );

        ArrayList<Rect> rects = new ArrayList<>();
        Collections.addAll(rects, faceDetections.toArray());
        return rects;
    }
}
