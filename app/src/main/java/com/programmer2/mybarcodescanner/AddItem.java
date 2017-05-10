package com.programmer2.mybarcodescanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class AddItem extends AppCompatActivity{
    //IF YOU CAN READ THIS IT MEANS THE MERGING DOUBT HAS BEEN PROVED, CHEERS MARKEY!
    EditText barcode,description;
    Button add;

    DBHelper dbhelper = new DBHelper(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        init();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(barcode.getText().toString().trim().isEmpty() && description.getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Item item = new Item();
                        String bcode = barcode.getText().toString().trim();
                        String des = description.getText().toString().trim();
                        int qty = 0;

                        item.setBarcode(bcode);
                        item.setDescription(des);
                        item.setQuantity(qty);

                        dbhelper.insertItem(item);

                        Toast.makeText(getApplicationContext(), "Successfully Added!", Toast.LENGTH_SHORT).show();
                        barcode.setText("");
                        description.setText("");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        barcode = (EditText) findViewById(R.id.etBarcode);
        description = (EditText) findViewById(R.id.etDescription);
        add = (Button) findViewById(R.id.btnAdd);
    }
}
