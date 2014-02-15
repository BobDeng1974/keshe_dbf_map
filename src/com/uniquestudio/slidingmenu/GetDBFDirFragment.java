package com.uniquestudio.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uniquestudio.R;
import com.uniquestudio.stringconstant.StringConstant;

public class GetDBFDirFragment extends Fragment{
    TextView myDbfDir;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
    View view  = inflater.inflate(R.layout.frag_menu_dbf_dir, container, false);
    myDbfDir = (TextView) view.findViewById(R.id.mydbfdir);
    return view ;
}


public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	myDbfDir.setText(StringConstant.root);
}
}
