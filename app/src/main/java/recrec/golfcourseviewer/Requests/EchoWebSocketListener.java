package recrec.golfcourseviewer.Requests;

import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public final class EchoWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("web", "Recieved: " +text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d("web","Closing" + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable okhttp3.Response response) {
        Log.d("web","FAILURE" + t.getMessage());
    }
}