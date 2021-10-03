package dysarthria.assessment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.File;

import dysarthria.assessment.databinding.ActivityResultBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String path = getExternalCacheDir().getAbsolutePath() + "/test.wav";

        uploadAudio(path);
    }

    private void uploadAudio(String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("audio_file", file.getName(), requestBody);
        PredictionApi predictionApi = RetrofitClient.getRetrofit().create(PredictionApi.class);
        Call<PredictionResponse> call = predictionApi.makePrediction(fileToUpload);

        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {

                PredictionResponse result = response.body();
                System.out.println(result.getIndex() + result.getIntelligibility());
                binding.index.setText(Integer.toString(result.getIndex()));
                binding.intelligibility.setText(result.getIntelligibility());
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                String err = t.getMessage() == null ? "" : t.getMessage();
                System.out.println("error:" + err);
            }
        });
    }
}