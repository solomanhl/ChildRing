package com.gky.bluetooth.le.soloman;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;

public class GlobalVar extends Application{

	public FileUtils file = new FileUtils();
	public String deviceAddress;
	public boolean autoConnect;
	
	 /**读写BLE终端*/
	public BluetoothGattCharacteristic  gattCharacteristic;
	public BluetoothLeClass mBLE_send, mBLE_reciv;
	public byte c[] =new byte [16];
	public boolean timeup;
	
	public int devicetarget = 6000; 	//获取的设备的目标
	public int step;	//步数 
	public int complete; //完成率
	public float distance, caroli;//里程 卡路里
	public int sporttime; //运动时间 分钟
	public String sptime = "00:00";//运动时间00:00
	
	public boolean runThread = false;
	public boolean firstActivityRunning = false;
	
	public void init_BluetoothLeClass () {
		mBLE_reciv = new BluetoothLeClass(this);
		mBLE_send = new BluetoothLeClass(this);
	}
	
	public void sendData(  byte c[]) {
		timeup = false;
    	//设置数据内容
		gattCharacteristic.setValue(c);
		//往蓝牙模块写入数据
		 mBLE_send.writeCharacteristic(gattCharacteristic);
    }
	
	//获取实时数据
	public void StartSportDateForTime ( ) {		
		c[0] = 0x09;	//
		c[1] = 0x00;	//A
		c[2] = 0x00;	//B
		c[3] = 0x00;	//C
		c[4] = 0x00;	//D
		c[5] = 0x00;	//E
		c[6] = 0x00;	//F
		c[7] = 0x00;	//G
		c[8] = 0x00;	//H
		c[9] = 0x00;	//I
		c[10] = 0x00;	//J
		c[11] = 0x00;	//K
		c[12] = 0x00;	//L
		c[13] = 0x00;	//M
		c[14] = 0x00;	//N
		c[15] = c[0];	//CRC
		for (int i = 1; i<15; i++){
			c[15] += c[i];
		}
		c[15] &= 0xff;
		sendData( c);
	}
	
	//停止实时数据
		public void StopSportDateForTime (  ) {		
			c[0] = 0x0A;	//
			c[1] = 0x00;	//A
			c[2] = 0x00;	//B
			c[3] = 0x00;	//C
			c[4] = 0x00;	//D
			c[5] = 0x00;	//E
			c[6] = 0x00;	//F
			c[7] = 0x00;	//G
			c[8] = 0x00;	//H
			c[9] = 0x00;	//I
			c[10] = 0x00;	//J
			c[11] = 0x00;	//K
			c[12] = 0x00;	//L
			c[13] = 0x00;	//M
			c[14] = 0x00;	//N
			c[15] = c[0];	//CRC
			for (int i = 1; i<15; i++){
				c[15] += c[i];
			}
			c[15] &= 0xff;
			sendData( c);
		}
		
	// 获取用户个人信息 0x42 00 00 00 00 00 00 00 00 00 00 00 00 00 00 CRC
	public void getbaseinfo() {
		c[0] = 0x42; //
		c[1] = 0x00; // A
		c[2] = 0x00; // B
		c[3] = 0x00; // C
		c[4] = 0x00; // D
		c[5] = 0x00; // E
		c[6] = 0x00; // F
		c[7] = 0x00; // G
		c[8] = 0x00; // H
		c[9] = 0x00; // I
		c[10] = 0x00; // J
		c[11] = 0x00; // K
		c[12] = 0x00; // L
		c[13] = 0x00; // M
		c[14] = 0x00; // N
		c[15] = c[0]; // CRC
		for (int i = 1; i < 15; i++) {
			c[15] += c[i];
		}
		c[15] &= 0xff;
		sendData(c);
	}
	
	// 获取目标步数 0x4B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 CRC 
	//返回的AA BB CC 个人目标步数 3字节，高字节在前
		public void gettarget() {
			c[0] = 0x4b; //
			c[1] = 0x00; // A
			c[2] = 0x00; // B
			c[3] = 0x00; // C
			c[4] = 0x00; // D
			c[5] = 0x00; // E
			c[6] = 0x00; // F
			c[7] = 0x00; // G
			c[8] = 0x00; // H
			c[9] = 0x00; // I
			c[10] = 0x00; // J
			c[11] = 0x00; // K
			c[12] = 0x00; // L
			c[13] = 0x00; // M
			c[14] = 0x00; // N
			c[15] = c[0]; // CRC
			for (int i = 1; i < 15; i++) {
				c[15] += c[i];
			}
			c[15] &= 0xff;
			sendData(c);
		}

		//处理实施运动数据
		public void ExecuteStortData (byte c[]) {
			step =(c[1] & 0xff) *256 *256 + (c[2] & 0xff) *256 +(c[3] & 0xff);
			
			caroli = (float) ( ((c[7] & 0xff) * 256 * 256 + (c[8] & 0xff) * 256 +(c[9] & 0xff)) / 100.00 ) ;
			//BigDecimal   b   =   new   BigDecimal(caroli);  
			//caroli   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();  
			
			distance = (float) ( ((c[10] & 0xff) * 256 *256 + (c[11] & 0xff) * 256 + (c[12] & 0xff)) / 100.00 ) ;
			
			sporttime = (c[13] & 0xff) *256 +(c[14] & 0xff); //分钟
			String hour,min;
			hour = String.valueOf(sporttime / 60);
			min = String.valueOf(sporttime % 60);
			if (hour.length() < 2){
				hour = "0" + hour;
			}
			if (min.length() < 2){
				min = "0" + min;
			}
			sptime = hour + ":" + min;
			
			if (devicetarget != 0){
				complete = step * 100 / devicetarget;
			}
		}
		
	// 处理目标完成率
	public void ExecuteTarget(byte c[]) {
		devicetarget = (c[1] & 0xff) * 256 * 256 + (c[2] & 0xff) * 256 + (c[3] & 0xff); // 个人
	}
	
	// 处理个人信息
	//0x42 AA BB CC DD EE FF GG HH II JJ KK 00 00 00 CRC 
	//AA:性别（0表示女性，1表示男性），BB:年龄，CC:身高，DD:体重，EE:步长，FFKK为6字节的设备ID码，高字节在前.
	public int sex, age, height, weight, length;
	public void ExecuteBaseinfo(byte c[]) {
		sex = c[1]& 0xff;
		age = c[2]& 0xff;
		height = c[3]& 0xff;
		weight = c[4]& 0xff;
		length = c[5]& 0xff;
	}
		

}
