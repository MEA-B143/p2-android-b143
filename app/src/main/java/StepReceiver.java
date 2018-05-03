import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

public class StepReceiver extends BroadcastReceiver {
    public TextView tvSteps;

    private StepReceiver myStepReceiver;

    public void onResume(){
        myStepReceiver = new StepReceiver();
        final IntentFilter intentFilter = new IntentFilter("YourAction");
        LocalBroadcastManager.getInstance().registerReceiver(myStepReceiver, intentFilter);
    }

    public void onPause() {
        if (myStepReceiver != null)
            LocalBroadcastManager.getInstance().unregisterReceiver(myStepReceiver);
        myStepReceiver = null;
    }

    public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String StepCount = b.getString("StepCount");
            tvSteps.setText(StepCount);
            double someDouble = b.getDouble("doubleName");
    }
}
