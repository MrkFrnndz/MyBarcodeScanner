package com.programmer2.mybarcodescanner;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class Scan extends AppCompatActivity {

    Button scan,add;
    EditText enterBarcode;
    TextView  code,description,quantity,usbresult;

    DBHelper dbhelper = new DBHelper(this);

    //USB Connection variables
    PendingIntent mPermissionIntent;
    UsbDevice usbDevice;
    UsbManager usbManager;
    private static final String ACTION_USB_PERMISSION = "com.mobilemerit.usbhost.USB_PERMISSION";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        init();

//        int usbAttachResult = checkUSBattached();
//        if( usbAttachResult > 0){
//            Toast.makeText(Scan.this, "OTG Connected!", Toast.LENGTH_SHORT).show();
//
//            enterBarcode.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                    Log.d("Hey Mark! -","Before TextChanged");
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                    Log.d("Hey Mark! -","ON TextChanged");
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    try {
//                        String mCode = enterBarcode.getText().toString().trim();
//
//                        int result = dbhelper.searchForItem(mCode);
//
//                        if(result > 0){
//                            printItem(mCode);
//                            enterBarcode.setText("");
//                        }
//                        else if(result == 0){
//                            Toast.makeText(Scan.this, "INCORRECT BARCODE!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }else {
//            Toast.makeText(Scan.this, "NO DEVICE ATTACH", Toast.LENGTH_SHORT).show();
//        }


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkUSBattached();
                    String mCode = enterBarcode.getText().toString().trim();

                    int result = dbhelper.searchForItem(mCode);

                    if(result > 0){
                        printItem(mCode);
                    }
                    else if(enterBarcode.getText().toString().isEmpty()){
                        Toast.makeText(Scan.this, "Enter the barcode!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Scan.this,AddItem.class);
                startActivity(intent);
            }
        });

    }

//    private void searchAction(final String code) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                printItem(code);
//            }
//        });
//    }

    private void printItem(String rCode) {
        String query = "SELECT * FROM item WHERE barcode=" + rCode;

        Cursor cursor = dbhelper.queryDataRead(query);

        String mCode = "",mDes = "";
        int mQty = 0, mId = 0;

        if (cursor != null) {
            cursor.moveToFirst();
            mId = cursor.getInt(0);
            mCode = cursor.getString(1);
            mDes = cursor.getString(2);
            mQty = cursor.getInt(3);
        }
        code.setText(mCode);
        description.setText(mDes);
        String cMqty = Integer.toString(mQty + 1);
        quantity.setText(cMqty);

        int newCount = Integer.parseInt(quantity.getText().toString());
        mUpdate(mId,newCount);
    }

    public void mUpdate(int id,int newCount){
        dbhelper.updateQuantity(id,newCount);
        dbhelper.close();
    }

    private void init() {
        enterBarcode = (EditText) findViewById(R.id.etInputBarCode);
        scan = (Button) findViewById(R.id.btnOk);
        code = (TextView) findViewById(R.id.txtCode);
        description = (TextView) findViewById(R.id.txtDescription);
        quantity = (TextView) findViewById(R.id.txtQuantity);
        add = (Button) findViewById(R.id.btnAdd);
        usbresult = (TextView) findViewById(R.id.txtResult);

    }


    //CONNECTED USB CHECKER//CONNECTED USB CHECKER//
    private void checkUSBattached() {
        try{
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        /*
         * this block required if you need to communicate to USB devices it's
         * take permission to device
         * if you want than you can set this to which device you want to communicate
         */
            // ------------------------------------------------------------------
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            // -------------------------------------------------------------------
            HashMap<String , UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            String i = "";
            while (deviceIterator.hasNext()) {
                usbDevice = deviceIterator.next();
                usbManager.requestPermission(usbDevice, mPermissionIntent);
                i += "\n" + "DeviceID: " + usbDevice.getDeviceId() + "\n"
                        + "DeviceName: " + usbDevice.getDeviceName() + "\n"
                        + "DeviceClass: " + usbDevice.getDeviceClass() + " - "
                        + "DeviceSubClass: " + usbDevice.getDeviceSubclass() + "\n"
                        + "VendorID: " + usbDevice.getVendorId() + "\n"
                        + "ProductID: " + usbDevice.getProductId() + "\n";
            }
            usbresult.setText(i);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                        }
                    } else {
                        Log.d("ERROR", "permission denied for device " + device);
                    }
                }
            }
        }
    };
}