package com.buaa.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.buaa.music.R;


/**
 * 本类为底部四个选项卡布局文件
 * 
 * @author Linux
 *
 */
public class MyTabWidget extends LinearLayout {

	private static final String TAG = "MyTabWidget";
	private int[] mDrawableIds = new int[] { R.drawable.bg_news,
			R.drawable.bg_phone_book, R.drawable.bg_activity,
			R.drawable.bg_setting };

	private List<CheckedTextView> mCheckedList = new ArrayList<CheckedTextView>();

	private List<View> mViewList = new ArrayList<View>();

	private List<ImageView> mIndicateImgs = new ArrayList<ImageView>();

	private CharSequence[] mLabels;

	public MyTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabWidget, defStyle, 0);

		mLabels = a.getTextArray(R.styleable.TabWidget_bottom_labels);

		if (null == mLabels || mLabels.length <= 0) {
			try {
				throw new RuntimeException("..");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			a.recycle();
			return;
		}

		a.recycle();

		init(context);
	}

	public MyTabWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyTabWidget(Context context) {
		super(context);
		init(context);
	}


	private void init(final Context context) {

		this.setOrientation(LinearLayout.HORIZONTAL);

		this.setBackgroundResource(R.mipmap.index_bottom_bar);

		LayoutInflater inflater = LayoutInflater.from(context);

		LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;

		int size = mLabels.length;
		for (int i = 0; i < size; i++) {

			final int index = i;

			final View view = inflater.inflate(R.layout.main_tab_item, null);

			final CheckedTextView itemName = (CheckedTextView) view
					.findViewById(R.id.item_name);
			itemName.setCompoundDrawablesWithIntrinsicBounds(null, context
					.getResources().getDrawable(mDrawableIds[i]), null, null);
			itemName.setText(mLabels[i]);

			final ImageView indicateImg = (ImageView) view
					.findViewById(R.id.indicate_img);

			this.addView(view, params);

			itemName.setTag(index);


			mCheckedList.add(itemName);

			mIndicateImgs.add(indicateImg);

			mViewList.add(view);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					setTabsDisplay(context, index);

					if (null != mTabListener) {

						mTabListener.onTabSelected(index);
					}
				}
			});


			if (i == 0) {
				itemName.setChecked(true);
				itemName.setTextColor(Color.rgb(2, 136, 209));
				// view.setBackgroundColor(Color.rgb(240, 241, 242));
			} else {
				itemName.setChecked(false);
				itemName.setTextColor(Color.rgb(153, 153, 153));
				// view.setBackgroundColor(Color.rgb(250, 250, 250));
			}

		}
	}


	public void setIndicateDisplay(Context context, int position,
			boolean visible) {
		int size = mIndicateImgs.size();
		if (size <= position) {
			return;
		}
		ImageView indicateImg = mIndicateImgs.get(position);
		indicateImg.setVisibility(visible ? View.VISIBLE : View.GONE);
	}


	public void setTabsDisplay(Context context, int index) {
		int size = mCheckedList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				checkedTextView.setChecked(true);
				checkedTextView.setTextColor(Color.rgb(2, 136, 209));
				// mViewList.get(i).setBackgroundColor(Color.rgb(240, 241,
				// 242));
			} else {
				checkedTextView.setChecked(false);
				checkedTextView.setTextColor(Color.rgb(153, 153, 153));
				// mViewList.get(i).setBackgroundColor(Color.rgb(250, 250,
				// 250));
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode != MeasureSpec.EXACTLY) {
			widthSpecSize = 0;
		}

		if (heightSpecMode != MeasureSpec.EXACTLY) {
			heightSpecSize = 0;
		}

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
		}

		int width;
		int height;
		width = Math.max(getMeasuredWidth(), widthSpecSize);
		height = Math.max(this.getBackground().getIntrinsicHeight(),
				heightSpecSize);
		setMeasuredDimension(width, height);
	}

	private OnTabSelectedListener mTabListener;

	public interface OnTabSelectedListener {
		void onTabSelected(int index);
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener) {
		this.mTabListener = listener;
	}

}
