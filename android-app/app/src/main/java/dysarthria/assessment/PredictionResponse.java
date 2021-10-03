package dysarthria.assessment;

import com.google.gson.annotations.SerializedName;

public class PredictionResponse {

    private int index;

    private String intelligibility;

    public int getIndex() {
        return index;
    }

    public String getIntelligibility() {
        return intelligibility;
    }
}
