package com.contact.randeesha;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.provider.ContactsContract;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;

import com.contact.randeesha.Add_Update.UpdateActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

public class  UserDetailOperationActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_CONTACTS = 0;
    private static int UPDATE_TOKEN = -1;


    public String name, id, number, address, street, city, location, intro;
    public byte[] imgByte;
    public Context context = UserDetailOperationActivity.this;
    Button btCall, btSms, btEmail, btLocation;
    DBForm dbForm;
    RetrieveContactRecord record;
    private TextView mName, mPhoneNumber, mEmailAddress, mMapLocation, mIntro;
    private ImageView mDisplayPic;

    Button editContact , deleteContact , button_fvt;

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
        setContentView(R.layout.activity_user_detail_operation);
        dbForm = DBForm.getInstance(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mDisplayPic = (ImageView) findViewById(R.id.profile_image);

        mName = (TextView) findViewById(R.id.tv_name);
        mPhoneNumber = (TextView) findViewById(R.id.tv_phoneNumber);
        mEmailAddress = (TextView) findViewById(R.id.tv_emailAddress);
        mMapLocation = (TextView) findViewById(R.id.tv_location);
        mIntro = (TextView) findViewById(R.id.tv_info);

        btCall = (Button) findViewById(R.id.bt_call);
        btSms = (Button) findViewById(R.id.bt_sms);
        btEmail = (Button) findViewById(R.id.bt_email);
        btLocation = (Button) findViewById(R.id.bt_location);

        editContact = (Button) findViewById(R.id.edit_contact);
        deleteContact = (Button) findViewById(R.id.delete_contact);
        button_fvt = (Button) findViewById(R.id.button_fvt);

        if (savedInstanceState == null) {
            getData();

        } else {
            id = savedInstanceState.getString("ID_INTENT");
            name = savedInstanceState.getString("NAME_SAVED");
            address = savedInstanceState.getString("EMAIL_SAVED");
            number = savedInstanceState.getString("PHONE_SAVED");
            street = savedInstanceState.getString("STREET_SAVED");
            city = savedInstanceState.getString("CITY_SAVED");
            intro = savedInstanceState.getString("INTRO_SAVED");
            imgByte = savedInstanceState.getByteArray("PICTURE_SAVED");
            setData();
        }


        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallOnClick();
            }
        });

        btSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmsOnClick();
            }
        });

        btEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEmailOnClick();
            }
        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocationOnClick();
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onContactDeleted();
            }
        });

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendDataToUpdate();
                } catch (TransactionTooLargeException e) {
                    Toast.makeText(context, "Too large data to handle", Toast.LENGTH_SHORT).show();
                };
            }
        });


        button_fvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         //       addedToFavRecords();
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
            }
        });



        mIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intro.isEmpty()) {
                    Snackbar.make(findViewById(R.id.sv_scroll), "Intro not found",
                                    Snackbar.LENGTH_LONG)
                            .setAction("Add", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        sendDataToUpdate();
                                    } catch (TransactionTooLargeException e) {
                                        Toast.makeText(context,
                                                "Too large data to handle",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void openSmsOnClick() {
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }



    private void sendDataToUpdate() throws TransactionTooLargeException {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("ID_INTENT", id);

        startActivityForResult(intent, 1);
    }

    private void getData() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("ID_INTENT");
            record = dbForm.getSingleContactById(id);
            name = record.getName();
            address = record.getEmail();
            number = record.getPhone();
            intro = record.getIntro();
            street = record.getStreet();
            city = record.getCity();
            imgByte = record.getPicture();
        }

        setData();

    }

    private void setData() {

        mName.setText(name);
        mEmailAddress.setText(address);
        mPhoneNumber.setText(number);
        mIntro.setText(intro);

        location = street + ", " + city;
        if (!location.equals(", ")) {
            mMapLocation.setText(location);
        } else {
            mMapLocation.setText("");
        }

        if (imgByte != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imgByte);
            mDisplayPic.setImageBitmap(BitmapFactory.decodeStream(imageStream));
        } else {
            mDisplayPic.setImageResource(R.drawable.ic_person_pin);
        }
        dbForm.close();
    }


    private void moveToPhone() {
        AlertDialog.Builder dialog = new AlertDialog
                .Builder(UserDetailOperationActivity.this,R.style.AlertDialog)
                .setTitle("Confirmation")
                .setMessage("Move to your phone contacts?")
                .setPositiveButton("Move to Phone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                        addContactIntent
                                .setType(ContactsContract.Contacts.CONTENT_TYPE);
                        addContactIntent
                                .putExtra(ContactsContract.Intents.Insert.NAME, name);
                        addContactIntent
                                .putExtra(ContactsContract.Intents.Insert.PHONE, number);
                        addContactIntent
                                .putExtra(ContactsContract.Intents.Insert.EMAIL, address);
                        addContactIntent
                                .putExtra(ContactsContract.Intents.Insert.NOTES, intro);
                        addContactIntent
                                .putExtra(ContactsContract.Intents.Insert.POSTAL, location);
                        if (addContactIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(addContactIntent);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void onContactDeleted() {

        final AlertDialog.Builder dialog;

        dialog = new AlertDialog.Builder(this,R.style.AlertDialog)
                .setTitle("Confirmation")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Delete Contact?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Snackbar.make(findViewById(R.id.sv_scroll), "Canceled",
                                        Snackbar.LENGTH_SHORT)
                                .show();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBForm dbForm = new DBForm(context);
                        dbForm.deleteContactByName(name);
                        dbForm.close();
                        Toast.makeText(context, " Contact Deleted ",
                                Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void setCallOnClick() {

        Intent mCallIntent = new Intent(Intent.ACTION_CALL);
        mCallIntent.setData(Uri.parse("tel:" + number));
        try {
            if (isCallPermissionGranted()) {
                startActivity(mCallIntent);
            } else {
                Snackbar.make(findViewById(R.id.sv_scroll),
                                "Grant Permission to call",
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.sv_scroll), "Try Again",
                            Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    private void setEmailOnClick() {

        if (!address.isEmpty()) {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            emailIntent.setType("plain/text");

            startActivity(Intent.createChooser(emailIntent, " Send... "));

        } else {
            Snackbar.make(findViewById(R.id.sv_scroll), "Email not found",
                            Snackbar.LENGTH_LONG)
                    .setAction("Add", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                sendDataToUpdate();
                            } catch (TransactionTooLargeException e) {
                                Toast.makeText(context,
                                        "Too large data to handle", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }

    }

    private void setLocationOnClick() {

        final Uri[] uri = new Uri[1];
        if (!location.equals(", ")) {

            uri[0] = Uri.parse("geo:0,0?q=" + location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri[0]);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);


        } else {
            Snackbar.make(findViewById(R.id.sv_scroll), "Location not found",
                            Snackbar.LENGTH_LONG)
                    .setAction("Add", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                sendDataToUpdate();
                            } catch (TransactionTooLargeException e) {
                                Toast.makeText(context,
                                        "Too large data to handle", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }
    }

    private void shareDetails() {
        String mimeType = "text/plain";
        String title = "Share with,";
        String text = name.toUpperCase() + "\nPhone:" + number + "\nEmail:" + address;
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(text)
                .startChooser();
    }

    public boolean isCallPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
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
                    Snackbar.make(findViewById(R.id.sv_scroll), "Permission granted",
                                    Snackbar.LENGTH_SHORT)
                            .show();

                } else {

                    Snackbar.make(findViewById(R.id.sv_scroll), "Permission Denied",
                                    Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("ID_INTENT", id);
        outState.putString("NAME_SAVED", name);
        outState.putString("EMAIL_SAVED", address);
        outState.putString("PHONE_SAVED", number);
        outState.putString("STREET_SAVED", street);
        outState.putString("CITY_SAVED", city);
        outState.putString("INTRO_SAVED", intro);
        outState.putByteArray("PICTURE_SAVED", imgByte);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_detail_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e("Menu Error", e.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.delete_contact:

                onContactDeleted();

                break;*/
            case R.id.export_contact:

                moveToPhone();

                break;
            case android.R.id.home:

                onBackPressed();
//                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right );

                break;
            case R.id.share_contact:

                shareDetails();

                break;
          /*  case R.id.edit_contact:

                try {
                    sendDataToUpdate();
                } catch (TransactionTooLargeException e) {
                    Toast.makeText(context, "Too large data to handle", Toast.LENGTH_SHORT).show();
                }

                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                UPDATE_TOKEN = 2;
                getData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (UPDATE_TOKEN == 2) {
            setResult(UPDATE_TOKEN);
            UPDATE_TOKEN = -1;
        }
        super.onBackPressed();
    }

    /*private boolean addedToFavRecords() {

        String id = record.getId();
        String name = record.getName();
        String email = record.getEmail();
        String phone = record.getPhone();
        String street = record.getStreet();
        String city = record.getCity();
        String intro = record.getIntro();
        String picture = record.getPicture();

        boolean res = dbForm.insertFinavValue(id,name,email,phone,street,city,intro,picture);
        if(res== true){
            Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Not Added to favourites", Toast.LENGTH_SHORT).show();
        }
        return true;

    }*/

}
