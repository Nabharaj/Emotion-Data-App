package com.example.emotiondetection2022_23;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class SecondActivity extends AppCompatActivity {
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1002;
    private Button startRecordingButton;
    private Button stopRecordingButton;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private TextView recordingResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        startRecordingButton = findViewById(R.id.startRecordingButton);
        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        recordingResultTextView = findViewById(R.id.recordingResultTextView);

        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        // Check for audio recording permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        } else {
            // Start audio recording
            audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                showToast("Recording started");
                recordingResultTextView.setText("");
                stopRecordingButton.setEnabled(true);
                startRecordingButton.setEnabled(false);
            } catch (IOException e) {
                Log.e("Recording", "Error starting audio recording: " + e.getMessage());
            }
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            // Stop audio recording
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            showToast("Recording stopped. Audio file saved: " + audioFilePath);
            recordingResultTextView.setText("Recording Result: " + audioFilePath);
            stopRecordingButton.setEnabled(false);
            startRecordingButton.setEnabled(true);


            performInference(audioFilePath);
        }
    }

    private void performInference(String audioFilePath) {

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                // Audio recording permission denied, handle accordingly
                Log.e("Recording", "Permission denied");
                showToast("Audio recording permission denied");
            }
        }
    }
}





