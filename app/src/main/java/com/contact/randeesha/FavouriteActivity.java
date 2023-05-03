package com.contact.randeesha;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// DataListener is an Interface which will Listen for the Data from the DataBase.
public class FavouriteActivity extends AppCompatActivity  {

    public static final int DELETE_REQUEST_CODE = 1;
    public static final int UPDATE_RESULT_OK = 2;
    public static final int ADD_REQUEST_CODE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 1;

    public ListView mListNames;
    public DBForm dbForm;
    public EditText mSearch;
    public TextView mInformationText;
    public com.github.clans.fab.FloatingActionButton mAddFloatingButton;
    public com.github.clans.fab.FloatingActionButton mCallLogFloatingButton;
    public CustomListAdapter mCustomListAdapter;
    public TextView switcher;
    public ProgressDialog progressDialog;
    public String findName;
    public String sendId;
    public String number;
    boolean isDataChanged = true;
    boolean onScroll = false;

    /*int dark = 0;*/

    int dark;

    SharedPrefs sharedPrefs;

    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_favourite);

        // Database class instance to use methods of database class.
        dbForm = DBForm.getInstance(this);

        mListNames = (ListView) findViewById(android.R.id.list);

        mSearch = (EditText) findViewById(R.id.search_names1);
        mSearch.setSingleLine(true);

        mInformationText = (TextView) findViewById(R.id.tv_information1);

        switcher = (TextView) findViewById(R.id.ts_alphabets1);

        mInformationText.setText("No Contacts");
        mListNames.setEmptyView(mInformationText);

        // To populate the context menu we need to register a view for it which here is a Listview.
        registerForContextMenu(mListNames);

        // The search bar function to search through The list of contacts via Name or Description.
        mSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (mSearch.hasFocus())
                        mCustomListAdapter.filter(charSequence.toString());
                } catch (NullPointerException nullPointer) {
                    Log.e("onTextChanged", nullPointer.getLocalizedMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // The list where click on contact will get you the Details of that contact.
        mListNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                position = mListNames.getPositionForView(view);
                try {
                    Intent intent = new Intent(FavouriteActivity.this,
                            UserDetailOperationActivity.class);
                    findName = mCustomListAdapter.getItem(position).getName();
                    sendId = dbForm.getIdByName(findName);

                    Log.d("Name Value", sendId);
                    intent.putExtra("ID_INTENT", sendId);

                    startActivityForResult(intent, 1);
//                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FavouriteActivity.this, "No Contact Found",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        // get the first visible position for the Alphabets on list scroll.
        mListNames.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    switcher.setVisibility(View.GONE);
                    Log.d("OnScrollCHanged", "not scrolling");
                    onScroll = false;
                }
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    onScroll = true;
                }
            }

            // When user will scroll the list, This function will get the first letter of
            // first visible item in the list
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.d("OnScroll", "scrolling");

                if (mCustomListAdapter != null && !view.isLayoutRequested() && onScroll) {
                    int firstChar = mListNames.getFirstVisiblePosition();
                    if (mListNames.getChildAt(0).getTop() < 0) {
                        firstChar = (mListNames.getFirstVisiblePosition() + 1);
                    }
                    switcher.setText(
                            mCustomListAdapter
                                    .getItem(firstChar)
                                    .getName().subSequence(0, 1));
                    switcher.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    // Creates the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // The menu options and their operations are defined here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.back_home:
                Intent i = new Intent(FavouriteActivity.this, MainActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
