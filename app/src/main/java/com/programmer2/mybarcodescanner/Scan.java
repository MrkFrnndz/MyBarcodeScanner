package com.programmer2.mybarcodescanner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class Scan extends AppCompatActivity {

    Button scan,add;
    EditText enterBarcode;
    TextView  code,description,quantity;
    Switch mySwitch;
    View dummyView;

    DBHelper dbhelper = new DBHelper(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //CASTING VIEWS
        init();



        manualScan();

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    enterBarcode.setText("");
                    Toast.makeText(Scan.this, "Barcode Scan ON", Toast.LENGTH_SHORT).show();
                    scan.setEnabled(false);
                    scan.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.myColorAppBarLayout));
                    scan.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_buttonscan_disable));//Change button scan color
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterBarcode.getWindowToken(), 0);
                    automaticScan();
                }
                if(!isChecked) {
                    Toast.makeText(Scan.this, "Manual Scan", Toast.LENGTH_SHORT).show();
                    scan.setEnabled(true);
                    scan.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.myColorTextWhite));
                    scan.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.custom_button_scan));//Change button scan color
                    manualScan();
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

    private void init() {
        enterBarcode = (EditText) findViewById(R.id.etInputBarCode);
        scan = (Button) findViewById(R.id.btnOk);
        code = (TextView) findViewById(R.id.txtCode);
        description = (TextView) findViewById(R.id.txtDescription);
        quantity = (TextView) findViewById(R.id.txtQuantity);
        add = (Button) findViewById(R.id.btnAdd);
        mySwitch = (Switch) findViewById(R.id.switchManual);
        dummyView = findViewById(R.id.dummyView);
    }

    private void manualScan() {
        enterBarcode.removeTextChangedListener(myTextWatcher);

        scan.setOnClickListener(new View.OnClickListener() {
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
                    else if(result == 0){
                        Toast.makeText(Scan.this, "Barcode no match!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void automaticScan() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        enterBarcode.addTextChangedListener(myTextWatcher);
        imm.hideSoftInputFromWindow(enterBarcode.getWindowToken(), 0);
    }

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

    private TextWatcher myTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            try {
                String mCode = enterBarcode.getText().toString().trim();
                int result = dbhelper.searchForItem(mCode);

                if(mCode.length() > 12){
                    if(result > 0) {
                        printItem(mCode);
                        enterBarcode.removeTextChangedListener(myTextWatcher);
                        enterBarcode.setText("");
                        dummyView.requestFocus();

                        enterBarcode.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                enterBarcode.requestFocus();
                            }}, 1000);
                    }
                    else if (result <= 0){
                        enterBarcode.removeTextChangedListener(myTextWatcher);
                        enterBarcode.setText("");
                        dummyView.requestFocus();

                        enterBarcode.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                enterBarcode.requestFocus();
                            }
                        }, 1000);
                        Toast.makeText(Scan.this, "Barcode invalid!", Toast.LENGTH_SHORT).show();
                    }
                    enterBarcode.addTextChangedListener(myTextWatcher);

                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }};

}