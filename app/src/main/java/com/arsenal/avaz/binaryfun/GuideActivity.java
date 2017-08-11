package com.arsenal.avaz.binaryfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.IOException;

public class GuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Tools.context = this;
        try {
            Tools.readData();
        } catch (IOException e) {
            try {
                Tools.createDatabase();
            } catch (IOException ignored) {
            }
        }

        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        if (!Tools.isFirst)
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
    }

    public static class FirstFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.first_guide, container, false);
            final View logo = root.findViewById(R.id.logo_icon);
            final View text = root.findViewById(R.id.text);
            final View hint = root.findViewById(R.id.hint);

            Animation logo_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.logo_anim);
            Animation text_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.text_anim);
            Animation hint_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.next_anim);
            if (!Tools.isFirst) {
                hint.setVisibility(View.GONE);
                text_anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(Tools.context, MenuActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            logo.clearAnimation();
            logo.startAnimation(logo_anim);

            text.clearAnimation();
            text.startAnimation(text_anim);

            hint.clearAnimation();
            hint.startAnimation(hint_anim);

            return root;
        }
    }

    public static class SecondFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.second_guide, container, false);
            final View hint = root.findViewById(R.id.hint);
            Animation hint_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.alpha_anim);

            hint.clearAnimation();
            hint.startAnimation(hint_anim);

            return root;
        }
    }

    public static class ThirdFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.third_guide, container, false);
            final View hint = root.findViewById(R.id.hint);
            Animation hint_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.alpha_anim);

            hint.clearAnimation();
            hint.startAnimation(hint_anim);

            return root;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                default:
                    return new FirstFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.50f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 0) {
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) {
                view.setAlpha(1 - position);
                view.setTranslationX(pageWidth * -position);
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else {
                view.setAlpha(0);
            }
        }
    }

    public void openMenu(View view) {
        startActivity(new Intent(GuideActivity.this, MenuActivity.class));
    }
}
