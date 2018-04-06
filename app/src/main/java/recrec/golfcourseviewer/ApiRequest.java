package recrec.golfcourseviewer;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiRequest {

    public enum RequestType {
        REQ_GET_COURSES,
        REQ_GET_POLYGONS,
        REQ_CREATE_COURSE,
        REQ_ADD_POLYGON
    }

    private String url;

    public ApiRequest(String url) {
        setUrl(url);
    }

    public void setUrl(String url) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        this.url = url;
    }

    public void getCourses() {
        // TODO
    }

    private void makeRequest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
    }

}
