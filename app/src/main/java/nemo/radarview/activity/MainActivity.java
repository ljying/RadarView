package nemo.radarview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import nemo.radarview.view.RadarView;
import wh.sesamecreditview.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadarView view = (RadarView) findViewById(R.id.radar_view);
        float[] scores = new float[]{83, 70, 40, 70, 60, 70};
        view.setData(scores);
        view.setOnItemClickListener(new RadarView.OnItemClickListener() {
            @Override
            public void onFraudPhoneClick(float value) {
                Toast.makeText(getApplicationContext(),"点击诈骗电话",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMobileOptimizeClick(float value) {
                Toast.makeText(getApplicationContext(),"点击手机优化",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTrafficClick(float value) {
                Toast.makeText(getApplicationContext(),"点击流量异常",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetSafeClick(float value) {
                Toast.makeText(getApplicationContext(),"点击上网安全",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSpamMessageClick(float value) {
                Toast.makeText(getApplicationContext(),"点击垃圾短信",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
