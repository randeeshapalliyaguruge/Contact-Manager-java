package com.contact.randeesha;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


class CustomListAdapter extends ArrayAdapter<RetrieveContactRecord> {

    private List<RetrieveContactRecord> mUserRecords;
    private ArrayList<RetrieveContactRecord> arrayList;

    CustomListAdapter(Activity context,
                      List<RetrieveContactRecord> userRecords) {
        super(context, 0, userRecords);
        mUserRecords = userRecords;
        arrayList = new ArrayList<>();
        arrayList.addAll(mUserRecords);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        View viewConverter = view;
        ViewHolder holder;
        if (view == null) {
            viewConverter =
                    LayoutInflater.from(getContext()).inflate(R.layout.list_item_view,
                            parent, false);
            holder = new ViewHolder(viewConverter);
            viewConverter.setTag(holder);
        } else {
            holder = (ViewHolder) viewConverter.getTag();
        }

        RetrieveContactRecord currentUser = mUserRecords.get(position);

        holder.txtName.setText(currentUser.getName());
        holder.txtIntro.setText(currentUser.getIntro());

        byte[] imgByte = currentUser.getPicture();
        if (imgByte != null) {

            Glide.with(getContext())
                    .load(imgByte)
                    .into(holder.profileImage);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.ic_person_round)
                    .into(holder.profileImage);
        }

        return viewConverter;
    }

    void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mUserRecords.clear();
        if (charText.length() == 0) {
            mUserRecords.addAll(arrayList);
        } else {
            for (RetrieveContactRecord record : arrayList) {
                if (record.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mUserRecords.add(record);
                } else if (record.getIntro().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mUserRecords.add(record);
                }
            }
        }
        notifyDataSetChanged();
    }
}




