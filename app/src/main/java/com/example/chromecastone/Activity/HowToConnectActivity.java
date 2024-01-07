package com.example.chromecastone.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chromecastone.R;

public class HowToConnectActivity extends AppCompatActivity {

    private LinearLayout main_ly_one;
    private LinearLayout main_ly_two;
    private LinearLayout main_ly_three;
    private LinearLayout main_ly_four;
    private LinearLayout main_ly_five;
    private LinearLayout main_ly_six;
    private LinearLayout main_ly_seven;

    private ImageView arrow_img_one;
    private ImageView arrow_img_two;
    private ImageView arrow_img_three;
    private ImageView arrow_img_four;
    private ImageView arrow_img_five;
    private ImageView arrow_img_seven;
    private ImageView arrow_img_six;

    private LinearLayout ly_bottom_txt_one;
    private LinearLayout ly_bottom_txt_two;
    private LinearLayout ly_bottom_txt_three;
    private LinearLayout ly_bottom_txt_four;
    private LinearLayout ly_bottom_txt_five;
    private LinearLayout ly_bottom_txt_six;
    private LinearLayout ly_bottom_txt_seven;


    private boolean is_open_one = false;
    private boolean is_open_two = false;
    private boolean is_open_three = false;
    private boolean is_open_four = false;
    private boolean is_open_five = false;
    private boolean is_open_six = false;
    private boolean is_open_seven = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_connect);
        idMethod();

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        main_ly_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_one) {
                    is_open_one = false;
                    ly_bottom_txt_one.setVisibility(View.GONE);
                    arrow_img_one.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_one = true;
                ly_bottom_txt_one.setVisibility(View.VISIBLE);
                arrow_img_one.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_two) {
                    is_open_two = false;
                    ly_bottom_txt_two.setVisibility(View.GONE);
                    arrow_img_two.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_two = true;
                ly_bottom_txt_two.setVisibility(View.VISIBLE);
                arrow_img_two.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_three) {
                    is_open_three = false;
                    ly_bottom_txt_three.setVisibility(View.GONE);
                    arrow_img_three.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_three = true;
                ly_bottom_txt_three.setVisibility(View.VISIBLE);
                arrow_img_three.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_four) {
                    is_open_four = false;
                    ly_bottom_txt_four.setVisibility(View.GONE);
                    arrow_img_four.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_four = true;
                ly_bottom_txt_four.setVisibility(View.VISIBLE);
                arrow_img_four.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_five) {
                    is_open_five = false;
                    ly_bottom_txt_five.setVisibility(View.GONE);
                    arrow_img_five.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_five = true;
                ly_bottom_txt_five.setVisibility(View.VISIBLE);
                arrow_img_five.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_six) {
                    is_open_six = false;
                    ly_bottom_txt_six.setVisibility(View.GONE);
                    arrow_img_six.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_six = true;
                ly_bottom_txt_six.setVisibility(View.VISIBLE);
                arrow_img_six.setImageResource(R.drawable.faq_up);
            }
        });
        main_ly_seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_open_seven) {
                    is_open_seven = false;
                    ly_bottom_txt_seven.setVisibility(View.GONE);
                    arrow_img_seven.setImageResource(R.drawable.faq_down);
                    return;
                }
                is_open_seven = true;
                ly_bottom_txt_seven.setVisibility(View.VISIBLE);
                arrow_img_seven.setImageResource(R.drawable.faq_up);
            }
        });
    }

    public void idMethod() {

        main_ly_one = (LinearLayout) findViewById(R.id.main_ly_one);
        main_ly_two = (LinearLayout) findViewById(R.id.main_ly_two);
        main_ly_three = (LinearLayout) findViewById(R.id.main_ly_three);
        main_ly_four = (LinearLayout) findViewById(R.id.main_ly_four);
        main_ly_five = (LinearLayout) findViewById(R.id.main_ly_five);
        main_ly_six = (LinearLayout) findViewById(R.id.main_ly_six);
        main_ly_seven = (LinearLayout) findViewById(R.id.main_ly_seven);

        arrow_img_one = (ImageView) findViewById(R.id.arrow_img_one);
        arrow_img_two = (ImageView) findViewById(R.id.arrow_img_two);
        arrow_img_three = (ImageView) findViewById(R.id.arrow_img_three);
        arrow_img_four = (ImageView) findViewById(R.id.arrow_img_four);
        arrow_img_five = (ImageView) findViewById(R.id.arrow_img_five);
        arrow_img_six = (ImageView) findViewById(R.id.arrow_img_six);
        arrow_img_seven = (ImageView) findViewById(R.id.arrow_img_seven);


        ly_bottom_txt_one = (LinearLayout) findViewById(R.id.ly_bottom_txt_one);
        ly_bottom_txt_two = (LinearLayout) findViewById(R.id.ly_bottom_txt_two);
        ly_bottom_txt_three = (LinearLayout) findViewById(R.id.ly_bottom_txt_three);
        ly_bottom_txt_four = (LinearLayout) findViewById(R.id.ly_bottom_txt_four);
        ly_bottom_txt_five = (LinearLayout) findViewById(R.id.ly_bottom_txt_five);
        ly_bottom_txt_six = (LinearLayout) findViewById(R.id.ly_bottom_txt_six);
        ly_bottom_txt_seven = (LinearLayout) findViewById(R.id.ly_bottom_txt_seven);

    }
}