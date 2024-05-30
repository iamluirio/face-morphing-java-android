package com.example.dlibandroidfacelandmark;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat6;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Subdiv2D;
import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test extends AppCompatActivity{
    ImageView imageViewTest1;
    ImageView imageViewTest2;
    ImageView imageViewTest3;

    Button buttonTest;

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

    DLibResult dLibResult;

    Bitmap img1;
    Bitmap img2;

    SeekBar seekBarAlpha;

    TextView textViewAlpha;

    double alpha = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity);

        // Inizializza la tua classe DLibResult con il nome del file del modello
        dLibResult = new DLibResult(this, "shape_predictor_68_face_landmarks_GTX.dat");

        imageViewTest1 = (ImageView) findViewById(R.id.imageViewTest1);
        imageViewTest2 = (ImageView) findViewById(R.id.imageViewTest2);
        imageViewTest3 = (ImageView) findViewById(R.id.imageViewTest3);
        buttonTest = findViewById(R.id.buttonTest);

        seekBarAlpha = findViewById(R.id.seekBarAlpha);
        textViewAlpha = findViewById(R.id.textViewAlpha);

        seekBarAlpha.setProgress((int) (alpha * 100));
        textViewAlpha.setText(getString(R.string.label_value, String.valueOf(alpha)));

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewTest1.setDrawingCacheEnabled(true);
                imageViewTest1.buildDrawingCache();
                img1 = imageViewTest1.getDrawingCache();

                imageViewTest2.setDrawingCacheEnabled(true);
                imageViewTest2.buildDrawingCache();
                img2 = imageViewTest2.getDrawingCache();

                firstImage = new Mat();
                secondImage = new Mat();

                Utils.bitmapToMat(img1, firstImage);
                Utils.bitmapToMat(img2, secondImage);

                loadFirstImage();
                loadSecondImage();

                draw_delaunay();
                generate_morphImage();
                draw_triangles();

                buttonTest.setVisibility(View.INVISIBLE);

                seekBarAlpha.setVisibility(View.VISIBLE);
                textViewAlpha.setVisibility(View.VISIBLE);

                seekBarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        alpha = progress / 100.0;
                        textViewAlpha.setText(getString(R.string.label_value, String.valueOf(alpha)));
                        generate_morphImage();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });
    }

    private void loadFirstImage() {
        firstKeyPoints = new MatOfKeyPoint();

        // Process the first image
        dLibResult.processFrame(img1);
        ArrayList<Face> faces = dLibResult.getFaces();

        // Iterare attraverso le facce
        for (Face face : faces) {
            // Ottenere le posizioni facciali per ogni faccia
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

        // Adding points of angles of image and their middle
        keyPointList1 = firstKeyPoints.toList();

        Point topLeft = new Point(0, 0);
        Point topRight = new Point(firstImage.cols() - 1, 0);
        Point bottomLeft = new Point(0, firstImage.rows() - 1);
        Point bottomRight = new Point(firstImage.cols() - 1, firstImage.rows() - 1);

        Point middleTop = new Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2);
        Point middleBottom = new Point((bottomLeft.x + bottomRight.x) / 2, (bottomLeft.y + bottomRight.y) / 2);
        Point middleLeft = new Point((topLeft.x + bottomLeft.x) / 2, (topLeft.y + bottomLeft.y) / 2);
        Point middleRight = new Point((topRight.x + bottomRight.x) / 2, (topRight.y + bottomRight.y) / 2);

        extendedKeyPoints1 = new ArrayList<>(keyPointList1);
        extendedKeyPoints1.add(new KeyPoint((float) topLeft.x, (float) topLeft.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) topRight.x, (float) topRight.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) bottomLeft.x, (float) bottomLeft.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) bottomRight.x, (float) bottomRight.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) middleTop.x, (float) middleTop.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) middleBottom.x, (float) middleBottom.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) middleLeft.x, (float) middleLeft.y, 1.0f));
        extendedKeyPoints1.add(new KeyPoint((float) middleRight.x, (float) middleRight.y, 1.0f));

        extendedKeyPointsMat1 = new MatOfKeyPoint();
        extendedKeyPointsMat1.fromList(extendedKeyPoints1);

        // Creating MatOfPoint2f
        FkeyPoints = extendedKeyPointsMat1.toArray();

        FarrayOfPoints = new ArrayList<>();

        for(int i = 0; i < FkeyPoints.length; i++) {
            FarrayOfPoints.add(FkeyPoints[i].pt);
        }
        firstMatOfPoint2f = new MatOfPoint2f();
        firstMatOfPoint2f.fromList(FarrayOfPoints);

        // Drawing keypoints on image
        //Features2d.drawKeypoints(firstImage, firstKeyPoints, firstImage);

        // Display the resultBitmap in imageViewTest1
        Bitmap resultBitmap = Bitmap.createBitmap(firstImage.cols(), firstImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(firstImage, resultBitmap);
        imageViewTest1.setImageBitmap(resultBitmap);
    }

    private void loadSecondImage() {
        secondKeyPoints = new MatOfKeyPoint();

        // Process the second image
        dLibResult.processFrame(img2);
        ArrayList<Face> faces = dLibResult.getFaces();

        // Iterare attraverso le facce
        for (Face face : faces) {
            // Ottenere le posizioni facciali per ogni faccia
            ArrayList<Position> facePositions = face.getPositions();

            // Creare un array di keypoints per la faccia corrente
            KeyPoint[] keypointsArray = new KeyPoint[facePositions.size()];

            // Creare i KeyPoints e aggiungerli all'array
            for (int i = 0; i < facePositions.size(); i++) {
                Position position = facePositions.get(i);
                keypointsArray[i] = new KeyPoint((float) position.getX(), (float) position.getY(), 5);
            }
            // Aggiungere gli array di KeyPoints alla MatOfKeyPoint
            secondKeyPoints.fromArray(keypointsArray);
        }

        // Adding points of angles of image and their middle
        keyPointList2 = secondKeyPoints.toList();

        Point topLeft = new Point(0, 0);
        Point topRight = new Point(secondImage.cols() - 1, 0);
        Point bottomLeft = new Point(0, secondImage.rows() - 1);
        Point bottomRight = new Point(secondImage.cols() - 1, secondImage.rows() - 1);

        Point middleTop = new Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2);
        Point middleBottom = new Point((bottomLeft.x + bottomRight.x) / 2, (bottomLeft.y + bottomRight.y) / 2);
        Point middleLeft = new Point((topLeft.x + bottomLeft.x) / 2, (topLeft.y + bottomLeft.y) / 2);
        Point middleRight = new Point((topRight.x + bottomRight.x) / 2, (topRight.y + bottomRight.y) / 2);

        extendedKeyPoints2 = new ArrayList<>(keyPointList2);
        extendedKeyPoints2.add(new KeyPoint((float) topLeft.x, (float) topLeft.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) topRight.x, (float) topRight.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) bottomLeft.x, (float) bottomLeft.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) bottomRight.x, (float) bottomRight.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) middleTop.x, (float) middleTop.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) middleBottom.x, (float) middleBottom.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) middleLeft.x, (float) middleLeft.y, 1.0f));
        extendedKeyPoints2.add(new KeyPoint((float) middleRight.x, (float) middleRight.y, 1.0f));

        extendedKeyPointsMat2 = new MatOfKeyPoint();
        extendedKeyPointsMat2.fromList(extendedKeyPoints2);

        // Creating MatOfPoint2f
        SkeyPoints = extendedKeyPointsMat2.toArray();

        SarrayOfPoints = new ArrayList<>();

        for(int i = 0; i < SkeyPoints.length; i++) {
            SarrayOfPoints.add(SkeyPoints[i].pt);
        }
        secondMatOfPoint2f = new MatOfPoint2f();
        secondMatOfPoint2f.fromList(SarrayOfPoints);

        // Drawing keypoints on image
        //Features2d.drawKeypoints(secondImage, secondKeyPoints, secondImage);

        // Display the resultBitmap in imageViewTest2
        Bitmap resultBitmap = Bitmap.createBitmap(secondImage.cols(), secondImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(secondImage, resultBitmap);
        imageViewTest2.setImageBitmap(resultBitmap);
    }

    private void draw_delaunay() {
        KeyPoint[] arrayKeyPoints1 = extendedKeyPointsMat1.toArray();
        KeyPoint[] arrayKeyPoints2 = extendedKeyPointsMat2.toArray();

        int numPunti = arrayKeyPoints1.length;
        KeyPoint[] arrayMediaKeyPoints = new KeyPoint[numPunti];

        for (int i = 0; i < numPunti; i++) {
            float xMedia = (float) ((arrayKeyPoints1[i].pt.x + arrayKeyPoints2[i].pt.x) / 2.0);
            float yMedia = (float) ((arrayKeyPoints1[i].pt.y + arrayKeyPoints2[i].pt.y) / 2.0);
            float sizeMedia = (float) ((arrayKeyPoints1[i].size + arrayKeyPoints2[i].size) / 2.0);
            float angleMedia = (float) ((arrayKeyPoints1[i].angle + arrayKeyPoints2[i].angle) / 2.0);
            float responseMedia = (float) ((arrayKeyPoints1[i].response + arrayKeyPoints2[i].response) / 2.0);
            int octaveMedia = (arrayKeyPoints1[i].octave + arrayKeyPoints2[i].octave) / 2;

            arrayMediaKeyPoints[i] = new KeyPoint(xMedia, yMedia, sizeMedia, angleMedia, responseMedia, octaveMedia);
        }

        mediaKeyPoints = new MatOfKeyPoint();
        mediaKeyPoints.fromArray(arrayMediaKeyPoints);

        Size size = firstImage.size();
        rect = new Rect(0, 0, (int) size.width, (int) size.height);
        Subdiv2D subdiv2D = new Subdiv2D(rect);

        FSkeyPoints = mediaKeyPoints.toArray();
        FSarrayOfPoints = new ArrayList<>();

        for(int i = 0; i < FSkeyPoints.length; i++) {
            FSarrayOfPoints.add(FSkeyPoints[i].pt);
        }

        fsMatOfPoint2f = new MatOfPoint2f();
        fsMatOfPoint2f.fromList(FSarrayOfPoints);

        subdiv2D.insert(fsMatOfPoint2f);
        triangleList = new MatOfFloat6();
        subdiv2D.getTriangleList(triangleList);

        org.opencv.core.Point[] pt = new org.opencv.core.Point[3];
        triangles = triangleList.toArray();

        for(int i = 0; i < triangles.length; i+=6) {
            pt[0] = new org.opencv.core.Point(Math.round(triangles[i]), Math.round(triangles[i+1]));
            pt[1] = new org.opencv.core.Point(Math.round(triangles[i+2]), Math.round(triangles[i+3]));
            pt[2] = new org.opencv.core.Point(Math.round(triangles[i+4]), Math.round(triangles[i+5]));
            /*
            Imgproc.line(firstImage, pt[0], pt[1], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            Imgproc.line(firstImage, pt[1], pt[2], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            Imgproc.line(firstImage, pt[2], pt[0], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            Imgproc.line(secondImage, pt[0], pt[1], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            Imgproc.line(secondImage, pt[1], pt[2], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            Imgproc.line(secondImage, pt[2], pt[0], new Scalar(255, 0, 0), 3, Imgproc.LINE_AA, 0);
            */
        }
        /*
        Bitmap imageBitmap1 = Bitmap.createBitmap(firstImage.cols(), firstImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(firstImage, imageBitmap1);
        imageViewTest1.setImageBitmap(imageBitmap1);

        Bitmap imageBitmap2 = Bitmap.createBitmap(secondImage.cols(), secondImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(secondImage, imageBitmap2);
        imageViewTest2.setImageBitmap(imageBitmap2);
        */
    }

    private void generate_morphImage() {
        imgMorph = Mat.zeros(firstImage.size(), CvType.CV_32FC3);

        firstImage.convertTo(firstImage, CvType.CV_32F);
        secondImage.convertTo(secondImage, CvType.CV_32F);

        firstM2F = firstMatOfPoint2f.toArray();
        secondM2F = secondMatOfPoint2f.toArray();

        TarrayOfPoints = new ArrayList<>();

        for(int i = 0; i < firstM2F.length; i++) {
            double x, y;
            x = (1 - alpha) * firstM2F[i].x + alpha * secondM2F[i].x;
            y = (1 - alpha) * firstM2F[i].y + alpha * secondM2F[i].y;

            TarrayOfPoints.add(new org.opencv.core.Point(x, y));
        }

        thirdMatOfPoint2f = new MatOfPoint2f();
        thirdMatOfPoint2f.fromList(TarrayOfPoints);
        thirdM2F = thirdMatOfPoint2f.toArray();

        vertices = new MatOfInt();
        vertices = getDelaunayIndexes(triangleList, fsMatOfPoint2f);
        vertices = reshapeVertices(vertices);

        // Applica il morphing per ogni triangolo
        for (int i = 0; i < vertices.rows(); i++) {
            int[] vertexIndexes = new int[3];
            vertices.get(i, 0, vertexIndexes);

            Point[] t1 = {firstM2F[vertexIndexes[0]], firstM2F[vertexIndexes[1]], firstM2F[vertexIndexes[2]]};
            Point[] t2 = {secondM2F[vertexIndexes[0]], secondM2F[vertexIndexes[1]], secondM2F[vertexIndexes[2]]};
            Point[] t = {thirdM2F[vertexIndexes[0]], thirdM2F[vertexIndexes[1]], thirdM2F[vertexIndexes[2]]};

            // Applica la tua funzione morphTriangle per ogni triangolo
            morphTriangle(t1, t2, t);
        }
        imgMorph.convertTo(imgMorph, CvType.CV_8U);
        Bitmap imageBitmap = Bitmap.createBitmap(imgMorph.cols(), imgMorph.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMorph, imageBitmap);
        imageViewTest3.setImageBitmap(imageBitmap);
    }

    private void morphTriangle(Point[] t1, Point[] t2, Point[] t) {
        Rect r1 = Imgproc.boundingRect(new MatOfPoint(t1));
        Rect r2 = Imgproc.boundingRect(new MatOfPoint(t2));
        Rect r  = Imgproc.boundingRect(new MatOfPoint(t));

        // Sposta i punti rispetto all'angolo in alto a sinistra del rettangolo
        Point[] t1Rect = new Point[3];
        Point[] t2Rect = new Point[3];
        Point[] tRect = new Point[3];

        for (int i = 0; i < 3; i++) {
            tRect[i] = new Point(t[i].x - r.x, t[i].y - r.y);
            t1Rect[i] = new Point(t1[i].x - r1.x, t1[i].y - r1.y);
            t2Rect[i] = new Point(t2[i].x - r2.x, t2[i].y - r2.y);
        }
        MatOfPoint tRectInt = new MatOfPoint();
        tRectInt.fromArray(tRect);

        Mat mask = Mat.zeros(r.height, r.width, CvType.CV_32FC3);
        Imgproc.fillConvexPoly(mask, tRectInt, new Scalar(1.0, 1.0, 1.0), 16, 7);

        img1Rect = new Mat(firstImage, r1);
        img2Rect = new Mat(secondImage, r2);

        warpImage1 = Mat.zeros(r.height, r.width, img1Rect.type());
        warpImage2 = Mat.zeros(r.height, r.width, img2Rect.type());
        applyAffineTransform(warpImage1, img1Rect, t1Rect, tRect);
        applyAffineTransform(warpImage2, img2Rect, t2Rect, tRect);

        // Alpha blend rectangular patches
        Mat imgRect = new Mat();
        Core.addWeighted(warpImage1, 1.0 - alpha, warpImage2, alpha, 0, imgRect);

        // Copy triangular region of the rectangular patch to the output image
        Imgproc.cvtColor(imgRect, imgRect, Imgproc.COLOR_BGRA2BGR);
        Core.multiply(imgMorph.submat(r), mask, imgMorph.submat(r));
        Core.add(imgMorph.submat(r), imgRect, imgMorph.submat(r));
    }

    public static void applyAffineTransform(Mat warpImage, Mat src, Point[] srcTri, Point[] dstTri) {
        // Given a pair of triangles, find the affine transform.
        MatOfPoint2f srcMat = new MatOfPoint2f();
        srcMat.fromArray(srcTri);

        MatOfPoint2f dstMat = new MatOfPoint2f();
        dstMat.fromArray(dstTri);

        Mat warpMat = Imgproc.getAffineTransform(srcMat, dstMat);

        // Apply the Affine Transform just found to the src image
        Imgproc.warpAffine(src, warpImage, warpMat, warpImage.size(), Imgproc.INTER_LINEAR, 4);
    }

    public static MatOfInt getDelaunayIndexes(MatOfFloat6 triangleList, MatOfPoint2f fsMatOfPoint2f) {
        Point[] pointArray = fsMatOfPoint2f.toArray();
        float[] trianglesArray = triangleList.toArray();
        MatOfInt triangles = new MatOfInt();

        for (int i = 0; i < trianglesArray.length; i += 6) {
            int[] vertexes = new int[3];
            for (int v = 0; v < 3; v++) {
                double x = trianglesArray[i + v * 2];
                double y = trianglesArray[i + v * 2 + 1];
                for (int j = 0; j < pointArray.length; j++) {
                    if (x == pointArray[j].x && y == pointArray[j].y) {
                        vertexes[v] = j;
                        break;
                    }
                }
            }
            triangles.push_back(new MatOfInt(vertexes));
        }
        return triangles;
    }

    public static MatOfInt reshapeVertices(MatOfInt vertices) {
        // Verifica se il numero di righe in vertices è multiplo di 3
        if (vertices.rows() % 3 != 0) {
            throw new IllegalArgumentException("Il numero di righe in vertices deve essere multiplo di 3.");
        }

        // Crea la nuova MatOfInt con 3 colonne
        MatOfInt reshapedVertices = new MatOfInt();
        int numRows = vertices.rows() / 3;
        reshapedVertices.create(numRows, 3, CvType.CV_32S);

        // Copia i dati da vertices alla nuova MatOfInt
        for (int i = 0; i < numRows; i++) {
            double[] row1 = vertices.get(i * 3, 0);
            double[] row2 = vertices.get(i * 3 + 1, 0);
            double[] row3 = vertices.get(i * 3 + 2, 0);

            // Converte ogni elemento dell'array double[] a int
            int[] intRow1 = new int[row1.length];
            int[] intRow2 = new int[row2.length];
            int[] intRow3 = new int[row3.length];

            for (int j = 0; j < row1.length; j++) {
                intRow1[j] = (int) row1[j];
                intRow2[j] = (int) row2[j];
                intRow3[j] = (int) row3[j];
            }

            reshapedVertices.put(i, 0, intRow1);
            reshapedVertices.put(i, 1, intRow2);
            reshapedVertices.put(i, 2, intRow3);
        }

        return reshapedVertices;
    }

    private void draw_triangles() {
        firstImage.convertTo(firstImage, CvType.CV_8U);
        secondImage.convertTo(secondImage, CvType.CV_8U);

        Mat exposedMat1 = firstImage.clone();
        Mat exposedMat2 = secondImage.clone();

        org.opencv.core.Point[] pt = new org.opencv.core.Point[3];

        for(int i = 0; i < triangles.length; i+=6) {
            pt[0] = new org.opencv.core.Point(Math.round(triangles[i]), Math.round(triangles[i+1]));
            pt[1] = new org.opencv.core.Point(Math.round(triangles[i+2]), Math.round(triangles[i+3]));
            pt[2] = new org.opencv.core.Point(Math.round(triangles[i+4]), Math.round(triangles[i+5]));

            Imgproc.line(exposedMat1, pt[0], pt[1], new Scalar(255, 0, 0), 2, Imgproc.LINE_AA, 0);
            Imgproc.line(exposedMat1, pt[1], pt[2], new Scalar(255, 0, 0), 2, Imgproc.LINE_AA, 0);
            Imgproc.line(exposedMat1, pt[2], pt[0], new Scalar(255, 0, 0), 2, Imgproc.LINE_AA, 0);
            Imgproc.line(exposedMat2, pt[0], pt[1], new Scalar(0, 0, 255), 2, Imgproc.LINE_AA, 0);
            Imgproc.line(exposedMat2, pt[1], pt[2], new Scalar(0, 0, 255), 2, Imgproc.LINE_AA, 0);
            Imgproc.line(exposedMat2, pt[2], pt[0], new Scalar(0, 0, 255), 2, Imgproc.LINE_AA, 0);

        }

        Bitmap imageBitmap1 = Bitmap.createBitmap(exposedMat1.cols(), exposedMat1.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(exposedMat1, imageBitmap1);
        imageViewTest1.setImageBitmap(imageBitmap1);

        Bitmap imageBitmap2 = Bitmap.createBitmap(exposedMat2.cols(), exposedMat2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(exposedMat2, imageBitmap2);
        imageViewTest2.setImageBitmap(imageBitmap2);
    }

}
