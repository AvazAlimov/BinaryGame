package com.arsenal.avaz.binaryfun;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TableLayout container;
    private TextView actionButton;
    private List<Button> buttons;
    private TextView timer;
    private long startTime = 0;
    private android.os.Handler handler;
    private Runnable startTimer;
    private View action_icon;
    private View mainLayout;
    private View pauseLayout;
    private View pauseButton;
    private TextView resume_text;
    private TextView final_time;
    private View score_layout;
    private View trophy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        buttons = new ArrayList<>();
        container = (TableLayout) findViewById(R.id.container);
        actionButton = (TextView) findViewById(R.id.actionButton);
        action_icon = findViewById(R.id.action_icon);
        timer = (TextView) findViewById(R.id.timer);
        mainLayout = findViewById(R.id.main_layout);
        pauseLayout = findViewById(R.id.pause_layout);
        pauseButton = findViewById(R.id.pause_button);
        resume_text = (TextView) findViewById(R.id.resume_text);
        final_time = (TextView) findViewById(R.id.final_time);
        score_layout = findViewById(R.id.score_layout);
        trophy = findViewById(R.id.trophy);

        startGame();
    }

    private void startGame() {
        fillTable();
        fillAnswers();
        checkAnswers(true);
        startTimerTick(0);
    }

    private void fillTable() {
        buttons.clear();
        actionButton.setTag("giveUp");
        container.removeAllViews();
        if (Tools.isTimerVisible)
            ((View) timer.getParent()).setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        score_layout.setVisibility(View.GONE);
        final_time.setText("");
        resume_text.setText(R.string.resume);
        trophy.setVisibility(View.GONE);

        Resources r = this.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());

        container.getLayoutParams().width = r.getDisplayMetrics().widthPixels - 9 * px;
        int fullWidth = container.getLayoutParams().width;

        int width = (fullWidth - 2 * px * (Tools.gameMode + 1)) / (Tools.gameMode + 2);
        int textSize = Tools.gameMode == 8 ? 12 : 18;

        for (int i = 0; i <= Tools.gameMode + 1; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            for (int j = 0; j <= Tools.gameMode + 1; j++) {
                if (i == Tools.gameMode + 1 && j == Tools.gameMode + 1)
                    break;

                Button button = new Button(this);
                button.setPadding(0, 0, 0, 2);
                button.setTextSize(textSize);
                button.setTextColor(Color.LTGRAY);
                TableRow.LayoutParams params = new TableRow.LayoutParams(width, width);
                params.setMargins(px, px, px, px);
                button.setLayoutParams(params);
                if (i == Tools.gameMode + 1 || i == 0 || j == Tools.gameMode + 1 || j == 0) {
                    if ((i == 0 && j == 0) || (j == Tools.gameMode + 1 && i == 0) || (i == Tools.gameMode + 1 && j == 0))
                        button.setVisibility(View.INVISIBLE);
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.grey_dutton_drawable));
                    button.setEnabled(false);
                } else {
                    Random random = new Random();
                    String text = random.nextInt(2) + "";
                    button.setTag(text);
                    if (Tools.isZeros) {
                        button.setText("0");
                        button.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_dutton_drawable));
                    } else {
                        button.setText("1");
                        button.setBackground(ContextCompat.getDrawable(this, R.drawable.red_dutton_drawable));
                    }
                    button.setOnClickListener(new Handler());
                }
                row.addView(button, j);
                buttons.add(button);
            }
            container.addView(row, i);
        }
    }

    private void fillAnswers() {

        //Column
        for (int i = Tools.gameMode + 3; i < 2 * Tools.gameMode + 3; i++) {
            int top = i - Tools.gameMode - 2;
            int bottom = top + (Tools.gameMode + 1) * (Tools.gameMode + 2);
            int sum = 0;
            int sum_rev = 0;
            int index = 0;
            int index_rev = Tools.gameMode - 1;

            for (int j = 0; j <= (Tools.gameMode - 1) * (Tools.gameMode + 2); j += Tools.gameMode + 2, index++, index_rev--) {
                sum += Integer.parseInt(buttons.get(i + j).getTag().toString()) * (int) Math.pow(2, index);
                sum_rev += Integer.parseInt(buttons.get(i + j).getTag().toString()) * (int) Math.pow(2, index_rev);
            }
            buttons.get(top).setText(String.valueOf(sum));
            buttons.get(bottom).setText(String.valueOf(sum_rev));
        }

        //Row
        for (int i = Tools.gameMode + 3; i <= (Tools.gameMode + 1) * (Tools.gameMode + 1); i += Tools.gameMode + 2) {
            int left = i - 1;
            int right = left + Tools.gameMode + 1;
            int sum = 0;
            int sum_rev = 0;
            int index = 0;
            int index_rev = Tools.gameMode - 1;
            for (int j = 0; j < Tools.gameMode; j++, index++, index_rev--) {
                sum += Integer.parseInt(buttons.get(i + j).getTag().toString()) * (int) Math.pow(2, index);
                sum_rev += Integer.parseInt(buttons.get(i + j).getTag().toString()) * (int) Math.pow(2, index_rev);
            }
            buttons.get(left).setText(String.valueOf(sum));
            buttons.get(right).setText(String.valueOf(sum_rev));
        }
    }

    private void checkAnswers(boolean state) {
        boolean win = true;

        //Column
        for (int i = Tools.gameMode + 3; i < 2 * Tools.gameMode + 3; i++) {
            int top = i - Tools.gameMode - 2;
            int bottom = top + (Tools.gameMode + 1) * (Tools.gameMode + 2);
            int sum = 0;
            int sum_rev = 0;
            int index = 0;
            int index_rev = Tools.gameMode - 1;

            for (int j = 0; j <= (Tools.gameMode - 1) * (Tools.gameMode + 2); j += Tools.gameMode + 2, index++, index_rev--) {
                sum += Integer.parseInt(buttons.get(i + j).getText().toString()) * (int) Math.pow(2, index);
                sum_rev += Integer.parseInt(buttons.get(i + j).getText().toString()) * (int) Math.pow(2, index_rev);
            }

            if (buttons.get(bottom).getText().equals(String.valueOf(sum_rev))) {
                if (Tools.isHighlight)
                    buttons.get(bottom).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.green_dutton_drawable));
            } else {
                buttons.get(bottom).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.grey_dutton_drawable));
                win = false;
            }
            if (buttons.get(top).getText().equals(String.valueOf(sum))) {
                if (Tools.isHighlight)
                    buttons.get(top).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.green_dutton_drawable));
            } else {
                buttons.get(top).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.grey_dutton_drawable));
                win = false;
            }
        }

        //Row
        for (int i = Tools.gameMode + 3; i <= (Tools.gameMode + 1) * (Tools.gameMode + 1); i += Tools.gameMode + 2) {
            int left = i - 1;
            int right = left + Tools.gameMode + 1;
            int sum = 0;
            int sum_rev = 0;
            int index = 0;
            int index_rev = Tools.gameMode - 1;

            for (int j = 0; j < Tools.gameMode; j++, index++, index_rev--) {
                sum += Integer.parseInt(buttons.get(i + j).getText().toString()) * (int) Math.pow(2, index);
                sum_rev += Integer.parseInt(buttons.get(i + j).getText().toString()) * (int) Math.pow(2, index_rev);
            }

            if (buttons.get(right).getText().equals(String.valueOf(sum_rev))) {
                if (Tools.isHighlight)
                    buttons.get(right).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.green_dutton_drawable));
            } else {
                buttons.get(right).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.grey_dutton_drawable));
                win = false;
            }

            if (buttons.get(left).getText().equals(String.valueOf(sum))) {
                if (Tools.isHighlight)
                    buttons.get(left).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.green_dutton_drawable));
            } else {
                buttons.get(left).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.grey_dutton_drawable));
                win = false;
            }
        }

        if (win && state) {
            score_layout.setVisibility(View.VISIBLE);
            final_time.setText(timer.getText());
            resume_text.setText(R.string.show_table);
            pause(null);
            pauseButton.setVisibility(View.GONE);
            setEnabled(false);
            handler.removeCallbacks(startTimer);
            ((View) timer.getParent()).setVisibility(View.INVISIBLE);
            actionButton.setText(R.string.retry);
            actionButton.setTag("retry");
            action_icon.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.ic_retry));
            checkResult(timer.getText().toString());
        }
    }

    private void startTimerTick(long currentTime) {
        handler = new android.os.Handler();
        startTimer = new Runnable() {
            @Override
            public void run() {
                timer.setText(String.format(Locale.ENGLISH, "%d.%02d", startTime / 100, startTime % 100));
                startTime += 2;
                handler.postDelayed(this, 17);
            }
        };

        startTime = currentTime;
        startTimer.run();
    }

    private void showAnswers() {
        for (int i = 0; i < buttons.size(); i++)
            if (buttons.get(i).isEnabled()) {
                buttons.get(i).setText(buttons.get(i).getTag().toString());
                if (buttons.get(i).getTag().equals("1"))
                    buttons.get(i).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.red_dutton_drawable));
                else if (buttons.get(i).getTag().equals("0"))
                    buttons.get(i).setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.blue_dutton_drawable));
            }
        checkAnswers(false);
    }

    public void giveUp(View view) {
        String text = actionButton.getTag().toString();
        switch (text) {
            case "retry":
                action_icon.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.ic_hand));
                actionButton.setText(R.string.give_up);
                pauseButton.setVisibility(View.VISIBLE);
                startGame();
                break;
            case "giveUp":
                action_icon.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.ic_retry));
                actionButton.setText(R.string.retry);
                actionButton.setTag("retry");
                handler.removeCallbacks(startTimer);
                ((View) timer.getParent()).setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.GONE);
                showAnswers();
                setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void setEnabled(boolean state) {
        for (Button button : buttons)
            button.setEnabled(state);
    }

    public void pause(View view) {
        handler.removeCallbacks(startTimer);

        Animation scale_in_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_in_anim);
        scale_in_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainLayout.setVisibility(View.INVISIBLE);
                pauseLayout.setVisibility(View.VISIBLE);
                Animation scale_out_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_out_anim);
                pauseLayout.clearAnimation();
                pauseLayout.startAnimation(scale_out_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainLayout.clearAnimation();
        mainLayout.startAnimation(scale_in_anim);
    }

    public void resume(final View view) {
        Animation scale_in_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_in_anim);
        scale_in_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pauseLayout.setVisibility(View.INVISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
                Animation scale_out_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_out_anim);
                scale_out_anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        handler.removeCallbacks(startTimer);
                        startTimerTick(startTime);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mainLayout.clearAnimation();
                mainLayout.startAnimation(scale_out_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pauseLayout.clearAnimation();
        pauseLayout.startAnimation(scale_in_anim);
    }

    public void quit(View view) {
        finish();
    }

    public void restart(final View view) {
        Animation scale_in_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_in_anim);
        scale_in_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pauseLayout.setVisibility(View.INVISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
                Animation scale_out_anim = AnimationUtils.loadAnimation(Tools.context, R.anim.scale_out_anim);
                scale_out_anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        action_icon.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.ic_hand));
                        actionButton.setText(R.string.give_up);
                        startGame();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mainLayout.clearAnimation();
                mainLayout.startAnimation(scale_out_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pauseLayout.clearAnimation();
        pauseLayout.startAnimation(scale_in_anim);
    }

    private class Handler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            switch (button.getText().toString()) {
                case "0":
                    button.setText("1");
                    button.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.red_dutton_drawable));
                    break;
                case "1":
                    button.setText("0");
                    button.setBackground(ContextCompat.getDrawable(Tools.context, R.drawable.blue_dutton_drawable));
                    break;
                default:
                    break;
            }
            checkAnswers(true);
        }
    }

    private void checkResult(String result) {
        if (Tools.gameMode == 4) {
            if (Tools.best4.equals("null"))
                Tools.best4 = result;
            else if (Float.parseFloat(result) <= Float.parseFloat(Tools.best4)) {
                Tools.best4 = result;
                trophy.setVisibility(View.VISIBLE);
            }
        } else if (Tools.gameMode == 6) {
            if (Tools.best6.equals("null"))
                Tools.best6 = result;
            else if (Float.parseFloat(result) <= Float.parseFloat(Tools.best6)) {
                Tools.best6 = result;
                trophy.setVisibility(View.VISIBLE);
            }
        } else if (Tools.gameMode == 8) {
            if (Tools.best8.equals("null"))
                Tools.best8 = result;
            else if (Float.parseFloat(result) <= Float.parseFloat(Tools.best8)) {
                Tools.best8 = result;
                trophy.setVisibility(View.VISIBLE);
            }
        }

        try {
            Tools.writeData();
        } catch (IOException ignored) {
        }
    }
}
