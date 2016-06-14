package james.motivatealarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

//TODO: text-to-speech, remove notification when alarm unset

public class MainActivity extends AppCompatActivity {

    //Make alarm manager
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    private static final int NOTIFICATION_ID = 202;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;

        //initialize alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //initialize timepicker
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        //initialize text update
        update_text = (TextView) findViewById(R.id.update_text);

        //creat instance of calendar
        final Calendar calendar = Calendar.getInstance();

        //create intent to alarm receiver class
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);

        //initialize start button
        Button alarm_on = (Button) findViewById(R.id.alarm_on);

        // notification
        // set up the notification service
        final NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        final Notification notification_set = new Notification.Builder(this)
                .setContentTitle("Alarm is set")
                .setContentText("Click me!")
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();

        //create onClick listener to start alarm
        assert alarm_on != null;
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setting calendar instance with the hour and minute from picker
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                //get int values of hour and minute
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                //convert ints to string
                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                //convert 24 hour to 12 hour
                if (hour > 12) {
                    hour_string = String.valueOf(hour - 12);
                }
                if (minute < 10) {
                    minute_string = "0" + String.valueOf(minute);
                }

                //method that changes update text
                set_alarm_text("Alarm set to: " + hour_string + ":" + minute_string);

                //put in extra string into my intent
                //tells clock you pressed alarm on button
                my_intent.putExtra("extra", "alarm on");

                //create pending intent that delays intent until specified calendar time
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //set the alarm manager
                alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_intent);

                //notification
                //notification_set = NotificationCompat.Builder.setContentTitle("Alarm is set for: " + hour_string + ":" + minute_string);
                //send notification
                notify_manager.notify(NOTIFICATION_ID, notification_set);
            }

        });
        // initialize the stop button
        Button alarm_off = (Button) findViewById(R.id.alarm_off);
        //create onClick listener to stop alarm
        assert alarm_off != null;
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //method that changes the update text
                set_alarm_text("Alarm off!");

                //cancel the alarm
                alarm_manager.cancel(pending_intent);

                //put extra string into my_intent
                //tells clock pressed alarm off button
                my_intent.putExtra("extra", "alarm off");

                //stop the ringtone
                sendBroadcast(my_intent);

                notify_manager.cancel(NOTIFICATION_ID);

            }
        });
    }
    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
