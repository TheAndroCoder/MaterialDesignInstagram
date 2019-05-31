package com.example.instamaterial;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CameraFragment extends Fragment {
    private TextureView textureView;
    private FloatingActionButton clickPicture;
    //check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);

    }
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;
    private Size imageDimension;
    //save to file
    private File file;
    private boolean mFlashSupported;
    private Handler mbackgroundHandler;
    private HandlerThread mHandlerThread;
    CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cd) {
            cameraDevice=cd;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cd) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cd, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera,container,false);
        textureView=v.findViewById(R.id.textureView);
        //closeCamera=v.findViewById(R.id.closeCamera);
        clickPicture=v.findViewById(R.id.clickPicture);
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},1002);
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera();
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
        });
        clickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        return v;
    }
    private void openCamera(){
        CameraManager manager =(CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId=manager.getCameraIdList()[0];
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map!=null;
            imageDimension=map.getOutputSizes(SurfaceTexture.class)[0];
            if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},1003);
            }
            manager.openCamera(cameraId,stateCallback,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    private void takePicture(){
        if(cameraDevice==null)
            return;
        CameraManager manager=(CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes=null;
            if(characteristics!=null){
                jpegSizes=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            //capture picture with custom size
            int width=640;
            int height=480;
            if(jpegSizes!=null && jpegSizes.length>0){
                width=jpegSizes[0].getWidth();
                height=jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface=new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));
            CaptureRequest.Builder builder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.addTarget(reader.getSurface());
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //check device rotation
            int rotation=getActivity().getWindowManager().getDefaultDisplay().getRotation();
            builder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
            final File file=new File(Environment.getExternalStorageDirectory()+"/insta/"+System.currentTimeMillis()+".jpg");
            ImageReader.OnImageAvailableListener listener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try{
                        image=reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes=new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                        System.out.print("saved file "+Environment.getDownloadCacheDirectory().getAbsolutePath());
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
                private void save(byte bytes[])throws Exception{
                    OutputStream outputStream=null;
                    try{
                        outputStream=new FileOutputStream(file);
                        outputStream.write(bytes);

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                        if(outputStream!=null)
                            outputStream.close();
                    }
                }
            };
            reader.setOnImageAvailableListener(listener,mbackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(getActivity(), "Saved "+file.getName(), Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try{
                        Log.d("sachin","camera capture session");
                        cameraCaptureSession.capture(captureRequestBuilder.build(),captureListener,mbackgroundHandler);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mbackgroundHandler);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        SurfaceTexture texture=textureView.getSurfaceTexture();
        System.out.println("trying to create preview");
        assert texture!=null;
        texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
        Surface surface = new Surface(texture);
        try {
            captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession ccs) {
                    if(cameraDevice==null)
                        return;
                    cameraCaptureSession=ccs;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },null);
        } catch (CameraAccessException e) {
            Log.d("sachin",e.getMessage());
            e.printStackTrace();
        }
    }
    private void updatePreview(){
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        Log.d("sachin","update preview");
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mbackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startBackgroundThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopbackgroundThread();
    }

    private void stopbackgroundThread() {
        mHandlerThread.quitSafely();
        try{
            mHandlerThread.join();
            mHandlerThread=null;
            mbackgroundHandler=null;
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mHandlerThread=new HandlerThread("Camera Background");
        mHandlerThread.start();
        mbackgroundHandler=new Handler(mHandlerThread.getLooper());
    }
}
