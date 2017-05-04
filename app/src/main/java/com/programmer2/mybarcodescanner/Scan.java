package com.programmer2.mybarcodescanner;

import android.content.Intent;
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

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class Scan extends AppCompatActivity {

    Button ok;
    EditText enterBarcode;
    TextView  code,description,quantity;

    DBHelper dbhelper = new DBHelper(this);

    //USB Connection variables
    UsbDevice device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        init();

//        Intent intent = new Intent();
//        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//        if(device != null){
//            Toast.makeText(Scan.this, "Device attached: " + device.toString(), Toast.LENGTH_SHORT).show();
//        }
//        else if(device == null){
//            Toast.makeText(Scan.this, "Device attached: " + "NONE!", Toast.LENGTH_SHORT).show();
//        }


        enterBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Hey Mark! -","Before TextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Hey Mark! -","ON TextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String mCode = enterBarcode.getText().toString().trim();

                    int result = dbhelper.searchForItem(mCode);

                    if(result > 0){
                        printItem(mCode);
                        enterBarcode.setText("");
                    }
                    else if(result == 0){
                        Toast.makeText(Scan.this, "INCORRECT BARCODE!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
        ok = (Button) findViewById(R.id.btnOk);
        code = (TextView) findViewById(R.id.txtCode);
        description = (TextView) findViewById(R.id.txtDescription);
        quantity = (TextView) findViewById(R.id.txtQuantity);
    }
}