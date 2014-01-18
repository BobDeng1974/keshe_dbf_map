package slidingmenu;

import stringconstant.StringConstant;
import baidumapsdk.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeAKFragment extends Fragment {
    SharedPreferences sharedPreferences;
    TextView myAK;
    EditText changeMyAK;
    Button changeConfirm;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.frag_change_ak, container, false);
	myAK = (TextView) view.findViewById(R.id.myAK);
	changeMyAK = (EditText) view.findViewById(R.id.frag_change_AK);
	changeConfirm = (Button) view.findViewById(R.id.change_AK_confirm);
	return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	sharedPreferences = getActivity().getSharedPreferences(StringConstant.PREFS_NAME,
		Context.MODE_PRIVATE);
	String myAkString = sharedPreferences.getString("AK",
		StringConstant.DEF_AK);
	myAK.setText(myAkString);
	changeConfirm.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		final String changeString = changeMyAK.getText().toString();
		//监测填入的AK值是否正确
		if (changeString.equals("") || changeString.length() != 32)
		    Toast.makeText(getActivity(), "请输入正确的32位AK..",
			    Toast.LENGTH_LONG).show();
		else {
		    //确认对话框
		    new AlertDialog.Builder(getActivity())
			    .setMessage("确认修改AK？")
			    .setPositiveButton("确定",
				    new DialogInterface.OnClickListener() {
					public void onClick(
						DialogInterface dialog,
						int whichButton) {
					    sharedPreferences.edit().putString("AK",changeString).commit();
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
