package dysarthria.assessment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dysarthria.assessment.databinding.ActivityResultBinding;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;

    private final Map<Double, String> intelligibilityMap = new HashMap<Double, String>() {{
        put(0.0, "low");
        put(0.5, "low-mid");
        put(1.0, "mid");
        put(1.5, "mid-high");
        put(2.0, "high");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] prompts = getIntent().getExtras().getStringArray("prompts");

        getPredictions(prompts);
    }

    private void getPredictions(String[] prompts) {
        List<Observable<PredictionResponse>> requests = new ArrayList<>();
        PredictionApi predictionApi = RetrofitClient.getRetrofit().create(PredictionApi.class);

        for (String prompt : prompts) {
            String path = getExternalCacheDir().getAbsolutePath() + "/" + prompt + ".wav";

            File file = new File(path);
            RequestBody requestBody = RequestBody.create(MediaType.parse("audio/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("audio_file", file.getName(), requestBody);
            requests.add(predictionApi.makePrediction(fileToUpload));
        }

        // Merge all requests into one
        Observable.zip(
                requests,
                responses -> {
                    List<PredictionResponse> predictionResponses = new ArrayList<>();
                    for (Object response : responses) {
                        PredictionResponse predictionResponse = (PredictionResponse) response;
                        predictionResponses.add(predictionResponse);
                    }

                    return predictionResponses;
                })
                .observeOn(AndroidSchedulers.mainThread()) // Update UI on main thread
                .subscribeOn(Schedulers.io()) // Wait for requests to complete on IO thread
                .subscribe(
                        predictionResponses -> { // All requests are successful
                            System.out.println("All done!");

                            addResultsToTable(prompts, predictionResponses);

                            int total = 0;
                            for (PredictionResponse pred : predictionResponses) {
                                total += pred.getIndex();
                            }
                            double avg = (double) total / prompts.length;
                            double roundedAvg = Math.round(avg * 2) / 2.0;

                            binding.intelligibility.setText(intelligibilityMap.get(roundedAvg));

                            binding.processingLayout.setVisibility(View.INVISIBLE);
                            binding.resultsLayout.setVisibility(View.VISIBLE);
                        },
                        throwable -> { // When there is an error in any request
                            System.out.println("error!");
                        }
                );
    }

    private void addResultsToTable(String[] prompts, List<PredictionResponse> predictions) {
        for (int i = 0; i < prompts.length; i++) {
            TableRow row = new TableRow(this);

            TextView word = new TextView(this);
            word.setText(prompts[i]);
            word.setTextSize(20);
            row.addView(word);

            TextView intelligibility = new TextView(this);
            intelligibility.setText(predictions.get(i).getIntelligibility());
            intelligibility.setTextSize(20);
            intelligibility.setGravity(Gravity.RIGHT);
            row.addView(intelligibility);

            binding.resultsTable.addView(row);
        }
    }
}