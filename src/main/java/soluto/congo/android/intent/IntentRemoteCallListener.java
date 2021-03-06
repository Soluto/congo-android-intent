package soluto.congo.android.intent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.google.gson.Gson;

import rx.Observable;
import rx.android.broadcastobservable.BroadcastObservable;
import rx.functions.Func1;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallListener;

public class IntentRemoteCallListener implements RemoteCallListener {
    private final Observable<RemoteCall> remoteCalls;
    public static final String INTENT_DATA_NAME = "data";

    public IntentRemoteCallListener(Context context, String intentPrefix, String incomingIntentPermission, String requestChannel) {
        this.remoteCalls = BroadcastObservable
                .fromBroadcast(context, new IntentFilter(intentPrefix + "_" + requestChannel), incomingIntentPermission, new Handler())
                .map(toRemoteCall());
    }

    @Override
    public Observable<RemoteCall> getRemoteCalls() {
        return this.remoteCalls;
    }

    private Func1<Intent, RemoteCall> toRemoteCall() {
        return new Func1<Intent, RemoteCall>() {
            @Override
            public RemoteCall call(Intent intent) {
                return new Gson().fromJson(intent.getStringExtra(INTENT_DATA_NAME), RemoteCall.class);
            }
        };
    }
}
