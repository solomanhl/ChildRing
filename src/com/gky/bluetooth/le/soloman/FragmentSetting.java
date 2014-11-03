package com.gky.bluetooth.le.soloman;


import com.gky.bluetooth.le.soloman.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FragmentSetting extends Fragment {

	public GlobalVar appState;
	public RadioGroup radioGroup1;
	public RadioButton radio0, radio1;
	public EditText editText2, editText3, editText4, editText5, et_tarstep, et_device;
	public Button submit;
	public Chronometer chronometer1;
	
	public byte c[] =new byte [16];
	public byte c1[] =new byte [16];
	public byte c2[] =new byte [16];
	public byte c3[] =new byte [16];
	
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
		
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		findView(view);		
		
		appState.timeup = false;
		
		if (appState.sex == 0x01){	//男
			radio0.setChecked(true);
		}else if (appState.sex == 0x00){	//女
			radio1.setChecked(true);
		}
		editText2.setText(String.valueOf(appState.age) );
		editText3.setText(String.valueOf(appState.height) );
		editText4.setText(String.valueOf(appState.weight) );
		editText5.setText(String.valueOf(appState.length) );
		
		et_tarstep.setText(String.valueOf(appState.devicetarget));
		
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);
				
        return view;       
	}

	
	
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
		radioGroup1 = (RadioGroup) view.findViewById(R.id.radioGroup1);
		radio0 = (RadioButton) view.findViewById(R.id.radio0);
		radio1 = (RadioButton) view.findViewById(R.id.radio1);
		editText2 = (EditText) view.findViewById(R.id.editText2);
		editText3 = (EditText) view.findViewById(R.id.editText3);
		editText4 = (EditText) view.findViewById(R.id.editText4);
		editText5 = (EditText) view.findViewById(R.id.editText5);
		et_tarstep = (EditText) view.findViewById(R.id.et_tarstep);
		et_device = (EditText) view.findViewById(R.id.et_device);
		submit = (Button) view.findViewById(R.id.submit);
//		button1 = (Button) view.findViewById(R.id.button1);
		chronometer1 = (Chronometer) view.findViewById(R.id.chronometer1);
		
		chronometer1.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer chronometer) {
						// 如果开始计时到现在超过了xx ms
						if (SystemClock.elapsedRealtime() - chronometer.getBase() > 200) {
							chronometer.stop();
							appState.timeup = true;
							Log.i("info", "timeup");
						}
					}
				}); 
		
		radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == radio0.getId()) {
					appState.sex = 0x01;
					Log.i("info", "男");
				}
				if (checkedId == radio1.getId()) {
					appState.sex = 0x00;
					Log.i("info", "女");								
				}				
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {  
		      
		    @Override  
		    public void onClick(View v) {  
		        // TODO Auto-generated method stub  
		        Log.i("info", "submit onClick");  
		        //停止运动
//		        appState.StopSportDateForTime();
//		        appState.timeup = false;
//		        chronometer1.setBase(SystemClock.elapsedRealtime());  
//		        chronometer1.start();
//		      while (!appState.timeup);
		        
		        //基本信息 0x02 AA BB CC DD EE 00 00 00 00 00 00 00 00 00 CRC 
		        c1[0] = 0x02; 
		        c1[1] = (byte) (appState.sex & 0xff);	//A
		        c1[2] = (byte) (Integer.valueOf(editText2.getText().toString()) &  0xff);	//B
		        c1[3] = (byte) (Integer.valueOf(editText3.getText().toString()) &  0xff);		//C
		        c1[4] = (byte) (Integer.valueOf(editText4.getText().toString()) &  0xff);		//D
		        c1[5] = (byte) (Integer.valueOf(editText5.getText().toString()) &  0xff);		//E
		        c1[6] = 0x00;	//F
				c1[7] = 0x00;	//G
				c1[8] = 0x00;	//H
				c1[9] = 0x00;	//I
				c1[10] = 0x00;	//J
				c1[11] = 0x00;	//K
				c1[12] = 0x00;	//L
				c1[13] = 0x00;	//M
				c1[14] = 0x00;	//N
				c1[15] = c1[0];	//CRC
				for (int i = 1; i<15; i++){
					c1[15] += c1[i];
				}
				c1[15] &= 0xff;						
//				appState.sendData(c1);
				
				appState.timeup = false;
		        chronometer1.setBase(SystemClock.elapsedRealtime());  
//		        chronometer1.start();
//				while (!appState.timeup);
				
				//目标步数  0x0B AA BB CC DD EE FF 00 00 00 00 00 00 00 00 CRC 
				int tar = Integer.valueOf(et_tarstep.getText().toString());
				c2[0] = 0x0B; 
				c2[1] = (byte) ((tar >> 16) & 0xff);	//A
				c2[2] = (byte) ((tar >> 8) & 0xff); // B
				c2[3] = (byte) (tar & 0xff); // C
				c2[4] =(byte) ((tar >> 16) & 0xff); // D
				c2[5] = (byte) ((tar >> 8) & 0xff); // E
				c2[6] = (byte) (tar & 0xff); // F
				c2[7] = 0x00; // G
				c2[8] = 0x00; // H
				c2[9] = 0x00; // I
				c2[10] = 0x00; // J
				c2[11] = 0x00; // K
				c2[12] = 0x00; // L
				c2[13] = 0x00; // M
				c2[14] = 0x00; // N
				c2[15] = c2[0]; // CRC
				for (int i = 1; i < 15; i++) {
					c2[15] += c2[i];
				}
				c2[15] &= 0xff;
//				appState.sendData(c2);
				
				appState.timeup = false;
		        chronometer1.setBase(SystemClock.elapsedRealtime());  
//		        chronometer1.start();
//				while (!appState.timeup);
				
				//设备名称  0x3D AA BB CC DD EE FF GG HH II JJ KK LL MM NN CRC 
//				String s = et_device.getText().toString();
//				char b[] = s.toCharArray();
//				if (s.length() >=14){
//					for (int i=0; i<14; i++){
//						c3[i+1] = (byte) b[i];
//					}
//				}else{
//					for (int i=0; i<s.length();i++){
//						c3[i+1] = (byte) b[i];
//					}
//					for (int i=s.length(); i<15;i++){
//						c3[i+1] = 0x00;
//					}
//				}
//				
//				c3[0] = 0x3D; 
//				c3[15] = c3[0]; // CRC
//				for (int i = 1; i < 15; i++) {
//					c3[15] += c3[i];
//				}
//				c3[15] &= 0xff;
//				
//				appState.timeup = false;
//		        chronometer1.setBase(SystemClock.elapsedRealtime());  
	
				
				//重启 0x2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 CRC  
//				c[0] = 0x2E;
//				for (int i=1; i<15; i++){
//					c[i]=0x00;
//				}
//				c[15] = c[0]; // CRC
//				for (int i = 1; i < 15; i++) {
//					c[15] += c[i];
//				}
//				c[15] &= 0xff;
//				appState.sendData(c);
//				
//				appState.timeup = false;
//		        chronometer1.setBase(SystemClock.elapsedRealtime());  
//		        chronometer1.start();
//				while (!appState.timeup);
		        
		        
		        timeThread = new timeThread();
		        timeThread.start();
		    }  
		});  
		
//		button1.setOnClickListener(new View.OnClickListener() {  		      
//		    @Override  
//		    public void onClick(View v) {  
//		    	//重启 0x2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 CRC  
//				c[0] = 0x2E;
//				for (int i=1; i<15; i++){
//					c[i]=0x00;
//				}
//				c[15] = c[0]; // CRC
//				for (int i = 1; i < 15; i++) {
//					c[15] += c[i];
//				}
//				c[15] &= 0xff;
//				appState.sendData(c);
//				
//				appState.timeup = false;
//		        chronometer1.setBase(SystemClock.elapsedRealtime());  
//		    }
//		});

	}
	
   
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
			if (!((String) msg.obj == null)) {
				System.out.println(String.valueOf(cnt));
				switch (cnt) {
				case 0 :
					cnt++;
					break;
				case 1:
					appState.sendData(c1);
					cnt++;
					break;
				case 2:
					appState.sendData(c2);
					cnt++;
					break;
				case 3:
					appState.sendData(c3);
					cnt++;
					break;
				}

			}
		}
	}
	
	private int cnt = 0;
	public Thread timeThread;
	public class timeThread extends Thread {

		public timeThread() {

		}

		@Override
		public void run() {
			while (!this.isInterrupted() && cnt < 4) {
				System.out.println("Thread run again");
				
				updateHandler("send");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}
	
}



