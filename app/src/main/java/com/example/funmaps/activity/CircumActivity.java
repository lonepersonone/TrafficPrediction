package com.example.funmaps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.example.funmaps.R;
import com.example.funmaps.fragment.FirstpageFragment;
import com.example.funmaps.fragment.NearbyFragment;
import com.example.funmaps.fragment.RouteFragment;

public class CircumActivity extends AppCompatActivity {
    RadioGroup groupHeader;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment firstpageFragment;
    Fragment nearbyFragment;
    Fragment routeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_circum);
        groupHeader=(RadioGroup)this.findViewById(R.id.groupheader);
        fragmentManager = getSupportFragmentManager();
        groupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                fragmentTransaction = fragmentManager.beginTransaction();
                if (firstpageFragment!=null){
                    fragmentTransaction.hide(firstpageFragment);
                }
                if (nearbyFragment!=null){
                    fragmentTransaction.hide(nearbyFragment);
                }
                if (routeFragment!=null){
                    fragmentTransaction.hide(routeFragment);
                }


                if (checkedId==R.id.rbtnFirstpage){
                    if (firstpageFragment==null){
                        firstpageFragment=new FirstpageFragment();
                        fragmentTransaction.add(R.id.frameLayout,firstpageFragment,"firstpage");
                    }
                    fragmentTransaction.show(firstpageFragment);
                }
                else if(checkedId==R.id.rbtnNearby){
                    if (nearbyFragment==null){
                        nearbyFragment=new NearbyFragment();
                        fragmentTransaction.add(R.id.frameLayout,nearbyFragment,"nearby");
                    }
                    fragmentTransaction.show(nearbyFragment);
                }
                else if(checkedId==R.id.rbtnRoute){
                    if (routeFragment==null){
                        routeFragment=new RouteFragment();
                        fragmentTransaction.add(R.id.frameLayout,routeFragment,"route");
                    }
                    fragmentTransaction.show(routeFragment);
                }

                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}