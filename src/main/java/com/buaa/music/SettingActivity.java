package com.buaa.music;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buaa.view.SwitchButton;

/**
 * 本类为设置界面的类
 * 
 * @author Linux
 *
 */
public class SettingActivity extends Activity {

	private Context context;


	private View CustomView;
	private TextView setting_version_text;
	private static boolean isShowDelay, isShowPosition;
	private SwitchButton setting__below_switch, setting_auto_start_switch,
			setting_close_net_switch;
	private LinearLayout setting_lin_position, setting_lin_delay;
	private Button setting_fanhui;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		init();
	}

	private void init() {
		context = SettingActivity.this;
		setting_version_text = (TextView) findViewById(R.id.setting_version_text);
		setting_fanhui = (Button) findViewById(R.id.setting_fanhui);
		setting_fanhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});

		setting__below_switch = (SwitchButton) findViewById(R.id.setting__below_switch);
		setting_auto_start_switch = (SwitchButton) findViewById(R.id.setting_auto_start_switch);
		setting_close_net_switch = (SwitchButton) findViewById(R.id.setting_close_net_switch);
		setting__below_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
//						user.setIsPopWindow(isChecked);
//						setting_pop_delay_switch.setEnabled(isChecked);
//						setting_pop_position_switch.setEnabled(isChecked);
//						Util.show(context, (isChecked ? "开启" : "关闭") + "弹窗");
					}
				});
		setting_auto_start_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
//						user.setIsWindowDelay(isChecked);
//						Util.show(context, (isChecked ? "延时" : "正常") + "显示弹窗");
					}
				});
		setting_close_net_switch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
//						user.setIsShowInTop(isChecked);
//						Util.show(context, "弹窗显示在屏幕" + (isChecked ? "上" : "下")
//								+ "方");
					}
				});

	}







}
