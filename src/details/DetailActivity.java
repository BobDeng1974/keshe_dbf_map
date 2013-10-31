package details;

import baidumapsdk.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewAnimator;

public class DetailActivity extends Activity {

	private TextView title;
	private  Button leftBtn;
	private Button rightBtn;
	private ViewAnimator animator;
	private Animation slideInLeft;
	private Animation slideInRight;
	private Animation slideOutLeft;
	private Animation slideOutRight;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.detail_acticity);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.detail_activity_title);
	initViewAnimatior();
	initTitleListener();
    }


    private void initViewAnimatior() {
	// TODO Auto-generated method stub
	title = (TextView) findViewById(R.id.detail_title);
	leftBtn = (Button) findViewById(R.id.title_left_btn);
	rightBtn = (Button) findViewById(R.id.title_right_btn);
	title.setText(getResources().getString(R.string.mission_info));
	leftBtn.setText(getResources().getString(R.string.title_quit));
	rightBtn.setText(getResources().getString(R.string.title_next));
	animator = (ViewAnimator) findViewById(R.id.animator);
	slideInLeft = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_in_left);
	slideInRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_in_right);
	slideOutLeft = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_left);
	slideOutRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_right);
    }
    private void initTitleListener() {
	// TODO Auto-generated method stub
	leftBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		switch (animator.getDisplayedChild()) {
		case 0:
		    finish();
		    break;
		case 1:
		    title.setText(getResources().getString(R.string.mission_info));
		    leftBtn.setText(getResources().getString(R.string.title_back));
		    rightBtn.setText(getResources().getString(R.string.title_next));
		    break;
		case 2:
		    title.setText(getResources().getString(R.string.fengkou_info));
		    leftBtn.setText(getResources().getString(R.string.title_back));
		    rightBtn.setText(getResources().getString(R.string.title_next));
		    break;
		default:
		    break;
		}
		animator.setInAnimation(slideInRight);
		animator.setOutAnimation(slideOutRight);
		animator.showPrevious();
	    }
	});
	rightBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		switch (animator.getDisplayedChild()) {
		case 0:
		    title.setText(getResources().getString(R.string.fengkou_info));
		    leftBtn.setText(getResources().getString(R.string.title_back));
		    rightBtn.setText(getResources().getString(R.string.title_next));
		    break;
		case 1:
		    title.setText(getResources().getString(R.string.mission_info));
		    leftBtn.setText(getResources().getString(R.string.title_back));
		    rightBtn.setText(getResources().getString(R.string.title_quit));
		    break;
		case 2:
		    String msg = "任务完成";
		    new AlertDialog.Builder(DetailActivity.this)
			    .setMessage(msg).setTitle("hmmm...")
			    .setIcon(android.R.drawable.ic_dialog_info)
			    .setPositiveButton("ok", null).show();
		    break;
		default:
		    break;
		}
		animator.setInAnimation(slideInLeft);
		animator.setOutAnimation(slideOutLeft);
		animator.showNext();
	    }
	});
    }
}
