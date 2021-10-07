package dysarthria.assessment;

import com.google.gson.annotations.SerializedName;

public class PredictionResponse {

    @SerializedName("index")
    private int index;

    @SerializedName("intelligibility")
    private String intelligibility;

    public int getIndex() {
        return index;
    }

    public String getIntelligibility() {
        return intelligibility;
    }
}
