package com.contact.randeesha.CallLog;

import android.content.Context;
import android.os.AsyncTask;

import com.contact.randeesha.DataListener;
import com.contact.randeesha.RetrieveContactRecord;

import java.util.ArrayList;


public class CallLogLoader extends AsyncTask<Object, Object, ArrayList<RetrieveContactRecord>> {

    private final Context context;
    private final DataListener dataListener;
    private final int startPos;
    private final int endPos;

    CallLogLoader(Context context,
                  DataListener dataListener,
                  int startPos,
                  int endPos) {
        this.context = context;
        this.dataListener = dataListener;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dataListener.onPreExecute();
    }

    @Override
    protected ArrayList<RetrieveContactRecord> doInBackground(Object... params) {
        return new com.contact.randeesha.CallLog.CallLogGetter().getLogList(context, startPos, endPos);
    }

    @Override
    protected void onPostExecute(ArrayList<RetrieveContactRecord> retrieveContactRecords) {
        super.onPostExecute(retrieveContactRecords);
        dataListener.onCompletion(retrieveContactRecords);
    }
}
