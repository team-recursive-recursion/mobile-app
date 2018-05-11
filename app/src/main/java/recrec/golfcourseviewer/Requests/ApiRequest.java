package recrec.golfcourseviewer.Requests;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import recrec.golfcourseviewer.R;

public class ApiRequest {

    public enum RequestType {
        REQ_GET_COURSES,
        REQ_GET_POLYGONS,
        REQ_CREATE_COURSE,
        REQ_ADD_POLYGON
    }

    public enum RequestResult {
        RES_EXCEPTION,
        RES_FAILURE,
        RES_SUCCESS
    }

    private String url;
    private Context context;

    public ApiRequest(Context context, String url) {
        this.context = context;
        setUrl(url);
    }

    public void setUrl(String url) {
        if (!url.startsWith(context.getString(R.string.http_beginning))) {

            url = context.getString(R.string.http_beginning)+ url;
        }
        if (!url.endsWith(context.getString(R.string.api_port_end))) {
            url = url + context.getString(R.string.api_port_end);
        }
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getCoursesAsync(ApiCallback)
     *
     *     Returns the results of the GetCourses call to the callback.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void getCoursesAsync(ApiCallback cb) {
        // create the request
        String target = url + "/api/GolfCoursesNew";
        Request req = new Request.Builder()
                .url(target)
                .build();
        makeRequest(req, RequestType.REQ_GET_COURSES, cb);
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getPolygonsAsync(ApiCallback, String)
     *
     *     Returns the results of the GetPolygons for the specified course
     *     ID to the callback.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void getPolygonsAsync(ApiCallback cb, String courseId) {
        // create the request
        String target = url + "/api/GolfCoursesNew/" + courseId;
        Request req = new Request.Builder()
                .url(target)
                .build();
        makeRequest(req, RequestType.REQ_GET_POLYGONS, cb);
    }

    private void makeRequest(Request req, final RequestType type, final ApiCallback cb) {
        OkHttpClient client = new OkHttpClient();
        // make asynchronous request and send the result to the callback
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                cb.receiveResponse(e.getMessage(), type, RequestResult.RES_EXCEPTION);
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (!response.isSuccessful()) {
                    cb.receiveResponse(response.toString(), type,
                            RequestResult.RES_FAILURE);
                } else {
                    try {
                        cb.receiveResponse(response.body().string(), type,
                                RequestResult.RES_SUCCESS);
                    } catch (IOException e) {
                        cb.receiveResponse(e.getMessage(), type,
                                RequestResult.RES_EXCEPTION);
                    }
                }
            }
        });
    }

}
