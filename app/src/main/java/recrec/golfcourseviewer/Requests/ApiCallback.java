package recrec.golfcourseviewer.Requests;

public interface ApiCallback {

    void receiveResponse(String response,
                         ApiRequest.RequestType type,
                         ApiRequest.RequestResult res);

}
