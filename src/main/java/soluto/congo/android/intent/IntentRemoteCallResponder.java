package soluto.congo.android.intent;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import rx.Completable;
import rx.functions.Action0;
import soluto.congo.core.RemoteCallResponder;
import soluto.congo.core.RemoteCallResult;

public class IntentRemoteCallResponder implements RemoteCallResponder {
    public static final String INTENT_DATA_NAME = "data";
    private final Context context;
    private final String intentPrefix;
    private String responseChannel;

    public IntentRemoteCallResponder(Context context, String intentPrefix, String responseChannel) {
        this.context = context;
        this.intentPrefix = intentPrefix;
        this.responseChannel = responseChannel;
    }

    @Override
    public Completable respond(final RemoteCallResult remoteCallResult) {
        return Completable.fromAction(new Action0() {
            @Override
            public void call() {
                Intent response = new Intent(intentPrefix + "_" + responseChannel + "_" + remoteCallResult.correlationId);
                response.putExtra(INTENT_DATA_NAME, new Gson().toJson(remoteCallResult));
                context.sendOrderedBroadcast(response, null);
            }
        });
    }
}
