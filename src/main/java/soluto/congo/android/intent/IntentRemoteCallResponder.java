package soluto.congo.android.intent;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import rx.Notification;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallResponder;

public class IntentRemoteCallResponder implements RemoteCallResponder {
    public static final String INTENT_DATA_NAME = "data";
    private final Context context;
    private final String intentPrefix;

    public IntentRemoteCallResponder(Context context, String intentPrefix) {
        this.context = context;
        this.intentPrefix = intentPrefix;
    }

    @Override
    public void onNext(RemoteCall remoteCall, Object o) {
        sendIntent(createIntent(remoteCall, Notification.createOnNext(o)));
    }

    @Override
    public void onCompleted(RemoteCall remoteCall) {
        sendIntent(createIntent(remoteCall, Notification.createOnCompleted()));
    }

    @Override
    public void onError(RemoteCall remoteCall, Throwable throwable) {
        sendIntent(createIntent(remoteCall, Notification.createOnError(throwable)));
    }

    private Intent createIntent(final RemoteCall remoteCall, Notification<Object> notification) {
        Intent response = new Intent(intentPrefix +  "_RemoteResponse_" + remoteCall.correlationId);
        response.putExtra(INTENT_DATA_NAME, new Gson().toJson(notification));

        return response;
    }

    private void sendIntent(Intent intent){
        context.sendOrderedBroadcast(intent, null);
    }
}
