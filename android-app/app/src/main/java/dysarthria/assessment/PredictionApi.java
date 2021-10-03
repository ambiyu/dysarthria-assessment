package dysarthria.assessment;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PredictionApi {

    @Multipart
    @POST("/")
    Call<PredictionResponse> makePrediction(@Part MultipartBody.Part file);
}
