package com.uniquestudio.bluetooth;

import static com.uniquestudio.bluetooth.BluetoothConstant.*;

import com.uniquestudio.stringconstant.StringConstant;

import android.R.string;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothClass.Device;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothSppClient {
    private static final int FRAME_MAX_NUM = 30;
    public static final int DATA_MAX_LEN = 252;
    private static final int FRAME_MAX_LEN = 255;
    private static final int FILE_BUF_LEN = (FRAME_MAX_NUM * DATA_MAX_LEN);
    private byte[][] glbFileDataBuf = new byte[FRAME_MAX_NUM][DATA_MAX_LEN];
    private int[] glbFileDataLen = new int[FRAME_MAX_NUM];
    private boolean isfinish = false;
    private boolean creatBondResult = false;
    private PecData pecData;
    private String pin = "00000000";
    private String macAddress;
    
    private String TAG = "BluetoothSppClient";
    private BluetoothChatService mChatService;
    private BluetoothDevice remoteDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName;
    private Context mContext;
    private boolean isDefaultDevice = false;
    private Handler mWaitMachineHandler;
    private boolean hasGetFileName = false;
    
    public BluetoothSppClient(Context context, String mac , Handler handler) {
	this.mContext = context;
	this.mChatService = new BluetoothChatService(this.mContext, mHandler);
	this.mWaitMachineHandler = handler;
	this.connectDevice(mac);
	this.macAddress = mac;
    }
    public BluetoothSppClient(Context context, String mac , Boolean isDefDevice,Handler handler) {
	this.mContext = context;
	this.mChatService = new BluetoothChatService(this.mContext, mHandler);
	this.mWaitMachineHandler = handler;
	this.connectDevice(mac);
	this.isDefaultDevice = isDefDevice;
	this.macAddress = mac;
    }
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case MESSAGE_STATE_CHANGE:
		if (true)
		    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
		switch (msg.arg1) {
		case BluetoothChatService.STATE_CONNECTED:

		    System.out.println("hanlder---------->STATE_CONNECTED");
		    
			    byte[] bytes = BuildFrameUtil.FrameBuid(
				    BluetoothConstant.TYPE_GETFI_FLRD,
				    "e:\\pecfile.dat"/* "e:\\pecfile.dat" */, -1, -1);
			    sendMyMessage(bytes);
		    break;
		case BluetoothChatService.STATE_CONNECTING:
		    // mTitle.setText(R.string.title_connecting);
		    break;
		case BluetoothChatService.STATE_LISTEN:
		case BluetoothChatService.STATE_NONE:
		    System.out
			    .println("hanlder---------->STATE_LISTEN or STATE_NONE");
		    // mTitle.setText(R.string.title_not_connected);
		    if (mChatService != null)
			mChatService = null;
		    break;
		}
		break;
	    case MESSAGE_WRITE:
//		byte[] writeBuf = (byte[]) msg.obj;
		// construct a string from the buffer
		break;
	    case MESSAGE_READ:
		byte[] readBuf = (byte[]) msg.obj;
		SppMessage sppMessage = BuildFrameUtil.AnalyseMyFrame(readBuf);
		hanlderMySppMessage(sppMessage);
		// construct a string from the valid bytes in the buffer

		break;
	    case MESSAGE_DEVICE_NAME:
		// save the connected device's name
		mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
		Toast.makeText(mContext,"Connected to " + mConnectedDeviceName,Toast.LENGTH_SHORT).show();
		
		break;
	    case MESSAGE_TOAST:
		String toast = msg.getData().getString(TOAST);
		Toast.makeText(mContext,
			toast, Toast.LENGTH_SHORT)
			.show();
		System.out.println("MESSAGE_DEVICE_NAME---------->"
			+ toast);
		if(toast.equals("Unable to connect device"))
		    mHandler.sendEmptyMessage(MESSAGE_CONNECT_FAILURE);
		break;
	    case MESSAGE_CONNECT_FAILURE:
		//通知连接失败
		System.out.println("MESSAGE_CONNECT_FAILURE");
		if(!isDefaultDevice)
		    mWaitMachineHandler.obtainMessage(2, -1, -1).sendToTarget();
		else
		    mWaitMachineHandler.obtainMessage(3, -1, -1).sendToTarget();
		break;
	    case MESSAGE_SAVE_DEF:
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(StringConstant.PREFS_NAME, 0);
		sharedPreferences.edit().putString("def_device", macAddress).commit();
		System.out.println("saved the default address:"+macAddress);
		break;
	    }
	}
    };

    private void sendMyMessage(String message) {
	sendMyMessage(message.getBytes());
	return;
    }
    private void sendMyMessage(byte[] message) {
	// Check that we're actually connected before trying anything
	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	    Toast.makeText(mContext, "未连接", Toast.LENGTH_SHORT).show();
	    return;
	}
	// Check that there's actually something to send
	if (message.length > 0) {
	    mChatService.write(message);
	}
    }
    
    private void connectDevice(String MAC) {
	// 监控蓝牙配对请求,自动设置PIN值连接
	mContext.registerReceiver(_mPairingRequest, new IntentFilter(
		BluetoothCtrl.PAIRING_REQUEST));
	// 监控蓝牙配对是否成功
	mContext.registerReceiver(_mPairingRequest, new IntentFilter(
		BluetoothDevice.ACTION_BOND_STATE_CHANGED));
	// Get the BLuetoothDevice object
	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC);
	if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	    try {
		BluetoothCtrl.createBond(device); // 开始配对
		 creatBondResult=true;
		 remoteDevice = device;
	    } catch (Exception e) {
		Log.d("mylog", "setPiN failed!");
		e.printStackTrace();
	    }
	}
	// Attempt to connect to the device
	this.mChatService.connect(device, true);
    }
    protected void hanlderMySppMessage(SppMessage sppMessage) {
	byte[] writeBytes;
	switch (sppMessage.getType()) {
	case BluetoothConstant.TYPE_GetFl_YERROR:
	    System.out.println("halderSppMessage error");
	    // error
	    break;
	case BluetoothConstant.TYPE_FLSD:
	    // 发送应答读文件返回命令
	    System.out.println("halderSppMessage 应答读文件");
	    writeBytes = BuildFrameUtil.FrameBuid(
		    BluetoothConstant.TYPE_SndFlNm_YFLSDOK, "", -1, -1);
	    sendMyMessage(writeBytes);
	    break;
	case BluetoothConstant.TYPE_FD:
	    // 发送应答帧命令
	    int NumFrameNow = (sppMessage.getHighFrame()) << 8;
	    NumFrameNow = NumFrameNow + sppMessage.getLowFrame();
	    ;
	    if (NumFrameNow >= FRAME_MAX_NUM) {
		// 发出消息停止获取
		break;
	    }
	    if (sppMessage.getDataLen() <= DATA_MAX_LEN) {
		// 存数据
		System.arraycopy(sppMessage.getData(), 0,
			this.glbFileDataBuf[NumFrameNow], 0, sppMessage.getDataLen());
		this.glbFileDataLen[NumFrameNow] = sppMessage.getDataLen();
		writeBytes = BuildFrameUtil.FrameBuid(
			BluetoothConstant.TYPE_SndData_YFD, "",
			sppMessage.getLowFrame(), sppMessage.getHighFrame());
		sendMyMessage(writeBytes);
	    } else {
		// 停止获取
	    }
	    break;
	case BluetoothConstant.TYPE_FLSE:
	    // 发送应答文件结束命令
	    writeBytes = BuildFrameUtil.FrameBuid(
		    BluetoothConstant.TYPE_SndFlEnd_FLRE, "", -1, -1);
	    sendMyMessage(writeBytes);
	    int FileBufferWriteInd = 0;
	    byte[] FileBufferTmp = new byte[FILE_BUF_LEN + 1];
	    for (int i = 0; i < FRAME_MAX_NUM; i++) {
		if (FileBufferWriteInd + this.glbFileDataLen[i] > FILE_BUF_LEN) {
		    // 异常：FLSEWaitData收到的数据长度大于数据接收缓冲";
		}
		System.arraycopy(this.glbFileDataBuf[i], 0, FileBufferTmp,
			FileBufferWriteInd, this.glbFileDataLen[i]);
		FileBufferWriteInd += this.glbFileDataLen[i];
	    }
	    if (FileBufferWriteInd == 0) {
		// 异常：取回的文件的长度为0
		break;
	    }
	    if (!hasGetFileName) {
		String fileName = BuildFrameUtil.getDataFileName(FileBufferTmp,
			FileBufferWriteInd);
		if (fileName.equals("")) {
		    // 异常：FLSEWaitData收到的配置文件中没有可以读取的数据文件名称"
		} else {
		    writeBytes = BuildFrameUtil.FrameBuid(
			    BluetoothConstant.TYPE_GETFI_FLRD,
			    fileName/* "e:\\pecfile.dat" */, -1, -1);
		    sendMyMessage(writeBytes);
		    System.out.println("halderSppMessage 读文件 " + fileName);
		    hasGetFileName = true;
		    this.glbFileDataBuf = new byte[FRAME_MAX_NUM][DATA_MAX_LEN];
		    this.glbFileDataLen = new int[FRAME_MAX_NUM];
		}
	    } else {
		// 格式化到的数据
		pecData = BuildFrameUtil.FormatPecData(FileBufferTmp,
			FileBufferWriteInd);
		if (pecData == null) {
		    // error
		    mWaitMachineHandler.obtainMessage(2, -1, -1).sendToTarget();
		} else {
		    mWaitMachineHandler.obtainMessage(1, -1, -1).sendToTarget();
		}
		//通知通信完毕
		this.isfinish = true;
	    }
	    break;
	default:
	    break;
	}
    }
    
    public PecData getPecData() {
	return this.pecData;
    }
    
    public boolean isFinished() {
	return this.isfinish;
    }
    public void close() {
	if (mChatService != null)
	    mChatService.stop();
	mContext.unregisterReceiver(_mPairingRequest);
	
	//断开配对
	if(creatBondResult && remoteDevice!=null) {
	    try {
		BluetoothCtrl.removeBond(remoteDevice);
	    } catch (Exception e) {
		Toast.makeText(mContext, "已与设备断开配对", Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	    }
	}
	//关蓝牙
//	if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
//	    mBluetoothAdapter.disable();
    }
    
    // 广播监听:蓝牙自动配对处理
    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
	    BluetoothDevice device = null;
	    if (intent.getAction().equals(BluetoothCtrl.PAIRING_REQUEST)) { // 配对开始时的广播处理
		device = intent
			.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		try {
		    BluetoothCtrl.cancelPairingUserInput(device);
		    BluetoothCtrl.setPin(device, pin); // 置入配对密码
		} catch (Exception e) {
		    Log.d(TAG,
			    ">>_mPairingRequest err!");
		}
	    }
	}
    };
}
