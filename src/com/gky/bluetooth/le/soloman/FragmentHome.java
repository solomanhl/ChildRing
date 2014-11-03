package com.gky.bluetooth.le.soloman;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;import android.location.GpsStatus.Listener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentHome extends Fragment {

	public GlobalVar appState;
	public TextView tv_goal, tv_step, tv_caroli, tv_distance, tv_sporttime;
	public ImageView iv_goal;
	public TextView tv_location;//定位
//	public ProgressBar p_goal;
	//public sportDataThread st = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		appState = (GlobalVar) getActivity().getApplicationContext(); // 获得全局变量
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return inflater.inflate(R.layout.fragment_sleep, container, false);	
		
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		findView(view);
		
		
		
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);

		messageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            	appState.gettarget();
            }
        }, 200);
		
		new sportDataThread().start();
		
		
        return view;       
	}

	public int cnt = 0;
	private Handler messageHandler;
	private void updateHandler(Object obj) {
		// 创建一个Message对象，并把得到的网络信息赋值给Message对象
		Message message = Message.obtain();// 第一步
		message = Message.obtain();// 第一步
		message.obj = obj; // 第二步
		messageHandler.sendMessage(message);// 第三步
	}
	
	// 子类化一个Handler
	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// 更新UI
			if (!((String) msg.obj == null)) {
//				if (cnt == 0) {
//					appState.gettarget();									
//				}else if (cnt ==1){
//					appState.getbaseinfo();						
//				}
//				if (cnt <5) {
//					cnt++;
//				}
				if ("sportdata".equals((String) msg.obj)) {
					tv_goal.setText(String.valueOf(appState.complete) + "%");
					tv_step.setText(String.valueOf(appState.step));
					tv_caroli.setText(String.valueOf(appState.caroli));
					tv_distance.setText(String.valueOf(appState.distance));
					tv_sporttime.setText(appState.sptime);
				}else if ("location".equals((String) msg.obj)){
					tv_location.setText(latLng);
				}

			}
		}
	}
	
	
	// 更新数据进程----------------------------------------
	public class sportDataThread extends Thread {
		public sportDataThread() {
			if (!appState.runThread){
				appState.StartSportDateForTime();		
				appState.runThread = true;	
			}
			
		}

		
		@Override
		public void run() {
			while (appState.runThread && !this.isInterrupted()) {
//				System.out.println("sportDataThread run again");
				updateHandler("sportdata");
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	//==================end thread
	
	/*
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		if (mAudioCapture != null) {
            mAudioCapture.stop();
            mAudioCapture.release();
            mAudioCapture = null;
        }
	}
	
	public void onClose() {
		if (mAudioCapture != null) {
            mAudioCapture.stop();
            mAudioCapture.release();
            mAudioCapture = null;
        }
	}
	*/
	
	public void findView(View view){
//		p_goal = (ProgressBar) view.findViewById(R.id.p_goal);
		iv_goal = (ImageView) view.findViewById(R.id.iv_goal);
		tv_goal = (TextView) view.findViewById(R.id.tv_goal);
		tv_step = (TextView) view.findViewById(R.id.tv_step);
		tv_caroli = (TextView) view.findViewById(R.id.tv_caroli);
		tv_distance= (TextView) view.findViewById(R.id.tv_distance);
		tv_sporttime= (TextView) view.findViewById(R.id.tv_sporttime);
		
		tv_location = (TextView) view.findViewById(R.id.tv_location);
	}
	@Override
	public void onResume(){
		super.onResume();
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
				Looper looper = Looper.myLooper();
				// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);
		location();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		manager.removeUpdates(locationListener);;
//		manager.removeGpsStatusListener((Listener) locationListener);
	}
	

	//--------------------------------------------------------------------------------------------
		// 创建lcoationManager对象

		private LocationManager manager;
		private static final String TAG = "LOCATION:";

		public void location() {
			// 获取系统的服务，
			manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			Location locationNet = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Location locationGPS = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			// 重要函数，监听数据测试
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 5, locationListener); //6秒 5米
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 5, locationListener); //6秒 5米
			
			// 第一次获得设备的位置
			updateLocation(locationNet);
			updateLocation(locationGPS);
		}		
		// 创建一个事件监听器
		private final LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateLocation(location);
			}

			public void onProviderDisabled(String provider) {
				updateLocation(null);
				Log.i(TAG, "Provider now is disabled..");
			}

			public void onProviderEnabled(String provider) {
				Log.i(TAG, "Provider now is enabled..");
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

		};

		public String latLng;
		// 获取用户位置的函数，利用Log显示
		private void updateLocation(Location location) {
			if (location != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.SSSZ");
				String time = sdf.format(new Date(location.getTime()));
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				double altitude = location.getAltitude();
				float bearing = location.getBearing();
				float speed = location.getSpeed();
				float accuracy = location.getAccuracy();
				String provider = location.getProvider();
				latLng = "纬度:" + lat + "  经度:" + lng + " 高度:" + altitude + 
						" 方向角:" + bearing + " 速度：" + speed + 
						" 准确度:" + accuracy + " 服务商:" + provider +
						time.toString() + ".";
			} else {
				latLng = "Can't access your location.";
			}
			
			
			updateHandler("location");
			Log.i(TAG, "The location has changed..");
			Log.i(TAG, "Your Location:" + latLng);

		}
	
}



