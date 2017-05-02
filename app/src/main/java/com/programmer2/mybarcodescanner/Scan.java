package com.programmer2.mybarcodescanner;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        init();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String mCode = enterBarcode.getText().toString().trim();

                    int result = dbhelper.searchForItem(mCode);

                    if(result > 0){
                        printItem(mCode);
//                        Toast.makeText(Scan.this, "Item punched!", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(Scan.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
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