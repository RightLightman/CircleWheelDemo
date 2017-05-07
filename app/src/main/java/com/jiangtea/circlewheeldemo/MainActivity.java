package com.jiangtea.circlewheeldemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private CircleWheel mCircleWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleWheel = (CircleWheel) findViewById(R.id.circlewheel);

        //给对应子控件设置点击事件
        for (int i = 0; i < mCircleWheel.getChildCount(); i++) {
            View childAt = mCircleWheel.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, v.getTag().toString(), Toast
                            .LENGTH_SHORT).show();
                }
            });
        }
    }
}
