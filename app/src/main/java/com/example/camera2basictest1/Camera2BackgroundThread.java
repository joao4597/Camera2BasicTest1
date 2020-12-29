package com.example.camera2basictest1;

import android.media.ImageReader;
import android.os.HandlerThread;
import android.util.Log;

public class Camera2BackgroundThread extends HandlerThread {

    public Camera2BackgroundThread(String name) {
        super(name);
    }

    ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.i("CAM2_BACKGROUND_THREAD", "ImageReader new image available");
                }
            };
}
