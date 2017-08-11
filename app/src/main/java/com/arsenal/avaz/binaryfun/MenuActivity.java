package com.arsenal.avaz.binaryfun;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    private LinearLayout pageContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.context = this;
        setContentView(R.layout.activity_menu);
        init();
    }

    private void init() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new PageListener());
        pageContainer = (LinearLayout) findViewById(R.id.page_container);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    public void startGame(View view) {
        Tools.gameMode = Integer.parseInt(view.getTag().toString());
        startActivity(new Intent(MenuActivity.this, GameActivity.class));
    }

    public void howToPlay(View view) {
        Dialog dialog = new Dialog(Tools.context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.instruction_layout, null);
        layout.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.background));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void about(View view) {
        Dialog dialog = new Dialog(Tools.context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_layout, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void settings(View view) {
        Dialog dialog = new Dialog(Tools.context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_layout, null);

        final SwitchCompat fill_switch = (SwitchCompat) layout.findViewById(R.id.fill_switch);
        final SwitchCompat answer_switch = (SwitchCompat) layout.findViewById(R.id.answer_switch);
        final SwitchCompat timer_switch = (SwitchCompat) layout.findViewById(R.id.timer_switch);

        fill_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMode(fill_switch);
            }
        });

        answer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMode(answer_switch);
            }
        });

        timer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMode(timer_switch);
            }
        });

        if (!Tools.isZeros)
            fill_switch.setChecked(true);
        if (!Tools.isHighlight)
            answer_switch.setChecked(true);
        if (!Tools.isTimerVisible)
            timer_switch.setChecked(true);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void changeMode(View view) {
        String tag = view.getTag().toString();
        boolean state = ((SwitchCompat) view).isChecked();
        System.out.println(state);
        switch (tag) {
            case "fill_mode":
                Tools.isZeros = !state;
                break;
            case "answer_mode":
                Tools.isHighlight = !state;
                break;
            case "timer_mode":
                Tools.isTimerVisible = !state;
                break;
        }
        try {
            Tools.writeData();
        } catch (IOException ignored) {
        }
    }

    public void refreshScore(View view) {
        Tools.best4 = "null";
        Tools.best6 = "null";
        Tools.best8 = "null";
        try {
            Tools.writeData();
        } catch (IOException ignored) {
        }
        Toast.makeText(this, R.string.scores_refreshed, Toast.LENGTH_SHORT).show();
        init();
    }

    public void feedbackMail(View view) {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"avazalimov96@gmail.com"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        Email.putExtra(Intent.EXTRA_TEXT, "Dear Developer\n");
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

    public void feedbackFacebook(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/BinaryGame/"));
        startActivity(browserIntent);
    }

    public static class FirstFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.first_menu, container, false);
            TextView easyTime = (TextView) root.findViewById(R.id.easy_best_time);
            if (!Tools.best4.equals("null"))
                easyTime.setText(Tools.best4);
            return root;
        }
    }

    public static class SecondFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.second_menu, container, false);
            TextView medTime = (TextView) root.findViewById(R.id.med_best_time);
            if (!Tools.best6.equals("null"))
                medTime.setText(Tools.best6);
            return root;
        }
    }

    public static class ThirdFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.third_menu, container, false);
            TextView hardTime = (TextView) root.findViewById(R.id.hard_best_time);
            if (!Tools.best8.equals("null"))
                hardTime.setText(Tools.best8);
            return root;
        }
    }

    public static class FourthFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fourth_menu, container, false);
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
                case 3:
                    return new FourthFragment();
                default:
                    return new FirstFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.80f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                view.setAlpha(0);

            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else {
                view.setAlpha(0);
            }
        }
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            for (int i = 0; i < 4; i++)
                pageContainer.getChildAt(i).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.empty_round));
            pageContainer.getChildAt(position).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.filled_round));
        }
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    /*
    * final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
    try {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (android.content.ActivityNotFoundException anfe) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }
    * */
}
