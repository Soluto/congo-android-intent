package soluto.congo.android.intent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;

import rx.Completable;
import rx.Notification;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import rxutils.BroadcastObservable;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallInvoker;
import soluto.congo.core.RemoteCallResult;

public class IntentRemoteCallInvoker implements RemoteCallInvoker {
    private static final String INTENT_DATA_NAME = "data";
    private final Context context;
    private final String intentPermission;
    private final String intentPrefix;
    private String requestChannel;
    private String responseChannel;

    public IntentRemoteCallInvoker(Context context, String intentPrefix, String intentPermission, String requestChannel, String responseChannel) {
        this.context = context;
        this.intentPermission = intentPermission;
        this.intentPrefix = intentPrefix;
        this.requestChannel = requestChannel;
        this.responseChannel = responseChannel;
    }

    @Override
    public Observable<Object> invoke(final RemoteCall remoteCall) {
        final Intent intent = new Intent(intentPrefix + "_" + requestChannel);
        intent.putExtra(INTENT_DATA_NAME, new Gson().toJson(remoteCall));

        return BroadcastObservable.fromBroadcast(context, new IntentFilter(intentPrefix + "_"+ responseChannel +"_" + remoteCall.correlationId))
                .map(new Func1<Intent, RemoteCallResult>() {
                    @Override
                    public RemoteCallResult call(Intent intent) {
                        final String data = intent.getStringExtra(INTENT_DATA_NAME);
                        return new Gson().fromJson(data, RemoteCallResult.class);
                    }
                })
                .filter(new Func1<RemoteCallResult, Boolean>() {
                    @Override
                    public Boolean call(RemoteCallResult remoteCallResult) {
                        return remoteCallResult.correlationId.equals(remoteCall.correlationId);
                    }
                })
                .map(new Func1<RemoteCallResult, Notification<Object>>() {
                    @Override
                    public Notification<Object> call(RemoteCallResult remoteCallResult) {
                        return remoteCallResult.notification;
                    }
                })
                .dematerialize()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        context.sendOrderedBroadcast(intent, intentPermission);
                    }
                });
    }
}
