package zmq.com.photoquiz.canvas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import zmq.com.photoquiz.R;
import zmq.com.photoquiz.activity.MyApp;
import zmq.com.photoquiz.activity.PlayActivity;
import zmq.com.photoquiz.database.Score;
import zmq.com.photoquiz.model.KeyAndChar;
import zmq.com.photoquiz.model.QuestionDetails;
import zmq.com.photoquiz.utility.AudioPlayer;
import zmq.com.photoquiz.utility.GlobalVariables;
import zmq.com.photoquiz.utility.Key;
import zmq.com.photoquiz.utility.Utility;

/**
 * Created by zmq181 on 4/2/19.
 * Recreated by Mohd Yusuf on 25/Aug/2025.
 */

enum GameMode {
    LOAD, READY, START
}

public class GameView extends BaseSurface {
    private ArrayList<QuestionDetails> questionDetailsList;
    private QuestionDetails questionDetails;
    private int game_score, game_time, game_lives, game_question, photo_frame;
    private boolean isAnimation = false;
    private ArrayList<String> question_lines = new ArrayList<>();
    private ArrayList<KeyAndChar> keyAndChars = new ArrayList<>();
    private ArrayList<KeyAndChar> keyboard = new ArrayList<>();
    public boolean timer_running = true, right_ans = false, wrong_ans = false, won_ans = false, saveData = false, isAnswerComplete = true;
    private String dialog_message = "", dialog_tittle = "";
    public Thread thread;
    private GameMode runningMode;
    private Key key;
    private List<Integer> frames = Arrays.asList(0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 14, 16, 17, 18, 19);
    private List<String> frames_sq = Arrays.asList(
            "00", "01", "02", "03", "04"
            , "10", "11", "12", "13", "14"
            , "20", "21", "22", "23", "24"
            , "30", "31", "33", "34"
            , "40", "41", "42", "43", "44"
            , "50", "51", "52", "53", "54"
            , "60", "61", "62", "63", "64");

    private AnimationSpirit background, touch, actress, score_light, timer_light, live,
            question, key_bg, curtain_left, curtain_right, right_ans_bg, wrong_ans_bg, won_bg;
    private TextAnimation score_text, score, timer_text, time, live_text, lives, q_text, ques;

    private Context context;

    /*float next_x = (GlobalVariables.width - GlobalVariables.xScale_factor * 100) / 2;
    float next_y = GlobalVariables.yScale_factor * 740;
    float next_width = GlobalVariables.xScale_factor * 100;
    float next_height = GlobalVariables.yScale_factor * 30;

    float again_x = GlobalVariables.xScale_factor * 60;
    float again_y = GlobalVariables.yScale_factor * 450;
    float again_width = GlobalVariables.xScale_factor * 120;
    float again_height = GlobalVariables.yScale_factor * 30;

    float exit_x = GlobalVariables.xScale_factor * 300;
    float exit_y = GlobalVariables.yScale_factor * 450;
    float exit_width = GlobalVariables.xScale_factor * 120;
    float exit_height = GlobalVariables.yScale_factor * 30;*/


    public GameView(Context context) {
        super(context);
        this.context = context;
        runningMode = GameMode.LOAD;
        key = new Key();
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (runningMode == GameMode.LOAD) {
            parseQuestion();
            loadResources();
            getQuestion();
        }
        drawSomething(canvas); // always draw based on current state
        invalidate(); // keep redrawing
    }


    private void parseQuestion() {
        System.out.println("GameView: parseQuestion() called");
        InputStream inputStream = getResources().openRawResource(R.raw.question);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String picture;
        questionDetailsList = new ArrayList<>();
        try {
            while ((picture = bufferedReader.readLine()) != null) {
                QuestionDetails questionDetails = new QuestionDetails();
                String[] q = new String[3];

                picture = picture.split("=")[1];
                picture = picture.substring(0, picture.length() - 5);
                questionDetails.setImage(picture.toLowerCase().trim());
                System.out.println("GameView: Loading image: " + picture.toLowerCase().trim());

                picture = bufferedReader.readLine();
                picture = picture.split("=")[1];
                picture = picture.substring(0, picture.length());
                q[0] = picture.trim();
                System.out.println("GameView: Loading question[0]: " + q[0]);

                picture = bufferedReader.readLine();
                picture = picture.split("=")[1];
                picture = picture.substring(0, picture.length());
                q[1] = picture.trim();
                System.out.println("GameView: Loading question[1]: " + q[1]);

                picture = bufferedReader.readLine();
                picture = picture.split("=")[1];
                picture = picture.substring(0, picture.length());
                q[2] = picture.trim();
                System.out.println("GameView: Loading question[2]: " + q[2]);
                questionDetails.setQuestion(q);

                picture = bufferedReader.readLine();
                picture = picture.substring(8, picture.length() - 1);
                questionDetails.setAnswer(picture.trim());
                System.out.println("GameView: Loading answer: " + picture.trim());

                questionDetailsList.add(questionDetails);
                System.out.println("GameView: Added question to list, total count: " + questionDetailsList.size());
                bufferedReader.readLine();
            }
            System.out.println("GameView: parseQuestion completed, total questions loaded: " + questionDetailsList.size());
        } catch (IOException e) {
            System.out.println("GameView: ERROR parsing questions: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void getQuestion() {
        System.out.println("GameView: getQuestion() called, questionDetailsList size: " + (questionDetailsList != null ? questionDetailsList.size() : "null"));
        
        if (questionDetailsList != null && questionDetailsList.size() > 0) {
            questionDetails = questionDetailsList.get(getRandomNumber(0, questionDetailsList.size()));
            System.out.println("GameView: Selected question at index: " + (questionDetailsList.indexOf(questionDetails)));
            
            String[] dumy_ans = questionDetails.getQuestion()[getRandomNumber(0, 2)].split(" ");
            System.out.println("GameView: Question text: " + String.join(" ", dumy_ans));
            
            question_lines.clear();
            String line = "";
            for (int i = 0; i < dumy_ans.length; i++) {
                if (line.length() + dumy_ans[i].length() > 30) {
                    question_lines.add(line);
                    line = "";
                    line += dumy_ans[i] + " ";
                } else {
                    line += dumy_ans[i] + " ";
                }
            }
            question_lines.add(line);
            System.out.println("GameView: Question lines created: " + question_lines.size());
            
            game_time = 60;
            game_lives = 9;
            game_question++;
            photo_frame = getRandomNumber(1, 5);
            int resourceId = getResources().getIdentifier(questionDetails.getImage(), "drawable", getContext().getPackageName());
            System.out.println("GameView: Resource ID for image: " + resourceId);
            
            time.setText(String.valueOf(game_time));
            lives.setText(String.valueOf(game_lives));
            ques.setText(String.valueOf(game_question));

            keyAndChars.clear();
            String ans[] = questionDetails.getAnswer().split(" ");
            System.out.println("GameView: Answer words: " + String.join(", ", ans));
            
            for (int i = 0; i < ans.length; i++) {
                for (int j = 0; j < ans[i].length(); j++) {
                    float x = (480 - (ans[i].length() * 40)) / 2 + (40 * j);
                    float y = 500 + (40 * i);
                    float dx = 40;
                    float dy = 40;
                    KeyAndChar keyAndChar = new KeyAndChar(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_ring), x, y, dx, dy, ans[i].substring(j, j + 1), 30);
                    keyAndChars.add(keyAndChar);
                    System.out.println("GameView: Added keyAndChar: " + ans[i].substring(j, j + 1) + " at position (" + x + ", " + y + ")");
                }
            }

            for (KeyAndChar keyAndChar : keyboard) {
                keyAndChar.clear();
            }
            isAnswerComplete = true;
            right_ans = false;

            startTimer();

            dividation = 20;
            row = 7;
            col = 5;
            frames = Arrays.asList(0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 14, 16, 17, 18, 19);
            frames_sq = Arrays.asList(
                    "00", "01", "02", "03", "04"
                    , "10", "11", "12", "13", "14"
                    , "20", "21", "22", "23", "24"
                    , "30", "31", "33", "34"
                    , "40", "41", "42", "43", "44"
                    , "50", "51", "52", "53", "54"
                    , "60", "61", "62", "63", "64");
            actress.setBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, resourceId));
            System.out.println("GameView: Question setup complete");
        } else {
            System.out.println("GameView: ERROR - questionDetailsList is null or empty!");
        }
    }

    private int getRandomNumber(int start, int end) {
        Random random_number = new Random();
        return random_number.nextInt(end - start) + start;
    }

    public void startTimer() {
        thread = new Thread(() -> {
            while (timer_running) {
                time.setText(String.valueOf(game_time).length() > 1 ? String.valueOf(game_time) : "0" + game_time);
                try {
                    System.out.println("Thread id = " + Thread.currentThread().getId());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                key.setPress(false);
                if (!right_ans && !wrong_ans && !won_ans && !saveData && game_time > 0) {
                    game_time--;
                } else {
                    if (game_time == 0) {
                        AudioPlayer.playSound(getContext(), R.raw.loose);
                        wrong_ans = true;
                        dialog_tittle = "Sorry!";
                        dialog_message = "Your time is up...";
                        ((PlayActivity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog();
                            }
                        });
                        frames = Arrays.asList();
                        frames_sq = Arrays.asList();
                    }
                    timer_running = false;
                }
            }
        });
    }


    private void loadResources() {

        background = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.play_bg)).build();

        touch = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.touch_to_play))
                .setPostion(108, 53)
                .setFrame(263, 330).build();

        actress = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.amirata))
                .setPostion(108, 53)
                .setFrame(263, 330).build();

        score_light = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.light_left))
                .setPostion(0, 0)
                .setFrame(177, 177).build();

        timer_light = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.light_right))
                .setPostion(305, 0)
                .setFrame(177, 177).build();

        live = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.lives_bg))
                .setPostion(0, 188)
                .setFrame(80, 130).build();

        question = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.question_bg))
                .setPostion(400, 188)
                .setFrame(80, 130).build();

        curtain_left = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.curtain_left))
                .setPostion(0, 0)
                .setFrame(100, 600).build();

        curtain_right = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.curtain_rigth))
                .setPostion(380, 0)
                .setFrame(100, 600).build();

        score_text = new TextAnimation.Builder("Score")
                .setPosition(25, 52)
                .build();

        timer_text = new TextAnimation.Builder("Timer")
                .setPosition(410, 52)
                .build();

        live_text = new TextAnimation.Builder("Lives")
                .setPosition(22, 225)
                .build();

        q_text = new TextAnimation.Builder("Q.No.")
                .setPosition(415, 225)
                .build();

        score = new TextAnimation.Builder(String.valueOf(game_score).length() > 1 ? String.valueOf(game_score) : "0" + game_score)
                .setPosition(30, 95)
                .setColor(Color.BLACK)
                .setText_size(30)
                .build();

        time = new TextAnimation.Builder(String.valueOf(game_time).length() > 1 ? String.valueOf(game_time) : "0" + game_time)
                .setPosition(420, 95)
                .setColor(Color.BLACK)
                .setText_size(30)
                .build();

        lives = new TextAnimation.Builder(String.valueOf(game_lives))
                .setPosition(33, 270)
                .setColor(Color.YELLOW)
                .setText_size(30)
                .build();

        ques = new TextAnimation.Builder(String.valueOf(game_question))
                .setPosition(427, 270)
                .setColor(Color.YELLOW)
                .setText_size(30)
                .build();

        key_bg = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.keyboard_bg))
                .setPostion(0, 620)
                .setFrame(480, 190).build();


        keyboard.clear();
        for (int i = 0; i < Utility.ABC.length; i++) {
            for (int j = 0; j < Utility.ABC[i].length; j++) {
                float x = 15 + (45 * j) + (23 * ((i * i * i) % 5));
                float y = 630 + (51 * i);
                float dx = 44;
                float dy = 50;
                KeyAndChar keyAndChar = new KeyAndChar(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_black), x, y, dx, dy, Utility.ABC[i][j], 40);
                keyAndChar.load();
                keyboard.add(keyAndChar);
            }
        }


        right_ans_bg = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.congrats_bg))
                .setPostion(0, 635)
                .setFrame(480, 150).build();

        wrong_ans_bg = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.congrats_bg))
                .setPostion(0, 300)
                .setFrame(480, 200).build();

        won_bg = new AnimationSpirit.Builder(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.congrats_bg))
                .setPostion(0, 220)
                .setFrame(480, 300).build();


        runningMode = GameMode.READY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void drawSomething(Canvas g) {
        switch (runningMode) {
            case LOAD:
                paint.setColor(Color.rgb(255, 51, 51));
                g.drawRect(0, 0, GlobalVariables.width, GlobalVariables.height, paint);
                paint.setColor(Color.rgb(255, 255, 255));
                paint.setTextSize(GlobalVariables.xScale_factor * 30);
                g.drawText("Loading...", (GlobalVariables.width - paint.measureText("Loading...")) / 2, GlobalVariables.height / 2, paint);
                break;

            case READY:
                background.paint(g, null);
                touch.paint(g, null);
                break;

            case START:
                background.paint(g, null);
                curtain_left.paint(g, null);
                curtain_right.paint(g, null);
                actress.paint(g, null);
                setFrame(g, photo_frame);
                touch.paint(g, null);
                score_light.paint(g, null);
                timer_light.paint(g, null);
                live.paint(g, null);
                question.paint(g, null);

                if (!isAnimation) {
                    score_text.drawText(g, paint);
                    timer_text.drawText(g, paint);
                    live_text.drawText(g, paint);
                    q_text.drawText(g, paint);

                    score.drawText(g, paint);
                    time.drawText(g, paint);
                    lives.drawText(g, paint);
                    ques.drawText(g, paint);

                    paint.setTextSize(GlobalVariables.xScale_factor * 18);
                    for (int x = 0; x < question_lines.size(); x++) {
                        g.drawText(question_lines.get(x), (GlobalVariables.width - paint.measureText(question_lines.get(x))) / 2, GlobalVariables.yScale_factor * 425 + (50 * x), paint);
                    }

                    for (KeyAndChar keyAndChar : keyAndChars) {
                        keyAndChar.drawKey(g, paint);
                    }

                    for (KeyAndChar keyAndChar : keyAndChars) {
                        keyAndChar.drawChar(g, paint);
                    }

                    key_bg.paint(g, null);
                    for (KeyAndChar keyAndChar : keyboard) {
                        keyAndChar.drawKey(g, paint);
                        keyAndChar.drawChar(g, paint);
                    }

                    if (key.isPress()) {
                        paint.setTextSize(GlobalVariables.textFont);
                        float width = 60 * GlobalVariables.xScale_factor;
                        float height = 100 * GlobalVariables.yScale_factor;
                        g.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_large), new Rect(0, 0, (int) width, (int) height), new RectF(key.getX_cord() - (GlobalVariables.xScale_factor * 3), key.getY_cord() - height + (GlobalVariables.yScale_factor * 15), key.getX_cord() - (GlobalVariables.xScale_factor * 3) + width, key.getY_cord() + (GlobalVariables.yScale_factor * 15)), paint);
                        g.drawText(key.getValue(), key.getX_cord() - (GlobalVariables.xScale_factor * 5) + (width - paint.measureText(key.getValue())) / 2, key.getY_cord() - (GlobalVariables.yScale_factor * 45), paint);
                    }

                    /*if (right_ans) {
                        right_ans_bg.paint(g, null);
                        paint.setTextSize(GlobalVariables.xScale_factor * 30);
                        g.drawText("Congratulation", (GlobalVariables.width - paint.measureText("Congratulation")) / 2, GlobalVariables.yScale_factor * 675, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 25);
                        paint.setColor(Color.WHITE);
                        g.drawText(dialog_message, (GlobalVariables.width - paint.measureText(dialog_message)) / 2, GlobalVariables.yScale_factor * 720, paint);
                        g.drawRoundRect(next_x, next_y, next_x + next_width, next_y + next_height, GlobalVariables.xScale_factor * 10, GlobalVariables.yScale_factor * 10, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 20);
                        paint.setColor(Color.BLACK);
                        g.drawText("Next", (GlobalVariables.width - paint.measureText("Next")) / 2, GlobalVariables.yScale_factor * 760, paint);
                    }*/


                    /*if (wrong_ans) {
                        wrong_ans_bg.paint(g, null);
                        paint.setTextSize(GlobalVariables.xScale_factor * 30);
                        g.drawText(dialog_tittle, (GlobalVariables.width - paint.measureText(dialog_tittle)) / 2, GlobalVariables.yScale_factor * 350, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 25);
                        paint.setColor(Color.WHITE);
                        g.drawText(dialog_message, (GlobalVariables.width - paint.measureText(dialog_message)) / 2, GlobalVariables.yScale_factor * 400, paint);
                        g.drawRoundRect(again_x, again_y, again_x + again_width, again_y + again_height, GlobalVariables.xScale_factor * 10, GlobalVariables.yScale_factor * 10, paint);
                        g.drawRoundRect(exit_x, exit_y, exit_x + exit_width, exit_y + exit_height, GlobalVariables.xScale_factor * 10, GlobalVariables.yScale_factor * 10, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 20);
                        paint.setColor(Color.BLACK);
                        g.drawText("Play Again", GlobalVariables.xScale_factor * 75, GlobalVariables.yScale_factor * 470, paint);
                        g.drawText("Exit", GlobalVariables.xScale_factor * 340, GlobalVariables.yScale_factor * 470, paint);
                    }*/

                    /*if (won_ans) {
                        won_bg.paint(g, null);
                        paint.setTextSize(GlobalVariables.xScale_factor * 30);
                        g.drawText("Congratulation!", (GlobalVariables.width - paint.measureText("Congratulation!")) / 2, GlobalVariables.yScale_factor * 280, paint);
                        g.drawText("You have Won!", (GlobalVariables.width - paint.measureText("You have Won!")) / 2, GlobalVariables.yScale_factor * 330, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 25);
                        paint.setColor(Color.WHITE);
                        g.drawText("You Score is : " + (game_score + game_time), (GlobalVariables.width - paint.measureText("You Score is :    ")) / 2, GlobalVariables.yScale_factor * 380, paint);
                        g.drawText("Do you want to save your score", (GlobalVariables.width - paint.measureText("Do you want to save your score")) / 2, GlobalVariables.yScale_factor * 430, paint);
                        g.drawRoundRect(again_x, again_y, again_x + again_width, again_y + again_height, GlobalVariables.xScale_factor * 10, GlobalVariables.yScale_factor * 10, paint);
                        g.drawRoundRect(exit_x, exit_y, exit_x + exit_width, exit_y + exit_height, GlobalVariables.xScale_factor * 10, GlobalVariables.yScale_factor * 10, paint);
                        paint.setTextSize(GlobalVariables.xScale_factor * 20);
                        paint.setColor(Color.BLACK);
                        g.drawText("Yes", GlobalVariables.xScale_factor * 100, GlobalVariables.yScale_factor * 470, paint);
                        g.drawText("No", GlobalVariables.xScale_factor * 350, GlobalVariables.yScale_factor * 470, paint);
                    }*/
                } else {

                }
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("GameView: onTouchEvent called, action: " + event.getAction() + ", runningMode: " + runningMode);
        
        RectF touchrectF = new RectF(event.getX(), event.getY(), event.getX() + 1, event.getY() + 1);
        if (runningMode == GameMode.READY && touchrectF.intersect(new RectF(touch.getX_position(), touch.getY_position(), touch.getX_position() + touch.getFrame_width(), touch.getY_position() + touch.getFrame_height()))) {
            System.out.println("GameView: Touch detected on READY screen, starting game...");
            touch.load(AnimationSpirit.SCALE_IN);
            isAnimation = true;
            touch.valueAnimator.start();
            score_light.load((AnimationSpirit.SCALE_OUT_FROM));
            score_light.valueAnimator.start();
            timer_light.load((AnimationSpirit.SCALE_OUT_FROM));
            timer_light.valueAnimator.start();
            live.load(AnimationSpirit.SCALE_OUT_IN);
            live.valueAnimator.start();
            question.load(AnimationSpirit.SCALE_OUT_IN);
            question.valueAnimator.start();
            runningMode = GameMode.START;
            System.out.println("GameView: runningMode changed to START");

            score_light.valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    System.out.println("GameView: Animation ended, starting text animations...");
                    isAnimation = false;
                    score_text.setValueAnimator(Animation.SPREAD);
                    score_text.valueAnimator.start();

                    timer_text.setValueAnimator(Animation.SPREAD);
                    timer_text.valueAnimator.start();

                    live_text.setValueAnimator(Animation.SPREAD);
                    live_text.valueAnimator.start();

                    q_text.setValueAnimator(Animation.SPREAD);
                    q_text.valueAnimator.start();

                }
            });
            thread.start();
            AudioPlayer.playBackground(getContext(), R.raw.background);
        } else if (!right_ans && !wrong_ans && !won_ans && !saveData) {

            for (KeyAndChar keyAndChar : keyboard) {
                if (touchrectF.intersect(keyAndChar.rectF)) {
                    if (!keyAndChar.isKeyPress) {
                        System.out.println("Key  " + keyAndChar.getKey());
                        key.setPress(true);
                        key.setValue(keyAndChar.getKey());
                        key.setX_cord(keyAndChar.getX_pos());
                        key.setY_cord(keyAndChar.getY_pos());
                        if (questionDetails.getAnswer().toUpperCase().contains(keyAndChar.getKey())) {
                            keyAndChar.setBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_green));
                            AudioPlayer.playSound(getContext(), R.raw.right);
                            game_score += 10;

                        } else {
                            keyAndChar.setBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.key_red));
                            AudioPlayer.playSound(getContext(), R.raw.wrong);
                            if (game_score >= 10)
                                game_score -= 10;
                            game_lives--;
                            lives.setText(String.valueOf(game_lives));
                            if (game_lives == 0) {
                                AudioPlayer.playSound(getContext(), R.raw.loose);
                                wrong_ans = true;
                                dialog_tittle = "Sorry!";
                                dialog_message = "Your lives is over...";
                                showDialog();
                            }

                            if (photo_frame == 1) {
                                switch (game_lives) {
                                    case 8:
                                        frames_sq = Arrays.asList(
                                                "00", "01", "02", "03", "04"
                                                , "10", "11", "12", "13", "14"
                                                , "20", "22", "24"
                                                , "30", "31", "33", "34"
                                                , "40", "42", "44"
                                                , "50", "51", "52", "53", "54"
                                                , "60", "61", "62", "63", "64");
                                        break;

                                    case 7:
                                        frames_sq = Arrays.asList(
                                                "00", "01", "02", "03", "04"
                                                , "11", "12", "13"
                                                , "20", "22", "24"
                                                , "30", "31", "33", "34"
                                                , "40", "42", "44"
                                                , "51", "52", "53"
                                                , "60", "61", "62", "63", "64");
                                        break;

                                    case 6:
                                        frames_sq = Arrays.asList(
                                                "01", "02", "03"
                                                , "11", "12", "13"
                                                , "20", "22", "24"
                                                , "30", "31", "33", "34"
                                                , "40", "42", "44"
                                                , "51", "52", "53"
                                                , "61", "62", "63");
                                        break;

                                    case 5:
                                        frames_sq = Arrays.asList(
                                                "11", "12", "13"
                                                , "20", "22", "24"
                                                , "30", "31", "33", "34"
                                                , "40", "42", "44"
                                                , "51", "52", "53");
                                        break;

                                    case 4:
                                        frames_sq = Arrays.asList(
                                                "11", "12", "13"
                                                , "22"
                                                , "31", "33"
                                                , "42"
                                                , "51", "52", "53");
                                        break;

                                    case 3:
                                        frames_sq = Arrays.asList(
                                                "22"
                                                , "31", "33"
                                                , "42");
                                        break;

                                    case 2:
                                        frames_sq = Arrays.asList(
                                                "22"
                                                , "42");
                                        break;

                                    case 1:
                                        frames_sq = Arrays.asList();
                                        break;

                                }
                            } else {
                                switch (game_lives) {
                                    case 8:
                                        frames = Arrays.asList(0, 1, 2, 3, 6, 7, 8, 9, 11, 12, 13, 14, 16, 17, 18, 19);
                                        break;

                                    case 7:
                                        frames = Arrays.asList(0, 2, 3, 4, 7, 8, 9, 11, 12, 13, 14, 16, 18, 19);
                                        break;

                                    case 6:
                                        frames = Arrays.asList(2, 3, 4, 7, 8, 9, 11, 12, 13, 14, 16, 18);
                                        break;

                                    case 5:
                                        frames = Arrays.asList(2, 3, 7, 8, 9, 11, 12, 13, 14, 18);
                                        break;

                                    case 4:
                                        frames = Arrays.asList(2, 3, 8, 9, 11, 12, 13, 14);
                                        break;

                                    case 3:
                                        frames = Arrays.asList(2, 8, 9, 11, 12, 14);
                                        break;

                                    case 2:
                                        frames = Arrays.asList(9, 11, 12);
                                        break;

                                    case 1:
                                        frames = Arrays.asList();
                                        break;

                                }
                            }
                        }
                        score.setText(String.valueOf(game_score).length() > 1 ? String.valueOf(game_score) : "0" + game_score);


                        for (KeyAndChar keyAndChar1 : keyAndChars) {
                            if (keyAndChar.getKey().contains(keyAndChar1.getKey().toUpperCase())) {
                                keyAndChar1.load();
                                keyAndChar1.valueAnimator.start();
                            }
                        }

                        for (KeyAndChar keyAndChar1 : keyAndChars) {
                            if (keyAndChar1.valueAnimator != null) {
                                isAnswerComplete = true;
                                continue;
                            }
                            isAnswerComplete = false;
                            break;
                        }

                        if (isAnswerComplete) {
                            if (game_question == 9) {
                                AudioPlayer.playSound(getContext(), R.raw.win);
                                won_ans = true;
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                View view = LayoutInflater.from(getContext()).inflate(R.layout.win, null, false);
                                ((TextView) view.findViewById(R.id.text3)).setText("You Score is : " + (game_score + game_time));
                                Button yes = view.findViewById(R.id.btn1);
                                Button no = view.findViewById(R.id.btn2);

                                builder.setView(view);
                                builder.setCancelable(false);
                                android.view.animation.Animation anim = new ScaleAnimation(
                                        0, 1,
                                        0, 1,
                                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
                                anim.setFillAfter(true);
                                anim.setDuration(1000);
                                view.startAnimation(anim);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                yes.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        won_ans = false;
                                        saveData = true;
                                        AudioPlayer.playSound(getContext(), R.raw.win);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        View view = LayoutInflater.from(getContext()).inflate(R.layout.save_data, null, false);
                                        TextView score = view.findViewById(R.id.score);
                                        EditText name = view.findViewById(R.id.edit);
                                        score.setText(String.valueOf(game_score + game_time));
                                        Button save = view.findViewById(R.id.save);

                                        builder.setView(view);
                                        builder.setCancelable(false);
                                        android.view.animation.Animation anim = new ScaleAnimation(
                                                2, 1,
                                                2, 1,
                                                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                                                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
                                        anim.setFillAfter(true);
                                        anim.setDuration(1000);
                                        view.startAnimation(anim);
                                        AlertDialog dialog1 = builder.create();
                                        dialog1.show();
                                        save.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Score score1 = new Score();
                                                score1.setName(name.getText().toString().trim());
                                                score1.setScore(game_score + game_time);
                                                MyApp.getDatabaseInstance().getScoreDao().insertAll(score1);
                                                dialog1.dismiss();
                                                wrong_ans = true;
                                                dialog_tittle = "Your Answer saved!";
                                                dialog_message = "";
                                                AudioPlayer.playSound(getContext(), R.raw.win);
                                                showDialog();
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });

                                no.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        won_ans = false;
                                        wrong_ans = true;
                                        dialog_tittle = "Choose Option!";
                                        dialog_message = "You Score was : " + (game_score + game_time);
                                        AudioPlayer.playSound(getContext(), R.raw.win);
                                        showDialog();
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                AudioPlayer.playSound(getContext(), R.raw.newques);
                                right_ans = true;
                                game_score +=game_time;
                                score.setText(String.valueOf(game_score).length() > 1 ? String.valueOf(game_score) : "0" + game_score);
                                dialog_message = "Click for Next Question...";

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                View view = LayoutInflater.from(getContext()).inflate(R.layout.next_question, null, false);
                                Typeface font = Typeface.createFromAsset(getContext().getAssets(), "noteworthy.ttf");
                                Button save = view.findViewById(R.id.btn1);

                                builder.setView(view);
                                builder.setCancelable(false);
                                android.view.animation.Animation anim = new ScaleAnimation(
                                        0, 1,
                                        0, 1,
                                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
                                anim.setFillAfter(true);
                                anim.setDuration(1000);
                                view.startAnimation(anim);
                                AlertDialog dialog = builder.create();
                                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                                wmlp.gravity = Gravity.CENTER;
                                wmlp.x = 0;   //x position
                                wmlp.y = (int) GlobalVariables.yScale_factor * 800;   //y position
                                dialog.show();
                                save.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getQuestion();
                                        timer_running = true;
                                        thread.start();
                                        dialog.dismiss();
                                    }
                                });
                            }
                            frames = Arrays.asList();
                            frames_sq = Arrays.asList();
                        }

                        keyAndChar.isKeyPress = true;
                    }
                    break;
                }
            }
        } else {
            /*if (touchrectF.intersect(new RectF(next_x, next_y, next_x + next_width, next_y + next_height)) && right_ans) {
                getQuestion();
                timer_running = true;
                thread.start();
            } else if (touchrectF.intersect(new RectF(again_x, again_y, again_x + again_width, again_y + again_height)) && wrong_ans) {
                Intent intent = new Intent(context, PlayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            } else if (touchrectF.intersect(new RectF(exit_x, exit_y, exit_x + exit_width, exit_y + exit_height)) && wrong_ans) {
                ((PlayActivity) context).finish();
            } else if (touchrectF.intersect(new RectF(again_x, again_y, again_x + again_width, again_y + again_height)) && won_ans) {
                won_ans = false;
                saveData = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.save_data,null,false);
                TextView score = view.findViewById(R.id.score);
                EditText name = view.findViewById(R.id.edit);
                score.setText(String.valueOf(game_score+game_time));
                Button save = view.findViewById(R.id.save);

                builder.setView(view);
                builder.setCancelable(false);
                android.view.animation.Animation anim = new ScaleAnimation(
                        2, 1,
                        2, 1,
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true);
                anim.setDuration(1000);
                view.startAnimation(anim);
                AlertDialog dialog = builder.create();
                dialog.show();
                save.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Score score1 = new Score();
                        score1.setName(name.getText().toString().trim());
                        score1.setScore(game_score+game_time);
                        MyApp.getDatabaseInstance().getScoreDao().insertAll(score1);
                        dialog.dismiss();
                        wrong_ans = true;
                        dialog_tittle = "Your Answer saved!";
                        dialog_message = "";
                        showDialog();
                    }
                });

            } else if (touchrectF.intersect(new RectF(exit_x, exit_y, exit_x + exit_width, exit_y + exit_height)) && won_ans) {
                won_ans = false;
                wrong_ans = true;
                dialog_tittle = "Choose Option!";
                dialog_message = "You Score was : " + (game_score + game_time);
                showDialog();
            }*/
        }

        return super.onTouchEvent(event);
    }

    int frame26[] = {R.drawable.frame_a, R.drawable.frame_b, R.drawable.frame_c, R.drawable.frame_d, R.drawable.frame_e, R.drawable.frame_f, R.drawable.frame_g, R.drawable.frame_h, R.drawable.frame_i, R.drawable.frame_j, R.drawable.frame_k, R.drawable.frame_l, R.drawable.frame_m, R.drawable.frame_n, R.drawable.frame_o, R.drawable.frame_p, R.drawable.frame_q, R.drawable.frame_r, R.drawable.frame_s, R.drawable.frame_t, R.drawable.frame_u, R.drawable.frame_v, R.drawable.frame_w, R.drawable.frame_x, R.drawable.frame_y, R.drawable.frame_z};
    int dividation = 20, row = 7, col = 5;

    private void setFrame(Canvas c, int frame) {
        switch (frame) {
            case 1:
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (frames_sq.contains("" + i + j))
                            c.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.frame_one), null, new RectF((GlobalVariables.xScale_factor * 108) + ((actress.getFrame_width() / col) * (j)), (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / row) * i), (GlobalVariables.xScale_factor * 108) + ((actress.getFrame_width() / col) * (j + 1)), (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / row) * (i + 1))), paint);
                    }
                }
                break;

            case 2:
                for (int i = 0; i < dividation; i++) {
                    if (frames.contains(i)) {
                        c.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.frame_two), null, new RectF(108 * GlobalVariables.xScale_factor, (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / dividation) * i), 371 * GlobalVariables.xScale_factor, (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / dividation) * (i + 1))), paint);
                    }
                }
                break;

            case 3:
                for (int i = 0; i < dividation; i++) {
                    if (frames.contains(i)) {
                        c.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.frame_three), null, new RectF(108 * GlobalVariables.xScale_factor, (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / dividation) * i), 371 * GlobalVariables.xScale_factor, (GlobalVariables.yScale_factor * 53) + ((actress.getFrame_height() / dividation) * (i + 1))), paint);
                    }
                }
                break;

            case 4:

                for (int i = 0; i < dividation; i++) {
                    if (frames.contains(i)) {
                        c.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.frame_four), null, new RectF((108 * GlobalVariables.xScale_factor) + ((actress.getFrame_width() / dividation) * i), (GlobalVariables.yScale_factor * 53), (108 * GlobalVariables.xScale_factor) + ((actress.getFrame_width() / dividation) * (i + 1)), (GlobalVariables.yScale_factor * 383)), paint);
                    }
                }
                break;

            case 5:
                for (int i = 0; i < dividation; i++) {
                    if (frames.contains(i)) {
                        c.drawBitmap(Utility.decodeSampledBitmapFromResource(GlobalVariables.getResource, R.drawable.frame_five), null, new RectF((108 * GlobalVariables.xScale_factor) + ((actress.getFrame_width() / dividation) * i), (GlobalVariables.yScale_factor * 53), (108 * GlobalVariables.xScale_factor) + ((actress.getFrame_width() / dividation) * (i + 1)), (GlobalVariables.yScale_factor * 383)), paint);
                    }
                }
                break;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.failed, null, false);
        ((TextView) view.findViewById(R.id.text1)).setText(dialog_tittle);
        ((TextView) view.findViewById(R.id.text2)).setText(dialog_message);
        ((TextView) view.findViewById(R.id.text3)).setText("Answer Was " + questionDetails.getAnswer());
        Button play = view.findViewById(R.id.btn1);
        Button exit = view.findViewById(R.id.btn2);

        builder.setView(view);
        builder.setCancelable(false);
        android.view.animation.Animation anim = new ScaleAnimation(
                0, 1,
                0, 1,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(1000);
        view.startAnimation(anim);
        AlertDialog dialog = builder.create();
        dialog.show();
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayActivity) getContext()).finish();
                dialog.dismiss();
            }
        });
    }

}
