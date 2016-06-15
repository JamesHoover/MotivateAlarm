package james.motivatealarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

//TODO: text-to-speech, remove notification when alarm unset, make time picker popup

public class MainActivity extends AppCompatActivity {

    //Make alarm manager
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Calendar alarm_calendar;
    TextView update_text;
    Context context;
    Intent my_intent;
    PendingIntent pending_intent;
    private static final int NOTIFICATION_ID = 202;
    private TextView switchStatus;
    private Switch mySwitch;
    int alarm_hour;
    int alarm_minute;


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

        //create instance of calendar
        alarm_calendar = Calendar.getInstance();

        //create intent to alarm receiver class
        my_intent = new Intent(this.context, Alarm_Receiver.class);

        alarm_hour = 0;
        alarm_minute = 0;

        //initialize start button
        //Button alarm_on = (Button) findViewById(R.id.alarm_on);

        // notification
        // set up the notification service
        final NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        final Notification notification_set = new Notification.Builder(this)
                .setContentTitle("Alarm is set")
                .setContentText("View")
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();


//        //create onClick listener to start alarm
//        assert alarm_on != null;
//        alarm_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //setting calendar instance with the hour and minute from picker
//                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
//                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());
//
//                //method that changes update text
//                //set_alarm_text("Alarm set to: " + hour_string + ":" + minute_string);
//                //send_alarm_intent();
//                //put in extra string into my intent
//                //tells clock you pressed alarm on button
//                my_intent.putExtra("extra", "alarm on");
//
//                //create pending intent that delays intent until specified calendar time
//                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//             //set the alarm manager
//             alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_intent);
//
//                //notification
//                //notification_set = NotificationCompat.Builder.setContentTitle("Alarm is set for: " + hour_string + ":" + minute_string);
//                //send notification
//                notify_manager.notify(NOTIFICATION_ID, notification_set);
//            }
//
//        });
//        // initialize the stop button
//        Button alarm_off = (Button) findViewById(R.id.alarm_off);
//        //create onClick listener to stop alarm
//        assert alarm_off != null;
//        alarm_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //method that changes the update text
//                set_alarm_text("Alarm off!");
//
//                //cancel the alarm
//                alarm_manager.cancel(pending_intent);
//
//                //put extra string into my_intent
//                //tells clock pressed alarm off button
//                my_intent.putExtra("extra", "alarm off");
//
//                //stop the ringtone
//                sendBroadcast(my_intent);
//
//                notify_manager.cancel(NOTIFICATION_ID);
//
//            }
//        });

        //Initiate switch
        switchStatus = (TextView) findViewById(R.id.switchStatus);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        setup_switch(switchStatus, mySwitch);




    }//end onCreate


    //not used
    private void set_alarm_text(String output) {
        update_text.setText(output);
    }






    //shows timepicker popup. popup part works and sets text
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            //initialize text update
            TextView update_text = (TextView) getActivity().findViewById(R.id.update_text);

            //Get the AM or PM for current time
            String aMpM = "AM";
            if(hourOfDay > 11)
                aMpM = "PM";

            //Make the 24 hour time format to 12 hour time format
            int currentHour;
            if(hourOfDay > 11)
                currentHour = hourOfDay - 12;
            else
                currentHour = hourOfDay;

            //Set a message for user
            update_text.setText("Alarm set for ");
            //Display the user changed time on TextView
            update_text.setText(update_text.getText() + String.valueOf(currentHour)
                    + ":" + String.valueOf(minute) + aMpM);
        }
    }




    //timepicker popup on button click
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    //setup the on off switch. working. need to add alarm on/off
    private void setup_switch(final TextView switchStatus, Switch mySwitch) {

        //set the switch to OFF
        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //TODO: also check to see if a time is set
                if (isChecked) {
                    switchStatus.setText("Alarm is ON");
                    //send alarm signal on
                    alarm_on();
                    Log.e("Alarm", "ON");
                }

                else {
                    switchStatus.setText("Alarm is OFF");
                    //send alarm signal off
                    alarm_off();
                    Log.e("Alarm", "OFF");
                }
            }
        });
    }





    //working on this
    public void alarm_on () {
        final Calendar c = Calendar.getInstance();

        //setting calendar instance with the hour and minute from picker
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        Log.e("hour", String.valueOf(Calendar.HOUR_OF_DAY));
        Log.e("Minute", String.valueOf(Calendar.MINUTE));


        //put in extra string into my intent
        //tells clock you pressed alarm on button
        my_intent.putExtra("extra", "alarm on");

        //create pending intent that delays intent until specified calendar time
        pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //set the alarm manager
        alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_intent);
        //send notification
        alarm_notification();
        //notification
        //notification_set = NotificationCompat.Builder.setContentTitle("Alarm is set for: " + hour_string + ":" + minute_string);
        //send notification
//                notify_manager.notify(NOTIFICATION_ID, notification_set);
    }



    //seems to work
    public void alarm_off () {
        //cancel the alarm
        alarm_manager.cancel(pending_intent);

        //put extra string into my_intent
        //tells clock pressed alarm off button
        my_intent.putExtra("extra", "alarm off");

        //stop the ringtone
        sendBroadcast(my_intent);

        cancel_notification(NOTIFICATION_ID);
    }


    public void alarm_notification () {
        // notification
        // set up the notification service
        final NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        final Notification notification_set = new Notification.Builder(this)
                .setContentTitle("Alarm is set")
                .setContentText("View")
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();

        notify_manager.notify(NOTIFICATION_ID, notification_set);
    }

    public void cancel_notification(int ID) {
        final NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notify_manager.cancel(ID);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //show toast for time set
    //Toast.makeText(MainActivity.this, hour_x + " : " + minute_x, Toast.LENGTH_SHORT).show();

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
