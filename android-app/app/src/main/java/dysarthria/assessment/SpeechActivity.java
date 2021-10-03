package dysarthria.assessment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import com.devlomi.record_view.OnRecordListener;

import java.io.IOException;

import dysarthria.assessment.databinding.ActivitySpeechBinding;

public class SpeechActivity extends AppCompatActivity {

    private ActivitySpeechBinding binding;
    private WavAudioRecorder recorder;
//    private MediaRecorder recorder;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeechBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 200);

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
//                recorder = new MediaRecorder();
//                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                recorder.setAudioEncodingBitRate(16*44100);
//                recorder.setAudioSamplingRate(44100);
//                recorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/test.m4a");
//
//                try {
//                    recorder.prepare();
//                    recorder.start();
//                } catch (Exception e) {
//
//                }

                recorder = new WavAudioRecorder(MediaRecorder.AudioSource.MIC, 16000,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                recorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/test.wav");

                if (WavAudioRecorder.State.INITIALIZING == recorder.getState()) {
                    recorder.prepare();
                    recorder.start();
                } else if (WavAudioRecorder.State.ERROR == recorder.getState()) {
                    recorder.release();
                } else {
                    recorder.stop();
                    recorder.reset();
                }
            }

            @Override
            public void onCancel() {
//                recorder.stop();
//                recorder.reset();
//                recorder.release();
                recorder.stop();
                recorder.reset();
                recorder = null;
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
//                recorder.stop();
//                recorder.reset();
//                recorder.release();
//                System.out.println("recorded");

                recorder.stop();
                recorder.reset();
                recorder = null;
                System.out.println("Time: " + recordTime);
            }

            @Override
            public void onLessThanSecond() {
                System.out.println("Less than a second");
                recorder.stop();
                recorder.reset();
                recorder = null;
            }
        });

        binding.playButton.setOnClickListener((View v) -> {
            play(getExternalCacheDir().getAbsolutePath() + "/test.wav");
        });

        binding.nextButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        });
    }

    private void play(String filePath) {
        player = new MediaPlayer();

        player.setOnCompletionListener((MediaPlayer mediaPlayer) -> {
            player.stop();
            player.release();

            player = null;
        });

        try {
            player.setDataSource(filePath);
            player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            player.prepare();
        }
        catch (IOException e) {
            // handle error
        }
        catch (IllegalArgumentException e) {
            // handle error
        }


        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
        }
    }
}