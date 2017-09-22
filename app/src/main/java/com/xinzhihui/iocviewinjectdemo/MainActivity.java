package com.xinzhihui.iocviewinjectdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ange.annotation.Bind;
import com.ange.ioc_api.ViewInjector;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.tv_hello)
   TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        textView.setText("ange");
    }
}
