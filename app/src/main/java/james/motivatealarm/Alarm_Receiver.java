package james.motivatealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by james on 6/13/16.
 */
public class Alarm_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("We are in the receiver.", "Fuck yeah");

        //fetch extra strings from intent
        String get_your_string = intent.getExtras().getString("extra");

        Log.e("What is the key?", get_your_string);

        //create intent to ringtone service
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        //pass the extra string from Main Activity to ringtone playing service
        service_intent.putExtra("extra", get_your_string);

        //start ringtone service
        context.startService(service_intent);
    }
}
