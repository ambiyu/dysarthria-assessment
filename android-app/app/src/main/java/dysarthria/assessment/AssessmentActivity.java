package dysarthria.assessment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import com.devlomi.record_view.OnRecordListener;

import java.io.IOException;

import dysarthria.assessment.databinding.ActivityAssessmentBinding;

public class AssessmentActivity extends AppCompatActivity {

    private ActivityAssessmentBinding binding;
    private WavAudioRecorder recorder;
    private MediaPlayer player;

    private final String[] prompts = new String[]
            { "autobiography", "celebrity", "inalienable", "battleship", "ablutions" };
    private int currentPromptIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup recording buttons and views
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                recorder = new WavAudioRecorder(MediaRecorder.AudioSource.MIC, 16000,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                recorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/" + prompts[currentPromptIndex] + ".wav");

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
                recorder.stop();
                recorder.reset();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                recorder.stop();
                recorder.reset();
                System.out.println("Time: " + recordTime);
                binding.recordLayout.setVisibility(View.INVISIBLE);
                binding.recordedLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                System.out.println("Less than a second");
                recorder.stop();
                recorder.reset();
            }
        });

        binding.playButton.setOnClickListener((View v) -> {
            play(getExternalCacheDir().getAbsolutePath() + "/" + prompts[currentPromptIndex] + ".wav");
        });

        binding.nextButton.setOnClickListener((View v) -> {

            if (currentPromptIndex + 1 == prompts.length) {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("prompts", prompts);
                startActivity(intent);
            } else {
                currentPromptIndex++;
                int promptNumber = currentPromptIndex + 1;
                binding.promptNum.setText("Prompt " + promptNumber);
                binding.prompt.setText(prompts[currentPromptIndex]);
                binding.recordLayout.setVisibility(View.VISIBLE);
                binding.recordedLayout.setVisibility(View.INVISIBLE);

//                if (currentPromptIndex + 1 == prompts.length) {
//                    binding.nextButton.setText("Get Result");
//                }
            }

        });

        binding.recordAgainButton.setOnClickListener((View v) -> {
            binding.recordedLayout.setVisibility(View.INVISIBLE);
            binding.recordLayout.setVisibility(View.VISIBLE);
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