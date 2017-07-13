package com.bway.zxingdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mSee;
    private TextView mTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        ViewfinderView
    }

    private void initView() {
        mSee = (Button) findViewById(R.id.button);
        mTextview = (TextView) findViewById(R.id.textView);
        mSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        //内容
        final EditText contentET = (EditText) findViewById(R.id.create_qr_content);
        //显示二维码图片
        final ImageView imageView = (ImageView) findViewById(R.id.create_qr_iv);
        //是否添加Logo
        final CheckBox addLogoCB = (CheckBox) findViewById(R.id.create_qr_addLogo);
        Button createQrBtn = (Button) findViewById(R.id.create_qr_btn);

        createQrBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String filePath = getFileRoot(MainActivity.this) + File.separator
                        + "qr_" + System.currentTimeMillis() + ".jpg";

                //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = QRCodeUtil.createQRImage(contentET.getText().toString().trim(), 800, 800,
                                addLogoCB.isChecked()? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : null,
                                filePath);

                        if (success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                                }
                            });
                        }
                    }
                }).start();

            }
        });
    }
    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode ==0){
            Bundle extras = data.getExtras();
            String result = extras.getString("result");
            mTextview.setText(result);
        }
    }
}
