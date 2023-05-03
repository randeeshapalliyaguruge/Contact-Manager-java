package com.contact.randeesha.CallLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.contact.randeesha.Add_Update.AddActivity;
import com.contact.randeesha.R;
import com.contact.randeesha.RetrieveContactRecord;

import java.util.ArrayList;
import java.util.List;


public class CallLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private final ArrayList<RetrieveContactRecord> logList;
    private String currentDateValue;

    public CallLogAdapter(ArrayList<RetrieveContactRecord> logList) {
        this.logList = logList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.call_log_view, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final RetrieveContactRecord record = logList.get(position);
        if (position == 0){
            currentDateValue = "";
        }else {
            currentDateValue = logList.get(position - 1).getLogDate();
        }
        if (holder instanceof CallLogViewHolder){

            if (!currentDateValue.equals(record.getLogDate())){
                currentDateValue = record.getLogDate();
                ((CallLogViewHolder) holder).date.setVisibility(View.VISIBLE);
                ((CallLogViewHolder) holder).date.setText(record.getLogDate());
            }else {
                ((CallLogViewHolder) holder).date.setVisibility(View.GONE);
            }

            if (record.getLogName().equals("Unknown")){
                ((CallLogViewHolder) holder).name.setText(record.getLogNumber());
            }else {
                ((CallLogViewHolder) holder).name.setText(record.getLogName());
            }

            ((CallLogViewHolder) holder).time.setText(record.getLogTime());
            ((CallLogViewHolder) holder).imageView.setImageBitmap(record.getLogCallImage());
            ((CallLogViewHolder) holder).duration.setText(record.getLogDuration());

            if (record.getLogName().equals("Unknown")){
                ((CallLogViewHolder) holder).add.setVisibility(View.VISIBLE);
            }else {
                ((CallLogViewHolder) holder).add.setVisibility(View.GONE);
            }
            ((CallLogViewHolder) holder).add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddActivity.class);
                    intent.putExtra("LOG_PHONE_NUMBER", record.getLogNumber());
                    ((Activity)mContext).startActivityForResult(intent, 3);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public void appendDataToAdapter(List<RetrieveContactRecord> logs, boolean isRefreshing) {
        Log.i("List Size", "Log List" + String.valueOf(logList.size()));

        if (isRefreshing) {
            logList.clear();
        }
        int listUpdateStarPos = logList.size();
        logList.addAll(logs);
        Log.i("List Size", "Log List" + String.valueOf(logList.size()));
        notifyDataSetChanged();
        notifyItemRangeInserted(listUpdateStarPos, logs.size());

    }
}
