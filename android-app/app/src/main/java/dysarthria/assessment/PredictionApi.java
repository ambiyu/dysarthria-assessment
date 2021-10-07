package dysarthria.assessment;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PredictionApi {

    @Multipart
    @POST("/predict")
    Observable<PredictionResponse> makePrediction(@Part MultipartBody.Part file);
}
