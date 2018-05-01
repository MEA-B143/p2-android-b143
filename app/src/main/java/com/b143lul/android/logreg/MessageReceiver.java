package com.b143lul.android.logreg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

@SuppressLint("ParcelCreator")
public class MessageReceiver extends ResultReceiver {
    private Pedometer.Message message;

    public MessageReceiver(Pedometer.Message message){
        super(new Handler());
        this.message = message;
    }

    protected void onRecieveResult(int resultCode, Bundle resultData){
        message.displayMessage(resultCode, resultData);
    }
}
