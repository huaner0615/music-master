package com.buaa.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.music.R;
import com.buaa.utils.L;


/**
 * 本类为专属客户的类
 * 
 * @author Linux
 *
 */
public class KeHuFragment extends Fragment {


	private TextView title;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.kehu_fragment, container, false);
		L.i("KeHuFragment onCreateView");
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		L.i("KeHuFragment onViewCreated");
	}


	private void initViews(View view) {
		title = (TextView) getActivity().findViewById(R.id.kehu_title_tv);
		title.setText("MV");

	}



	@Override
	public void onDestroy() {
		L.i("KeHuFragment onDestroy");
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		System.out.println("onSaveInstanceState:");
		super.onSaveInstanceState(outState);
	}







}
