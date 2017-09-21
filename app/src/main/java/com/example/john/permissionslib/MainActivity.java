package com.example.john.permissionslib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.lib.NeedsPermission;
import com.example.john.lib.OnNeverAskAgain;
import com.example.john.lib.OnPermissionDenied;
import com.example.john.lib.RunTimePermissions;

@RunTimePermissions
public class MainActivity extends AppCompatActivity {

    private TextView mHelloWorld;

    private final static int READ_STORAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelloWorld = (TextView) findViewById(R.id.hello_world);
        mHelloWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.e("ziq", "not granted read storage");
                    Log.e("ziq", "requestPermissions");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
                }else{
                    Log.e("ziq", "granted read storage");
                }
            }
        });


        TextView grantPermission = (TextView) findViewById(R.id.grant_permission);
        grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("ziq", "onRequestPermissionsResult");
        switch (requestCode){
            case READ_STORAGE_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("ziq", "granted allow");
                }else{
                    Log.e("ziq", "granted deny");
                    if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Log.e("ziq", "deny by never ask, show an explanation");
                    }else{
                        Log.e("ziq", "really granted deny");
                    }
                }
                break;
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private void openSDcard(String url){
        Log.e("ziq", "openSDcard permission granted");
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    private void openSDcardDeny(String url){
        Log.e("ziq", "openSDcard permission deny");
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    private void openSDcardNeverAsk(String url){
        Log.e("ziq", "openSDcard never ask again");
    }

}
