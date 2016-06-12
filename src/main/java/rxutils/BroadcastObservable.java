package rxutils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import rx.Observable;

public class BroadcastObservable {

    public static Observable<Intent> fromBroadcast(Context context, IntentFilter filter){
        return Observable.create(new OnSubscribeBroadcastRegister(context, filter, null, null));
    }

    public static Observable<Intent> fromBroadcast(Context context, IntentFilter filter, String broadcastPermission, Handler schedulerHandler){
        return Observable.create(new OnSubscribeBroadcastRegister(context, filter, broadcastPermission, schedulerHandler));
    }
}
