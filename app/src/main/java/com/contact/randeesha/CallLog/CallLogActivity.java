package com.contact.randeesha.CallLog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.contact.randeesha.DataListener;
import com.contact.randeesha.R;
import com.contact.randeesha.RetrieveContactRecord;
import com.contact.randeesha.SharedPrefs;

import java.util.ArrayList;

public class CallLogActivity extends AppCompatActivity implements DataListener {
    private static final String TAG = "CallLogActivity";
    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 1;
    private static final int ADD_NEW = 3;
    private RecyclerView recyclerView;
    private com.contact.randeesha.CallLog.CallLogAdapter recyclerAdapter;
    boolean isRefreshing = false;
    private int SET_RESULT = RESULT_CANCELED;
    private boolean hasData = false;
    private int startPos = 0;
    private int endPos = 25;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    int dark;

    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dark mode
        sharedPrefs = new SharedPrefs(this);
        if(sharedPrefs.loadNightModeState())
        {
            setTheme(android.R.style.ThemeOverlay_Material_Dark);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkPrimary)));
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.darkPrimaryDark));
            dark = 1;
        }
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_call_log);

        if (!isRequiredPermissionGranted()){
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        retrieveData(startPos, endPos);
        recyclerView = (RecyclerView) findViewById(R.id.rv_callLogList);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    // Recycle view scrolling up...
                    Log.d(TAG, "Scrolling up");

                } else if (dy > 0) {
                    // Recycle view scrolling down...
                    Log.d(TAG, "Scrolling down");

                    Log.i("Test1", "visible position " +
                            String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));
                    Log.i("Test1", "adapter total item count " +
                            String.valueOf(recyclerAdapter.getItemCount()));

                    Log.i("Test1", "adapter 1st condition is "
                            + String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()
                            == recyclerAdapter.getItemCount() - 10));

                    Log.i("Test1", "start position " + startPos + " end position " + endPos);

                    Log.i("Test1", "adapter 2nd (1) condition is "
                            + String.valueOf(recyclerAdapter.getItemCount() == endPos + 1));

                    Log.i("Test1", "adapter 2nd (2) condition is "
                            + String.valueOf(recyclerAdapter.getItemCount() > endPos));

                    if (layoutManager.findLastVisibleItemPosition()
                            == recyclerAdapter.getItemCount() - 10) {
                        recyclerView.stopScroll();
                        if (recyclerAdapter.getItemCount() == endPos + 1 &&
                                recyclerAdapter.getItemCount() > endPos) {
                            startPos = endPos + 1;
                            endPos = endPos + 25;
                            retrieveData(startPos, endPos);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(CallLogActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                startPos = 0;
                endPos = 25;
                isRefreshing = true;
                retrieveData(startPos, endPos);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!hasData || recyclerAdapter.getItemCount() <= 0) {
            retrieveData(0, 25);
            hasData = true;
            Log.d(TAG, "Its false right now onResume");

        } else if (recyclerAdapter.getItemCount() > 0) {
//            attachAdapterWithDataList(listData);
            Log.d(TAG, "Its true right now onResume");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecute() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onCompletion(ArrayList<RetrieveContactRecord> userRecords) {
        if (recyclerAdapter != null) {
            recyclerAdapter.appendDataToAdapter(userRecords, isRefreshing);
            isRefreshing = false;
        } else {
            recyclerAdapter = new com.contact.randeesha.CallLog.CallLogAdapter(userRecords);
            recyclerView.setAdapter(recyclerAdapter);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void retrieveData(int startPos, int endPos) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                new CallLogLoader(getApplicationContext(), this, startPos, endPos).execute("");
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public boolean isRequiredPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CALL_CONTACTS);
                return true;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_CONTACTS: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasData = false;
                    Log.d("Permission", "Granted");
                } else {
                    finish();
                    Log.d("Permission", "Denied");

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW) {
            if (resultCode == RESULT_OK) {
                SET_RESULT = RESULT_OK;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(SET_RESULT);
        super.onBackPressed();
    }
}
