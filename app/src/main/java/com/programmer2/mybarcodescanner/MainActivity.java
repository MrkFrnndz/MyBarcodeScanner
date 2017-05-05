//package com.programmer2.mybarcodescanner;
//
//import android.content.Intent;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//public class MainActivity extends AppCompatActivity {
//
//    private Button add,scan;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        init();
//
//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,Scan.class);
//                startActivity(intent);
//            }
//        });
//
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,AddItem.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void init() {
//        scan = (Button) findViewById(R.id.btnScan);
//        add = (Button) findViewById(R.id.btnAdd);
//    }
//}
