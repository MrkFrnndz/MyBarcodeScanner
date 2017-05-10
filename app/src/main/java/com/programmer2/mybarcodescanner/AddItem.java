package com.programmer2.mybarcodescanner;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class AddItem extends ListActivity{

    EditText barcode,description;
    Button add, excel, export;

    DBHelper dbhelper = new DBHelper(this);

    //INTEGRATING EXCEL UPLOAD
    TextView uploadResultMsg;
    ListView myListview;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        init();


        //ADDING RECORDS TO TABLE ITEM
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

        //IMPORTING FILE
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("*/*");
                try {
                    startActivityForResult(fileintent, requestcode);
                } catch (ActivityNotFoundException e) {
                    uploadResultMsg.setText("No app found for importing the file.");
                }
            }
        });

        //EXPORTING FILE
        export.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase sqldb = dbhelper.getReadableDatabase(); //My Database class
            Cursor cursor = null;

            @Override
            public void onClick(View view) {
                try {
                    cursor = sqldb.rawQuery("select * from item", null);
                    int rowcount = 0;
                    int colcount = 0;
                    File sdCardDir = Environment.getExternalStorageDirectory();
                    String filename = "MySampleExport.txt";
                    // the name of the file to export with
                    File saveFile = new File(sdCardDir, filename);
                    FileWriter fw = new FileWriter(saveFile);
                    BufferedWriter bw = new BufferedWriter(fw);
                    rowcount = cursor.getCount();
                    colcount = cursor.getColumnCount();
//                    if (rowcount > 0) {
//                        cursor.moveToFirst();
//                        for (int i = 1; i < colcount; i++) {
//                            if (i != colcount - 1) {
//                                bw.write(cursor.getColumnName(i) + "\t");
//                            } else {
//                                bw.write(cursor.getColumnName(i));
//                            }
//                        }
//                        bw.newLine();
                    //I COMMENTED THE ABOVE CODE SNIPPET TO REMOVE THE FIELD NAMES WHEN EXPORTING FILE
                        for (int i = 0; i < rowcount; i++) {
                            cursor.moveToPosition(i);
                            for (int j = 1; j < colcount; j++) { //I'VE CHANGED THE VALUE OF VARIABLE int j to 1. (original value 0)
                                if (j != colcount - 1)
                                bw.write(cursor.getString(j) + "\t");
                                else
                                bw.write(cursor.getString(j));
                            }
                            bw.newLine();
//                        }
                        bw.flush();
                        uploadResultMsg.setText("Exported Successfully.");
                    }
                } catch (Exception ex) {
                    if (sqldb.isOpen()) {
                        sqldb.close();
                        uploadResultMsg.setText(ex.getMessage().toString());
                    }
                }
            }
        });

        myList = dbhelper.getAllProducts();
        if (myList.size() != 0) {
            ListView lv = getListView();
            adapter = new SimpleAdapter(AddItem.this, myList,
                    R.layout.activity_excel_items, new String[]{"barcode", "description", "quantity"}, new int[]{
                    R.id.txtItemBarcode, R.id.txtItemDescription, R.id.txtItemQuantity});
            setListAdapter(adapter);
            uploadResultMsg.setText("");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
        return;
        switch (requestCode) {
            case requestcode:
                String filepath = data.getData().getPath();
                dbhelper = new DBHelper(this);
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String tableName = "item";
                db.execSQL("delete from " + tableName);
                try {
                    if (resultCode == RESULT_OK) {
                        try {
                            FileReader file = new FileReader(filepath);
                            BufferedReader buffer = new BufferedReader(file);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();
                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split("\t",3);  // defining 3 columns with null or blank field //values acceptance
                                //id, barcode,description,quantity
                                String barcode = str[0].toString();
                                String description = str[1].toString();
                                String quantity = str[2].toString();
                                contentValues.put("barcode", barcode);
                                contentValues.put("description", description);
                                contentValues.put("quantity", quantity);
                                db.insert(tableName, null, contentValues);
                                uploadResultMsg.setText("Successfully Updated Database.");
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                        } catch (SQLException e) {
                            Log.e("Error",e.getMessage().toString());

                        } catch (IOException e) {
                            if (db.inTransaction())
                            db.endTransaction();
                            Log.e("IOException message: ", e.getMessage());
//                            Dialog d = new Dialog(this);
//                            d.setTitle(e.getMessage().toString() + "first");
//                            d.show();
                            // db.endTransaction();
                        }
                    } else {
                        if (db.inTransaction())
                        db.endTransaction();
                        Dialog d = new Dialog(this);
                        d.setTitle("Only CSV files allowed");
                        Toast.makeText(AddItem.this, "Failed CSV", Toast.LENGTH_SHORT).show();
                        d.show();
                    }
                } catch (Exception ex) {
                    if (db.inTransaction())
                    db.endTransaction();
                    Dialog d = new Dialog(this);
                    d.setTitle(ex.getMessage().toString() + "second");
                    Toast.makeText(AddItem.this, "Failed Second", Toast.LENGTH_SHORT).show();
                    d.show();
                    // db.endTransaction();
                }
        }
        myList= dbhelper.getAllProducts();

        if (myList.size() != 0) {
            ListView lv = getListView();
            adapter = new SimpleAdapter(AddItem.this, myList,
                    R.layout.activity_excel_items, new String[]{"barcode", "description", "quantity"}, new int[]{
                    R.id.txtItemBarcode, R.id.txtItemDescription, R.id.txtItemQuantity});
            setListAdapter(adapter);
            uploadResultMsg.setText("Data Imported");
        }
    }


    private void init() {
        barcode = (EditText) findViewById(R.id.etBarcode);
        description = (EditText) findViewById(R.id.etDescription);
        add = (Button) findViewById(R.id.btnAdd);
        excel = (Button) findViewById(R.id.btnExcel);
        myListview = getListView();
        uploadResultMsg = (TextView)findViewById(R.id.txtUploadResult);
        export = (Button) findViewById(R.id.btnExport);
    }
}
