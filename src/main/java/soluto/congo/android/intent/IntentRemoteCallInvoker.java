package soluto.congo.android.intent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;

import rx.Notification;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rxutils.BroadcastObservable;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallInvoker;

public class IntentRemoteCallInvoker implements RemoteCallInvoker {
    private static final String INTENT_DATA_NAME = "data";
    private final Context context;
    private final String intentPermission;
    private final String intentPrefix;

    public IntentRemoteCallInvoker(Context context, String intentPrefix, String intentPermission) {
        this.context = context;
        this.intentPermission = intentPermission;
        this.intentPrefix = intentPrefix;
    }

    @Override
    public <TResult> Observable<TResult> invoke(RemoteCall remoteCall, Class<TResult> tResultClass) {
        final Intent intent = new Intent(intentPrefix + "_RemoteCall");
        intent.putExtra(INTENT_DATA_NAME, new Gson().toJson(remoteCall));

        return BroadcastObservable.fromBroadcast(context, new IntentFilter(intentPrefix + "_RemoteResponse_" + remoteCall.correlationId))
                .map(toNotification())
                .dematerialize()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        context.sendOrderedBroadcast(intent, intentPermission);
                    }
                }).cast(tResultClass);
    }

    private static Func1<Intent, Notification<Object>> toNotification() {
        return new Func1<Intent, Notification<Object>>() {
            @Override
            public Notification<Object> call(Intent intent) {
                final String data = intent.getStringExtra(INTENT_DATA_NAME);
                return new Gson().fromJson(data, Notification.class);
            }
        };
    }
}
