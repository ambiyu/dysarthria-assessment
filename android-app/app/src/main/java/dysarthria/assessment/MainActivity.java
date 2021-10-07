package dysarthria.assessment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import dysarthria.assessment.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.beginButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, AssessmentActivity.class);
            startActivity(intent);
        });

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 200);
    }
}