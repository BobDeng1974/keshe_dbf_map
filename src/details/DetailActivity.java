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

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.detail_acticity);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.detail_activity_title);

	final TextView title = (TextView) findViewById(R.id.detail_title);
	final Button leftBtn = (Button) findViewById(R.id.title_left_btn);
	final Button rightBtn = (Button) findViewById(R.id.title_right_btn);
	title.setText("任务信息");
	leftBtn.setText("quit");
	rightBtn.setText("next");
	final ViewAnimator animator = (ViewAnimator) findViewById(R.id.animator);
	final Animation slideInLeft = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_in_left);
	final Animation slideInRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_in_right);
	final Animation slideOutLeft = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_left);
	final Animation slideOutRight = AnimationUtils.loadAnimation(this,
		R.anim.i_slide_out_right);

	leftBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		switch (animator.getDisplayedChild()) {
		case 0:
		    finish();
		    break;
		case 1:
		    title.setText("任务信息");
		    leftBtn.setText("quit");
		    rightBtn.setText("next");
		    break;
		case 2:
		    title.setText("封口信息");
		    leftBtn.setText("back");
		    rightBtn.setText("next");
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
		    title.setText("封口信息");
		    leftBtn.setText("back");
		    rightBtn.setText("next");
		    break;
		case 1:
		    title.setText("任务状态");
		    leftBtn.setText("back");
		    rightBtn.setText("ok");
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
