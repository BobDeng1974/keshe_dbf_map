package slidingmenu;

import baidumapsdk.demo.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GetIMEIFragment extends Fragment{
    
    TextView myIMEI;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    View view  = inflater.inflate(R.layout.frag_menu_imei, container, false);
	    myIMEI = (TextView) view.findViewById(R.id.myIMEI);
	    return view ;
}


public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	//获取手机设备号
	TelephonyManager telephonyManager = (TelephonyManager) getActivity()
		.getSystemService(Context.TELEPHONY_SERVICE);
	String imei = telephonyManager.getDeviceId();
	myIMEI.setText(imei);
}
}
