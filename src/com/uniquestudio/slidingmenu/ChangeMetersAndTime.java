package com.uniquestudio.slidingmenu;

import com.uniquestudio.R;
import com.uniquestudio.stringconstant.StringConstant;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeMetersAndTime extends android.support.v4.app.Fragment{


    SharedPreferences sharedPreferences;
    EditText changeMyMinDistance;
    EditText changeMyTime;
    Button changeConfirm;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.frag_change_meters, container, false);
	changeMyMinDistance = (EditText) view.findViewById(R.id.frag_change_meters);
	changeMyTime = (EditText) view.findViewById(R.id.frag_change_time);
	changeConfirm = (Button) view.findViewById(R.id.change_meters_confirm);
	return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	sharedPreferences = getActivity().getSharedPreferences(StringConstant.PREFS_NAME,Context.MODE_PRIVATE);
	int myDistance = sharedPreferences.getInt("minMeters",StringConstant.DEF_MIN_DISTANCE);
	changeMyMinDistance.setText(myDistance+"");
	int myTime = sharedPreferences.getInt("postMinute", StringConstant.DEF_MIN_MINUTES);
	changeMyTime.setText(myTime+"");
	changeConfirm.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		final String changeDistance = changeMyMinDistance.getText().toString();
		final String changeTime = changeMyTime.getText().toString();
		//监测填入的AK值是否正确
		if (changeDistance.equals("") || changeTime.equals(""))
		    Toast.makeText(getActivity(), "请输入正确的值..",Toast.LENGTH_LONG).show();
		else {
		    //确认对话框
		    new AlertDialog.Builder(getActivity())
			    .setMessage("确认修改？")
			    .setPositiveButton("确定",
				    new DialogInterface.OnClickListener() {
					public void onClick(
						DialogInterface dialog,
						int whichButton) {
					    sharedPreferences.edit().putString("minMeters",changeDistance).commit();
					    sharedPreferences.edit().putString("postMinute",changeTime).commit();
					}
				    })
				    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				        }
				    }).show();
		}
	    }
	});
    }
    
}
