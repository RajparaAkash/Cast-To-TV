package com.example.chromecastone.Activity;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chromecastone.R;
import com.example.chromecastone.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {

    private ActivityHelpBinding binding;
    private TextView[] dots;
    private int[] layouts;
    private MyViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        initView();

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        this.viewPagerAdapter = new MyViewPagerAdapter();
        this.layouts = new int[]{
                R.layout.layout_help_1,
                R.layout.layout_help_2,
                R.layout.layout_help_3,
                R.layout.layout_help_4,
                R.layout.layout_help_5};
        addPagerDots(0);
        this.binding.htuViewPager.setAdapter(this.viewPagerAdapter);
        this.binding.htuViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                HelpActivity.this.addPagerDots(i);
                if (i == HelpActivity.this.layouts.length - 1) {
                    HelpActivity.this.binding.skipButton.setText("Finish");
                    return;
                }
                HelpActivity.this.binding.skipButton.setText("Next");
            }
        });
        this.binding.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layouts.length - 1 == binding.htuViewPager.getCurrentItem()) {
                    finish();
                    return;
                }
                binding.htuViewPager.setCurrentItem(binding.htuViewPager.getCurrentItem() + 1);

            }
        });
    }

    public void addPagerDots(int i) {
        TextView[] textViewArr;
        this.dots = new TextView[this.layouts.length];
        int[] intArray = getResources().getIntArray(R.array.array_dot_active);
        int[] intArray2 = getResources().getIntArray(R.array.array_dot_inactive);
        this.binding.dotLayout.removeAllViews();
        int i2 = 0;
        while (true) {
            textViewArr = this.dots;
            if (i2 >= textViewArr.length) {
                break;
            }
            textViewArr[i2] = new TextView(this);
            this.dots[i2].setText(Html.fromHtml("&#8226;"));
            this.dots[i2].setTextSize(35.0f);
            this.dots[i2].setTextColor(intArray2[i]);
            this.binding.dotLayout.addView(this.dots[i2]);
            i2++;
        }
        if (textViewArr.length > 0) {
            textViewArr[i].setTextColor(intArray[i]);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            layoutInflater = (LayoutInflater) HelpActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View inflate = layoutInflater.inflate(HelpActivity.this.layouts[i], viewGroup, false);
            viewGroup.addView(inflate);
            return inflate;
        }

        @Override
        public int getCount() {
            return HelpActivity.this.layouts.length;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }
    }
}
