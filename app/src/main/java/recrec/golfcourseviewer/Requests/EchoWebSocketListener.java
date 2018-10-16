package recrec.golfcourseviewer.Requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import recrec.golfcourseviewer.Entity.CourseViewModel;

public final class EchoWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private Context context;
    private SharedPreferences sharedPreferences;
    private CourseViewModel viewModel;

    public EchoWebSocketListener(Context c, CourseViewModel vm){
        context = c;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        viewModel = vm;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {

    }

    private final int KELVINRATIO = 273;
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JSONObject received = new JSONObject(text);
            if (received.has("UserID")){
                String id = received.getString("UserID");
                sharedPreferences.edit().putString("userId", id).apply();
            }
//  {UserId : "0000-0000-00000", weather : { name: "City", temp : "°C"}}
            if(received.has("Weather")){
                JSONObject weather = received.getJSONObject("Weather");
                String weatherPrint = weather.getString("name");
                weatherPrint += ": " + Integer.toString((int)weather
                        .getDouble("temp") - KELVINRATIO) + "°C";
                viewModel.weatherData.setValue(weatherPrint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("web", "Recieved: " +text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d("web","Closing" + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t,
                          @Nullable okhttp3.Response response) {
        Log.d("web","FAILURE" + t.getMessage());
    }
}