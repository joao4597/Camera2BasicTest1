package com.example.camera2basictest1;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class ImageProcessingThread extends Thread {
    private TextureView mTextureView;
    private Context mMainActivityContext;
    private int IMAGE_PROCESSING_INTERVAL_MILLISECONDS = 100;

    ImageProcessingThread(TextureView mTextureView, Context mMainActivityContext) {
        this.mTextureView = mTextureView;
        this.mMainActivityContext = mMainActivityContext;
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        FirebaseApp.initializeApp(mMainActivityContext);
        imageProcessingLoop();
    }

    public void imageProcessingLoop() {
        Log.i("IMAGE_PROCESSING_THREAD","THREAD STARTED");
        for(int i = 0; true; i++) {
            try {
                Thread.sleep(IMAGE_PROCESSING_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("IMAGE_PROCESSING_THREAD", "THREAD STARTED, 3 seconds in");
            //mImageReader.acquireNextImage();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mTextureView.getBitmap());

            //FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mImageReader.acquireNextImage(), FirebaseVisionImageMetadata.ROTATION_0);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    // ...
                                    Log.i("IMAGE_PROCESSING_THREAD", "Image successfully processed");
                                    String resultText = firebaseVisionText.getText();
                                    for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                                        String blockText = block.getText();
                                        Log.i("DETECTED_TEXT", blockText);
                                        Float blockConfidence = block.getConfidence();
                                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();
                                        for (FirebaseVisionText.Line line: block.getLines()) {
                                            String lineText = line.getText();
                                            Float lineConfidence = line.getConfidence();
                                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();
                                            for (FirebaseVisionText.Element element: line.getElements()) {
                                                String elementText = element.getText();
                                                Float elementConfidence = element.getConfidence();
                                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();
                                            }
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                            Log.i("IMAGE_PROCESSING_THREAD", "Image processing failed");
                                        }
                                    });
        }
    }
}
