/***********************************************************************
 *        FILE: MainActivity.java
 * DESCRIPTION:
 *        DATE:
 *          BY: Darren Ting
 *
 **********************************************************************/

package com.fun_picks.fpphoto;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Vibrator;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.fun_picks.fpphoto.SimpleGestureFilter.SimpleGestureListener;
import com.google.android.gms.ads.AdView;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fun_picks.fpphoto.util.IabHelper;
import com.fun_picks.fpphoto.util.IabResult;
import com.fun_picks.fpphoto.util.Inventory;
import com.fun_picks.fpphoto.util.Purchase;

import bolts.AppLinks;


@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
         SimpleGestureListener{
    private static final String PB_IMPORT_DIRECTORY = "/PhotoBuddy/Import";
    //private static final String PB_TEST_DIRECTORY = "/PhotoBuddy/Test";
    public static final int PB_IMPORT_MODE_SD = 1;
    public static final int PB_IMPORT_MODE_ASSET = PB_IMPORT_MODE_SD + 1;
    //public static int importMode = PB_IMPORT_MODE_ASSET;
    static final boolean imageEncoded = true;
    private static final boolean BetaFlag = false;
    private static final boolean NoPurchaseFlag = true;


    private static final boolean doEncrypt=false;
    private static final int POINTS_PER_SELECTION = 1;

    private static boolean activityStopped=false;
    private static boolean newRoundScreenPaused=false;
    private static boolean sendINP_CLASSIC_IMAGE_WON=false;
    private static boolean sendINP_CLASSIC_IMAGE_LOST=false;
    private static boolean sendINP_SURVIVAL_IMAGE_WON=false;
    private static boolean sendINP_SURVIVAL_IMAGE_LOST=false;
    private static boolean sendINP_SURVIVAL_GAME_OVER=false;



    public static final String TAG = "FunPicksPhoto";
    static boolean mIsPackage1 = false;
    static boolean mIsPackage2 = false;
    static boolean mIsPackage3 = false;
    static boolean mIsPackage4 = false;
    static final String SKU_PACKAGE_1 = "package_1";
    static final String SKU_PACKAGE_2 = "package_2";
    static final String SKU_PACKAGE_3 = "package_3";
    static final String SKU_PACKAGE_4 = "package_4";

    static boolean photoPurchaseFlag=false;
    private InterstitialAd mInterstitialAd;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    static IabHelper mHelper;

    private SimpleGestureFilter detector=null;
    static View rootView;
    static View layoutStartScreen;
    static View layoutPlayAllScreen;

/*    static Button playAllScreenClassicButton;
    static Button playAllScreenSurvivalButton;
    static Button classicScreenBeginButton;
    static Button ClassicScreenHighScoreButton;
    static Button ClassicScreenHowToButton;*/

    private static final int INP_BACK_PRESSED = 100;
    private static final int INP_INITIALIZED = 101;
    private static final int INP_START_SCREEN_PLAY_BUTTON_PRESSED = 102;
    private static final int INP_START_SCREEN_HOF_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 1;
    private static final int INP_START_SCREEN_OPTIONS_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 2;
    private static final int INP_PLAYALL_SCREEN_CLASSIC_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 3;
    private static final int INP_PLAYALL_SCREEN_SURVIVAL_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 4;
    private static final int INP_CLASSIC_SCREEN_BEGIN_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 5;
    private static final int INP_CLASSIC_SCREEN_HIGH_SCORE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 6;
    private static final int INP_CLASSIC_SCREEN_HOW_TO_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 7;
    private static final int INP_NEW_ROUND_SCREEN_DONE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 8;
    private static final int INP_CLASSIC_POSITIVE_RESULT_SCREEN_DONE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 9;
    private static final int INP_CLASSIC_IMAGE_WON = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 10;
    private static final int INP_CLASSIC_IMAGE_LOST = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 11;
    private static final int INP_CLASSIC_NEGATIVE_RESULT_SCREEN_DONE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 12;
    private static final int INP_CLASSIC_EOR_SCREEN_CONTINUE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 13;
    private static final int INP_CLASSIC_EOR_SCREEN_REVIEW_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 14;
    private static final int INP_REVIEW_SCREEN_TOP_TIPS_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 15;
    private static final int INP_REVIEW_SCREEN_BOTTOM_TIPS_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 16;
    private static final int INP_REVIEW_SCREEN_BACK_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 17;
    private static final int INP_REVIEW_SCREEN_FORWARD_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 18;
    private static final int INP_REVIEW_SCREEN_IMAGE_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 19;
    private static final int INP_FAVORITES_SCREEN_IMAGE_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 20;
    private static final int INP_HOF_SCREEN_AWARDS_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 21;
    private static final int INP_HOF_SCREEN_FAVORITES_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 22;
    private static final int INP_QUIT_PLAY_SCREEN_CONTINUE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 23;
    private static final int INP_QUIT_PLAY_SCREEN_QUIT_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 24;
    private static final int INP_OPTIONS_SCREEN_STORE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 25;
    private static final int INP_OPTIONS_SCREEN_FACEBOOK_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 26;
    private static final int INP_OPTIONS_SCREEN_SETTINGS_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 27;
    private static final int INP_OPTIONS_SCREEN_ABOUT_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 28;
    private static final int INP_SURVIVAL_SCREEN_BEGIN_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 29;
    private static final int INP_SURVIVAL_SCREEN_HIGH_SCORE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 30;
    private static final int INP_SURVIVAL_SCREEN_HOW_TO_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 31;
    private static final int INP_SURVIVAL_IMAGE_WON = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 32;
    private static final int INP_SURVIVAL_IMAGE_LOST = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 33;
    private static final int INP_SURVIVAL_EOR_SCREEN_CONTINUE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 34;
    private static final int INP_SURVIVAL_EOR_SCREEN_REVIEW_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 35;
    private static final int INP_SURVIVAL_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 36;
    private static final int INP_SURVIVAL_GAME_OVER = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 37;
    private static final int INP_SURVIVAL_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 38;
    private static final int INP_CLASSIC_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 39;
    private static final int INP_CLASSIC_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 40;
    private static final int INP_CLASSIC_GAME_OVER = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 41;
    private static final int INP_CLASSIC_SOG_SCREEN_START_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 42;
    private static final int INP_P1_INITIALIZED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 43;
    public static final int INP_EULA_ACCEPTED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 44;
    public static final int INP_TIPS_SCREEN_MAP_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 45;
    public static final int INP_START_SCREEN_CLASSIC_START_PLAY_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 46;
    public static final int INP_CLASSIC_EOG_SCREEN_REVIEW_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 47;
    public static final int INP_SHARE_SCREEN_SHARE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 48;
    public static final int INP_SHARE_SCREEN_INVITE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 49;
    public static final int INP_SHARE_SCREEN_MORE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 50;
    public static final int INP_TIPS_SCREEN_FAVORITES_LIMIT_DIALOGUE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 51;
    public static final int INP_TIPS_SCREEN_START_IAP = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 52;
    public static final int INP_CLASSIC_SHOW_TIPS = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 53;
    public static final int INP_SURVIVAL_SHOW_TIPS = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 54;
    public static final int INP_SURVIVAL_EOG_SCREEN_REVIEW_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 55;
    public static final int INP_START_SCREEN_INFO_HINT = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 56;
    public static final int INP_CLASSIC_START_GAME_SCREEN_INFO_HINT = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 57;
    public static final int INP_REVIEW_SCREEN_REVIEW_HINT = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 58;
    public static final int INP_TIPS_SCREEN_TIPS_HINT = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 59;
    public static final int INP_SURVIVAL_START_GAME_SCREEN_INFO_HINT_DONE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 60;
    public static final int INP_STORE_SCREEN_PURCHASE_DONE = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 61;
    public static final int INP_CLASSIC_TIPS_DURING_PLAY_HINT = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 62;
    public static final int INP_CLASSIC_EOR_SCREEN_SHARE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 63;
    public static final int INP_SURVIVAL_EOR_SCREEN_SHARE_BUTTON_PRESSED = INP_START_SCREEN_PLAY_BUTTON_PRESSED + 64;


    private final static int L1_INVALID_STATE = 999;
    private final static int L1_S0_PRE_INIT = 1000;
    private final static int L1_S1_START_SCREEN = L1_S0_PRE_INIT + 1;
    private final static int L1_S2_PLAYALL_SCREEN = L1_S0_PRE_INIT + 2;
    private final static int L1_S3_CLASSIC_SCREEN = L1_S0_PRE_INIT + 3;
    private final static int L1_S4_CLASSIC_PLAY_SCREEN = L1_S0_PRE_INIT + 4;
    private final static int L1_S5_CLASSIC_NEW_ROUND_SCREEN = L1_S0_PRE_INIT + 5;
    private final static int L1_CLASSIC_RESULT_POSITIVE_SCREEN = L1_S0_PRE_INIT + 6;
    private final static int L1_CLASSIC_RESULT_NEGATIVE_SCREEN = L1_S0_PRE_INIT + 7;
    private final static int L1_CLASSIC_END_OF_ROUND_SCREEN = L1_S0_PRE_INIT + 8;
    private final static int L1_REVIEW_SCREEN = L1_S0_PRE_INIT + 9;
    private final static int L1_TIPS_SCREEN = L1_S0_PRE_INIT + 10;
    private final static int L1_FAVORITES_SCREEN = L1_S0_PRE_INIT + 11;
    private final static int L1_HOF_SCREEN = L1_S0_PRE_INIT + 12;
    private final static int L1_AWARDS_SCREEN = L1_S0_PRE_INIT + 13;
    private final static int L1_HIGH_SCORES_SCREEN = L1_S0_PRE_INIT + 14;
    private final static int L1_QUIT_PLAY_SCREEN = L1_S0_PRE_INIT + 15;
    private final static int L1_OPTIONS_SCREEN = L1_S0_PRE_INIT + 16;
    private final static int L1_STORE_SCREEN = L1_S0_PRE_INIT + 17;
    private final static int L1_SURVIVAL_SCREEN = L1_S0_PRE_INIT + 18;
    private final static int L1_SURVIVAL_PLAY_SCREEN = L1_S0_PRE_INIT + 19;
    private final static int L1_SURVIVAL_NEW_ROUND_SCREEN = L1_S0_PRE_INIT + 20;
    private final static int L1_SURVIVAL_RESULT_POSITIVE_SCREEN = L1_S0_PRE_INIT + 21;
    private final static int L1_SURVIVAL_RESULT_NEGATIVE_SCREEN = L1_S0_PRE_INIT + 22;
    private final static int L1_SURVIVAL_END_OF_ROUND_SCREEN = L1_S0_PRE_INIT + 23;
    private final static int L1_SURVIVAL_END_OF_GAME_SCREEN = L1_S0_PRE_INIT + 24;
    private final static int L1_CLASSIC_END_OF_GAME_SCREEN = L1_S0_PRE_INIT + 25;
    private final static int L1_CLASSIC_START_GAME_SCREEN = L1_S0_PRE_INIT + 26;
    private final static int L1_ABOUT_SCREEN = L1_S0_PRE_INIT + 27;
    private final static int L1_HOW_TO_SCREEN = L1_S0_PRE_INIT + 28;
    private final static int L1_INIT_P1 = L1_S0_PRE_INIT + 29;
    private final static int L1_SHARE_SCREEN = L1_S0_PRE_INIT + 30;

    private static final int INP_RS_SHOW_REVIEW_IMAGES = 3000;
    private static final int INP_RS_SHOW_RIGHT_WRONG_ICONS = INP_RS_SHOW_REVIEW_IMAGES + 1;

    private static final int INP_TS_SHOW_TIPS_IMAGE = 4000;

    public static final String PB_BUNDLE_MSG_INT_1 = "b_int_1";
    public static final String PB_BUNDLE_MSG_INT_2 = "b_int_2";


    static int L1State = L1_S0_PRE_INIT;
    static int previousL1State = L1_INVALID_STATE;

    private static final int CLASSIC_EOG_UP_LEVEL = 1;
    private static final int CLASSIC_EOG_STAY_LEVEL = CLASSIC_EOG_UP_LEVEL+1;

    private static final int PB_TIP_DIALOG_QUIZ_DELAY = 100;
    private static final int PB_NEW_ROUND_SCREEN_DELAY = 2000;
    private static final long PB_VIBRATE_DURATION = 200;
    private static final int PB_SURVIVAL_COUNTDOWN_VALUE = 15;
    private static final int PB_LIVES_REMAIN_SCREEN_DELAY = 3000;
    private static final int PB_COUNTDOWN_COLOR_CHANGE_THRESHOLD = 2;
    private static final int PB_POST_SELECT_SCREEN_DELAY = 1000;
    private static final int PB_POST_SELECT_SCREEN_DELAY_FAST = 500;
    private static final int CLASSIC_PLAY_CHECKMARK_FADE_IN_DURATION = 800;
    private static final int IMAGE_FADE_IN_DURATION = 800;
    private static final int SURVIVAL_PLAY_CHECKMARK_FADE_IN_DURATION = 800;
    private static final int SURVIVAL_COUNTDOWN_TIMER_VALUE_SEC = 15;
    private static final int SURVIVAL_COUNTDOWN_FACTOR = 2;
    private static final int SURVIVAL_COUNTDOWN_MINIMUM_VALUE = 6;
    private static final int SURVIVAL_TIMEOUT_MSG_DURATION = 2000;
    private static final int TIPS_IMAGE_FADE_IN_DURATION=600;
    private static final int TIPS_TEXT_FADE_IN_DURATION=300;
    private static final int TIPS_TEXT_START_OFFSET=500;
    private static final int SPLASH_DELAY=800;

    private static final int BONUS_ALPHA_DURATION = 400;
    private static final int BONUS_ALPHA_DURATION_FAST = 200;
    private static final int BONUS_SCALE_UP_DURATION = 500;
    private static final int BONUS_SCALE_UP_DURATION_FAST = 300;
    private static final int BONUS_SCALE_DOWN_DELAY = 500;
    private static final int BONUS_SCALE_DOWN_DELAY_FAST = 300;
    private static final int BONUS_SCALE_DOWN_DURATION = 400;
    private static final int BONUS_SCALE_DOWN_DURATION_FAST = 200;
    private static final int BONUS_ANIMATE_DELAY = 400;
    private static final int BONUS_ANIMATE_DELAY_FAST = 200;
    private static final int BONUS_ANIMATE_DURATION = 600;
    private static final int BONUS_ANIMATE_DURATION_FAST = 400;

    private static int bonusAlphaDuration = BONUS_ALPHA_DURATION;
    private static int bonusScaleUpDuation = BONUS_SCALE_UP_DURATION;
    private static int bonusScaleDownDelay = BONUS_SCALE_DOWN_DELAY;
    private static int bonusScaleDownDuration = BONUS_SCALE_DOWN_DURATION;
    private static int bonusAnimateDelay = BONUS_ANIMATE_DELAY;
    private static int bonusAnimateDuration = BONUS_ANIMATE_DURATION;
    private static int postSelectDelay = PB_POST_SELECT_SCREEN_DELAY;

    //private static final int PB_POST_SELECT_SCREEN_DELAY = 500;
    private static final int PB_POST_BONUS_POINTS_SCREEN_DELAY = 4100;
    public static byte xyz;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String VERSION_CODE_KEY = "VERSION_CODE_KEY";
    private static final int SEQUENCE_BEGIN = 0;
    private static final int SEQUENCE_END = 4;
    private static final int MODE_BEGIN = 4;
    private static final int MODE_END = 6;
    private static final int GROUP_BEGIN = 6;
    private static final int GROUP_END = 10;
    private static final int GROUP_SEQ_BEGIN = 10;
    private static final int GROUP_SEQ_END = 12;
    private static final int RATING_BEGIN = 12;
    private static final int RATING_END = 14;
    private static final int FLAG_BEGIN = 14;
    private static final int FLAG_END = 15;

    private static final int FLAG_SURVIVAL_YES = 0;
    private static final int FLAG_SURVIVAL_NO = 1;

    private static final int PB_MAX_RATING = 10;
    private static final int PB_MAX_IMAGES_PER_RATING = 200;
    private static final int IMAGE_FILE_NAME_LENGTH = 15;
    // private static final int MAX_GROUPS = 14;
    // private static final int MAX_IMAGES_PER_LEVEL = 5;
    private static final int MAX_IMAGES_PER_SCREEN = 2;
    // private static final int MAX_PLAY_LEVELS = 10;
    private static final int PB_LIVES_REMAIN_SURVIVAL_MODE = 3;
    private static final int SURVIVAL_MAX_LIVES=5;
    private static final int SURVIVAL_ROUNDS_FOR_LIFE = 10;

    private static final int MAX_IMAGE_ARRAY_SIZE = 1000;

    private static final int IMAGES_PER_ROUND_CLASSIC = 3;
    private static final int PB_IMAGES_PER_LEVEL_TRAVEL = 10;
    private static final int PB_IMAGES_PER_LEVEL_NATURE = 5;
    private static final int PB_IMAGES_PER_LEVEL_QUIZ_TRAVEL = 10;
    private static final int PB_IMAGES_PER_LEVEL_QUIZ_NATURE = 5;
    private static final int PB_IMAGES_PER_LEVEL_SURVIVAL = 3;
    private static final int PB_IMAGES_PER_LEVEL_RELAY = 10;
    private static final int MAX_REVIEW_SCREENS = IMAGES_PER_ROUND_CLASSIC*2;

    private static final int PB_LEVEL_CLASSIC = 3;
    private static final int PB_LEVEL_TRAVEL = 4;
    private static final int PB_LEVEL_NATURE = 4;
    private static final int PB_LEVEL_QUIZ_TRAVEL = 10;
    private static final int PB_LEVEL_QUIZ_NATURE = 10;
    private static final int PB_LEVEL_SURVIVAL = 1000;
    private static final int PB_LEVEL_RELAY = 10;

    private static final long PB_MODE_BASIC = 1;
    private static final long PB_MODE_TRAVEL = 2;
    private static final long PB_MODE_NATURE = PB_MODE_TRAVEL + 1;
    private static final long PB_MODE_QUIZ_CLASSIC = PB_MODE_TRAVEL + 2;
    private static final long PB_MODE_QUIZ_TRAVEL = PB_MODE_TRAVEL + 3;
    private static final long PB_MODE_SURVIVAL = PB_MODE_TRAVEL + 4;
    private static final long PB_MODE_SCOREBOARD = PB_MODE_TRAVEL + 5;
    private static final long PB_MODE_AWARDS = PB_MODE_TRAVEL + 6;
    private static final long PB_MODE_RELAY = PB_MODE_TRAVEL + 7;
    private static final long PB_MODE_ARTISTIC = PB_MODE_TRAVEL + 4;
    private static final long PB_MODE_PEOPLE = PB_MODE_TRAVEL + 5;
    private static final long PB_MODE_BUILDING = PB_MODE_TRAVEL + 6;
    private static final long PB_MODE_FOOD = PB_MODE_TRAVEL + 7;
    private static final long PB_MODE_SPORT = PB_MODE_TRAVEL + 8;

    private static final int PB_PLAY_LEVEL_1 = 1;
    private static final int PB_PLAY_LEVEL_2 = PB_PLAY_LEVEL_1 + 1;
    private static final int PB_PLAY_LEVEL_3 = PB_PLAY_LEVEL_1 + 2;
    private static final int PB_PLAY_LEVEL_4 = PB_PLAY_LEVEL_1 + 3;
    private static final int PB_PLAY_LEVEL_5 = PB_PLAY_LEVEL_1 + 4;
    private static final int PB_PLAY_LEVEL_6 = PB_PLAY_LEVEL_1 + 5;
    private static final int PB_PLAY_LEVEL_7 = PB_PLAY_LEVEL_1 + 6;
    private static final int PB_PLAY_LEVEL_8 = PB_PLAY_LEVEL_1 + 7;
    private static final int PB_PLAY_LEVEL_9 = PB_PLAY_LEVEL_1 + 8;
    private static final int PB_PLAY_LEVEL_10 = PB_PLAY_LEVEL_1 + 9;

    private static final int PB_Level2_HIGH_RATING_THRESHOLD = 6;
    private static final int PB_Level2_LOW_RATING_START = 1;
    private static final int PB_Level2_LOW_RATING_END = 4;

    // L2 Bundle Message defines
    private static final String PB_BUNDLE_MSG_INT_MSG_ID = "b_msg_id";
    private static final String PB_BUNDLE_MSG_STRING_1 = "b_string_1";
    private static final String PB_BUNDLE_MSG_STRING_2 = "b_string_2";
    private static final String PB_BUNDLE_MSG_LONG_1 = "b_long_1";
    private static final String PB_BUNDLE_MSG_LONG_2 = "b_long_2";

    // L1 message IDs used in intent messages
    private static final int PB_L1_PLAY = 100;
    private static final int PB_L1_EULA_ACCEPTED = PB_L1_PLAY + 1;
    private static final int PB_L1_INIT_P1_DONE = PB_L1_PLAY + 2;
    private static final int PB_L1_MSG2 = PB_L1_PLAY + 3;
    private static final int PB_L1_ASK_TIPS_WON = PB_L1_PLAY + 4;
    private static final int PB_L1_ASK_TIPS_LOST = PB_L1_PLAY + 5;
    private static final int PB_L1_NEXT_BUTTON_PRESSED = PB_L1_PLAY + 6;
    private static final int PB_L1_BACK_BUTTON_PRESSED = PB_L1_PLAY + 7;
    private static final int PB_L1_SHOW_TIP_1 = PB_L1_PLAY + 8;
    private static final int PB_L1_SHOW_TIP_2 = PB_L1_PLAY + 9;
    private static final int PB_L1_SHOW_REVIEW_TIP = PB_L1_PLAY + 10;
    private static final int PB_L1_NEW_ROUND = PB_L1_PLAY + 11;
    private static final int PB_L1_COUNTDOWN_EXPIRED = PB_L1_PLAY + 12;
    private static final int PB_L1_DISPLAY_SCOREBOARD = PB_L1_PLAY + 13;
    private static final int PB_L1_DISPLAY_AWARDS = PB_L1_PLAY + 14;

    private static final int PB_STATE_PLAY = 900;
    private static final int PB_STATE_TIP_1 = PB_STATE_PLAY + 1;
    private static final int PB_STATE_TIP_2 = PB_STATE_PLAY + 2;
    private static final int PB_STATE_REVIEW = PB_STATE_PLAY + 3;
    private static final int PB_STATE_GAME_ENDED = PB_STATE_PLAY + 4;

    private static int currentTipDialogDuration;
    private static final int PB_TIP_DURATION_0_5_SEC = 500;
    private static final int PB_TIP_DURATION_1_0_SEC = 1000;
    private static final int PB_TIP_DURATION_1_5_SEC = 1500;
    private static final int PB_TIP_DURATION_2_0_SEC = 2000;
    private static final int PB_TIP_DURATION_3_0_SEC = 3000;

    // Level 2 message IDs used in bundles messages
    private static final int PB_L2_MSG1 = 1000;
    private static final int PB_L2_MSG2 = PB_L2_MSG1 + 1;

    private static final int PB_COLOR_GREY = 0xff404040;
    private static final int PB_COLOR_REVIEW = 0xffababab;
    private static final int PB_COLOR_COUNTDOWN_WARNING = 0xff700000;
    private static final int PB_COLOR_BLACK = 0xff000000;
    private static final int PB_COLOR_WHITE = 0xffFFFFFF;
    private static final int PB_COLOR_LIGHT_BLUE = 0xff0099CC;

    private static int[] maxAwardLevelPerMode = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final long PB_AWARD_LEVEL_1_SCORE = 5;
    private static int MAX_AWARD_LEVELS = 20;
    private static int[] classicAwardPerLevelTrigger = {0, 10, 50, 100, 300, 500,
            1000, 2000, 5000, 8000, 10000, 20000, 50000, 100000, 200000, 500000, 50000, 50000, 50000, 50000,
            50000, 50000, 50000};
    public static int jct;
    private static int[] survivalAwardPerLevelTrigger = {0, 10, 20, 50, 100, 300,
            500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000, 50000, 50000, 50000, 50000, 50000,
            50000, 50000, 50000};

    private static final int ROUNDS_PER_LEVEL = 3;
    //private static final int ROUNDS_PER_LEVEL = 40;
    private static final int MAX_CLASSIC_LEVELS = 100;
    private static int[] classicMinScorePerLevel = new int[MAX_CLASSIC_LEVELS];
    /*private static int[] classicMinScorePerLevel = {0, 9, 20, 50, 80, 135, 200, 300, 400, 500, 600,
            700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 2000, 2000,2000,2000,2000,
            2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000};*/
    private static int currentClassicLevel;


    private static final String PB_USER_NAME = "Godzilla";

    private static int currentPlayModeColor;


    private static final boolean PB_DEF_SOUND_ONOFF = true;
    private static final boolean PB_DEF_VIBRATE_ONOFF = false;
    private static final boolean PB_DEF_FAST_MODE_ONOFF = false;
    private static final String PB_DEF_BACKGROUND_COLOR = "1";
    private static final String PB_DEF_TIP_DIALOG_DURATION = "3";
    private static final int PB_DEF_FAVORITES_LIMIT = 30; //increased to 30 from 3 to improve user experience
    private static final boolean PB_DEF_ADS_ONOFF = true;
    private static final int PB_DEF_PHOTOS_LIMIT = 110;
    private static final int FAVORITES_MAX_LIMIT = 600;
    private static final boolean PB_DEF_PREMIUM =false;
    private static final boolean PB_DEF_SURVIVAL_LOCKED = true;
    private static final int PB_DEF_CLASSIC_ROUNDS_PLAYED = 0;

    private static boolean soundOnOffPreference = PB_DEF_SOUND_ONOFF;
    private static boolean vibrateOnOffPreference = PB_DEF_VIBRATE_ONOFF;
    private static boolean fastModeOnOffPreference = PB_DEF_FAST_MODE_ONOFF;
    private static byte bhl= 6552-6501;


    private final static int INVALID_NUMBER = -999;
    private AppInviteDialog appInviteDialog;
    private CallbackManager callbackManager;

    private static final String PERMISSION = "publish_actions";
    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.facebook.samples.hellofacebook:PendingAction";

    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private ShareDialog shareDialog;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("Pick A Pic", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("Pick A Pic", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("Pick A Pic", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    static Context baseContext;
    private static int totalImageDBCount;
    private static int totalGroupDBCount;
    private final static int SCREEN_WIDITH_BASE = 1440;
    private final static int SCREEN_HEIGHT_BASE = 2560;
    private final static int IMAGE_SIDE_MARGIN_DP = 30;
    private static float screenWFactor;
    private static float screenHFactor;
    private static float screenWHRatio;
    private static float screenDensity;
    private static boolean inAppBillingStarted=false;
    private static Tracker mTracker;
    private static int quizTimerSeconds;
    private static int survivalTimeRemain;
    private static Context mContext;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(): entered.");

        // Assume we're starting fresh each time, even when activity is restarted after being killed by system
        if(savedInstanceState!=null) {
            Log.i(TAG, "onCreate(): savedInstanceState not null.  Setting it to null.");
            savedInstanceState = null;
        }
        setFullScreen();
        mContext = this;
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory()/1000000;
        Log.i(TAG, "        ****MaxMemory(MB):" + Long.toString(maxMemory));
        //Toast.makeText(getApplicationContext(),
        //"Max Memory =" + maxMemory,Toast.LENGTH_LONG).show();


        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            Log.i(TAG, "        ****Version:" + versionName);
            Log.i(TAG, "        ****versionCode:" + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        baseContext = getBaseContext();

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.splash_screen);

        Point size = new Point();

        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);

            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            screenWidth = d.getWidth();
            screenHeight = d.getHeight();
        }

        screenWFactor = (float)screenWidth/(float)SCREEN_WIDITH_BASE;
        screenHFactor = (float)screenHeight/(float)SCREEN_HEIGHT_BASE;

        screenWHRatio = ((float)SCREEN_WIDITH_BASE/(float)SCREEN_HEIGHT_BASE)/((float)screenWidth/(float)screenHeight);

        screenDensity = getResources().getDisplayMetrics().density;
        Log.i(TAG, "screen width=" + screenWidth + "  height=" + screenHeight+ "  density=" + screenDensity);

                initFacebook(savedInstanceState);

        // Toast.makeText(getApplicationContext(),
        // "Screen height=" + screenHeight + ", width=" + screenWidth,
        // Toast.LENGTH_SHORT).show();

         //hRefresh = new Handler(handleMessage);
        hStateMachine = new Handler(stateMachineMessage);


        GameIterationCounter = 0;

    /*this.deleteDatabase("photobuddy.db");
        if(true)
            return;*/

        pBudDBAdapter = new PBudDBAdapter(this);
        // Open or create the database
        pBudDBAdapter.open();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FPPApplication application = (FPPApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // Enable demo tracking
        //mTracker.enableAdvertisingIdCollection(true);
        activityStopped=false;
        newRoundScreenPaused=false;
        sendINP_CLASSIC_IMAGE_WON=false;
        sendINP_CLASSIC_IMAGE_LOST=false;
        sendINP_SURVIVAL_IMAGE_WON=false;
        sendINP_SURVIVAL_IMAGE_LOST=false;
        sendINP_SURVIVAL_GAME_OVER=false;

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8304696008384187~7188602152");
        jct -= xyz;
        initBackgroundP1();


        //long ret=pBudDBAdapter.insertTestItem(999);


        // MUST REMOVE LATER
        //appInitializedPreference = false;
   }

    private void setFullScreen(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else

        {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    void initFacebook(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }

        callbackManager = CallbackManager.Factory.create()              ;
        FacebookCallback<AppInviteDialog.Result> appInviteCallback =
                new FacebookCallback<AppInviteDialog.Result>() {
                    @Override
                    public void onSuccess(AppInviteDialog.Result result) {
                        Log.d(TAG, "Success!");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Canceled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, String.format("Error: %s", error.toString()));
                    }
                };
        appInviteDialog = new AppInviteDialog(this);
        appInviteDialog.registerCallback(callbackManager, appInviteCallback);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        //updateUI();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        //updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        //updateUI();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Canceled")
                                .setMessage("Permission not granted")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);


    }

   

    void initBackgroundP1() {
        new Thread(new Runnable() {
            public void run() {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean appInitializedPreference = prefs.getBoolean(
                        PREF_STR_APP_INITIALIZED, false);
                long savedVersionCode = prefs.getLong(VERSION_CODE_KEY, 0);
                try {
                    PackageInfo pinfo = getPackageManager().getPackageInfo(
                            getPackageName(), 0);
                    currentVersionCode = pinfo.versionCode;
                } catch (Exception e) {
                    Log.e(TAG, "onCreate(): getPackageManager, " + e.getMessage(), e);
                }
                initSound();
                if (!appInitializedPreference) {
                    uponInstallInit();
                    totalImageDBCount = pBudDBAdapter.getTotalItemCount();

                } else {
                    totalImageDBCount = pBudDBAdapter.getTotalItemCount();
                    if(totalImageDBCount == 0)
                        initDBItemsOnly();
                    try {
                        Thread.sleep(SPLASH_DELAY);
                        // Do some stuff
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                }

                totalGroupDBCount = pBudDBAdapter.getTotalGroupCount();


                for (int i=0;i<MAX_CLASSIC_LEVELS; i++){
                    classicMinScorePerLevel[i] = i*IMAGES_PER_ROUND_CLASSIC*i*ROUNDS_PER_LEVEL;
                }

                if(BetaFlag || NoPurchaseFlag){
                    premiumPreference = true;
                    favoritesLimitPreference = FAVORITES_MAX_LIMIT;
                    adsOnOffPreference = false;
                    photosLimitPreference = totalGroupDBCount;
                    //photoPurchaseFlag = true;
                    prefs = PreferenceManager
                            .getDefaultSharedPreferences(baseContext);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PREF_STR_PREMIUM, premiumPreference);
                    editor.putInt(PREF_STR_FAVORITES_LIMIT, favoritesLimitPreference);
                    editor.putBoolean(PREF_STR_ADS_ONOFF, adsOnOffPreference);
                    editor.putInt(PREF_STR_PHOTOS_LIMIT, photosLimitPreference);
                    editor.commit();
                }
                if(activityStopped){
                    finish();
                    activityStopped=false;
                    Log.e(TAG, "initBackgroundP1(): activity stopped; finish() called");
                    return;
                }

                setL1State(L1_INIT_P1);
                Message m = MainActivity.hStateMachine.obtainMessage();
                m.what = MainActivity.INP_P1_INITIALIZED;
                MainActivity.hStateMachine.sendMessage(m);

            }
        }).start();
    }
  



    private void initP2() {
        setContentView(R.layout.activity_main);

        detector = new SimpleGestureFilter(this, this);
        detector.setSwipeMaxDistance(screenWidth / 2);
        detector.setSwipeMinDistance(screenWidth / 5);
        detector.setSwipeMinVelocity(screenWidth / 7);
        currentImageCount = 1;
        survivalImageCount = 1;
        currentPlayRound = PB_PLAY_LEVEL_1;
        currentMode = PB_MODE_BASIC;
        currentScore = 0;
        livesRemain = PB_LIVES_REMAIN_SURVIVAL_MODE;
        totalQuestions = 0;
        pBudState = PB_STATE_PLAY;
        displayRoundChange = true;
        countDownTimerValue = PB_SURVIVAL_COUNTDOWN_VALUE;
        classicGroupCount = 0;
        survivalHighImageCount = 0;
        survivalLowImageCount = 0;

        image1Array = new MyImage[MAX_IMAGE_ARRAY_SIZE];
        image2Array = new MyImage[MAX_IMAGE_ARRAY_SIZE];

        getPrefs();


        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));

        //loadInterstitial();
        inAppBillingStarted=false;
        //initInAppBilling();  //JCT
        getCurrentAwardLevels();

        currentClassicLevel=1;

        //Eula.show(this);

        setL1State(L1_S0_PRE_INIT);
        Message m = MainActivity.hStateMachine.obtainMessage();
        m.what = MainActivity.INP_INITIALIZED;
        MainActivity.hStateMachine.sendMessage(m);

    }

    private void initDBItemsOnly(){
        boolean result;
        /*if (importMode == PB_IMPORT_MODE_SD)
            result = loadDBFromSD();
        else*/
            result = loadDB();

        if (!result) {
            Log.e(TAG, "initDBItemsOnly:loadDB() Failed");

            /*Toast.makeText(getApplicationContext(), "loadDB() Failed",
                    Toast.LENGTH_LONG).show();*/
            exitApp();
        }


    }

    private void uponInstallInit() {
        boolean result;
        Log.i(TAG, "uponInstallInit: entered.");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        /*if (importMode == PB_IMPORT_MODE_SD)
            result = loadDBFromSD();
        else*/
            result = loadDB();

        if (!result) {
            //Toast.makeText(getApplicationContext(), "loadDB() Failed",
              //      Toast.LENGTH_LONG).show();
            Log.e(TAG, "uponInstallInit: loadDB() Failed");
            exitApp();
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_STR_APP_INITIALIZED, true);
        editor.putBoolean(PREF_STR_SOUND_ONOFF, PB_DEF_SOUND_ONOFF);
        editor.putBoolean(PREF_STR_VIBRATE_ONOFF, PB_DEF_VIBRATE_ONOFF);
        editor.putBoolean(PREF_STR_FAST_MODE_ONOFF, PB_DEF_FAST_MODE_ONOFF);
        editor.putString(PREF_STR_BACKGROUND_COLOR, PB_DEF_BACKGROUND_COLOR);
        editor.putString(PREF_STR_TIP_DIALOG_DURATION, PB_DEF_TIP_DIALOG_DURATION);
        editor.putBoolean(PREF_STR_HINT_REVIEW, true);
        editor.putBoolean(PREF_STR_HINT_TIPS, true);
        editor.putBoolean(PREF_STR_HINT_SURVIVAL_START_PLAY, true);
        editor.putBoolean(PREF_STR_HINT_SET_ALL_ON, false);
        editor.commit();

        Log.i(TAG, "uponInstallInit: exiting.");
    }


  


    private void verifyDataFiles(){

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        Log.i(TAG, "onRestoreInstanceState(): entered.");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        Log.i(TAG, "onSaveInstanceState(): entered.");
        //outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
        //        .getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu(): entered.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private static int preferencesActivityStarted = 0; // for efficiency use
    // later

    private void displaySettingsPref() {
        Intent settingsActivity = new Intent(getBaseContext(),
                Preferences.class);
        startActivity(settingsActivity);
        preferencesActivityStarted++;
        sendAnalyticsScreen("Settings");
    }

    static boolean sendStateMachineMsg(int msg) {
        Message m = MainActivity.hStateMachine.obtainMessage();
        m.what = msg;
        MainActivity.hStateMachine.sendMessage(m);
        return (true);
    }

    static boolean sendStateMachineMsg(int msg, int arg1) {
        Message m = MainActivity.hStateMachine.obtainMessage();
        m.what = msg;
        Bundle b = new Bundle();
        b.putInt(PB_BUNDLE_MSG_INT_1, arg1);
        m.setData(b);
        MainActivity.hStateMachine.sendMessage(m);
        return (true);
    }

    static boolean sendStateMachineMsg(int msg, int arg1, int arg2) {
        Message m = MainActivity.hStateMachine.obtainMessage();
        m.what = msg;
        Bundle b = new Bundle();
        b.putInt(PB_BUNDLE_MSG_INT_1, arg1);
        b.putInt(PB_BUNDLE_MSG_INT_2, arg2);
        m.setData(b);
        MainActivity.hStateMachine.sendMessage(m);
        return (true);
    }


    static void setL1State(int state) {
        previousL1State = L1State;
        L1State = state;
    }
    static void setL1StateToPrevious() {
        int tmp = L1State;
        L1State = previousL1State;
        previousL1State = tmp;
    }

    static int getPreviousL1State() {
        return(previousL1State);
    }



  

    /** Verifies the developer payload of a purchase. */
    static boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }

        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void complain(String message) {
        Log.e(TAG, "**** PAP: InAppBilling Error: " + message);
        //alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /**
     * STORE SCREEN FRAGMENT
     */
    public static class StoreScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        final private static int INP_STORE_SCREEN_BUY_PACKAGE_1 = 1001;
        final private static int INP_STORE_SCREEN_BUY_PACKAGE_2 = 1002;
        final private static int INP_STORE_SCREEN_BUY_PACKAGE_3 = 1003;
        final private static int INP_STORE_SCREEN_BUY_PACKAGE_4 = 1004;
        private Button storeScreenPackage1Button;
        private Button storeScreenPackage2Button;
        private Button storeScreenPackage3Button;
        private Button storeScreenPackage4Button;
        private TextView storeScreenImageCountTextView;
        Handler hStoreStateMachine = null;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static StoreScreenFragment newInstance(int sectionNumber) {
            StoreScreenFragment fragment = new StoreScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public StoreScreenFragment() {
            return;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            hStoreStateMachine = new Handler(storeStateMachineMessage);
            System.gc();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_store, container,
                    false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            storeScreenPackage1Button = (Button) rootView
                    .findViewById(R.id.storeScreenPackage1Button);
            storeScreenPackage2Button = (Button) rootView
                    .findViewById(R.id.storeScreenPackage2Button);
            storeScreenPackage3Button = (Button) rootView
                    .findViewById(R.id.storeScreenPackage3Button);
            storeScreenPackage4Button = (Button) rootView
                    .findViewById(R.id.storeScreenPackage4Button);
            storeScreenImageCountTextView = (TextView) rootView
                    .findViewById(R.id.storeScreenImageCountTextView);

            storeScreenImageCountTextView.setText("Image Count: "+(photosLimitPreference*2));

            //storeScreenImageCountTextView.setText("DB Image Count: "+totalImageDBCount+
              //      "\nDB Group Count: "+totalGroupDBCount);



            //Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            //storeScreenPackage1Button.setTypeface(font);
            if(mIsPackage1 || (favoritesLimitPreference>PB_DEF_FAVORITES_LIMIT)){
                //storeScreenPackage1Button.setText("Unlimited Favorites $0.99");
                //storeScreenPackage1Button.setTextColor(0x707070);
                //storeScreenPackage1Button.setBackgroundResource(R.drawable.bought_favorites_button);

                storeScreenPackage1Button.setClickable(false);
            }
            else {
                storeScreenPackage1Button.setBackgroundResource(R.drawable.buy_favorites_button);
                storeScreenPackage1Button.setClickable(true);
                storeScreenPackage1Button
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                playSound(soundIDButtonClick);
                                sendStoreStateMachineMsg(INP_STORE_SCREEN_BUY_PACKAGE_1);
                                //JCT  Remove later
                                /*favoritesLimitPreference = FAVORITES_MAX_LIMIT;
                                savePreferenceInt(PREF_STR_FAVORITES_LIMIT, favoritesLimitPreference);
                                storeScreenPackage1Button.setBackgroundResource(R.drawable.bought_favorites_button);
                                storeScreenPackage1Button.setClickable(false);*/

                                sendAnalyticsEvent("Action", "BuyFavorites");

                            }
                        });


            }

            if(mIsPackage2 || !adsOnOffPreference){
                //storeScreenPackage2Button.setBackgroundResource(R.drawable.bought_noads_button);
                //storeScreenPackage2Button.setText("Ads Removed");
                //storeScreenPackage2Button.setTextColor(0x707070);
                storeScreenPackage2Button.setClickable(false);

            }
            else {
                storeScreenPackage2Button.setBackgroundResource(R.drawable.buy_noads_button);
                storeScreenPackage2Button.setClickable(true);
                storeScreenPackage2Button
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                playSound(soundIDButtonClick);
                                sendStoreStateMachineMsg(INP_STORE_SCREEN_BUY_PACKAGE_2);
                                //JCT  Remove later
                                /*adsOnOffPreference = false;
                                savePreferenceBool(PREF_STR_ADS_ONOFF, adsOnOffPreference);
                                storeScreenPackage2Button.setBackgroundResource(R.drawable.bought_noads_button);
*/
                                sendAnalyticsEvent("Action", "BuyNoAds");
                            }
                        });

            }

            if(mIsPackage3){
                //storeScreenPackage3Button.setText("Photos: "+photosLimitPreference*2);
                //storeScreenPackage2Button.setTextColor(0x707070);
                storeScreenPackage3Button.setClickable(false);

            }
            else {
                if(photosLimitPreference >= totalGroupDBCount ){
                    //storeScreenPackage3Button.setText("Photos: "+photosLimitPreference*2);
                    storeScreenPackage3Button.setClickable(false);
                } else {
                    //storeScreenPackage3Button.setText("Photos: "+photosLimitPreference*2+". Get "+(totalGroupDBCount-photosLimitPreference)*2+" More for $0.99 ");
                    storeScreenPackage3Button.setBackgroundResource(R.drawable.buy_photos_button);
                    storeScreenPackage3Button.setClickable(true);
                    storeScreenPackage3Button
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    playSound(soundIDButtonClick);
                                    sendStoreStateMachineMsg(INP_STORE_SCREEN_BUY_PACKAGE_3);
                                    //JCT  Remove later
                                    /*photosLimitPreference = totalGroupDBCount;
                                    photoPurchaseFlag = true;
                                    storeScreenPackage3Button.setBackgroundResource(R.drawable.bought_photos_button);
*/
                                    savePreferenceInt(PREF_STR_PHOTOS_LIMIT, photosLimitPreference);
                                    sendAnalyticsEvent("Action", "BuyPhotos");

                                }
                            });

                }
            }

            if(mIsPackage4 || premiumPreference || (mIsPackage3 && mIsPackage2 && mIsPackage1)){
                //storeScreenPackage4Button.setText("Premium Version Purchased");
                //storeScreenPackage2Button.setTextColor(0x707070);
                storeScreenPackage4Button.setClickable(false);

            }
            else {
                storeScreenPackage4Button.setBackgroundResource(R.drawable.buy_premium_button);
                storeScreenPackage4Button.setClickable(true);
                storeScreenPackage4Button
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                playSound(soundIDButtonClick);
                                sendStoreStateMachineMsg(INP_STORE_SCREEN_BUY_PACKAGE_4);
                                //JCT  Remove later
                                /*premiumPreference = true;
                                favoritesLimitPreference = FAVORITES_MAX_LIMIT;
                                adsOnOffPreference = false;
                                photosLimitPreference = totalGroupDBCount;
                                photoPurchaseFlag = true;
                                SharedPreferences prefs = PreferenceManager
                                        .getDefaultSharedPreferences(baseContext);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(PREF_STR_PREMIUM, premiumPreference);
                                editor.putInt(PREF_STR_FAVORITES_LIMIT, favoritesLimitPreference);
                                editor.putBoolean(PREF_STR_ADS_ONOFF, adsOnOffPreference);
                                editor.putInt(PREF_STR_PHOTOS_LIMIT, photosLimitPreference);
                                editor.commit();
                                storeScreenPackage4Button.setBackgroundResource(R.drawable.bought_premium_button);
*/
                                sendAnalyticsEvent("Action", "BuyPremium");
                            }
                        });

            }
            if(!NoPurchaseFlag){
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "If you enjoy the app, please consider making in-app purchases to help us with the development costs."
                        , Toast.LENGTH_LONG)
                        .show();
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "If you enjoy the app, please consider making in-app purchases to help us with the development costs."
                        , Toast.LENGTH_SHORT)
                        .show();
            }
            else{
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "EARLY ADAPTOR VERSION.  ALL ITEMS PRE-INSTALLED.  THANK YOU FOR PLAYING"
                        , Toast.LENGTH_LONG)
                        .show();
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "EARLY ADAPTOR VERSION.  ALL ITEMS PRE-INSTALLED.  THANK YOU FOR PLAYING"
                        , Toast.LENGTH_SHORT)
                        .show();
            }
            sendAnalyticsScreen("Store");
            return rootView;
        }

        void buyPackage(String sku_product){

            String payload = PB_USER_NAME;

            if(!inAppBillingStarted){
                Log.d(TAG, "buyPackage: In App Billing not activated");
                /*Toast.makeText(getActivity().getApplicationContext(), "In App Billing not activated",
                                Toast.LENGTH_SHORT).show();*/
                return;
            }
            mHelper.launchPurchaseFlow(getActivity(), sku_product, RC_REQUEST,
                    mPurchaseFinishedListener, payload);

        }

        // Callback for when a purchase is finished
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null) return;

                //sendStateMachineMsg(MainActivity.INP_STORE_SCREEN_PURCHASE_DONE);

                if (result.isFailure()) {
                    complain("Error purchasing: " + result);
                    //setWaitScreen(false);
                    return;
                }
                if (!verifyDeveloperPayload(purchase)) {
                    complain("Error purchasing. Authenticity verification failed.");
                    //setWaitScreen(false);
                    return;
                }

                Log.d(TAG, "Purchase successful.");

                if (purchase.getSku().equals(SKU_PACKAGE_1)) {
                    Log.d(TAG, "Purchase is Endless Favorites. Congratulating user.");
                    alert("Thank you for purchasing Endless Favorites");
                    mIsPackage1 = true;
                    favoritesLimitPreference = FAVORITES_MAX_LIMIT;
                    savePreferenceInt(PREF_STR_FAVORITES_LIMIT, favoritesLimitPreference);
                    storeScreenPackage1Button.setBackgroundResource(R.drawable.bought_favorites_button);
                }

                if (purchase.getSku().equals(SKU_PACKAGE_2)) {
                    // bought the premium upgrade!
                    Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                    alert("Thank you for purchasing No Ads");
                    mIsPackage2 = true;
                    adsOnOffPreference = false;
                    savePreferenceBool(PREF_STR_ADS_ONOFF, adsOnOffPreference);
                    storeScreenPackage2Button.setBackgroundResource(R.drawable.bought_noads_button);
                    //updateUi();
                    //setWaitScreen(false);
                }

                if (purchase.getSku().equals(SKU_PACKAGE_3)) {
                    // bought the premium upgrade!
                    Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                    alert("Thank you for purchasing more Photos");
                    mIsPackage3 = true;
                    photosLimitPreference = totalGroupDBCount;
                    photoPurchaseFlag = true;
                    savePreferenceInt(PREF_STR_PHOTOS_LIMIT, photosLimitPreference);
                    storeScreenPackage3Button.setBackgroundResource(R.drawable.bought_photos_button);

                }

                if ((purchase.getSku().equals(SKU_PACKAGE_4)) || (mIsPackage3 && mIsPackage2 && mIsPackage1)) {
                    // bought the premium upgrade!
                    Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                    alert("Thank you for purchasing Premium Package");
                    mIsPackage4 = true;
                    premiumPreference = true;
                    favoritesLimitPreference = FAVORITES_MAX_LIMIT;
                    adsOnOffPreference = false;
                    photosLimitPreference = totalGroupDBCount;
                    photoPurchaseFlag = true;
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(baseContext);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PREF_STR_PREMIUM, premiumPreference);
                    editor.putInt(PREF_STR_FAVORITES_LIMIT, favoritesLimitPreference);
                    editor.putBoolean(PREF_STR_ADS_ONOFF, adsOnOffPreference);
                    editor.putInt(PREF_STR_PHOTOS_LIMIT, photosLimitPreference);
                    editor.commit();
                    storeScreenPackage4Button.setBackgroundResource(R.drawable.bought_premium_button);

                }

            }
        };

        void complain(String message) {
            Log.e(TAG, "**** Error: " + message);
            //alert("Error: " + message);
        }

        void alert(String message) {
            AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
            bld.setMessage(message);
            bld.setNeutralButton("OK", null);
            Log.d(TAG, "Showing alert dialog: " + message);
            bld.create().show();
        }


        boolean sendStoreStateMachineMsg(int msg) {
            Message m = hStoreStateMachine.obtainMessage();
            m.what = msg;
            hStoreStateMachine.sendMessage(m);
            return (true);
        }

        private final Handler.Callback storeStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;

                switch (msg.what) {
                    case INP_STORE_SCREEN_BUY_PACKAGE_1:
                        Log.i(TAG,
                                "handleMessage: INP_RS_SHOW_REVIEW_IMAGES:  Done.");
                        buyPackage(SKU_PACKAGE_1);
                        break;
                    case INP_STORE_SCREEN_BUY_PACKAGE_2:
                        buyPackage(SKU_PACKAGE_2);
                        break;
                    case INP_STORE_SCREEN_BUY_PACKAGE_3:
                        buyPackage(SKU_PACKAGE_3);
                        break;
                    case INP_STORE_SCREEN_BUY_PACKAGE_4:
                        buyPackage(SKU_PACKAGE_4);
                        break;
                    default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;
               }
                return true;
            }

        };

    }

    private void showError(String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(messageId).
                setPositiveButton("OK", null);
        builder.show();
    }

    private void fbInvite() {
        AppInviteContent content = new AppInviteContent.Builder()
                .setApplinkUrl("https://fb.me/1573645349587112")
                .setPreviewImageUrl("https://static.wixstatic.com/media/486cbe_8cfea205d9354d46ab7cad006b9d82aa~mv2.jpg/v1/fill/w_1200,h_628,al_c,q_85/486cbe_8cfea205d9354d46ab7cad006b9d82aa~mv2.jpg")
                .build();
        if (AppInviteDialog.canShow()) {
            appInviteDialog.show(this, content);
        } else {
            showError("AppInviteDialog.canShow returned false");
        }
    }


    /**
     * PLAY ALL SCREEN FRAGMENT
     */
    public static class ShareScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Button shareScreenFBShareButton;
        private static Button shareScreenFBInviteButton;
        private static Button shareScreenMoreButton;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static ShareScreenFragment newInstance(int sectionNumber) {
            ShareScreenFragment fragment = new ShareScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ShareScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "ShareScreenFragment onCreateView(): entered.");
            System.gc();

            rootView = inflater.inflate(R.layout.fragment_share, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            shareScreenFBShareButton = (Button) rootView
                    .findViewById(R.id.shareScreenFBShareButton);
            shareScreenFBShareButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(BetaFlag)
                                return;
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SHARE_SCREEN_SHARE_BUTTON_PRESSED);
                            sendAnalyticsEvent("Action", "DoFBShare");
                        }
                    });
            shareScreenFBInviteButton = (Button) rootView
                    .findViewById(R.id.shareScreenFBInviteButton);
            shareScreenFBInviteButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(BetaFlag)
                                return;
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SHARE_SCREEN_INVITE_BUTTON_PRESSED);
                            sendAnalyticsEvent("Action", "DoFBInvite");
                        }
                    });
            shareScreenMoreButton = (Button) rootView
                    .findViewById(R.id.shareScreenMoreButton);
            shareScreenMoreButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(BetaFlag)
                                return;
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SHARE_SCREEN_MORE_BUTTON_PRESSED);
                            sendAnalyticsEvent("Action", "DoGeneralShare");
                        }
                    });
            sendAnalyticsScreen("Share");
            return rootView;
        }
    }
    /**
     * QUIT PLAY SCREEN FRAGMENT
     */
    public static class QuitPlayScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        static TextView quitPlayScreenQuestionTextView;
        static Button quitPlayScreenContinueButton;
        static Button quitPlayScreenQuitButton;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static QuitPlayScreenFragment newInstance(int sectionNumber) {
            QuitPlayScreenFragment fragment = new QuitPlayScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public QuitPlayScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_quit_play, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            quitPlayScreenQuestionTextView = (TextView) rootView
                    .findViewById(R.id.quitPlayScreenQuestionTextView);
            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            quitPlayScreenQuestionTextView.setTypeface(font);

            quitPlayScreenContinueButton = (Button) rootView
                    .findViewById(R.id.quitPlayScreenContinueButton);
            quitPlayScreenContinueButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_QUIT_PLAY_SCREEN_CONTINUE_BUTTON_PRESSED);
                        }
                    });
            quitPlayScreenQuitButton = (Button) rootView
                    .findViewById(R.id.quitPlayScreenQuitButton);
            quitPlayScreenQuitButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_QUIT_PLAY_SCREEN_QUIT_BUTTON_PRESSED);
                        }
                    });
            sendAnalyticsScreen("QuitPlay");
            return rootView;
        }
    }


    /**
     * High Scores SCREEN FRAGMENT
     */
    public static class HighScoresScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private final static int MAX_HIGH_SCORES = 5;
        private final static int INP_SS_SHOW_SCORES = 1000;
        private ImageView highScoresScreenHeadlineImageView;
        private TextView[]  highScoresScreenScoreTextView={null,null,null,null,null};
        private TextView  highScoresScreenScore2TextView;
        private ArrayList<FPPItem> pbudItems;
        int numberOfScores;
        Handler hScoresStateMachine = null;
        long mode;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static HighScoresScreenFragment newInstance(int sectionNumber) {
            HighScoresScreenFragment fragment = new HighScoresScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public HighScoresScreenFragment() {
            return;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            hScoresStateMachine = new Handler(scoresStateMachineMessage);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_scores, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            mode = (long) b.getInt(ARG_SECTION_NUMBER);

            highScoresScreenHeadlineImageView = (ImageView) rootView
                    .findViewById(R.id.highScoresScreenHeadlineTextView);
            if(mode == PB_MODE_SURVIVAL)
                highScoresScreenHeadlineImageView.setBackgroundResource(R.drawable.top_survival_scores_label);

            highScoresScreenScoreTextView[0] = (TextView) rootView
                    .findViewById(R.id.highScoresScreenScore1TextView);

            highScoresScreenScoreTextView[1] = (TextView) rootView
                    .findViewById(R.id.highScoresScreenScore2TextView);
            highScoresScreenScoreTextView[2] = (TextView) rootView
                    .findViewById(R.id.highScoresScreenScore3TextView);
            highScoresScreenScoreTextView[3] = (TextView) rootView
                    .findViewById(R.id.highScoresScreenScore4TextView);
            highScoresScreenScoreTextView[4] = (TextView) rootView
                    .findViewById(R.id.highScoresScreenScore5TextView);

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            for (int i = 0;i<MAX_HIGH_SCORES;i++)
                highScoresScreenScoreTextView[i].setTypeface(font);

            sendScoresStateMachineMsg(INP_SS_SHOW_SCORES);
            sendAnalyticsScreen("HighScore");
            return rootView;
        }



        void highScoresFillTextViews(long _gameMode){
            pbudItems = new ArrayList<FPPItem>();

            Cursor listCursor = pBudDBAdapter
                    .getAllScoresCursorByModeDesc(_gameMode);
            int i = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            if (listCursor.moveToFirst()) {

                do {
                    long creationTime = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_ST_CREATION_TIME));
                    long gameMode = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_ST_GAME_MODE));
                    long gameScore = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_ST_SCORE));
                    long scoreTime = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_ST_SCORE_TIME));
                    String userName = listCursor.getString(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_ST_USER_NAME));
                    FPPItem newItem = new FPPItem(userName, creationTime,
                            gameMode, gameScore, scoreTime);
                    pbudItems.add(i, newItem);
                    String dateString = formatter.format(new Date(creationTime));
                    String s= ""+gameScore+" Points\n"+dateString;
                    int len = String.valueOf(gameScore).length();
                    SpannableString ss1=  new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(2.0f), 0,len+7, 0); // set size
                    ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#b0d506")), 0, len+7, 0);// set color
                    highScoresScreenScoreTextView[i].setText(ss1);

                    //highScoresScreenScoreTextView[i].setText(""+gameScore+" Points   "+dateString);
                    i++;
                    if(i>=MAX_HIGH_SCORES)
                        break;
                } while (listCursor.moveToNext());
            } else {
                Log.e(TAG, "getPreviousList(): listCursor.moveToFirst() failed.");
            }
            if(i<MAX_HIGH_SCORES)
                numberOfScores = i;
            else
                numberOfScores = MAX_HIGH_SCORES;
        }

        void highScoresDisplayTextViews(){

            if(numberOfScores==0){
                highScoresScreenScoreTextView[0].setText("No Score Recorded\n\nPlay a Game");
                highScoresScreenScoreTextView[0].setVisibility(View.VISIBLE);
                return;
            }

            for(int i=numberOfScores-1; i>=0;i--){
                AnimationSet animation = new AnimationSet(false);
                Animation fadeIn = new AlphaAnimation((float)(0), (float)(1.0));
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(1000);
                fadeIn.setStartOffset(800*(numberOfScores-i-1));

                animation.addAnimation(fadeIn);
                animation.setRepeatCount(1);
                highScoresScreenScoreTextView[i].setAnimation(animation);

                highScoresScreenScoreTextView[i].setVisibility(View.VISIBLE);
            }
        }
        boolean sendScoresStateMachineMsg(int msg) {
            Message m = hScoresStateMachine.obtainMessage();
            m.what = msg;
            hScoresStateMachine.sendMessage(m);
            return (true);
        }
        private final Handler.Callback scoresStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;

                switch (msg.what) {
                    case INP_SS_SHOW_SCORES:
                        Log.i(TAG,
                                "handleMessage: INP_RS_SHOW_REVIEW_IMAGES:  Done.");
                        highScoresFillTextViews(mode);
                        highScoresDisplayTextViews();
                        break;
                    default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;

                }
                return true;
            }
        };

    }

    final private static int MAX_AWARD_COUNT = 15;

    static void initAwardList(){
        int cnt = MAX_AWARD_COUNT;
        String[] titles = {"Hobbyist","Novice","Amateur","Appreciator","Enthusiast","Collector","Connoisseur","Advanced","Expert","Advanced Expert","Master","Zen","Zen Master","Guru", "Master Guru"};
        String[] description = {"Win 10 points","Win 50 points","Win 100 points","Win 300 points","Win 500 points","Win 1,000 points","Win 2,000 points","Win 5,000 points","Win 8,000 points","Win 10,000 points",
                "Win 20,000 points","Win 50,000 points","Win 100,000 points","Win 200,000 points","Win 500,000 points"};
        long[] id = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

        pbudAwards = new ArrayList<FPPAward>();
        for(int i=0;i<cnt;i++){
            FPPAward award = new FPPAward(id[i], false, PB_MODE_BASIC, 0, PB_USER_NAME, titles[i], description[i]);
            pbudAwards.add(i,award);
        }

        Cursor listCursor = pBudDBAdapter
                .getAllAwardsCursorByMode(PB_MODE_BASIC);

        if (listCursor.moveToFirst()) {
            do {
                long creationTime = listCursor.getLong(listCursor
                        .getColumnIndex(PBudDBAdapter.KEY_AT_CREATION_TIME));
                long awardId = listCursor.getLong(listCursor
                        .getColumnIndex(PBudDBAdapter.KEY_AT_AWARD_ID));
                turnOnAward(awardId);

            } while (listCursor.moveToNext());
        } else {
            Log.e(TAG, "getPreviousList(): listCursor.moveToFirst() failed.");
        }
    }

    static boolean turnOnAward(long id){
        FPPAward award;
        for(int i=0;i<MAX_AWARD_COUNT;i++){
            award = pbudAwards.get(i);
            if(id == award.getAwardId()){
                award.setAwardOn(true);
                pbudAwards.set(i,award);
                return(true);
            }
         }
        return(false);
    }


    /**
     * Award SCREEN FRAGMENT
     */
    public static class AwardScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static AwardScreenFragment newInstance(int sectionNumber) {
            AwardScreenFragment fragment = new AwardScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initAwardList();
            System.gc();
        }


        public AwardScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.awardlistview, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            if ((awardListView = (ListView) rootView
                    .findViewById(R.id.awardListView)) == (ListView) null) {
                Log.e(TAG,
                        "processRecord: findViewById(R.id.awardListView)returned null");
                return (rootView);
            }

            awardListView.setDividerHeight(1);

            //pbudAwards = new ArrayList<FPPAward>();
            int resID = R.layout.pbudlist_award;
            awardAdapter = new FPPAwardAdapter(getActivity().getApplicationContext(), resID, pbudAwards);
            awardListView.setAdapter(awardAdapter);
            awardAdapter.notifyDataSetChanged();

            sendAnalyticsScreen("Awards");
            return rootView;
        }
    }


    private static ArrayList<FPPFavorite> pbudFavorites;
    private static GridView favoritesGridView;
    private static FPPFavoriteAdapter favoritesAdapter;

    static void initFavoritesList(){
        //int cnt = MAX_FAVORITES_COUNT;
        long imageId=0;
        String imageFile = " ";

        pbudFavorites = new ArrayList<FPPFavorite>();

        Cursor cursor = pBudDBAdapter.getAllFavoritesCursorByUser(PB_USER_NAME);
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                imageId = cursor.getLong(cursor
                        .getColumnIndex(PBudDBAdapter.KEY_FT_FAVORITE_IMAGE_ID));

                Cursor listCursor = pBudDBAdapter
                        .getAllItemsCursorBySequence(imageId);
                if (listCursor.moveToFirst()) {
                    do {
                        imageFile = listCursor.getString(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
                    } while (listCursor.moveToNext());
                } else {
                    Log.e(TAG, "selectImageById(): listCursor.moveToFirst() failed.");
                }

                FPPFavorite favorite = new FPPFavorite(imageId,imageFile);
                pbudFavorites.add(i,favorite);
                i++;
            } while (cursor.moveToNext());
        } else {
            Log.i(TAG,
                    "initFavoritesList(): listCursor.moveToFirst() failed for high rating.");
            cursor.close();
            return;
        }
        cursor.close();
    }
    /**
     * Favorites Grid SCREEN FRAGMENT
     */

    public static class FavoritesGridScreenFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        GridView favoritesGridView;
        /*
         * Returns a new instance of this fragment for the given section number.
         */
        public static FavoritesGridScreenFragment newInstance(int sectionNumber) {
            FavoritesGridScreenFragment fragment = new FavoritesGridScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initFavoritesList();
            System.gc();

        }


        public FavoritesGridScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "FavoritesGridScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.favoritesgridview, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            if ((favoritesGridView = (GridView) rootView
                    .findViewById(R.id.favoritesGridView)) == (GridView) null) {
                Log.e(TAG,
                        "processRecord: findViewById(R.id.favoritesGridView)returned null");
                return (rootView);
            }

            favoritesAdapter = new FPPFavoriteAdapter(getActivity().getApplicationContext(), 0, pbudFavorites);
            favoritesGridView.setAdapter(favoritesAdapter);
            favoritesGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            imageClicked = false;
            favoritesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    //Toast.makeText(getActivity().getApplicationContext(), "" + position,
                    //        Toast.LENGTH_SHORT).show();

                    long seqNum = pbudFavorites.get(position).getSequenceNumber();
                    if (imageClicked)
                        return;
                    else
                        imageClicked = true;
                    sendStateMachineMsg(
                            MainActivity.INP_FAVORITES_SCREEN_IMAGE_PRESSED,
                            (int) seqNum);
                    String strSequence = String.valueOf(seqNum);
                    sendAnalyticsEvent("Action", "SeeTipFromFavorite", strSequence);

                }
            });
            favoritesAdapter.notifyDataSetChanged();
            sendAnalyticsScreen("Favorites");
            return rootView;
        }
    }


    public static class TipsScreenPagerFragment extends Fragment {
        ViewPager pager;
        TipsScreenAdapter adapter;
        int sequence1;
        int sequence2;
        //TextView favoritesScreenNoFavoritesTextView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            tipsScreenInit();
            Bundle b = getArguments();
            if (b == null)
                return;
            int sequence1 = b.getInt(PB_BUNDLE_MSG_INT_1, 0);
            int sequence2 = b.getInt(PB_BUNDLE_MSG_INT_2, 0);
            adapter = new TipsScreenAdapter(getActivity(), getChildFragmentManager(),sequence1,sequence2);
            System.gc();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.pager_tips, container, false);

            pager = (ViewPager) rootView.findViewById(R.id.tipsPager);
            pager.setAdapter(adapter);

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageSelected(int pageNumber) {
                    Log.i(TAG, "StartScreenFragment onCreateView(): entered.");
                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "StartScreenFragment onCreateView(): entered.");
                }

                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "StartScreenFragment onCreateView(): entered.");
                }
            });
            if(hintTipsPreference)
                sendStateMachineMsg(MainActivity.INP_TIPS_SCREEN_TIPS_HINT);
            return (rootView);
        }

        private PagerAdapter buildAdapter() {
            return (new TipsScreenAdapter(getActivity(), getChildFragmentManager(),sequence1,sequence2));
        }
    }

    public static class TipsScreenAdapter extends FragmentStatePagerAdapter {
        Context ctxt = null;
        int[] sequence = {0,0};

      public TipsScreenAdapter(Context ctxt, FragmentManager mgr, int seq1, int seq2) {
            super(mgr);
            this.ctxt = ctxt;
          sequence[0]= seq1;
          sequence[1] = seq2;
        }

        @Override
        public int getCount() {
            int cnt;
            if(sequence[1] == INVALID_NUMBER)
                cnt = 1;
            else
                cnt = 2;
            return(cnt);
        }

        @Override
        public Fragment getItem(int position) {
            return(TipsFavoritesScreenFragment.newInstance(sequence[position]));
        }

        @Override
        public String getPageTitle(int position) {
            return (String.valueOf(position+1));
        }
    }

    public static void tipsScreenInit(){

    }
    /**
     * TIPS SCREEN FRAGMENT
     */
    public static class TipsFavoritesScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SEQUENCE_1 = "sequence_1";
        private int sequence1;
        TextView tipsScreenHeadlineTextView;
        TextView tipsScreenTipsTextView;
        ImageView tipsImg1;
        ImageView tipsFavoriteOffImageView;
        ImageView tipsFavoriteOnImageView;
        ImageView tipsFavoriteMapButton;
        TextView tipsMapTextView;
        MyImage tipsImageObj;
        Handler hTipsStateMachine = null;
        String strSequence;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static TipsFavoritesScreenFragment newInstance(int sequence1 ) {
            TipsFavoritesScreenFragment fragment = new TipsFavoritesScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SEQUENCE_1, sequence1);
            //args.putInt(ARG_SEQUENCE_2, sequence2);
            //args.putInt(ARG_SEQUENCE_NUMBER, sequenceNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public TipsFavoritesScreenFragment() {
            return;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            tipsImageObj = new MyImage(0, 0, 0, 0, "", "", "");
            hTipsStateMachine = new Handler(tipsStateMachineMessage);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView(): entered.");
            System.gc();

            rootView = inflater.inflate(R.layout.fragment_tips_favorites, container,
                    false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            sequence1 = b.getInt(ARG_SEQUENCE_1);
            strSequence = String.valueOf(sequence1);

            tipsFavoriteOffImageView = (ImageView) rootView
                    .findViewById(R.id.tipsFavoriteOffImageView2);
            tipsFavoriteOnImageView = (ImageView) rootView
                    .findViewById(R.id.tipsFavoriteOnImageView2);

            tipsFavoriteMapButton = (ImageView) rootView
                    .findViewById(R.id.tipsFavoriteMapButton);


            tipsFavoriteOffImageView.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {

                    int cnt;
                    if ((cnt = pBudDBAdapter.getFavoritesItemCount(PB_USER_NAME)) < 0){
                        Log.e(TAG, "tipsFavoriteOffImageView: getFavoritesItemCount() failed.");
                        return;
                    }

                    if (cnt < favoritesLimitPreference) {
                        tipsFavoriteOffImageView.setVisibility(View.INVISIBLE);
                        tipsFavoriteOnImageView.setVisibility(View.VISIBLE);
                        if (pBudDBAdapter.insertFavorite(PB_USER_NAME, tipsImageObj.getSequence()) < 0) {
                            Log.e(TAG, "tipsFavoriteOffImageView: insertFavorite() failed.");
                            return;
                        }
                        sendAnalyticsEvent("Action", "SetTipFavoriteOn", strSequence);

                    } else {
                        sendStateMachineMsg(MainActivity.INP_TIPS_SCREEN_FAVORITES_LIMIT_DIALOGUE);
                    }

                }
            });


            tipsFavoriteOnImageView.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    tipsFavoriteOnImageView.setVisibility(View.INVISIBLE);
                    tipsFavoriteOffImageView.setVisibility(View.VISIBLE);
                    if (pBudDBAdapter.deleteFavoriteByImageId(PB_USER_NAME, tipsImageObj.getSequence()) < 0) {
                        Log.e(TAG, "tipsFavoriteOffImageView: deleteFavorite() failed.");
                    return;
                }
                    sendAnalyticsEvent("Action", "SetTipFavoriteOff", strSequence);
                    return;
                }
            });

            tipsFavoriteMapButton.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    //sendStateMachineMsg(MainActivity.INP_TIPS_SCREEN_MAP_BUTTON_PRESSED);
                    sendTipsStateMachineMsg(INP_TIPS_SCREEN_MAP_BUTTON_PRESSED);
                    sendAnalyticsEvent("Action", "SeeTipMap", strSequence);
                    return;
                }
            });

            tipsScreenTipsTextView = (TextView) rootView
                    .findViewById(R.id.tipsFavoritesScreenTipsTextView);
            tipsScreenTipsTextView
                    .setMovementMethod(new ScrollingMovementMethod());
            tipsMapTextView = (TextView) rootView
                    .findViewById(R.id.tipsMapTextView);

            tipsImg1 = (ImageView) rootView
                    .findViewById(R.id.tipsFavoritesScreenImageView);

            int maxHeight = (int)((float)((screenWidth - 80) * 2 / 3)*screenWHRatio);
            tipsImg1.setAdjustViewBounds(true);
            tipsImg1.getLayoutParams().height = maxHeight;
            tipsImg1.getLayoutParams().width = screenWidth - 90;
            sendTipsStateMachineMsg(INP_TS_SHOW_TIPS_IMAGE);

            sendAnalyticsScreen("Tips");
            //sendAnalyticsEvent("Action", "SeeTip", strSequence);

            //.setLabel(getString(labelId))
            return rootView;
        }


        private void playTipsImages() {
            imageClicked = false;
            selectTipsImage();
            displayTipsImages();
            tipsScreenTipsTextView.setText(tipsImageObj.getComments());
            tipsScreenTipsTextView.scrollTo(0, 0);
        }

        private void selectTipsImage() {
            selectImageById(sequence1,tipsImageObj);

            if (!tipsImageObj.getReady()) {
                Log.i(TAG, "selectReviewImages: Images failed to be selected.");

            }
        }

        private void selectImageById(int seq, MyImage imageObj) {
            imageObj.setReady(false);
            Cursor listCursor = pBudDBAdapter
                    .getAllItemsCursorBySequence(seq);
            if (listCursor.moveToFirst()) {
                do {
                    long rating = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_RATING));
                    long sequence = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
                    long group = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_GROUP));
                    long groupSeq = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
                    String imageFile = listCursor.getString(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
                    String comments = listCursor.getString(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));
                    Float latitude = listCursor.getFloat(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_LATITUDE));
                    Float longitude = listCursor.getFloat(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_LONGITUDE));
                    String location = listCursor.getString(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_LOCATION));
                    imageObj.setMyImage(sequence, group, groupSeq,
                            rating, imageFile, "", comments,latitude,longitude,location );
                    imageObj.setReady(true);

                } while (listCursor.moveToNext());
            } else {
                Log.e(TAG, "selectImageById(): listCursor.moveToFirst() failed.");
            }
            if (!imageObj.getReady()) {
                Log.e(TAG,
                        "selectImageById: Images failed to be selected.seq: "
                                + imageObj.getSequence());
                /*Toast.makeText(
                        getActivity().getApplicationContext(),
                        "selectImageById: Images failed to be selected.seq: "
                                + imageObj.getSequence(), Toast.LENGTH_LONG)
                        .show();*/
            }
        }

        private void displayTipsImages() {

            Context ctx = getActivity().getApplicationContext();
            displayTipsImageFile(ctx, tipsImageObj.getImageFile(), 1, false);

        }


        private void displayTipsImageFile(Context ctx, String fileName,
                                          int img, boolean animate) {


            displayImageReturn ret = decodeDisplayImage(ctx,fileName);
            int size = ret.size;
            byte[] bytes = ret.bytes;
            Bitmap bMap = ret.bMap;

            setLayoutSizeParams(bytes,size, tipsImg1,tipsImageObj);
                Animation fadeIn = new AlphaAnimation((float)(0), (float)(1.0));
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(TIPS_IMAGE_FADE_IN_DURATION);
                fadeIn.setStartOffset(0);

                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                animation.setRepeatCount(1);
                tipsImg1.setAnimation(animation);
                tipsImg1.setImageBitmap(bMap);

                tipsImg1.setVisibility(View.VISIBLE);

                fadeIn = new AlphaAnimation((float)(0), (float)(1.0));
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(TIPS_TEXT_FADE_IN_DURATION);
                fadeIn.setStartOffset(TIPS_TEXT_START_OFFSET);

                animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                animation.setRepeatCount(1);

               animation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        if (pBudDBAdapter.getFavoritesCursorByImageId(PB_USER_NAME, tipsImageObj.getSequence())) {
                            tipsFavoriteOffImageView.setVisibility(View.INVISIBLE);
                            tipsFavoriteOnImageView.setVisibility(View.VISIBLE);
                         } else {
                            tipsFavoriteOnImageView.setVisibility(View.INVISIBLE);
                            tipsFavoriteOffImageView.setVisibility(View.VISIBLE);
                        }
                        if(tipsImageObj.getLatitude()!= 0){
                            tipsFavoriteMapButton.setVisibility(View.VISIBLE);
                        }
                        else
                            tipsFavoriteMapButton.setVisibility(View.INVISIBLE);

                        String location=tipsImageObj.getLocation();
                        if(!location.equals("")){
                            tipsMapTextView.setText(location);
                            tipsMapTextView.setVisibility(View.VISIBLE);
                        }
                        else
                            tipsMapTextView.setVisibility(View.INVISIBLE);


                    }
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });

                tipsScreenTipsTextView.setAnimation(animation);
                tipsScreenTipsTextView.setVisibility(View.VISIBLE);
        }

        boolean sendTipsStateMachineMsg(int msg) {
            Message m = hTipsStateMachine.obtainMessage();
            m.what = msg;
            hTipsStateMachine.sendMessage(m);
            return (true);
        }

        private final Handler.Callback tipsStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;

                switch (msg.what) {
                    case INP_TS_SHOW_TIPS_IMAGE:
                        Log.i(TAG,
                                "handleMessage: INP_RS_SHOW_REVIEW_IMAGES:  Done.");
                        playTipsImages();
                        break;
                    case INP_TIPS_SCREEN_MAP_BUTTON_PRESSED:
                        Intent mapActivity = new Intent(getActivity(),
                                MapActivity.class);
                        mapActivity.putExtra(MapActivity.MAP_PARAMETER_LONGITUDE,tipsImageObj.getLongitude());
                        mapActivity.putExtra(MapActivity.MAP_PARAMETER_LATITUDE,tipsImageObj.getLatitude());
                        startActivity(mapActivity);
                        break;
                    default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;

                }
                return true;
            }

        };

    }

    public static class StartInfoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Welcome to FunPicks Photo\n\nSelect \"Classic Game\" to start playing the tutorial game.\n\nOnce you’re comfortable with \"Classic\", you may want to play \"Survival\", which is more challenging.\n\nRemember, the goal of the app is to learn basic composition tips by subliminal learning, and is less about gamesmanship.\n\nDisclaimer: Photography is a subjective art form, hence all materials within this application are strictly the personal opinions of the photographers and authors of the tips.  If you find yourself in disagreement of the selection results and tips, please know the application is merely presenting one view point.").append("\n");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintStartInfoPreference=false;
                            savePreferenceBool(PREF_STR_HINT_START_INFO, false);
                        }
                    }).create();

        }
    }

    public static class ClassicStartPlayDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Press on the better of the two photos and win points.  Level 1 includes three rounds.  Win 9 points gets you to Level 2.\n\n");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintClassicStartPlayPreference=false;
                            savePreferenceBool(PREF_STR_HINT_CLASSIC_START_PLAY, false);
                        }
                    }).create();

        }
    }
    public static class ClassicTipsInPlayDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Shortcut to Tips Screen: After selecting image, press on image second time after checkmark is displayed, and Tips screen will appear.\n\n");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintClassicStartPlayPreference=false;
                            savePreferenceBool(PREF_STR_HINT_CLASSIC_TIPS_DURING_PLAY, false);
                        }
                    }).create();

        }
    }


    public static class ReviewDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Swipe to see previous comparisons.  Press image to see Tips.\n\n");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintReviewPreference=false;
                            savePreferenceBool(PREF_STR_HINT_REVIEW, false);
                        }
                    }).create();
        }
    }

    public static class TipsDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Press    ");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.favorite_icon_on);
            builder.setSpan(new ImageSpan(getActivity(), bitmap),
                    builder.length() - 3, builder.length() - 2, 0);
            builder.append(" to set favorite.\n\nPress    ");
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_icon);
            builder.setSpan(new ImageSpan(getActivity(), bitmap),
                    builder.length() - 3, builder.length() - 2, 0);
                    builder.append(" to see camera location where photo was taken.");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintTipsPreference=false;
                            savePreferenceBool(PREF_STR_HINT_TIPS, false);
                        }
                    }).create();
        }
    }

    public static class SurvivalStartPlayDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("You have 15 seconds to select the photo you think it's the better image.  Game over after you lose three lives.\n\n");
            builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
            return new AlertDialog.Builder(getActivity())
                    .setTitle("HINT")
                    .setMessage(builder)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hintSurvivalStartPlayPreference=false;
                            savePreferenceBool(PREF_STR_HINT_SURVIVAL_START_PLAY, false);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_START_GAME_SCREEN_INFO_HINT_DONE);
                        }
                    }).create();
        }
    }

    public static class FavoriteLimitDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            sendAnalyticsScreen("FavoritesExceededDialogue");

            return new AlertDialog.Builder(getActivity())
                    .setTitle("Favorites Limit Reached")
                    .setMessage("Would you like to purchase unlimited Favorites for $1.99?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sendStateMachineMsg(MainActivity.INP_TIPS_SCREEN_START_IAP);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,	int which) {
                            // Do something else
                        }
                    }).create();
        }
    }


    public static void setLayoutSizeParams(String fileName, ImageView imageView, MyImage imgObj) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        double imageWidth = options.outWidth;
        double imageHeight = options.outHeight;
        double maxHeight;
        double maxWidth;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView
                .getLayoutParams();
        if (imageWidth >= imageHeight) {
            maxWidth = (screenWidth - (IMAGE_SIDE_MARGIN_DP*screenDensity))*screenWHRatio;
            maxHeight = (maxWidth + 10) * imageHeight / imageWidth;
            params.width = (int) maxWidth;
            params.height = (int) maxHeight;
            imgObj.setWidth((int) maxWidth);
            imgObj.setHeight((int) maxHeight);
        } else {
            maxHeight = (screenHeight - (screenHeight / 4)) / 2;
            maxWidth = maxHeight * (imageWidth / imageHeight);
            params.height = (int) maxHeight;
            params.width = (int) maxWidth;
            imgObj.setWidth((int) maxWidth);
            imgObj.setHeight((int) maxHeight);
        }
        imageView.setLayoutParams(params);

    }

    public static void setLayoutSizeParams(byte[] bytes, int size, ImageView imageView, MyImage imgObj) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,size, options);
        double imageWidth = options.outWidth;
        double imageHeight = options.outHeight;
        double maxHeight;
        double maxWidth;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView
                .getLayoutParams();
        if (imageWidth >= imageHeight) {
            maxWidth = (screenWidth - (IMAGE_SIDE_MARGIN_DP*screenDensity))*screenWHRatio;
            maxHeight = (maxWidth + 10) * imageHeight / imageWidth;
            params.width = (int) maxWidth;
            params.height = (int) maxHeight;
            imgObj.setWidth((int) maxWidth);

            imgObj.setHeight((int) maxHeight);
        } else {
            //maxHeight = ((screenHeight - (screenHeight / 3.6)) / 2)*screenWHRatio;
            maxHeight = ((screenHeight - (screenHeight / 4.5)) / 2);

            maxWidth = maxHeight * (imageWidth / imageHeight);
            params.height = (int) maxHeight;
            params.width = (int) maxWidth;
            imgObj.setWidth((int) maxWidth);
            imgObj.setHeight((int) maxHeight);
        }
        imageView.setLayoutParams(params);

    }
    public class ReviewScreenPagerFragment extends Fragment {
        ViewPager pager;
        ReviewScreenAdapter adapter;
        boolean[] showRightWrongIcon = new boolean[100];

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "ReviewScreenPagerFragment onCreate(): entered.");
            Arrays.fill(showRightWrongIcon, Boolean.TRUE);
            showRightWrongIcon[0] = false;
            showReviewPageOneCheckmark = true;
            adapter = (ReviewScreenAdapter) buildAdapter();
            System.gc();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View result = inflater.inflate(R.layout.pager, container, false);
            pager = (ViewPager) result.findViewById(R.id.reviewPager);
            pager.setAdapter(adapter);

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                public void onPageSelected(int pageNumber) {
                    // Just define a callback method in your fragment and call it like this!
                    Log.i(TAG, "ReviewScreenPagerFragment onPageSelected(): entered.  Page=" + pageNumber);
                    if (showRightWrongIcon[pageNumber]) {
                        showRightWrongIcon[pageNumber] = false;
                        ReviewScreenFragment frag = adapter.getReviewFragment(pageNumber);
                        frag.sendReviewStateMachineMsg(INP_RS_SHOW_RIGHT_WRONG_ICONS);
                    }
                    for (int i = 0; i < MAX_REVIEW_SCREENS; i++) {
                        if (i < pageNumber - 1 || i > pageNumber + 1)
                            adapter.resetReviewFragment(i);
                    }
                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub
                    //Log.i(TAG, "ReviewScreenPagerFragment onPageScrolled(): entered. arg0="+arg0
                    //        +" arg1="+arg1+" arg2="+arg2);
                }

                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "ReviewScreenPagerFragment onPageScrollStateChanged(): entered. arg0=" + arg0);
                }
            });
            sendAnalyticsScreen("Review");
            if(hintReviewPreference)
                sendStateMachineMsg(MainActivity.INP_REVIEW_SCREEN_REVIEW_HINT);
            return (result);
        }

        private PagerAdapter buildAdapter() {
            Log.i(TAG, "ReviewScreenPagerFragment buildAdapter(): entered.");
            return (new ReviewScreenAdapter(getActivity(), getChildFragmentManager()));
        }
    }

    public class ReviewScreenAdapter extends FragmentStatePagerAdapter {
        Context ctxt = null;
        Fragment[] reviewFragment = new Fragment[MAX_REVIEW_SCREENS];


        public ReviewScreenAdapter(Context ctxt, FragmentManager mgr) {
            super(mgr);
            this.ctxt = ctxt;
            Log.i(TAG, "ReviewScreenAdapter ReviewScreenAdapter(): entered.");
        }

        @Override
        public int getCount() {
            int reviewScreenCount;
            if(currentMode==PB_MODE_BASIC)
                reviewScreenCount = ((currentImageCount - 1)>MAX_REVIEW_SCREENS)?MAX_REVIEW_SCREENS:(currentImageCount - 1);
            else
                reviewScreenCount = ((survivalImageCount - 1)>MAX_REVIEW_SCREENS)?MAX_REVIEW_SCREENS:(survivalImageCount - 1);
            return(reviewScreenCount);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "ReviewScreenAdapter getItem(): entered.  Page="+position);

            reviewFragment[position] = ReviewScreenFragment.newInstance(position);
            return (reviewFragment[position]);
        }

        @Override
        public String getPageTitle(int position) {
            Log.i(TAG, "ReviewScreenAdapter getPageTitle(): entered.  Page="+position);
            return (String.valueOf(position + 1));
        }

        public ReviewScreenFragment getReviewFragment(int position) {
            Log.i(TAG, "ReviewScreenAdapter getReviewFragment(): entered.  Page="+position);
            return ((ReviewScreenFragment) reviewFragment[position]);
        }

        public void resetReviewFragment(int position) {
            Log.i(TAG, "ReviewScreenAdapter resetReviewFragment(): entered.  Page="+position);
            reviewFragment[position] = null;
        }
    }

    /**
     * REVIEW SCREEN FRAGMENT *
     */
    public static class ReviewScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        int position;
        TextView reviewScreenImageNumberTextView;
        CheckBox reviewScreenTopCheckBox;
        CheckBox reviewScreenBottomCheckBox;
        ImageView reviewImg1;
        ImageView reviewImg2;

        ImageView reviewScreenTopCorrectPickImageView;
        ImageView reviewScreenTopWrongPickImageView;
        ImageView reviewScreenBottomWrongPickImageView;
        ImageView reviewScreenBottomCorrectPickImageView;
        //ImageView reviewScreenTopDarkBackgroundImageView;


        final int REVIEW_FADE_IN_DURATION = 800;
        final int REVIEW_FADE_OUT_DURATION = 800;
        final int REVIEW_MAINTAIN_DURATION = 800;
        final int REVIEW_FADE_IN_DELAY = 400;

        final int REVIEW_BACKGROUND_FADE_IN_DURATION = 800;
        final int REVIEW_BACKGROUND_FADE_OUT_DURATION = 400;

        MyImage[] reviewImageObj;
        int reviewImageIndex;
        Handler hReviewStateMachine = null;

        /**
         * Returns a new instance of this fragment for the given section number.
         */

        public static ReviewScreenFragment newInstance(int sectionNumber) {
            ReviewScreenFragment fragment = new ReviewScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ReviewScreenFragment() {
            return;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            reviewImageObj = new MyImage[2];
            reviewImageObj[0] = new MyImage(0, 0, 0, 0, "", "", "");
            reviewImageObj[1] = new MyImage(0, 0, 0, 0, "", "", "");
            hReviewStateMachine = new Handler(reviewStateMachineMessage);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "ReviewScreenFragment onCreateView(): entered.");
            System.gc();

            rootView = inflater.inflate(R.layout.fragment_review, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            position = b.getInt(ARG_SECTION_NUMBER);
            if(currentMode==PB_MODE_BASIC)
                reviewImageIndex = currentImageCount - position - 1;
            else
                reviewImageIndex = survivalImageCount - position - 1;

            reviewScreenTopCheckBox = (CheckBox) rootView
                    .findViewById(R.id.reviewScreenTopCheckBox);
            reviewScreenBottomCheckBox = (CheckBox) rootView
                    .findViewById(R.id.reviewScreenBottomCheckBox);

            reviewScreenTopCorrectPickImageView = (ImageView) rootView
                    .findViewById(R.id.reviewScreenTopCorrectPickImageView);
            reviewScreenTopWrongPickImageView = (ImageView) rootView
                    .findViewById(R.id.reviewScreenTopWrongPickImageView);
            reviewScreenBottomCorrectPickImageView = (ImageView) rootView
                    .findViewById(R.id.reviewScreenBottomCorrectPickImageView);
            reviewScreenBottomWrongPickImageView = (ImageView) rootView
                    .findViewById(R.id.reviewScreenBottomWrongPickImageView);

            reviewImg1 = (ImageView) rootView
                    .findViewById(R.id.reviewScreenTopImageView);
            reviewImg2 = (ImageView) rootView
                    .findViewById(R.id.reviewScreenBottomImageView);


            reviewImg1.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    if (imageClicked)
                        return;
                    else
                        imageClicked = true;

                    playSound(soundIDImageSelect);
                    sendStateMachineMsg(
                            MainActivity.INP_REVIEW_SCREEN_IMAGE_PRESSED,
                            (int) reviewImageObj[0].getSequence(), (int) reviewImageObj[1].getSequence());
                    String strSequence = String.valueOf(reviewImageObj[0].getSequence());
                    sendAnalyticsEvent("Action", "SeeTipFromReview",strSequence);
                }

            });

            reviewImg2.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    if (imageClicked)
                        return;
                    else
                        imageClicked = true;
                    playSound(soundIDImageSelect);
                    sendStateMachineMsg(
                            MainActivity.INP_REVIEW_SCREEN_IMAGE_PRESSED,
                            (int) reviewImageObj[1].getSequence(), (int) reviewImageObj[0].getSequence());
                    String strSequence = String.valueOf(reviewImageObj[1].getSequence());
                    sendAnalyticsEvent("Action", "SeeTipFromReview",strSequence);

                }
            });

            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_UP){
                        displayRightWrongIcons();
                    }
                    return true;
                }
            });

            sendReviewStateMachineMsg(INP_RS_SHOW_REVIEW_IMAGES);


            return rootView;
        }

        private void playReviewImages() {
            imageClicked = false;
            selectReviewImages();

            reviewScreenTopCheckBox.setVisibility(View.INVISIBLE);
            reviewScreenBottomCheckBox.setVisibility(View.INVISIBLE);

            displayReviewImages();
            if(position == 0 && showReviewPageOneCheckmark) {
                displayRightWrongIcons();
                showReviewPageOneCheckmark = false;
            }

        }

        private void selectReviewImages() {
            int groupNumber;

            reviewImageObj[0] = new MyImage(image1Array[reviewImageIndex - 1]);
            reviewImageObj[1] = new MyImage(image2Array[reviewImageIndex - 1]);

            if (!reviewImageObj[0].getReady() || !reviewImageObj[1].getReady()) {
                Log.i(TAG, "selectReviewImages: Images failed to be selected.");
                // Toast.makeText(this,
                // "selectImages: Images failed to be selected ",
                // Toast.LENGTH_SHORT).show();
            }
        }

        private void displayReviewImages() {

            Context ctx = getActivity().getApplicationContext();
            displayImageFile(ctx, reviewImageObj[0].getImageFile(), 1, false);
            displayImageFile(ctx, reviewImageObj[1].getImageFile(), 2, false);

        }

        private void displayImageFile(Context ctx, String fileName, int img,
                                      boolean animate) {

            byte[] bytes = null;
            int size = 0;

            try {

               /* if (importMode == PB_IMPORT_MODE_SD) {
                    File imageFile = new File(fileName);
                    size = (int) imageFile.length();
                    bytes = new byte[size];
                    RandomAccessFile raf = new RandomAccessFile(fileName, "r");
                    raf.seek(0);
                    int cnt = raf.read(bytes);
                } else {*/
                    InputStream is;
                    is = ctx.getAssets().open(fileName);
                    size = is.available();
                    bytes=new byte[size];
                    is.read(bytes);
                //}

                if(imageEncoded)
                    for (int i = 0; i < (bhl*5-10); i++) {
                        bytes[i] ^= xyz;
                    }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0 ,size, options);


            if (img == 1) {

                    setLayoutSizeParams(bytes,size, reviewImg1, reviewImageObj[0]);
                    reviewImg1.setImageBitmap(bMap);

                } else {
                    setLayoutSizeParams(bytes,size, reviewImg2, reviewImageObj[1]);
                    reviewImg2.setImageBitmap(bMap);
                }
                reviewImg1.setVisibility(View.VISIBLE);
                reviewImg2.setVisibility(View.VISIBLE);

        }


        void displayRightWrongIcons(){
            reviewScreenTopCorrectPickImageView.clearAnimation();
            reviewScreenTopWrongPickImageView.clearAnimation();
            reviewScreenBottomCorrectPickImageView.clearAnimation();
            reviewScreenBottomWrongPickImageView.clearAnimation();

            Animation fadeIn = new AlphaAnimation((float)(0), (float)(1.0));
            fadeIn.setInterpolator(new AccelerateInterpolator());
            fadeIn.setDuration(REVIEW_FADE_IN_DURATION);
            fadeIn.setStartOffset(REVIEW_FADE_IN_DELAY);

            Animation fadeOut = new AlphaAnimation((float)1.0, (float) 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(REVIEW_FADE_IN_DELAY+REVIEW_FADE_IN_DURATION+REVIEW_MAINTAIN_DURATION);
            fadeOut.setDuration(REVIEW_FADE_OUT_DURATION);

            AnimationSet animation = new AnimationSet(false); // change to false
            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);
            animation.setRepeatCount(1);

            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    reviewScreenTopCorrectPickImageView.setVisibility(View.INVISIBLE);
                    reviewScreenTopWrongPickImageView.setVisibility(View.INVISIBLE);
                    reviewScreenBottomCorrectPickImageView.setVisibility(View.INVISIBLE);
                    reviewScreenBottomWrongPickImageView.setVisibility(View.INVISIBLE);

                    if (reviewImageObj[0].getSelected()) {
                        reviewImg1.setBackgroundResource(R.drawable.background_select);
                    }
                    else
                        reviewImg2.setBackgroundResource(R.drawable.background_select);

                    }
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }
            });


            ImageView iv;
            if (reviewImageObj[0].getSelected()) {
                if (reviewImageObj[0].getWinner())
                    iv = reviewScreenTopCorrectPickImageView;
                else
                    iv = reviewScreenTopWrongPickImageView;
                iv.getLayoutParams().height = reviewImageObj[0].getHeight();
                iv.getLayoutParams().width = reviewImageObj[0].getWidth();
                reviewImg1.setBackgroundResource(R.drawable.background_pressed);

            }else {
                if (reviewImageObj[1].getWinner())
                        iv = reviewScreenBottomCorrectPickImageView;
                    else
                        iv = reviewScreenBottomWrongPickImageView;
                iv.getLayoutParams().height = reviewImageObj[1].getHeight();
                iv.getLayoutParams().width = reviewImageObj[1].getWidth();
                reviewImg2.setBackgroundResource(R.drawable.background_pressed);

            }
            iv.setAnimation(animation);
            iv.setVisibility(View.VISIBLE);

        }


        boolean sendReviewStateMachineMsg(int msg) {
            Message m = hReviewStateMachine.obtainMessage();
            m.what = msg;
            hReviewStateMachine.sendMessage(m);
            return (true);
        }

        private final Handler.Callback reviewStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;

                switch (msg.what) {
                    case INP_RS_SHOW_REVIEW_IMAGES:
                        Log.i(TAG,
                                "handleMessage: INP_RS_SHOW_REVIEW_IMAGES:  Done.");
                        playReviewImages();
                        break;
                    case INP_RS_SHOW_RIGHT_WRONG_ICONS:
                        Log.i(TAG,
                                "handleMessage: INP_RS_SHOW_REVIEW_IMAGES:  Done.");
                            displayRightWrongIcons();
                        break;

                    default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;

                }
                // msg.recycle();
                return true;
            }
        };

    }


  


    private static displayImageReturn decodeDisplayImage(Context ctx, String fileName){
        byte[] bytes = null;
        int size = 0;

        try {

           /* if (importMode == PB_IMPORT_MODE_SD) {
                File imageFile = new File(fileName);
                size = (int) imageFile.length();
                bytes = new byte[size];
                RandomAccessFile raf = new RandomAccessFile(fileName, "r");
                raf.seek(0);
                int cnt = raf.read(bytes);
            } else {*/
                InputStream is;
                is = ctx.getAssets().open(fileName);
                size = is.available();
                bytes=new byte[size];
                is.read(bytes);
            //}

            if(imageEncoded)
                for (int i = 0; i < (bhl*5-10); i++) {
                    bytes[i] ^= xyz;
                }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Set image resolution rediuction
        //options.inSampleSize = 2;
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0 ,size, options);

        return new displayImageReturn(size, bytes, bMap);

    }




    /**
     * NEW ROUND SCREEN FRAGMENT
     */
    public static class NewRoundScreenFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        static TextView newRoundScreenGameLevelTextView;
        static TextView newRoundScreenHeadlineTextView;
        static TextView newRoundScreenNumberTextView;
        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static NewRoundScreenFragment newInstance(int sectionNumber) {
            NewRoundScreenFragment fragment = new NewRoundScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public NewRoundScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_new_round, container,
                    false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);
            long mode = (long) b.getInt(ARG_SECTION_NUMBER);


            newRoundScreenHeadlineTextView = (TextView) rootView
                    .findViewById(R.id.newRoundScreenHeadlineTextView);
            newRoundScreenNumberTextView = (TextView) rootView
                    .findViewById(R.id.newRoundScreenNumberTextView);
            newRoundScreenNumberTextView.setText(String
                    .valueOf(currentPlayRound));
            newRoundScreenGameLevelTextView = (TextView) rootView
                    .findViewById(R.id.newRoundScreenGameLevelTextView);

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            newRoundScreenNumberTextView.setTypeface(font);
            newRoundScreenGameLevelTextView.setTypeface(font);
            newRoundScreenHeadlineTextView.setTypeface(font);

            if(mode == PB_MODE_BASIC) {
                String s= "Classic Level " + String.valueOf(currentClassicLevel);
                int len = String.valueOf(currentClassicLevel).length();
                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(1.4f), 14,len+14, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#b0d506")), 14, 14+len, 0);// set color
                newRoundScreenGameLevelTextView.setText(ss1);

            }
            else if (mode == PB_MODE_SURVIVAL)
                newRoundScreenGameLevelTextView.setText(
                        " Survival");

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(activityStopped){
                        //getActivity().finish();
                        activityStopped=false;
                        newRoundScreenPaused=true;
                        Log.e(TAG, "NewRoundScreenFragment().Runnable: activity stopped; nothing done. ");
                        return;
                    }
                    sendStateMachineMsg(INP_NEW_ROUND_SCREEN_DONE);

                }
            };
            handler.postDelayed(runnable, PB_NEW_ROUND_SCREEN_DELAY);
            playSound(soundIDNewRound);
            if(mode == PB_MODE_BASIC) {
                sendAnalyticsScreen("ClassicNewRound");
            } else {
                sendAnalyticsScreen("SurvivalNewRound");
            }

            return rootView;
        }
    }
    /*****************************************/
    /** CLASSIC END OF GAME SCREEN FRAGMENT **/
    /**
     * *************************************
     */
    public static class ClassicEndOfGameScreenFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        static Button classicEndOfGameScreenPlayAgainButton;
        static Button classicEndOfGameScreenQuitGameButton;
        static Button classicEndOfGameScreenReviewButton;
        static TextView classicEndOfGameScreenActualPointsTextView;
        static TextView classicEndOfGameScreenCongratsTextView;
        static TextView classicEndOfGameScreenTotalPointsTextView;
        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static ClassicEndOfGameScreenFragment newInstance(
                int sectionNumber) {
            ClassicEndOfGameScreenFragment fragment = new ClassicEndOfGameScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public ClassicEndOfGameScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_classic_end_of_game,
                    container, false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long mode = (long) b.getInt(ARG_SECTION_NUMBER);

            classicEndOfGameScreenTotalPointsTextView = (TextView) rootView
                            .findViewById(R.id.classicEndOfGameScreenTotalPointsTextView);
            classicEndOfGameScreenActualPointsTextView = (TextView) rootView
                    .findViewById(R.id.classicEndOfGameScreenActualPointsTextView);
            classicEndOfGameScreenActualPointsTextView.setText(String
                    .valueOf(currentScore));

            classicEndOfGameScreenCongratsTextView = (TextView) rootView
                    .findViewById(R.id.classicEndOfGameScreenCongratsTextView);

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            classicEndOfGameScreenTotalPointsTextView.setTypeface(font);
            classicEndOfGameScreenActualPointsTextView.setTypeface(font);
            classicEndOfGameScreenCongratsTextView.setTypeface(font);

            classicEndOfGameScreenReviewButton = (Button) rootView
                    .findViewById(R.id.classicEndOfGameScreenReviewButton);
            classicEndOfGameScreenReviewButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_EOG_SCREEN_REVIEW_BUTTON_PRESSED);
                        }
                    });

            classicEndOfGameScreenPlayAgainButton = (Button) rootView
                    .findViewById(R.id.classicEndOfGameScreenPlayAgainButton);

            classicEndOfGameScreenPlayAgainButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED);
                        }
                    });

            classicEndOfGameScreenQuitGameButton = (Button) rootView
                    .findViewById(R.id.classicEndOfGameScreenQuitGameButton);
            classicEndOfGameScreenQuitGameButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED);
                        }
                    });

            if(mode == CLASSIC_EOG_UP_LEVEL) {
                classicEndOfGameScreenCongratsTextView.setText("Congratulations!!  \nYou reached Level " +
                        String.valueOf(currentClassicLevel) + " !!");
                playSound(soundIDGameOverPositive);
            } else if(mode == CLASSIC_EOG_STAY_LEVEL) {
                classicEndOfGameScreenPlayAgainButton.setBackgroundResource(R.drawable.play_again_button);
                classicEndOfGameScreenCongratsTextView.setText("Sorry, you need " +
                        String.valueOf(classicMinScorePerLevel[currentClassicLevel]) + " Points \n to reach next level");
                playSound(soundIDGameOver);
            }
            sendAnalyticsScreen("ClassicEndOfGame");
            return rootView;
        }
    }

    /*****************************************/
    /** CLASSIC START GAME SCREEN FRAGMENT **/
    /**
     * *************************************
     */
    public static class ClassicStartGameScreenFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        static Button classicStartGameScreenStartButton;
        static TextView classicStartGameScreenLevelNumberTextView;
        static TextView classicStartGameScreenDescriptionTextView;
        static TextView classicStartGameScreenLevelHeadlineTextView;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static ClassicStartGameScreenFragment newInstance(
                int sectionNumber) {
            ClassicStartGameScreenFragment fragment = new ClassicStartGameScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public ClassicStartGameScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_classic_start_game,
                    container, false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long mode = (long) b.getInt(ARG_SECTION_NUMBER);


            classicStartGameScreenLevelHeadlineTextView = (TextView) rootView
                    .findViewById(R.id.classicStartGameScreenLevelHeadlineTextView);
            classicStartGameScreenLevelNumberTextView = (TextView) rootView
                    .findViewById(R.id.classicStartGameScreenLevelNumberTextView);

            classicStartGameScreenLevelNumberTextView.setText(String
                    .valueOf(currentClassicLevel));

            classicStartGameScreenDescriptionTextView = (TextView) rootView
                    .findViewById(R.id.classicStartGameScreenDescriptionTextView);

            classicStartGameScreenDescriptionTextView.setText("" + String.valueOf(ROUNDS_PER_LEVEL * currentClassicLevel)
                    + " rounds this level\n"
                    + "" + String.valueOf(classicMinScorePerLevel[currentClassicLevel]) + " points to next level");
            classicStartGameScreenDescriptionTextView.setSelected(true);

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            classicStartGameScreenLevelHeadlineTextView.setTypeface(font);
            classicStartGameScreenLevelNumberTextView.setTypeface(font);
            classicStartGameScreenDescriptionTextView.setTypeface(font);

            classicStartGameScreenStartButton = (Button) rootView
                    .findViewById(R.id.classicStartGameScreenStartButton);
            classicStartGameScreenStartButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_SOG_SCREEN_START_BUTTON_PRESSED);
                        }
                    });

            playSound(soundIDGameStart);
            sendAnalyticsScreen("ClassicStartGame");
            if(hintClassicStartPlayPreference)
                sendStateMachineMsg(MainActivity.INP_CLASSIC_START_GAME_SCREEN_INFO_HINT);
            else if(hintClassicTipsDuringPlayPreference && currentClassicLevel==2)
                sendStateMachineMsg(MainActivity.INP_CLASSIC_TIPS_DURING_PLAY_HINT);
            return rootView;
        }
    }


    private void survivalModeInit() {
        survivalImageCount = 1;
        currentPlayRound = PB_PLAY_LEVEL_1;
        currentScore = 0;
        currentWinStreak = 0;
        totalQuestions = 0;
        currentMode = PB_MODE_SURVIVAL;
        survivalLivesRemain = PB_LIVES_REMAIN_SURVIVAL_MODE;
        survivalTimeRemain=0;
    }

    static boolean survivalFragmentEnded=false;

    /**
     * SURVIVAL PLAY SCREEN FRAGMENT
     */
    public static class SurvivalPlayScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static Handler hSurvivalStateMachine = null;
        static ImageView survivalImg1;
        static ImageView survivalImg2;
        MyImage[] survivalImageObj;
        TextView survivalPlayScreenRoundNumberTextView;
        TextView survivalPlayScreenPointsNumberTextView;
        TextView survivalPlayScreenTimerNumberTextView;
        TextView survivalPlayScreenLivesNumberTextView;
        TextView survivalPlayScreenRoundTextView;
        TextView survivalPlayScreenPointsTextView;
        TextView survivalPlayScreenTimerTextView;
        TextView survivalPlayScreenLivesTextView;
        ProgressBar survivalProgressBar;
        boolean stopProgressbar = false;

        static ImageView survivalPlayScreenTopCorrectPickImageView;
        static ImageView survivalPlayScreenTopWrongPickImageView;
        static ImageView survivalPlayScreenBottomWrongPickImageView;
        static ImageView survivalPlayScreenBottomCorrectPickImageView;
        static ImageView survivalPlayScreenTopDarkBackgroundImageView;
        static TextView survivalPlayScreenBonusPointsTextView;
        static TextView survivalPlayScreenBonusWordsTextView;
        static TextView survivalPlayScreenTimeoutWordsTextView;

        private static final int INP_CS_SHOW_SURVIVAL_IMAGES = 2000;
        private static final int INP_CS_IMAGE_SELECTED = INP_CS_SHOW_SURVIVAL_IMAGES + 1;
        private static final int INP_CS_SHOW_BONUS_POINTS = INP_CS_SHOW_SURVIVAL_IMAGES + 2;
        private static final int INP_CS_CHECKMARK_ANIMATION_DONE = INP_CS_SHOW_SURVIVAL_IMAGES + 3;
        private static final int INP_CS_IMAGE0_CLICKED = INP_CS_SHOW_SURVIVAL_IMAGES + 4;
        private static final int INP_CS_IMAGE1_CLICKED = INP_CS_SHOW_SURVIVAL_IMAGES + 5;
        private static final int INP_CS_TIMER_EXPIRED = INP_CS_SHOW_SURVIVAL_IMAGES + 6;

        private static final int CS_READY = 100;
        private static final int CS_IMAGES_DISPLAYED = CS_READY+1;
        private static final int CS_IMAGE_SELCTED = CS_READY+2;
        private static final int CS_CHECKMARK_ANIMATION_DONE = CS_READY+3;
        private static final int CS_TIPS_MODE = CS_READY+4;
        private static final int CS_TIMER_EXPIRED = CS_READY+5;
        private static int survivalState = CS_READY;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            survivalImageObj = new MyImage[2];
            survivalImageObj[0] = new MyImage(0, 0, 0, 0, "", "", "");
            survivalImageObj[1] = new MyImage(0, 0, 0, 0, "", "", "");
            hSurvivalStateMachine = new Handler(survivalStateMachineMessage);
            survivalFragmentEnded=false;
            survivalState = CS_READY;
            System.gc();
        }

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static SurvivalPlayScreenFragment newInstance(int sectionNumber) {
            SurvivalPlayScreenFragment fragment = new SurvivalPlayScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SurvivalPlayScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_survival_play,
                    container, false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long tmp  = (long) b.getInt(ARG_SECTION_NUMBER);

            survivalPlayScreenRoundTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenRoundTextView);
            survivalPlayScreenPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenPointsTextView);
            survivalPlayScreenTimerTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenTimerTextView);
            survivalPlayScreenLivesTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenLivesTextView);

            survivalPlayScreenRoundNumberTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenRoundNumberTextView);
            survivalPlayScreenPointsNumberTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenPointsNumberTextView);
            survivalPlayScreenRoundNumberTextView.setText(String
                    .valueOf(currentPlayRound));
            survivalPlayScreenPointsNumberTextView.setText(String
                    .valueOf(currentScore));

            survivalPlayScreenTimerNumberTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenTimerNumberTextView);

            survivalPlayScreenLivesNumberTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenLivesNumberTextView);
            survivalPlayScreenLivesNumberTextView.setText(String
                    .valueOf(survivalLivesRemain));

            survivalPlayScreenTopCorrectPickImageView = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenTopCorrectPickImageView);
            survivalPlayScreenTopWrongPickImageView = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenTopWrongPickImageView);
            survivalPlayScreenBottomCorrectPickImageView = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenBottomCorrectPickImageView);
            survivalPlayScreenBottomWrongPickImageView = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenBottomWrongPickImageView);

            survivalPlayScreenTopDarkBackgroundImageView = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenTopDarkBackgroundImageView);

            survivalPlayScreenBonusPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenBonusPointsTextView);
            survivalPlayScreenBonusWordsTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenBonusWordsTextView);
            survivalPlayScreenTimeoutWordsTextView = (TextView) rootView
                    .findViewById(R.id.survivalPlayScreenTimeoutWordsTextView);

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            survivalPlayScreenRoundTextView.setTypeface(font);
            survivalPlayScreenPointsTextView.setTypeface(font);
            survivalPlayScreenTimerTextView.setTypeface(font);
            survivalPlayScreenLivesTextView.setTypeface(font);
            survivalPlayScreenRoundNumberTextView.setTypeface(font);
            survivalPlayScreenPointsNumberTextView.setTypeface(font);
            survivalPlayScreenTimerNumberTextView.setTypeface(font);
            survivalPlayScreenLivesNumberTextView.setTypeface(font);

            survivalProgressBar= (ProgressBar)rootView.findViewById(R.id.survivalProgressBar);

            survivalImg1 = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenImageViewTop);
            survivalImg2 = (ImageView) rootView
                    .findViewById(R.id.survivalPlayScreenImageViewBottom);

            survivalImg1.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    if (imageClicked)
                        return;
                    else
                        imageClicked = true;
                    survivalImg1.setBackgroundResource(R.drawable.background_pressed);
                    sendSurvivalStateMachineMsg(INP_CS_IMAGE0_CLICKED);
                }

            });

            survivalImg2.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    if (imageClicked)
                        return;
                    else
                        imageClicked = true;
                    survivalImg2.setBackgroundResource(R.drawable.background_pressed);
                    sendSurvivalStateMachineMsg(INP_CS_IMAGE1_CLICKED);
                }
            });

            int survivalTimerValue = getSurvivalTimerValue();

            sendSurvivalStateMachineMsg(INP_CS_SHOW_SURVIVAL_IMAGES);
            if(survivalTimeRemain>0 && survivalTimeRemain<survivalTimerValue)
                progressStatus=(survivalTimerValue-survivalTimeRemain)*(300/survivalTimerValue);
            else
                progressStatus = 0;
            stopProgressbar = false;
            doSomeWork(survivalTimerValue);

            sendAnalyticsScreen("SurvivalPlay");
            return rootView;
        }

        private int getSurvivalTimerValue(){
            int timerValue=SURVIVAL_COUNTDOWN_TIMER_VALUE_SEC-((currentPlayRound-1)/SURVIVAL_COUNTDOWN_FACTOR);
            if(timerValue<SURVIVAL_COUNTDOWN_MINIMUM_VALUE)
                timerValue=SURVIVAL_COUNTDOWN_MINIMUM_VALUE;
            return(timerValue);
        }


        private boolean didWeWin(){
            return ((survivalImageObj[0].getWinner()
                    && survivalImageObj[0].getSelected()
                    || survivalImageObj[1].getWinner()
                    && survivalImageObj[1].getSelected()));

        }


        void displayLifeLost(){
            survivalPlayScreenLivesNumberTextView.bringToFront();
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(survivalPlayScreenLivesNumberTextView, "scaleX", 2.0f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(survivalPlayScreenLivesNumberTextView, "scaleY", 2.0f);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(survivalPlayScreenLivesNumberTextView, "scaleX", 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(survivalPlayScreenLivesNumberTextView, "scaleY", 1.0f);
            scaleUpX.setStartDelay(0);
            scaleUpX.setDuration(700);
            scaleUpY.setDuration(700);
            scaleDownX.setStartDelay(400);
            scaleDownX.setDuration(700);
            scaleDownY.setDuration(700);

            AnimatorSet animator = new AnimatorSet();
            Animator.AnimatorListener animatorListener
                    = new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }
                public void onAnimationRepeat(Animator animation) {
                }
                public void onAnimationEnd(Animator animation) {
                    sendSurvivalStateMachineMsg(INP_CS_CHECKMARK_ANIMATION_DONE);
                }

                public void onAnimationCancel(Animator animation) {
                }
            };
            animator.addListener(animatorListener);
            animator.playTogether(scaleUpX, scaleUpY);
            animator.playTogether(scaleDownX, scaleDownY);
            animator.playSequentially(scaleUpX,scaleDownX);
            animator.start();
        }

        void processTimeout(){
            currentWinStreak = 0;
            survivalLivesRemain--;
            survivalTimeRemain=0;
            survivalPlayScreenLivesNumberTextView.setText(String
                    .valueOf(survivalLivesRemain));
            Vibrator vib = (Vibrator) getActivity().getSystemService(
                    Context.VIBRATOR_SERVICE);
            if (vibrateOnOffPreference)
                vib.vibrate(PB_VIBRATE_DURATION);

            if(survivalImageObj[0].getWinner())
                survivalImageObj[1].setSelected(true);
            else
                survivalImageObj[0].setSelected(true);
            displayTimeoutMsg();
            playSound(soundIDTimerExpired);

        }

        void displayTimeoutMsg(){

            if(survivalLivesRemain <= 1)
                survivalPlayScreenTimeoutWordsTextView.setText(" Timer Expired  \n " + String
                        .valueOf(survivalLivesRemain) + " Life Left ");
            else
                survivalPlayScreenTimeoutWordsTextView.setText(" Timer Expired  \n " + String
                        .valueOf(survivalLivesRemain) + " Lives Left ");

            ObjectAnimator wordsAlpha1 = ObjectAnimator.ofFloat(survivalPlayScreenTimeoutWordsTextView, "alpha",0f, 1.0f);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(survivalPlayScreenTimeoutWordsTextView, "scaleX", 0.15f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(survivalPlayScreenTimeoutWordsTextView, "scaleY", 0.15f);
            wordsAlpha1.setDuration(800);
            scaleDownX.setStartDelay(SURVIVAL_TIMEOUT_MSG_DURATION);
            scaleDownX.setDuration(500);
            scaleDownY.setDuration(500);
            AnimatorSet animator = new AnimatorSet();
            Animator.AnimatorListener animatorListener
                    = new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }
                public void onAnimationRepeat(Animator animation) {
                }
                public void onAnimationEnd(Animator animation) {
                    if(survivalFragmentEnded)
                           return;
                    survivalPlayScreenTimeoutWordsTextView.setVisibility(View.INVISIBLE);
                    sendSurvivalStateMachineMsg(INP_CS_TIMER_EXPIRED);

                }

                public void onAnimationCancel(Animator animation) {
                }
            };
            animator.addListener(animatorListener);
            animator.playTogether(scaleDownX, scaleDownY);
            animator.playSequentially(wordsAlpha1, scaleDownX);
            animator.start();
            survivalPlayScreenTimeoutWordsTextView.setVisibility(View.VISIBLE);
        }

        private void displayBothCheckAndX() {

            Animation fadeIn = new AlphaAnimation((float)0.0, (float)1.0);
            fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
            fadeIn.setDuration(SURVIVAL_PLAY_CHECKMARK_FADE_IN_DURATION);

            AnimationSet animation = new AnimationSet(false); // change to false
            animation.addAnimation(fadeIn);
            animation.setRepeatCount(1);

            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {

                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                        if(survivalState != CS_TIMER_EXPIRED) {
                                return;
                            }
                            if(survivalLivesRemain==0){
                                sendStateMachineMsg(INP_SURVIVAL_GAME_OVER);
                                return;
                            }
                            sendStateMachineMsg(INP_SURVIVAL_IMAGE_LOST);
                         }
                    };
                    handler.postDelayed(runnable, postSelectDelay);

                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            imageClicked=true;
                            Log.i(TAG, "runnable: imageClicked=true");
                        }
                    };
                    handler2.postDelayed(runnable2, postSelectDelay-300);

                }
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }
            });

            ImageView iv0;
            ImageView iv1;

            if (survivalImageObj[0].getWinner())
                iv0 = survivalPlayScreenTopCorrectPickImageView;
            else
                iv0 = survivalPlayScreenTopWrongPickImageView;
            iv0.getLayoutParams().height = survivalImageObj[0].getHeight();
            iv0.getLayoutParams().width = survivalImageObj[0].getWidth();

            if (survivalImageObj[1].getWinner())
                iv1 = survivalPlayScreenBottomCorrectPickImageView;
            else
                iv1 = survivalPlayScreenBottomWrongPickImageView;
            iv1.getLayoutParams().height = survivalImageObj[1].getHeight();
            iv1.getLayoutParams().width = survivalImageObj[1].getWidth();

            iv0.setAnimation(animation);
            iv0.setVisibility(View.VISIBLE);
            iv1.setAnimation(animation);
            iv1.setVisibility(View.VISIBLE);

            image1Array[survivalImageCount - 1] = new MyImage(survivalImageObj[0]);
            image2Array[survivalImageCount - 1] = new MyImage(survivalImageObj[1]);
        }

        private void playSurvivalImages() {
            imageClicked = false;
            selectSurvivalImages();
            displaySurvivalImages();
            int timerValue=getSurvivalTimerValue();
            if(survivalTimeRemain>0)
                startSurvivalCountDownTimer(survivalTimeRemain);
            else
                startSurvivalCountDownTimer(timerValue);
        }

        private void selectSurvivalImages() {
            int groupNumber;

            survivalImageObj[0].reset();
            survivalImageObj[1].reset();
            /*groupNumber = selectGroupForBasicMode();
            selectImagesCompare(groupNumber);
            if (!survivalImageObj[0].getReady() || !survivalImageObj[1].getReady()) {
                Log.i(TAG, "selectSurvivalImages: Images failed to be selected.");
            }
*/
            switch (currentPlayRound) {
/*                case PB_PLAY_LEVEL_1:
                    groupNumber = selectGroupForBasicMode();
                    selectImagesCompare(groupNumber);
                    break;
                case PB_PLAY_LEVEL_2:
                    selectImagesByModeRange(PB_MODE_TRAVEL, 1, 3, 7, 10);
                    break;
                case PB_PLAY_LEVEL_3:
                    selectImagesByModeRange(PB_MODE_NATURE, 1, 3, 7, 10);
                    break;
                case PB_PLAY_LEVEL_4:
                    selectImagesByModeRange(PB_MODE_TRAVEL, 1, 4, 6, 10);
                    break;
                case PB_PLAY_LEVEL_5:
                    selectImagesByModeRange(PB_MODE_NATURE, 1, 4, 6, 10);
                    break;
                case PB_PLAY_LEVEL_6:
                    countDownTimerValue = PB_SURVIVAL_COUNTDOWN_VALUE - 1;
                    selectImagesByModeRange(PB_MODE_NATURE, 1, 4, 7, 10);
                    break;
                case PB_PLAY_LEVEL_7:
                    countDownTimerValue = PB_SURVIVAL_COUNTDOWN_VALUE - 2;
                    selectImagesByModeRange(PB_MODE_TRAVEL, 1, 4, 7, 10);
                    break;
                case PB_PLAY_LEVEL_8:
                    countDownTimerValue = PB_SURVIVAL_COUNTDOWN_VALUE - 3;
                    selectImagesByRange(1, 4, 6, 9);
                    break;
                case PB_PLAY_LEVEL_9:
                    countDownTimerValue = PB_SURVIVAL_COUNTDOWN_VALUE - 4;
                    selectImagesByRange(2, 4, 7, 9);
                    break;
                case PB_PLAY_LEVEL_10:
                    selectImagesByRange(1, 4, 8, 9);
                    break;
                case PB_PLAY_LEVEL_10 + 1:
                    selectImagesByRange(1, 4, 7, 10);
                    break;
                case PB_PLAY_LEVEL_10 + 2:
                    selectImagesByRange(2, 4, 6, 9);
                    break;
                case PB_PLAY_LEVEL_10 + 3:
                    selectImagesByRange(1, 3, 6, 8);
                    break;
                case PB_PLAY_LEVEL_10 + 4:
                    selectImagesByRange(3, 4, 7, 10);
                    break;
                case PB_PLAY_LEVEL_10 + 5:
                    selectImagesByRange(2, 4, 7, 10);
                    break;
                case PB_PLAY_LEVEL_10 + 6:
                    selectImagesByRange(1, 4, 6, 9);
                    break;
                case PB_PLAY_LEVEL_10 + 7:
                    selectImagesByRange(3, 4, 7, 8);
                    break;
                case PB_PLAY_LEVEL_10 + 8:
                    selectImagesByRange(3, 4, 6, 7);
                    break;
                case PB_PLAY_LEVEL_10 + 9:
                    selectImagesByRange(3, 4, 6, 7);
                    break;*/
                default:
                    selectImagesByRange(1, 4, 6, 10);
                    break;
            }

        }

        private void displaySurvivalImages() {

            if (!survivalImageObj[0].getReady() || !survivalImageObj[1].getReady()) {
                Log.e(TAG, "displayImages: Images failed to be selected.");
                return;
            }
            Context ctx = getActivity().getApplicationContext();
            displayImageFile(ctx, survivalImageObj[0].getImageFile(), 1, true);
            displayImageFile(ctx, survivalImageObj[1].getImageFile(), 2, true);

        }

        private int selectGroupForBasicMode() {

            if (survivalImageCount == 1 || survivalImageCount > groupList.size()) {
                groupList = new ArrayList<Integer>();
                Cursor cursor = pBudDBAdapter.getAllItemsCursorWithGroupNumber();

                if (cursor.moveToFirst()) {
                    do {
                        long groupNumber = cursor.getLong(cursor
                                .getColumnIndex(PBudDBAdapter.KEY_GROUP));
                        groupList.add((int) groupNumber);
                    } while (cursor.moveToNext());
                } else {
                    Log.i(TAG,
                            "selectGroupForBasicMode(): listCursor.moveToFirst() failed for high rating.");
                }

                if (!groupList.isEmpty()) {
                    Collections.shuffle(groupList);

                }
            }

            return (groupList.get(survivalImageCount - 1));

        }

        private void selectImagesByModeRange(long _mode, int lowStart, int lowEnd,
                                                    int highStart, int highEnd) {
            if (survivalImageCount == 1) {
                l2HighRatingList = new ArrayList<Integer>();
                Cursor l2Cursor = pBudDBAdapter
                        .getAllItemsCursorByModeBetweenRatingsSortSequenceAscend(
                                _mode, highStart, highEnd);

                if (l2Cursor.moveToFirst()) {
                    do {
                        long sequence = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));

                        l2HighRatingList.add((int) sequence);
                    } while (l2Cursor.moveToNext());
                } else {
                    Log.i(TAG,
                            "selectImagesL2(): listCursor.moveToFirst() failed for high rating.");
                }

                Collections.shuffle(l2HighRatingList);
                l2Highshuffled = new int[l2HighRatingList.size()];
                for (int i = 0; i < l2HighRatingList.size(); i++) {
                    l2Highshuffled[i] = l2HighRatingList.get(i);
                }
            }

            Random rand = new Random();
            int imageOrder = rand.nextInt((int) 2);

            Cursor l2Cursor = pBudDBAdapter
                    .getAllItemsCursorBySequence(l2Highshuffled[survivalImageCount - 1]);
            l2Cursor.moveToFirst();
            long rating = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_RATING));
            long sequence = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
            long mode = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_MODE));
            long group = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP));
            long groupSeq = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
            String imageFile = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
            String comments = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));

            survivalImageObj[imageOrder].setMyImage(sequence, mode, group, groupSeq,
                    rating, imageFile, "", comments);
            survivalImageObj[imageOrder].setReady(true);
            survivalImageObj[imageOrder].setWinner(true);

            // LOW RATING

            if (survivalImageCount == 1) {
                l2LowRatingList = new ArrayList<Integer>();
                l2Cursor = pBudDBAdapter
                        .getAllItemsCursorByModeBetweenRatingsSortSequenceAscend(
                                _mode, lowStart, lowEnd);

                if (l2Cursor.moveToFirst()) {
                    do {
                        sequence = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));

                        l2LowRatingList.add((int) sequence);
                    } while (l2Cursor.moveToNext());
                } else {
                    Log.i(TAG,
                            "selectImagesL2(): listCursor.moveToFirst() failed for low rating.");
                }

                Collections.shuffle(l2LowRatingList);
                l2Lowshuffled = new int[l2LowRatingList.size()];
                for (int i = 0; i < l2LowRatingList.size(); i++) {
                    l2Lowshuffled[i] = l2LowRatingList.get(i);
                }
            }

            l2Cursor = pBudDBAdapter
                    .getAllItemsCursorBySequence(l2Lowshuffled[survivalImageCount - 1]);
            l2Cursor.moveToFirst();
            rating = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_RATING));
            sequence = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
            mode = l2Cursor
                    .getLong(l2Cursor.getColumnIndex(PBudDBAdapter.KEY_MODE));
            group = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP));
            groupSeq = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
            imageFile = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
            comments = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));

            survivalImageObj[imageOrder ^ 0x1].setMyImage(sequence, group, groupSeq,
                    rating, imageFile, "", comments);
            survivalImageObj[imageOrder ^ 0x1].setReady(true);
            survivalImageObj[imageOrder ^ 0x1].setWinner(false);

        }



        private void selectImagesByRange(int lowStart, int lowEnd, int highStart,
                                                int highEnd) {
            survivalHighImageCount++;
            survivalLowImageCount++;
            if (survivalHighImageCount == 1 || survivalHighImageCount == l2Highshuffled.length) {
                l2HighRatingList = new ArrayList<Integer>();
                Cursor l2Cursor = pBudDBAdapter
                        .getAllItemsCursorBetweenRatingsSortSequenceAscend(
                                highStart, highEnd);

                if (l2Cursor.moveToFirst()) {
                    do {
                        long flag = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_FLAG));
                        if(flag != FLAG_SURVIVAL_YES)
                            continue;
                        long sequence = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));

                        l2HighRatingList.add((int) sequence);
                    } while (l2Cursor.moveToNext());
                } else {
                    Log.i(TAG,
                            "selectImagesL2(): listCursor.moveToFirst() failed for high rating.");
                }

                Collections.shuffle(l2HighRatingList);
                l2Highshuffled = new int[l2HighRatingList.size()];
                for (int i = 0; i < l2HighRatingList.size(); i++) {
                    l2Highshuffled[i] = l2HighRatingList.get(i);
                }
                survivalHighImageCount=1;
            }

            Random rand = new Random();
            int imageOrder = rand.nextInt((int) 2);

            Cursor l2Cursor = pBudDBAdapter
                    .getAllItemsCursorBySequence(l2Highshuffled[survivalHighImageCount - 1]);
            l2Cursor.moveToFirst();
            long rating = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_RATING));
            long sequence = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
            long group = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP));
            long groupSeq = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
            String imageFile = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
            String comments = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));

            survivalImageObj[imageOrder].setMyImage(sequence, group, groupSeq, rating,
                    imageFile, "", comments);
            survivalImageObj[imageOrder].setReady(true);
            survivalImageObj[imageOrder].setWinner(true);

            // LOW RATING

            if (survivalLowImageCount == 1 || survivalLowImageCount == l2Lowshuffled.length) {
                l2LowRatingList = new ArrayList<Integer>();
                l2Cursor = pBudDBAdapter
                        .getAllItemsCursorBetweenRatingsSortSequenceAscend(
                                lowStart, lowEnd);

                if (l2Cursor.moveToFirst()) {
                    do {
                        long flag = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_FLAG));
                        if(flag != FLAG_SURVIVAL_YES)
                            continue;
                        sequence = l2Cursor.getLong(l2Cursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));

                        l2LowRatingList.add((int) sequence);
                    } while (l2Cursor.moveToNext());
                } else {
                    Log.i(TAG,
                            "selectImagesL2(): listCursor.moveToFirst() failed for low rating.");
                }

                Collections.shuffle(l2LowRatingList);
                l2Lowshuffled = new int[l2LowRatingList.size()];
                for (int i = 0; i < l2LowRatingList.size(); i++) {
                    l2Lowshuffled[i] = l2LowRatingList.get(i);
                }
                survivalLowImageCount=1;
            }

            l2Cursor = pBudDBAdapter
                    .getAllItemsCursorBySequence(l2Lowshuffled[survivalLowImageCount - 1]);
            l2Cursor.moveToFirst();
            rating = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_RATING));
            sequence = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
            group = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP));
            groupSeq = l2Cursor.getLong(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
            imageFile = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
            comments = l2Cursor.getString(l2Cursor
                    .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));

            survivalImageObj[imageOrder ^ 0x1].setMyImage(sequence, group, groupSeq,
                    rating, imageFile, "", comments);
            survivalImageObj[imageOrder ^ 0x1].setReady(true);
            survivalImageObj[imageOrder ^ 0x1].setWinner(false);

        }


        private void displayImageFile(Context ctx, String fileName, int img,
                                      boolean animate) {

            displayImageReturn ret = decodeDisplayImage(ctx,fileName);
            int size = ret.size;
            byte[] bytes = ret.bytes;
            Bitmap bMap = ret.bMap;


            if (img == 1) {

                    setLayoutSizeParams(bytes,size, survivalImg1,survivalImageObj[0] );
                    if (animate) {
                        survivalImg1.clearAnimation();
                        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(IMAGE_FADE_IN_DURATION);
                        animation.setFillAfter(true);
                        animation.setInterpolator(new AccelerateInterpolator());
                        survivalImg1.setAnimation(animation);
                    }

                    survivalImg1.setImageBitmap(bMap);
                    survivalImg1.setVisibility(View.VISIBLE);
                } else {

                    setLayoutSizeParams(bytes,size, survivalImg2, survivalImageObj[1]);
                    if (animate) {
                        survivalImg2.clearAnimation();
                        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(IMAGE_FADE_IN_DURATION);
                        animation.setFillAfter(true);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            public void onAnimationEnd(Animation animation) {
                                int bonusPoints = getSurvivalBonusPoints(currentWinStreak+1);
                                if(bonusPoints>0){
                                    animateBonusPointsMessage(bonusPoints);
                                }
                            }
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub
                            }
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                            }
                        });
                        survivalImg2.setAnimation(animation);
                    }

                    survivalImg2.setImageBitmap(bMap);
                    survivalImg2.setVisibility(View.VISIBLE);
                }


        }

        private void selectImagesCompare(int groupNumber) {
            long highRating = 0;
            long winnerGroupSeq = 0;
            int randInt;

            survivalImageObj[0].setReady(false);
            survivalImageObj[1].setReady(false);
            long groupCount = pBudDBAdapter.getGroupItemCount(groupNumber);

            if (groupCount < 2) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "selectImagesL1: groupCount < 2; group=" + groupNumber,
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "selectImagesL1: groupCount < 2; group=" + groupNumber);
                return;
            }
            Random rand = new Random();
            int imageOrder = rand.nextInt((int) 2);

            Cursor listCursor = pBudDBAdapter
                    .getAllItemsCursorByGroupSortSeqAscend(groupNumber);

            if (listCursor.moveToFirst()) {
                do {
                    long rating = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_RATING));
                    if (rating > highRating) {
                        long sequence = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
                        long group = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_GROUP));
                        long groupSeq = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
                        String imageFile = listCursor.getString(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
                        String comments = listCursor.getString(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));
                        survivalImageObj[imageOrder].setMyImage(sequence, group, groupSeq,
                                rating, imageFile, "", comments);
                        survivalImageObj[imageOrder].setReady(true);
                        survivalImageObj[imageOrder].setWinner(true);
                        highRating = rating;
                        winnerGroupSeq = groupSeq;
                    }
                    if (winnerGroupSeq > groupCount) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Error winnerGroupSeq > groupCount"
                                        + winnerGroupSeq, Toast.LENGTH_LONG).show();
                        return;
                    }
                } while (listCursor.moveToNext());
            } else {
                Log.e(TAG, "getPreviousList(): listCursor.moveToFirst() failed.");
            }

            do {
                rand = new Random();
                randInt = rand.nextInt((int) groupCount);
                randInt++;
            } while (randInt == winnerGroupSeq);

            if (listCursor.moveToFirst()) {
                do {

                    long groupSeq = listCursor.getLong(listCursor
                            .getColumnIndex(PBudDBAdapter.KEY_GROUP_SEQ));
                    if (groupSeq == randInt) {
                        long rating = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_RATING));
                        long sequence = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_SEQUENCE));
                        long group = listCursor.getLong(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_GROUP));
                        String imageFile = listCursor.getString(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_IMAGE_FILE));
                        String comments = listCursor.getString(listCursor
                                .getColumnIndex(PBudDBAdapter.KEY_COMMENTS));
                        survivalImageObj[imageOrder ^ 0x1].setMyImage(sequence, group,
                                groupSeq, rating, imageFile, "", comments);
                        survivalImageObj[imageOrder ^ 0x1].setReady(true);
                        survivalImageObj[imageOrder ^ 0x1].setWinner(false);
                    }

                } while (listCursor.moveToNext());
            } else {
                Log.e(TAG, "getPreviousList(): listCursor.moveToFirst() failed.");
            }

            if (!survivalImageObj[0].getReady() || !survivalImageObj[1].getReady()) {
                Log.e(TAG,
                        "selectImagesCompare: Images failed to be selected. img[0].seq: "
                                + survivalImageObj[0].getSequence() + "img[1].seq: "
                                + survivalImageObj[1].getSequence());
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "selectImagesCompare: Images failed to be selected. img[0].seq: "
                                + survivalImageObj[0].getSequence() + "img[1].seq: "
                                + survivalImageObj[1].getSequence(), Toast.LENGTH_LONG)
                        .show();

            }
        }

        private void playPostSelectionSurvivalImages() {

            Animation fadeIn = new AlphaAnimation((float)0.0, (float)1.0);
            fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
            fadeIn.setDuration(SURVIVAL_PLAY_CHECKMARK_FADE_IN_DURATION);

            AnimationSet animation = new AnimationSet(false); // change to false
            animation.addAnimation(fadeIn);
            animation.setRepeatCount(1);

            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {

                    if(!didWeWin()) {
                        displayLifeLost();
                    } else {
                        sendSurvivalStateMachineMsg(INP_CS_CHECKMARK_ANIMATION_DONE);
                    }

                }
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }
            });

            ImageView iv;
            if (survivalImageObj[0].getSelected()) {
                if (survivalImageObj[0].getWinner())
                    iv = survivalPlayScreenTopCorrectPickImageView;
                else
                    iv = survivalPlayScreenTopWrongPickImageView;
                iv.getLayoutParams().height = survivalImageObj[0].getHeight();
                iv.getLayoutParams().width = survivalImageObj[0].getWidth();
            }else {
                if (survivalImageObj[1].getWinner())
                    iv = survivalPlayScreenBottomCorrectPickImageView;
                else
                    iv = survivalPlayScreenBottomWrongPickImageView;
                iv.getLayoutParams().height = survivalImageObj[1].getHeight();
                iv.getLayoutParams().width = survivalImageObj[1].getWidth();
            }
            iv.setAnimation(animation);
            iv.setVisibility(View.VISIBLE);

            image1Array[survivalImageCount - 1] = new MyImage(survivalImageObj[0]);
            image2Array[survivalImageCount - 1] = new MyImage(survivalImageObj[1]);

        }

        private void displayPostSelectSurvivalImages() {

            Context ctx = getActivity().getApplicationContext();
            displayImageFile(ctx, survivalImageObj[0].getImageFile(), 1, false);
            displayImageFile(ctx, survivalImageObj[1].getImageFile(), 2, false);

        }

        void animateBonusPointsMessage(int bonusPoints){
            playSound(soundIDBonusMessage);
            survivalPlayScreenBonusWordsTextView.setText(" Win " + String
                    .valueOf(bonusPoints) + " Points ");
            ObjectAnimator wordsAlpha1 = ObjectAnimator.ofFloat(survivalPlayScreenBonusWordsTextView, "alpha",0f, 1.0f);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(survivalPlayScreenBonusWordsTextView, "scaleX", 0.15f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(survivalPlayScreenBonusWordsTextView, "scaleY", 0.15f);
            wordsAlpha1.setDuration(600);
            scaleDownX.setStartDelay(800);
            scaleDownX.setDuration(500);
            scaleDownY.setDuration(500);
            AnimatorSet animator = new AnimatorSet();
            Animator.AnimatorListener animatorListener
                    = new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }
                public void onAnimationRepeat(Animator animation) {
                }
                public void onAnimationEnd(Animator animation) {
                    survivalPlayScreenBonusWordsTextView.setVisibility(View.INVISIBLE);
                    // should make images clickable here
                }

                public void onAnimationCancel(Animator animation) {
                }
            };
            animator.addListener(animatorListener);
            animator.playTogether(scaleDownX, scaleDownY);
            animator.playSequentially(wordsAlpha1, scaleDownX);
            animator.start();
            survivalPlayScreenBonusWordsTextView.setVisibility(View.VISIBLE);
        }




        void animatePointsScored2(int bonusPoints){
            survivalPlayScreenBonusPointsTextView.setText("+" + String
                    .valueOf(bonusPoints));
            survivalPlayScreenBonusWordsTextView.setVisibility(View.INVISIBLE);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "alpha", 0f, 1.0f);
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "scaleX",0f, 1f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "scaleY", 0f, 1f);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "scaleX", 0.20f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "scaleY", 0.20f);
            ObjectAnimator animX1 = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "x", screenWidth - (230 * screenWidth / 720));
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "y", screenHeight / 2 - (180 * screenHeight / 1280));
            ObjectAnimator animX2 = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "x", screenWidth - (230 * screenWidth / 720));
            ObjectAnimator animY2 = ObjectAnimator.ofFloat(survivalPlayScreenBonusPointsTextView, "y", -30f);

            alpha.setDuration(bonusAlphaDuration);
            scaleUpX.setDuration(bonusScaleUpDuation);
            scaleUpY.setDuration(bonusScaleUpDuation);
            scaleDownX.setStartDelay(bonusScaleDownDelay);
            scaleDownX.setDuration(bonusScaleDownDuration);
            scaleDownY.setDuration(bonusScaleDownDuration);
            animX1.setStartDelay(bonusAnimateDelay);
            animX1.setDuration(bonusAnimateDuration);
            animY1.setDuration(bonusAnimateDuration);
            animX2.setDuration(bonusAnimateDuration);
            animY2.setDuration(bonusAnimateDuration);

            AnimatorSet animator = new AnimatorSet();

            animator.playTogether(scaleUpX, scaleUpY);
            animator.playTogether(scaleDownX, scaleDownY);
            animator.playTogether(animX1, animY1);
            animator.playTogether(animX2, animY2);
            animator.playSequentially(scaleUpX, scaleDownX, animX1, animX2);
            Animator.AnimatorListener animatorListener
                    = new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }
                public void onAnimationRepeat(Animator animation) {
                }
                public void onAnimationEnd(Animator animation) {
                    if(survivalState != CS_CHECKMARK_ANIMATION_DONE) {
                        return;
                    }
                    survivalPlayScreenPointsNumberTextView.setText(String
                            .valueOf(currentScore));
                    if(survivalLivesRemain==0){
                        if (activityStopped) {
                            activityStopped = false;
                            sendINP_SURVIVAL_GAME_OVER = true;
                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; sendINP_SURVIVAL_GAME_OVER ");
                            return;
                        }
                        sendStateMachineMsg(INP_SURVIVAL_GAME_OVER);
                        return;
                    }
                    if (didWeWin()){
                        if (activityStopped) {
                            activityStopped = false;
                            sendINP_SURVIVAL_IMAGE_WON = true;
                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; sendINP_SURVIVAL_IMAGE_WON ");
                            return;
                        }
                        sendStateMachineMsg(INP_SURVIVAL_IMAGE_WON);
                    }
                    else{
                        if (activityStopped) {
                            activityStopped = false;
                            sendINP_SURVIVAL_IMAGE_LOST = true;
                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; sendINP_SURVIVAL_IMAGE_LOST ");
                            return;
                        }
                        sendStateMachineMsg(INP_SURVIVAL_IMAGE_LOST);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }
            };
            animator.addListener(animatorListener);
            animator.start();
            survivalPlayScreenBonusPointsTextView.setVisibility(View.VISIBLE);
        }


        int getSurvivalBonusPoints(int winStreak){
            if(winStreak < 3)
                return(0);
            int factor=(((currentPlayRound-1)/6)+1);
            switch(winStreak){
                case 3:
                    return(5*factor);
                case 5:
                    return(10*factor);
                case 10:
                    return(20*factor);
                case 15:
                    return(50*factor);
                case 20:
                    return(100*factor);
                case 25:
                case 30:
                    return(200*factor);
                case 35:
                case 40:
                    return(300*factor);
                case 45:
                case 50:
                    return(400*factor);
                case 55:
                case 60:
                    return(500*factor);
                default:
                    if(winStreak%5 != 0)
                        return(0);
                    else
                        return(500*factor);
            }
        }

        private int getTopClassicScore() {
            Cursor listCursor = pBudDBAdapter
                    .getAllScoresCursorByModeDesc(PB_MODE_BASIC);
            if (listCursor.moveToFirst()) {
                long gameScore = listCursor.getLong(listCursor
                        .getColumnIndex(PBudDBAdapter.KEY_ST_SCORE));
                return ((int) gameScore);
            } else {
                return (0);
            }
        }

        private void processSurvivalAward() {

            /*if (currentScore >= survivalAwardPerLevelTrigger[maxAwardLevelPerMode[(int) PB_MODE_SURVIVAL] + 1]) {
                if (pBudDBAdapter.insertAward(PB_USER_NAME, PB_MODE_SURVIVAL,
                        maxAwardLevelPerMode[(int) PB_MODE_SURVIVAL] + 1,
                        maxAwardLevelPerMode[(int) PB_MODE_SURVIVAL] + 1) < 0) {
                    Log.e(TAG, "processSurvivalAward: insertAward() failed.");
                    return;
                }
                maxAwardLevelPerMode[(int) PB_MODE_SURVIVAL]++;
            }*/
            // For now, just have one award for both games
            if ((currentScore+getTopClassicScore())>= classicAwardPerLevelTrigger[maxAwardLevelPerMode[(int) PB_MODE_BASIC] + 1]) {
                if (pBudDBAdapter.insertAward(PB_USER_NAME, PB_MODE_BASIC,
                        maxAwardLevelPerMode[(int) PB_MODE_BASIC] + 1,
                        maxAwardLevelPerMode[(int) PB_MODE_BASIC] + 1) < 0) {
                    Log.e(TAG, "processClassicAward: insertAward() failed.");
                    return;
                }
                if(maxAwardLevelPerMode[(int) PB_MODE_BASIC] < MAX_AWARD_LEVELS)
                    maxAwardLevelPerMode[(int) PB_MODE_BASIC]++;
            }

            //if (currentScore >= classicAwardPerLevelTrigger[maxAwardLevelPerMode[(int) PB_MODE_BASIC] + 1]) {
        }



        private final Handler.Callback survivalStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;

                switch (msg.what) {
                    case INP_CS_SHOW_SURVIVAL_IMAGES:
                        Log.i(TAG, "handleMessage: INP_CS_SHOW_SURVIVAL_IMAGES:  Done.");
                        playSurvivalImages();
                        survivalState = CS_IMAGES_DISPLAYED;
                        break;
                    case INP_CS_IMAGE_SELECTED:
                        Log.i(TAG, "handleMessage: INP_CS_SHOW_SURVIVAL_IMAGES:  Done.");
                        playPostSelectionSurvivalImages();
                        break;
                    case INP_CS_CHECKMARK_ANIMATION_DONE:
                        Log.i(TAG, "handleMessage: INP_CS_SHOW_SURVIVAL_IMAGES:  Done.");
                        int bonusPoints = getSurvivalBonusPoints(currentWinStreak);
                        currentScore+=bonusPoints;
                        processSurvivalAward();
                        survivalState = CS_CHECKMARK_ANIMATION_DONE;
                        imageClicked=false;
                        if(bonusPoints==0) {
                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if(survivalState != CS_CHECKMARK_ANIMATION_DONE) {
                                        return;
                                    }
                                    if(survivalLivesRemain==0){
                                        if (activityStopped) {
                                            activityStopped = false;
                                            sendINP_SURVIVAL_GAME_OVER = true;
                                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; sendINP_SURVIVAL_GAME_OVER ");
                                            return;
                                        }
                                        sendStateMachineMsg(INP_SURVIVAL_GAME_OVER);
                                        return;
                                    }
                                    if (survivalImageObj[0].getWinner()
                                            && survivalImageObj[0].getSelected()
                                            || survivalImageObj[1].getWinner()
                                            && survivalImageObj[1].getSelected()){
                                        if (activityStopped) {
                                            activityStopped = false;
                                            sendINP_SURVIVAL_IMAGE_WON = true;
                                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; nothing done. ");
                                            return;
                                        }
                                        sendStateMachineMsg(INP_SURVIVAL_IMAGE_WON);
                                    }
                                    else{
                                        if (activityStopped) {
                                            activityStopped = false;
                                            sendINP_SURVIVAL_IMAGE_LOST = true;
                                            Log.e(TAG, "INP_CS_CHECKMARK_ANIMATION_DONE.Runnable: activity stopped; nothing done. ");
                                            return;
                                        }
                                        sendStateMachineMsg(INP_SURVIVAL_IMAGE_LOST);
                                    }
                                }
                            };
                            handler.postDelayed(runnable, postSelectDelay);

                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable() {
                                @Override
                                public void run() {
                                    imageClicked=true;
                                    Log.i(TAG, "runnable: imageClicked=true");
                                }
                            };
                            handler2.postDelayed(runnable2, postSelectDelay-300);
                            break;
                        }
                        animatePointsScored2(bonusPoints);
                        break;
                    case INP_CS_TIMER_EXPIRED:
                        survivalState = CS_TIMER_EXPIRED;
                        imageClicked=false;
                        displayBothCheckAndX();
                        break;
                    case INP_CS_IMAGE0_CLICKED:
                        switch(survivalState){
                            case CS_CHECKMARK_ANIMATION_DONE:
                            case CS_TIMER_EXPIRED:
                                sendStateMachineMsg(
                                        MainActivity.INP_SURVIVAL_SHOW_TIPS,
                                        (int)survivalImageObj[0].getSequence(), (int)survivalImageObj[1].getSequence());
                                survivalState = CS_TIPS_MODE;
                                Log.i(TAG, "CS_TIPS_MODE set.");
                                break;
                            default:
                                processSurvivalAnswer(0);
                            break;
                        }
                        break;
                    case INP_CS_IMAGE1_CLICKED:
                        switch(survivalState){
                            case CS_CHECKMARK_ANIMATION_DONE:
                            case CS_TIMER_EXPIRED:
                                sendStateMachineMsg(
                                        MainActivity.INP_SURVIVAL_SHOW_TIPS,
                                        (int)survivalImageObj[1].getSequence(), (int)survivalImageObj[0].getSequence());
                                survivalState = CS_TIPS_MODE;
                                Log.i(TAG, "CS_TIPS_MODE set.");
                                break;
                            default:
                                processSurvivalAnswer(1);
                                break;
                        }
                        break;
                    default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;

                }
                return true;
            }

        };


        private static Timer survivalCoundownTimer = null;
        final Handler handler = new Handler();

        static void stopSurvivalTimer(){
            if(survivalCoundownTimer!= null) {
                survivalCoundownTimer.cancel();
                survivalCoundownTimer.purge();
                survivalCoundownTimer = null;
            }
        }

        private void startSurvivalCountDownTimer(int _seconds) {

            if (_seconds < 1) {
                /*Toast.makeText(getActivity().getApplicationContext(),
                        "startCountDownTimer: _seconds = " + _seconds,
                        Toast.LENGTH_LONG).show();*/
                Log.e(TAG, "startCountDownTimer: _seconds = " + _seconds);
                return;
            }

            quizTimerSeconds = _seconds;
            stopSurvivalTimer();

            survivalCoundownTimer = new Timer();

            survivalCoundownTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            survivalPlayScreenTimerNumberTextView.setText(""
                                    + String.valueOf(quizTimerSeconds) + "");
                            if (quizTimerSeconds-- <= 0) {
                                if (imageClicked)
                                    return;
                                else
                                    imageClicked = true;
                                stopSurvivalTimer();
                                survivalPlayScreenLivesNumberTextView.setText(String
                                        .valueOf(survivalLivesRemain));
                                processTimeout();

                            }


                        }

                    });
                }

            }, 0, 1000);

        }
    }

    /*****************************************/
    /** SURVIVAL END OF ROUND SCREEN FRAGMENT **/
    /**
     * *************************************
     */
    public static class SurvivalEndOfRoundScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        static Button survivalEndofRoundScreenContinueButton;
        static Button survivalEndofRoundScreenReviewButton;
        static Button survivalEndofRoundScreenShareButton;


        static TextView SurvivalEndOfRoundScreenHeadlineTextView;
        static TextView SurvivalEndOfRoundScreenActualPointsTextView;
        static TextView survivalEndOfRoundScreenTotalPointsTextView;
        static TextView survivalEndOfRoundScreenMessageTextView;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static SurvivalEndOfRoundScreenFragment newInstance(
                int sectionNumber) {
            SurvivalEndOfRoundScreenFragment fragment = new SurvivalEndOfRoundScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public SurvivalEndOfRoundScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_survival_end_of_round,
                    container, false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);


            SurvivalEndOfRoundScreenHeadlineTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfRoundScreenHeadlineTextView);
            SurvivalEndOfRoundScreenHeadlineTextView.setText("End of Round "
                    + currentPlayRound);
            survivalEndOfRoundScreenTotalPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfRoundScreenTotalPointsTextView);

            SurvivalEndOfRoundScreenActualPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfRoundScreenActualPointsTextView);
            SurvivalEndOfRoundScreenActualPointsTextView.setText(String
                    .valueOf(currentScore));

            survivalEndOfRoundScreenMessageTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfRoundScreenMessageTextView);
            survivalEndOfRoundScreenMessageTextView.setVisibility(View.INVISIBLE);


            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            SurvivalEndOfRoundScreenHeadlineTextView.setTypeface(font);
            SurvivalEndOfRoundScreenActualPointsTextView.setTypeface(font);
            survivalEndOfRoundScreenTotalPointsTextView.setTypeface(font);
            survivalEndOfRoundScreenMessageTextView.setTypeface(font);

            if((currentPlayRound % SURVIVAL_ROUNDS_FOR_LIFE) == 0) {
                if (survivalLivesRemain < SURVIVAL_MAX_LIVES) {
                    survivalLivesRemain++;
                    survivalEndOfRoundScreenMessageTextView.setVisibility(View.VISIBLE);
                }
            }

            survivalEndofRoundScreenContinueButton = (Button) rootView
                    .findViewById(R.id.survivalEndofRoundScreenContinueButton);
            survivalEndofRoundScreenContinueButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOR_SCREEN_CONTINUE_BUTTON_PRESSED);
                        }
                    });
            survivalEndofRoundScreenReviewButton = (Button) rootView
                    .findViewById(R.id.survivalEndofRoundScreenReviewButton);
            survivalEndofRoundScreenReviewButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOR_SCREEN_REVIEW_BUTTON_PRESSED);
                        }
                    });
            survivalEndofRoundScreenShareButton = (Button) rootView
                    .findViewById(R.id.survivalEndofRoundScreenShareButton);
            survivalEndofRoundScreenShareButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOR_SCREEN_SHARE_BUTTON_PRESSED);
                            sendAnalyticsEvent("Action","SurvivalSharePressed");

                        }
                    });
            SurvivalRoundsCount++;
            savePreferenceInt(PREF_STR_CUM_SURVIVAL_ROUNDS_PLAYED, ++cumulativeSurvivalRoundsPreference);
            sendAnalyticsScreen("SurvivalEndOfRound");
            return rootView;
        }
    }
    /*****************************************/
    /** SURVIVAL END OF GAME SCREEN FRAGMENT **/
    /**
     * *************************************
     */
    public static class SurvivalEndOfGameScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        static Button survivalEndOfGameScreenReviewButton;
        static Button survivalEndOfGameScreenPlayAgainButton;
        static Button survivalEndOfGameScreenQuitGameButton;
        static TextView survivalEndOfGameScreenActualPointsTextView;
        static TextView survivalEndOfGameScreenTotalPointsTextView;

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static SurvivalEndOfGameScreenFragment newInstance(
                int sectionNumber) {
            SurvivalEndOfGameScreenFragment fragment = new SurvivalEndOfGameScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public SurvivalEndOfGameScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_survival_end_of_game,
                    container, false);
            Bundle b = getArguments();
            if (b == null)
                return (rootView);


            survivalEndOfGameScreenTotalPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfGameScreenTotalPointsTextView);
            survivalEndOfGameScreenActualPointsTextView = (TextView) rootView
                    .findViewById(R.id.survivalEndOfGameScreenActualPointsTextView);
            survivalEndOfGameScreenActualPointsTextView.setText(String
                    .valueOf(currentScore));

            Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/forced_square.ttf");
            survivalEndOfGameScreenTotalPointsTextView.setTypeface(font);
            survivalEndOfGameScreenActualPointsTextView.setTypeface(font);

            survivalEndOfGameScreenReviewButton = (Button) rootView
                    .findViewById(R.id.survivalEndOfGameScreenReviewButton);
            survivalEndOfGameScreenReviewButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOG_SCREEN_REVIEW_BUTTON_PRESSED);
                        }
                    });
            survivalEndOfGameScreenPlayAgainButton = (Button) rootView
                    .findViewById(R.id.survivalEndOfGameScreenPlayAgainButton);
            survivalEndOfGameScreenPlayAgainButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED);
                        }
                    });

            survivalEndOfGameScreenQuitGameButton = (Button) rootView
                    .findViewById(R.id.survivalEndOfGameScreenQuitGameButton);
            survivalEndOfGameScreenQuitGameButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED);
                        }
                    });
            playSound(soundIDGameOver);
            sendAnalyticsScreen("SurvivalEndOfGame");
            return rootView;
        }
    }

    /**
     * START SCREEN FRAGMENT
     */
    public static class StartScreenFragment extends Fragment {
        static Button startScreenClassicGameButton;
        //static Button startScreenHallOfFameButton;
        static Button startScreenOptionsButton;
        static Button startScreenClassicStartPlayButton;
        static Button startScreenClassicHighScoreButton;
        static Button startScreenClassicHowToButton;
        static Button startScreenHOFFavoritesButton;
        static Button startScreenHOFAwardButton;
        static Button startScreenOptionsStoreButton;
        static Button startScreenOptionsSettingsButton;
        static Button startScreenOptionsAboutButton;
        static Button startScreenOptionsFacebookButton;
        static Button startScreenSurvivalGameButton;
        static Button startScreenSurvivalStartPlayButton;
        static Button startScreenSurvivalHighScoreButton;
        static Button startScreenSurvivalHowToButton;
        static boolean classicGameMenuDropped = false;
        static boolean survivalGameMenuDropped = false;
        static boolean hofMenuDropped = false;
        static boolean optionsMenuDropped = false;
        final private static boolean DROP_MENU=true;
        final private static boolean FOLD_MENU=false;
        static Handler hStartScreenStateMachine = null;
        private static int ssState;
        final private static int SS_CLASSIC_WAIT_DROP_MENU = 91;
        final private static int SS_SURVIVAL_WAIT_DROP_MENU = SS_CLASSIC_WAIT_DROP_MENU+1;
        final private static int SS_HOF_WAIT_DROP_MENU = SS_CLASSIC_WAIT_DROP_MENU+2;
        final private static int SS_OPTIONS_WAIT_DROP_MENU = SS_CLASSIC_WAIT_DROP_MENU+3;

        final private static int INP_CLASSIC_MENU_FOLDED = 901;
        final private static int INP_SURVIVAL_MENU_FOLDED = INP_CLASSIC_MENU_FOLDED+1;
        final private static int INP_HOF_MENU_FOLDED = INP_CLASSIC_MENU_FOLDED+2;
        final private static int INP_OPTIONS_MENU_FOLDED = INP_CLASSIC_MENU_FOLDED+3;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static StartScreenFragment newInstance(int sectionNumber) {
            StartScreenFragment fragment = new StartScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public StartScreenFragment() {
            return;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            classicGameMenuDropped = false;
            survivalGameMenuDropped = false;
            hofMenuDropped = false;
            optionsMenuDropped = false;
            ssState = 0;
            hStartScreenStateMachine = new Handler(startScreenStateMachineMessage);
            System.gc();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "StartScreenFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_start, container,
                    false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            layoutStartScreen = rootView.findViewById(R.id.startScreenLayout);

            startScreenClassicGameButton = (Button) rootView
                    .findViewById(R.id.startScreenClassicGameButton);
            startScreenOptionsButton = (Button) rootView
                    .findViewById(R.id.startScreenOptionsButton);

            startScreenClassicStartPlayButton = (Button) rootView
                    .findViewById(R.id.startScreenClassicStartPlayButton);

            startScreenClassicHighScoreButton = (Button) rootView
                    .findViewById(R.id.startScreenClassicHighScoreButton);

            startScreenClassicHowToButton = (Button) rootView
                    .findViewById(R.id.startScreenClassicHowToButton);

            startScreenSurvivalGameButton = (Button) rootView
                    .findViewById(R.id.startScreenSurvivalGameButton);

            startScreenSurvivalStartPlayButton = (Button) rootView
                    .findViewById(R.id.startScreenSurvivalStartPlayButton);

            startScreenSurvivalHighScoreButton = (Button) rootView
                    .findViewById(R.id.startScreenSurvivalHighScoreButton);

            startScreenSurvivalHowToButton = (Button) rootView
                    .findViewById(R.id.startScreenSurvivalHowToButton);

            startScreenHOFAwardButton = (Button) rootView
                    .findViewById(R.id.startScreenHOFAwardButton);

            startScreenHOFFavoritesButton = (Button) rootView
                    .findViewById(R.id.startScreenHOFFavoritesButton);

            startScreenOptionsStoreButton = (Button) rootView
                    .findViewById(R.id.startScreenOptionsStoreButton);

            startScreenOptionsSettingsButton = (Button) rootView
                    .findViewById(R.id.startScreenOptionsSettingsButton);

            startScreenOptionsAboutButton = (Button) rootView
                    .findViewById(R.id.startScreenOptionsAboutButton);

            startScreenOptionsFacebookButton = (Button) rootView
                    .findViewById(R.id.startScreenOptionsFacebookButton);

            startScreenClassicGameButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            //sendStateMachineMsg(MainActivity.INP_START_SCREEN_PLAY_BUTTON_PRESSED);
                            if(!classicGameMenuDropped) {
                                if(survivalGameMenuDropped) {
                                    survivalGameMenuDropFold(FOLD_MENU);
                                    survivalGameMenuDropped = false;
                                    ssState = SS_CLASSIC_WAIT_DROP_MENU;
                                }
                                else if(optionsMenuDropped) {
                                    optionsMenuDropFold(FOLD_MENU);
                                    optionsMenuDropped = false;
                                    ssState = SS_CLASSIC_WAIT_DROP_MENU;
                                }
                                else {
                                    classicGameMenuDropFold(DROP_MENU);
                                    classicGameMenuDropped = true;
                                }
                            } else {
                                classicGameMenuDropFold(FOLD_MENU);
                                classicGameMenuDropped = false;
                            }
                        }
                    });

            startScreenClassicStartPlayButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_SCREEN_BEGIN_BUTTON_PRESSED);
                        }
                    });
            startScreenClassicHighScoreButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_SCREEN_HIGH_SCORE_BUTTON_PRESSED);
                        }
                    });
            startScreenClassicHowToButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_CLASSIC_SCREEN_HOW_TO_BUTTON_PRESSED);
                        }
                    });


            if(survivalLockedPreference) {
                startScreenSurvivalGameButton.setBackgroundResource(R.drawable.survival_button_locked);
                startScreenSurvivalGameButton.setClickable(true);
                startScreenSurvivalGameButton
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                playSound(soundIDButtonClick);
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "Play " + ROUNDS_TO_UNLOCK_SURVIVAL + " rounds of Classic to unlock"
                                        , Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
            }
            else{
                startScreenSurvivalGameButton.setBackgroundResource(R.drawable.survival_button);
                startScreenSurvivalGameButton.setClickable(true);
                startScreenSurvivalGameButton
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                playSound(soundIDButtonClick);
                                if(!survivalGameMenuDropped) {
                                    if(optionsMenuDropped) {
                                        optionsMenuDropFold(FOLD_MENU);
                                        optionsMenuDropped = false;
                                        ssState = SS_SURVIVAL_WAIT_DROP_MENU;
                                    }
                                    else if(classicGameMenuDropped) {
                                        classicGameMenuDropFold(FOLD_MENU);
                                        classicGameMenuDropped = false;
                                        ssState = SS_SURVIVAL_WAIT_DROP_MENU;
                                    }
                                    else {
                                        survivalGameMenuDropFold(DROP_MENU);
                                        survivalGameMenuDropped = true;
                                    }
                                } else {
                                    survivalGameMenuDropFold(FOLD_MENU);
                                    survivalGameMenuDropped = false;
                                }
                            }
                        });

            }

            startScreenSurvivalStartPlayButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_SCREEN_BEGIN_BUTTON_PRESSED);
                        }
                    });
            startScreenSurvivalHighScoreButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_SCREEN_HIGH_SCORE_BUTTON_PRESSED);
                        }
                    });
            startScreenSurvivalHowToButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_SURVIVAL_SCREEN_HOW_TO_BUTTON_PRESSED);
                        }
                    });

            startScreenHOFAwardButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_HOF_SCREEN_AWARDS_BUTTON_PRESSED);
                        }
                    });

            startScreenHOFFavoritesButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_HOF_SCREEN_FAVORITES_BUTTON_PRESSED);
                        }
                    });

            startScreenOptionsButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            //sendStateMachineMsg(MainActivity.INP_START_SCREEN_OPTIONS_BUTTON_PRESSED);
                            if(!optionsMenuDropped) {
                                if(classicGameMenuDropped) {
                                    classicGameMenuDropFold(FOLD_MENU);
                                    classicGameMenuDropped = false;
                                    ssState = SS_OPTIONS_WAIT_DROP_MENU;
                                }
                                else if(survivalGameMenuDropped) {
                                    survivalGameMenuDropFold(FOLD_MENU);
                                    survivalGameMenuDropped = false;
                                    ssState = SS_OPTIONS_WAIT_DROP_MENU;
                                }
                                else{
                                    optionsMenuDropFold(DROP_MENU);
                                    optionsMenuDropped = true;
                                }
                            } else {
                                optionsMenuDropFold(FOLD_MENU);
                                optionsMenuDropped = false;
                            }
                        }
                    });

            startScreenOptionsStoreButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_OPTIONS_SCREEN_STORE_BUTTON_PRESSED);
                        }
                    });
            startScreenOptionsSettingsButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_OPTIONS_SCREEN_SETTINGS_BUTTON_PRESSED);
                        }
                    });
            startScreenOptionsAboutButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_OPTIONS_SCREEN_ABOUT_BUTTON_PRESSED);
                        }
                    });
            startScreenOptionsFacebookButton
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            playSound(soundIDButtonClick);
                            sendStateMachineMsg(MainActivity.INP_OPTIONS_SCREEN_FACEBOOK_BUTTON_PRESSED);
                        }
                    });

            sendAnalyticsScreen("Start");

            if(hintStartInfoPreference)
                sendStateMachineMsg(MainActivity.INP_START_SCREEN_INFO_HINT);

            return rootView;
        }




        private static void classicGameMenuDropFold(boolean drop){

            if(drop) {
                ObjectAnimator animSurvival = ObjectAnimator.ofFloat(startScreenSurvivalGameButton, "translationY", 0, 215*screenWFactor);
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 0, 215*screenWFactor);
                ObjectAnimator animStartPlay = ObjectAnimator.ofFloat(startScreenClassicStartPlayButton, "translationY", 0, 130*screenWFactor);
                ObjectAnimator animHighScore = ObjectAnimator.ofFloat(startScreenClassicHighScoreButton, "translationY", 0, 245*screenWFactor);
                ObjectAnimator animHowTo = ObjectAnimator.ofFloat(startScreenClassicHowToButton, "translationY", 0, 245*screenWFactor);
                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(animSurvival, animOPTIONS, animStartPlay, animHighScore, animHowTo);

                animator.start();
                startScreenClassicStartPlayButton.setVisibility(View.VISIBLE);
                startScreenClassicHighScoreButton.setVisibility(View.VISIBLE);
                startScreenClassicHowToButton.setVisibility(View.VISIBLE);
            } else {
                ObjectAnimator animSurvival = ObjectAnimator.ofFloat(startScreenSurvivalGameButton, "translationY", 215*screenWFactor, 0);
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 215*screenWFactor, 0);
                ObjectAnimator animStartPlay = ObjectAnimator.ofFloat(startScreenClassicStartPlayButton, "translationY", 130*screenWFactor, 0);
                ObjectAnimator animHighScore = ObjectAnimator.ofFloat(startScreenClassicHighScoreButton, "translationY", 245*screenWFactor, 0);
                ObjectAnimator animHowTo = ObjectAnimator.ofFloat(startScreenClassicHowToButton, "translationY", 245*screenWFactor, 0);
                AnimatorSet animator = new AnimatorSet();
                Animator.AnimatorListener animatorListener
                        = new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }
                    public void onAnimationRepeat(Animator animation) {
                    }
                    public void onAnimationEnd(Animator animation) {
                        startScreenClassicStartPlayButton.setVisibility(View.INVISIBLE);
                        startScreenClassicHighScoreButton.setVisibility(View.INVISIBLE);
                        startScreenClassicHowToButton.setVisibility(View.INVISIBLE);
                        sendStartScreenStateMachineMsg(INP_CLASSIC_MENU_FOLDED);
                    }

                    public void onAnimationCancel(Animator animation) {
                    }
                };
                animator.addListener(animatorListener);
                animator.playTogether(animSurvival, animOPTIONS, animStartPlay, animHighScore, animHowTo);
                animator.start();

            }
        }
        private static void survivalGameMenuDropFold(boolean drop){
            if(drop) {
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 0, 215*screenWFactor);
                ObjectAnimator animStartPlay = ObjectAnimator.ofFloat(startScreenSurvivalStartPlayButton, "translationY", 0, 130*screenWFactor);
                ObjectAnimator animHighScore = ObjectAnimator.ofFloat(startScreenSurvivalHighScoreButton, "translationY", 0, 245*screenWFactor);
                ObjectAnimator animHowTo = ObjectAnimator.ofFloat(startScreenSurvivalHowToButton, "translationY", 0, 245*screenWFactor);
                AnimatorSet animator = new AnimatorSet();

                animator.playTogether(animOPTIONS, animStartPlay, animHighScore, animHowTo);
                animator.start();
                startScreenSurvivalStartPlayButton.setVisibility(View.VISIBLE);
                startScreenSurvivalHighScoreButton.setVisibility(View.VISIBLE);
                startScreenSurvivalHowToButton.setVisibility(View.VISIBLE);
            } else {
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 215*screenWFactor, 0);
                ObjectAnimator animStartPlay = ObjectAnimator.ofFloat(startScreenSurvivalStartPlayButton, "translationY", 130*screenWFactor, 0);
                ObjectAnimator animHighScore = ObjectAnimator.ofFloat(startScreenSurvivalHighScoreButton, "translationY", 245*screenWFactor, 0);
                ObjectAnimator animHowTo = ObjectAnimator.ofFloat(startScreenSurvivalHowToButton, "translationY", 245*screenWFactor, 0);
                AnimatorSet animator = new AnimatorSet();
                Animator.AnimatorListener animatorListener
                        = new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }
                    public void onAnimationRepeat(Animator animation) {
                    }
                    public void onAnimationEnd(Animator animation) {
                        startScreenSurvivalStartPlayButton.setVisibility(View.INVISIBLE);
                        startScreenSurvivalHighScoreButton.setVisibility(View.INVISIBLE);
                        startScreenSurvivalHowToButton.setVisibility(View.INVISIBLE);
                        sendStartScreenStateMachineMsg(INP_SURVIVAL_MENU_FOLDED);
                    }

                    public void onAnimationCancel(Animator animation) {
                    }
                };
                animator.addListener(animatorListener);
                animator.playTogether(animOPTIONS, animStartPlay, animHighScore, animHowTo);
                animator.start();

            }
        }
        private static void hofMenuDropFold(boolean drop) {
            if (drop) {
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 0, 200*screenWFactor);
                ObjectAnimator animAwards = ObjectAnimator.ofFloat(startScreenHOFAwardButton, "translationY", 0, 200*screenWFactor);
                ObjectAnimator animFavorites = ObjectAnimator.ofFloat(startScreenHOFFavoritesButton, "translationY", 0, 200*screenWFactor);
                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(animOPTIONS, animAwards, animFavorites);
                animator.start();
                startScreenHOFAwardButton.setVisibility(View.VISIBLE);
                startScreenHOFFavoritesButton.setVisibility(View.VISIBLE);
            } else {
                ObjectAnimator animOPTIONS = ObjectAnimator.ofFloat(startScreenOptionsButton, "translationY", 200*screenWFactor, 0);
                ObjectAnimator animAwards = ObjectAnimator.ofFloat(startScreenHOFAwardButton, "translationY", 200*screenWFactor, 0);
                ObjectAnimator animFavorites = ObjectAnimator.ofFloat(startScreenHOFFavoritesButton, "translationY", 200*screenWFactor, 0);
                AnimatorSet animator = new AnimatorSet();
                Animator.AnimatorListener animatorListener
                        = new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }
                    public void onAnimationRepeat(Animator animation) {
                    }
                    public void onAnimationEnd(Animator animation) {
                        startScreenHOFAwardButton.setVisibility(View.INVISIBLE);
                        startScreenHOFFavoritesButton.setVisibility(View.INVISIBLE);
                        sendStartScreenStateMachineMsg(INP_HOF_MENU_FOLDED);
                    }
                    public void onAnimationCancel(Animator animation) {
                    }
                };
                animator.addListener(animatorListener);
                animator.playTogether(animOPTIONS, animAwards, animFavorites);
                animator.start();
            }
        }
        private static void optionsMenuDropFold(boolean drop) {
            if (drop) {
                ObjectAnimator animAbout = ObjectAnimator.ofFloat(startScreenOptionsAboutButton, "translationY", 0, 245*screenWFactor);
                ObjectAnimator animStore = ObjectAnimator.ofFloat(startScreenOptionsStoreButton, "translationY", 0, 130*screenWFactor);
                ObjectAnimator animSettings = ObjectAnimator.ofFloat(startScreenOptionsSettingsButton, "translationY", 0, 245*screenWFactor);
                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(animAbout, animStore,animSettings);
                animator.start();
                startScreenOptionsAboutButton.setVisibility(View.VISIBLE);
                startScreenOptionsStoreButton.setVisibility(View.VISIBLE);
                startScreenOptionsSettingsButton.setVisibility(View.VISIBLE);
            } else {
                ObjectAnimator animAbout = ObjectAnimator.ofFloat(startScreenOptionsAboutButton, "translationY", 245*screenWFactor, 0);
                ObjectAnimator animStore = ObjectAnimator.ofFloat(startScreenOptionsStoreButton, "translationY", 130*screenWFactor, 0);
                ObjectAnimator animSettings = ObjectAnimator.ofFloat(startScreenOptionsSettingsButton, "translationY", 245*screenWFactor, 0);
                AnimatorSet animator = new AnimatorSet();
                Animator.AnimatorListener animatorListener
                        = new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }
                    public void onAnimationRepeat(Animator animation) {
                    }
                    public void onAnimationEnd(Animator animation) {
                        startScreenOptionsAboutButton.setVisibility(View.INVISIBLE);
                        startScreenOptionsStoreButton.setVisibility(View.INVISIBLE);
                        startScreenOptionsSettingsButton.setVisibility(View.INVISIBLE);
                        sendStartScreenStateMachineMsg(INP_OPTIONS_MENU_FOLDED);
                    }
                    public void onAnimationCancel(Animator animation) {
                    }
                };
                animator.addListener(animatorListener);
                animator.playTogether(animAbout, animStore,animSettings);
                animator.start();
            }
        }

        static boolean sendStartScreenStateMachineMsg(int msg) {
            Message m = hStartScreenStateMachine.obtainMessage();
            m.what = msg;
            hStartScreenStateMachine.sendMessage(m);
            return (true);
        }

        private static final Handler.Callback startScreenStateMachineMessage = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean ret;
                switch (msg.what) {
                    case INP_CLASSIC_MENU_FOLDED:

                        switch(ssState){
                            case SS_SURVIVAL_WAIT_DROP_MENU:
                                survivalGameMenuDropFold(DROP_MENU);
                                survivalGameMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_HOF_WAIT_DROP_MENU:
                                hofMenuDropFold(DROP_MENU);
                                hofMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_OPTIONS_WAIT_DROP_MENU:
                                optionsMenuDropFold(DROP_MENU);
                                optionsMenuDropped = true;
                                ssState = 0;
                                break;
                            default:
                                break;
                        }
                        break;
                    case INP_SURVIVAL_MENU_FOLDED:
                        switch(ssState){
                            case SS_CLASSIC_WAIT_DROP_MENU:
                                classicGameMenuDropFold(DROP_MENU);
                                classicGameMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_HOF_WAIT_DROP_MENU:
                                hofMenuDropFold(DROP_MENU);
                                hofMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_OPTIONS_WAIT_DROP_MENU:
                                optionsMenuDropFold(DROP_MENU);
                                optionsMenuDropped = true;
                                ssState = 0;
                                break;
                            default:
                                break;
                        }
                    case INP_HOF_MENU_FOLDED:
                        switch(ssState){
                            case SS_CLASSIC_WAIT_DROP_MENU:
                                classicGameMenuDropFold(DROP_MENU);
                                classicGameMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_SURVIVAL_WAIT_DROP_MENU:
                                survivalGameMenuDropFold(DROP_MENU);
                                survivalGameMenuDropped = true;
                                ssState = 0;
                                break;

                            case SS_OPTIONS_WAIT_DROP_MENU:
                                optionsMenuDropFold(DROP_MENU);
                                optionsMenuDropped = true;
                                ssState = 0;
                                break;
                            default:
                                break;
                        }
                        break;
                    case INP_OPTIONS_MENU_FOLDED:
                        switch(ssState){
                            case SS_CLASSIC_WAIT_DROP_MENU:
                                classicGameMenuDropFold(DROP_MENU);
                                classicGameMenuDropped = true;
                                ssState = 0;
                                break;
                            case SS_SURVIVAL_WAIT_DROP_MENU:
                                survivalGameMenuDropFold(DROP_MENU);
                                survivalGameMenuDropped = true;
                                ssState = 0;
                                break;

                            case SS_HOF_WAIT_DROP_MENU:
                                hofMenuDropFold(DROP_MENU);
                                hofMenuDropped = true;
                                ssState = 0;
                                break;
                            default:
                                break;
                        }
                        break;
                     default:
                        Log.e(TAG,
                                "onCreate: handleMessage(), L1 Message not defined msgId="
                                        + msg.what);
                        break;
                }
                return true;
            }

        };
    }


    public static class AboutScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView about;

        public static AboutScreenFragment newInstance(int sectionNumber) {
            AboutScreenFragment fragment = new AboutScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            System.gc();
            return fragment;
        }

        public AboutScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "AboutFragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_about, container,
                    false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);

            about =  (TextView) rootView
                    .findViewById(R.id.about);
            about.setMovementMethod(new ScrollingMovementMethod());

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("This app uses a novel, fun approach to help novice photographers learn basic photo composition skills. In general, photography has no rules; however if you are a beginner there are numerous tips that may be helpful. Playing the game helps you discern image quality by making comparisons of images from the same scene. You should imagine yourself at the scene, deciding on the better composition. Look at the example below:\n\n").append(" \n ");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.about_img1);
            builder.setSpan(new ImageSpan(getActivity(), bitmap),
                    builder.length()-3, builder.length()-2, 0);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.about_img2);
            builder.setSpan(new ImageSpan(getActivity(), bitmap),
                    builder.length() - 1, builder.length(), 0);
            builder.append("\n\nAfter each round of 3 comparisons, you may review the prior image pairs and read tips on why one image is considered better than the other. The app aims to develop basic composition skills by learning through repetition with less emphasis on the game play. The theory is that the more you view the same image pairs, the more your mind retains the tips, resulting in better images the next time you shoot.\n" +
                    "\n" +
                    "The Classic game is a tutorial game with a pair of images from the same scene but with varying camera positions and angles. To get the most out of the game, review the images after each round and read the tips to understand why one image is better than the other one.\n" +
                    "\n" +
                    "The Survival game is unlocked after completing two levels in the Classic game. The Survival game doesn't limit itself to images from the same scene making it more challenging than the Classic game. The Survival game uses a 15 second timer and starts you with three lives, whereas the Classic game is untimed and has no limits on mistakes.\n" +
                    "\nThis app was created with the hope of helping beginner photographers around the world. If you enjoy the app, please give us a positive rating in the app store and share the app with friends." +
                    "\n\nDisclaimer: Photography is a subjective art form, hence all materials within this application are strictly the personal opinions of the photographers and authors of the tips.  If you find yourself in disagreement of the selection results and tips, please know the application is merely presenting one view point.");
            //builder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), builder.length()-222, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            builder.append("\n\n" +
                    "Version:  " + versionName + "\n\n" +
                    "Software:\nJames Ting\nDarren Ting\n\n" +
                    "Photography:\nJames Ting\nDarren Ting\n\n" +
                    "Tips:\nJames Ting\n\n" +
                    "FunPicks LLC is an US-based, educational software company, focusing on making learning fun.\n\n"+
                    "©FunPicks LLC, 2016. Unauthorized use and/or duplication of this material without express and written permission from this product’s author and/or owner is strictly prohibited.");

            about.setText(builder);
            sendAnalyticsScreen("About");
            return rootView;
        }
    }


   



    /** AD FRAGMENT */

	public static class AdFragment extends Fragment {
        private AdView mAdView;
		public AdFragment() {
		}

		@Override
		public void onActivityCreated(Bundle bundle) {
			super.onActivityCreated(bundle);



			// Gets the ad view defined in layout/ad_fragment.xml with ad unit
			// ID set in
			// values/strings.xml.
			mAdView = (AdView) getView().findViewById(R.id.adView);


            // Create an ad request. Check logcat output for the hashed device
			// ID to
			// get test ads on a physical device. e.g.
			// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
			//AdRequest adRequest = new AdRequest.Builder().addTestDevice(
			//		AdRequest.DEVICE_ID_EMULATOR).build();

            AdRequest adRequest = new AdRequest.Builder().build();


            if(adsOnOffPreference){
                mAdView.loadAd(adRequest);
                mAdView.setVisibility(View.VISIBLE);
            }
            else
                mAdView.setVisibility(View.GONE);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

                return inflater.inflate(R.layout.fragment_ad, container, false);
		}

		@Override
		public void onPause() {
			if (mAdView != null) {
				mAdView.pause();
			}
			super.onPause();
		}

		@Override
		public void onResume() {
			super.onResume();
			if (mAdView != null) {
				mAdView.resume();
			}
		}

		@Override
		public void onDestroy() {
			if (mAdView != null) {
				mAdView.destroy();
			}
			super.onDestroy();
		}

	}

    public static class BlankScreenFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView aboutTitle;
        private TextView about;

        public static BlankScreenFragment newInstance(int sectionNumber) {
            BlankScreenFragment fragment = new BlankScreenFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public BlankScreenFragment() {
            return;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "Aboutragment onCreateView(): entered.");

            rootView = inflater.inflate(R.layout.fragment_blank, container,
                    false);

            Bundle b = getArguments();
            if (b == null)
                return (rootView);

            long sectionNumber = (long) b.getInt(ARG_SECTION_NUMBER);
            return rootView;
        }
    }

    private int interstitialState;
    void setInterstitialState(int state){
        interstitialState = state;
    }

    private final Handler.Callback stateMachineMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean ret;
            ReviewScreenPagerFragment reviewPagerFragment;
            TipsScreenPagerFragment tipsPagerFragment;

            Log.i(TAG, "stateMachineMessage: State=" + L1State + " input = "
                    + msg.what);
            setFullScreen();
            switch (L1State) {
                case L1_INIT_P1:
                    switch (msg.what) {
                        case INP_P1_INITIALIZED:
                            initP2();
                            break;
                        case INP_EULA_ACCEPTED:
                            break;
                        case INP_BACK_PRESSED:
                            exitApp();
                            break;
                        default:
                            break;
                    }
                    break;
               case L1_S0_PRE_INIT:
                    switch (msg.what) {
                        case INP_INITIALIZED:
                            showRatingDialog();

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();

                            setL1State(L1_S1_START_SCREEN);
                            break;
                        case INP_BACK_PRESSED:
                            exitApp();
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_S1_START_SCREEN:
                    switch (msg.what) {
                        case INP_START_SCREEN_INFO_HINT:
                            FragmentManager fm = getSupportFragmentManager();
                            StartInfoDialogFragment dialogFrag = new StartInfoDialogFragment();
                            dialogFrag.show(fm, "Start Info Fragment");
                            //sendAnalyticsEvent("Action","ShowFavoriteLimitExceeded");
                            break;
                        case INP_CLASSIC_SCREEN_BEGIN_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(R.id.container,
                                            ClassicStartGameScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_CLASSIC_START_GAME_SCREEN);
                            break;
                        case INP_CLASSIC_SCREEN_HIGH_SCORE_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(
                                            R.id.container,
                                            HighScoresScreenFragment.newInstance((int)PB_MODE_BASIC))
                                    .commit();
                            currentMode = PB_MODE_BASIC;
                            setL1State(L1_HIGH_SCORES_SCREEN);
                            break;

                        case INP_CLASSIC_SCREEN_HOW_TO_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            HowToScreenFragment
                                            .newInstance((int) PB_MODE_BASIC))
                                    .commit();
                            setL1State(L1_HOW_TO_SCREEN);
                            break;
                        case INP_SURVIVAL_SCREEN_BEGIN_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(R.id.container,
                                            NewRoundScreenFragment.newInstance((int)PB_MODE_SURVIVAL))
                                    .commit();
                            setL1State(L1_SURVIVAL_NEW_ROUND_SCREEN);
                            survivalModeInit();
                            break;
                        case INP_SURVIVAL_SCREEN_HIGH_SCORE_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(
                                            R.id.container,
                                            HighScoresScreenFragment.newInstance((int)PB_MODE_SURVIVAL))
                                    .commit();
                            setL1State(L1_HIGH_SCORES_SCREEN);
                            break;

                        case INP_SURVIVAL_SCREEN_HOW_TO_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            HowToScreenFragment
                                                    .newInstance((int)PB_MODE_SURVIVAL))
                                    .commit();
                            setL1State(L1_HOW_TO_SCREEN);
                            break;
                        case INP_HOF_SCREEN_AWARDS_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(
                                            R.id.container,
                                            AwardScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_AWARDS_SCREEN);
                            break;

                        case INP_HOF_SCREEN_FAVORITES_BUTTON_PRESSED:
                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                FavoritesGridScreenFragment
                                                        .newInstance(1)).commit();
                            }
                            setL1State(L1_FAVORITES_SCREEN);
                            break;
                        case INP_OPTIONS_SCREEN_STORE_BUTTON_PRESSED:

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .add(
                                            R.id.container,
                                            StoreScreenFragment
                                                    .newInstance(1))
                                    .addToBackStack(null)
                                    .commit();
                            setL1State(L1_STORE_SCREEN);
                            break;
                        case INP_OPTIONS_SCREEN_FACEBOOK_BUTTON_PRESSED:
                            //fbInvite();
                            //performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);


                           getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            ShareScreenFragment
                                                    .newInstance(1)).commit();
                            setL1State(L1_SHARE_SCREEN);
                            break;
                        case INP_OPTIONS_SCREEN_ABOUT_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            AboutScreenFragment
                                                    .newInstance(1)).commit();

                            setL1State(L1_ABOUT_SCREEN);
                            /*if(doEncrypt) {
                                encryptFiles();
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Encoding done"
                                        , Toast.LENGTH_LONG)
                                        .show();

                            }*/
                            break;
                        case INP_OPTIONS_SCREEN_SETTINGS_BUTTON_PRESSED:
                            displaySettingsPref();
                            break;

                        case INP_BACK_PRESSED:
/*                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);*/

                            exitApp();
                            break;
                    }
                    break;
                case L1_SHARE_SCREEN:
                    switch (msg.what) {
                        case INP_SHARE_SCREEN_SHARE_BUTTON_PRESSED:
                            performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
                            break;
                        case INP_SHARE_SCREEN_INVITE_BUTTON_PRESSED:
                            fbInvite();
                            break;
                        case INP_SHARE_SCREEN_MORE_BUTTON_PRESSED:
                            startShareDialogue();
                            break;

                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_SURVIVAL_NEW_ROUND_SCREEN:
                    switch (msg.what) {
                        case INP_NEW_ROUND_SCREEN_DONE:
                            if(hintSurvivalStartPlayPreference){
                                FragmentManager fm = getSupportFragmentManager();
                                SurvivalStartPlayDialogFragment dialogFrag = new SurvivalStartPlayDialogFragment();
                                dialogFrag.show(fm, "Hint Fragment");
                                break;
                            }
                        case INP_SURVIVAL_START_GAME_SCREEN_INFO_HINT_DONE:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                            SurvivalPlayScreenFragment.newInstance((int)PB_MODE_SURVIVAL))
                                    .commit();
                            setL1State(L1_SURVIVAL_PLAY_SCREEN);
                            break;
                        case INP_BACK_PRESSED:
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_SURVIVAL_PLAY_SCREEN:
                    switch (msg.what) {
                        case INP_SURVIVAL_IMAGE_WON:
                        case INP_SURVIVAL_IMAGE_LOST:
                           if ((survivalImageCount % PB_IMAGES_PER_LEVEL_SURVIVAL) == 0) {

                               getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                SurvivalEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_SURVIVAL_END_OF_ROUND_SCREEN);
                            } else {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                SurvivalPlayScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_SURVIVAL_PLAY_SCREEN);
                            }
                            totalQuestions++;
                            survivalImageCount++;
                            break;
                        case INP_SURVIVAL_GAME_OVER:
                            SurvivalPlayScreenFragment.stopSurvivalTimer();
                            if (pBudDBAdapter.insertScore(PB_USER_NAME, PB_MODE_SURVIVAL, currentScore, 0) < 0) {
                                Log.e(TAG,
                                        "displaySurvivalResults: insertScore() failed. currentScore="
                                                + currentScore);
                            }
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            SurvivalEndOfGameScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_SURVIVAL_END_OF_GAME_SCREEN);
                            totalQuestions++;
                            survivalImageCount++;
                            break;
                        case INP_SURVIVAL_SHOW_TIPS:
                            Bundle b = msg.getData();
                            if (b == null) {
                                Log.e(TAG,
                                        "handleMessage: L1_SURVIVAL_SCREEN: bundle == null.  Skip processing");
                                break;
                            }
                            int sequence1 = b.getInt(PB_BUNDLE_MSG_INT_1, 0);
                            int sequence2 = b.getInt(PB_BUNDLE_MSG_INT_2, 0);

                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                tipsPagerFragment = new TipsScreenPagerFragment();
                                Bundle args = new Bundle();
                                args.putInt(PB_BUNDLE_MSG_INT_1, sequence1);
                                args.putInt(PB_BUNDLE_MSG_INT_2, sequence2);
                                tipsPagerFragment.setArguments(args);
                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .add(R.id.container, tipsPagerFragment)
                                        .addToBackStack(null).commit();

                            }

                            setL1State(L1_TIPS_SCREEN);
                            break;
                        case INP_BACK_PRESSED:
                            survivalFragmentEnded=true;
                            SurvivalPlayScreenFragment.stopSurvivalTimer();
                            survivalTimeRemain = quizTimerSeconds;
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_SURVIVAL_END_OF_ROUND_SCREEN:
                    switch (msg.what) {
                        case INP_SURVIVAL_EOR_SCREEN_CONTINUE_BUTTON_PRESSED:
                            /*if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_SURVIVAL_EOR_SCREEN_CONTINUE_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                currentPlayRound++;
                                //currentImageCount = 1;
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container,
                                                NewRoundScreenFragment.newInstance((int) PB_MODE_SURVIVAL))
                                        .commit();
                                setL1State(L1_SURVIVAL_NEW_ROUND_SCREEN);
                            //}
                            break;
                        case INP_SURVIVAL_EOR_SCREEN_REVIEW_BUTTON_PRESSED:
                            // reviewImageIndex = currentImageCount - 1;

                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                reviewPagerFragment = new ReviewScreenPagerFragment();

                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container, reviewPagerFragment, TAG_REVIEW_PAGER)
                                        .commit();

                            }

                            setL1State(L1_REVIEW_SCREEN);
                            break;
                        case INP_SURVIVAL_EOR_SCREEN_SHARE_BUTTON_PRESSED:
                            startShareDialogue();
                            break;
                        case INP_BACK_PRESSED:

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_SURVIVAL_END_OF_GAME_SCREEN:
                    switch (msg.what) {
                        case INP_SURVIVAL_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED:
                            SurvivalGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_GAMES_PLAYED, ++cumulativeSurvivalGamesPreference);
                            SurvivalRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_ROUNDS_PLAYED, ++cumulativeSurvivalRoundsPreference);
                            /*if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_SURVIVAL_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                currentPlayRound++;
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container,
                                                NewRoundScreenFragment.newInstance((int) PB_MODE_SURVIVAL))
                                        .commit();
                                setL1State(L1_SURVIVAL_NEW_ROUND_SCREEN);
                                survivalModeInit();
                            //}
                            break;
                        case INP_SURVIVAL_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED:
                            SurvivalGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_GAMES_PLAYED, ++cumulativeSurvivalGamesPreference);
                            SurvivalRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_ROUNDS_PLAYED, ++cumulativeSurvivalRoundsPreference);
                            sendAnalyticsEvent("Exit", "SurvivalExit", "ExitAtEndOfGame");
                            /*if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_SURVIVAL_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container,
                                                StartScreenFragment.newInstance(1))
                                        .commit();
                                setL1State(L1_S1_START_SCREEN);
                            //}
                            break;
                        case INP_SURVIVAL_EOG_SCREEN_REVIEW_BUTTON_PRESSED:
                            // reviewImageIndex = currentImageCount - 1;
                            endOfGameReviewFlag=true;
                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                reviewPagerFragment = new ReviewScreenPagerFragment();

                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .replace(R.id.container, reviewPagerFragment, TAG_REVIEW_PAGER)
                                        .addToBackStack(null).commit();

                            }

                            setL1State(L1_REVIEW_SCREEN);
                            break;
                        case INP_BACK_PRESSED:
                            SurvivalGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_GAMES_PLAYED, ++cumulativeSurvivalGamesPreference);
                            SurvivalRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_SURVIVAL_ROUNDS_PLAYED, ++cumulativeSurvivalRoundsPreference);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_CLASSIC_START_GAME_SCREEN:
                    switch (msg.what) {
                        case INP_CLASSIC_SOG_SCREEN_START_BUTTON_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                    .replace(R.id.container,
                                            NewRoundScreenFragment.newInstance((int)PB_MODE_BASIC))
                                    .commit();
                            setL1State(L1_S5_CLASSIC_NEW_ROUND_SCREEN);
                            classicModeInit();
                            break;
                        case INP_CLASSIC_START_GAME_SCREEN_INFO_HINT:
                            FragmentManager fm = getSupportFragmentManager();
                            ClassicStartPlayDialogFragment dialogFrag = new ClassicStartPlayDialogFragment();
                            dialogFrag.show(fm, "Hint Fragment");
                            break;
                        case INP_CLASSIC_TIPS_DURING_PLAY_HINT:
                            fm = getSupportFragmentManager();
                            ClassicTipsInPlayDialogFragment tipDialogFrag = new ClassicTipsInPlayDialogFragment();
                            tipDialogFrag.show(fm, "Hint Fragment");
                            break;

                        case INP_BACK_PRESSED:
                            sendAnalyticsEvent("Exit", "ClassicExit", "ExitAtStartOfGame");
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_S5_CLASSIC_NEW_ROUND_SCREEN:
                    switch (msg.what) {
                        case INP_NEW_ROUND_SCREEN_DONE:
                            //recycleFragments();
                             getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                            ClassicPlayScreenFragment.newInstance((int)PB_MODE_BASIC),TAG_CLASSIC_PLAY)
                                    .commit();
                            setL1State(L1_S4_CLASSIC_PLAY_SCREEN);
                            break;

                        case INP_BACK_PRESSED:
                            break;
                        default:
                            break;
                    }
                    break;

                case L1_S4_CLASSIC_PLAY_SCREEN:
                    //recycleFragments();
                    System.gc();
                    switch (msg.what) {
                        case INP_CLASSIC_IMAGE_WON:
                        case INP_CLASSIC_IMAGE_LOST:
                            if ((currentImageCount % IMAGES_PER_ROUND_CLASSIC) == 0) {
                                int round = currentImageCount/IMAGES_PER_ROUND_CLASSIC;
                                int level = ((round%ROUNDS_PER_LEVEL)==0)?(round/ROUNDS_PER_LEVEL):(round/ROUNDS_PER_LEVEL)+1;
                                if(ClassicPlayScreenFragment.isGameOver(round,currentScore)){
                                    if (pBudDBAdapter.insertScore(PB_USER_NAME, PB_MODE_BASIC, currentScore, 0) < 0) {
                                        Log.e(TAG,
                                                "displayClassicResults: insertScore() failed. currentScore="
                                                        + currentScore);
                                    }
                                    if (currentScore >= classicMinScorePerLevel[level]){
                                        currentClassicLevel++;
                                        getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                                .replace(R.id.container,
                                                        ClassicEndOfGameScreenFragment.newInstance(CLASSIC_EOG_UP_LEVEL))
                                                .commit();
                                    } else {
                                        getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                                .replace(R.id.container,
                                                        ClassicEndOfGameScreenFragment.newInstance(CLASSIC_EOG_STAY_LEVEL))
                                                .commit();
                                    }
                                    currentImageCount++;
                                    setL1State(L1_CLASSIC_END_OF_GAME_SCREEN);
                                    break;

                                }

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                ClassicEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_CLASSIC_END_OF_ROUND_SCREEN);


                            } else {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                ClassicPlayScreenFragment
                                                        .newInstance(1),TAG_CLASSIC_PLAY).commit();
                                setL1State(L1_S4_CLASSIC_PLAY_SCREEN);

                            }
                            totalQuestions++;
                            currentImageCount++;
                            break;


                        case INP_CLASSIC_SHOW_TIPS:
                            Bundle b = msg.getData();
                            if (b == null) {
                                Log.e(TAG,
                                        "handleMessage: L1_CLASSIC_SCREEN: bundle == null.  Skip processing");
                                break;
                            }
                            int sequence1 = b.getInt(PB_BUNDLE_MSG_INT_1, 0);
                            int sequence2 = b.getInt(PB_BUNDLE_MSG_INT_2, 0);

                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                tipsPagerFragment = new TipsScreenPagerFragment();
                                Bundle args = new Bundle();
                                args.putInt(PB_BUNDLE_MSG_INT_1, sequence1);
                                args.putInt(PB_BUNDLE_MSG_INT_2, sequence2);
                                tipsPagerFragment.setArguments(args);
                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .add(R.id.container, tipsPagerFragment)
                                        .addToBackStack(null).commit();

                            }

                            setL1State(L1_TIPS_SCREEN);
                            break;

                        case INP_CLASSIC_GAME_OVER:

                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_CLASSIC_RESULT_POSITIVE_SCREEN:
                case L1_CLASSIC_RESULT_NEGATIVE_SCREEN:
                    switch (msg.what) {
                        case INP_CLASSIC_POSITIVE_RESULT_SCREEN_DONE:
                        case INP_CLASSIC_NEGATIVE_RESULT_SCREEN_DONE:
                            if ((currentImageCount % IMAGES_PER_ROUND_CLASSIC) == 0) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                ClassicEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_CLASSIC_END_OF_ROUND_SCREEN);
                            } else {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                ClassicPlayScreenFragment
                                                        .newInstance(1),TAG_CLASSIC_PLAY).commit();
                                setL1State(L1_S4_CLASSIC_PLAY_SCREEN);
                            }
                            totalQuestions++;
                            currentImageCount++;
                            break;
                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_CLASSIC_END_OF_ROUND_SCREEN:
                    switch (msg.what) {
                        case INP_CLASSIC_EOR_SCREEN_CONTINUE_BUTTON_PRESSED:
                            /*if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_CLASSIC_EOR_SCREEN_CONTINUE_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                currentPlayRound++;
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                        .replace(R.id.container,
                                                NewRoundScreenFragment.newInstance((int)PB_MODE_BASIC))
                                        .commit();
                                setL1State(L1_S5_CLASSIC_NEW_ROUND_SCREEN);
                            //}

                            break;
                        case INP_CLASSIC_EOR_SCREEN_REVIEW_BUTTON_PRESSED:
                            // reviewImageIndex = currentImageCount - 1;

                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                reviewPagerFragment = new ReviewScreenPagerFragment();

                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container, reviewPagerFragment, TAG_REVIEW_PAGER)
                                        .commit();
                                        //.addToBackStack(null).commit();

                            }

                            setL1State(L1_REVIEW_SCREEN);
                            break;
                        case INP_CLASSIC_EOR_SCREEN_SHARE_BUTTON_PRESSED:
                            startShareDialogue();
                            break;
                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            QuitPlayScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_QUIT_PLAY_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_CLASSIC_END_OF_GAME_SCREEN:
                    switch (msg.what) {
                        case INP_CLASSIC_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED:
                            ClassicGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_GAMES_PLAYED, ++cumulativeClassicGamesPreference);
                            ClassicRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_ROUNDS_PLAYED, ++cumulativeClassicRoundsPreference);
                            /*if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {

                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_CLASSIC_EOG_SCREEN_PLAY_AGAIN_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .replace(R.id.container,
                                                ClassicStartGameScreenFragment.newInstance(1))
                                        .commit();
                                setL1State(L1_CLASSIC_START_GAME_SCREEN);
                            //}

                            break;
                        case INP_CLASSIC_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED:
                            ClassicGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_GAMES_PLAYED, ++cumulativeClassicGamesPreference);
                            ClassicRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_ROUNDS_PLAYED, ++cumulativeClassicRoundsPreference);
                            sendAnalyticsEvent("Exit", "ClassicExit", "ExitAtEndOfGame");
                            showRatingDialog();
                           /* if (adsOnOffPreference && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.container,
                                                BlankScreenFragment.newInstance(1))
                                        .commit();
                                setInterstitialState(INP_CLASSIC_EOG_SCREEN_QUIT_GAME_BUTTON_PRESSED);
                            } else {*/
                                //loadInterstitial();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout, R.anim.zoomin, R.anim.zoomout)
                                        .replace(R.id.container,
                                                StartScreenFragment.newInstance(1))
                                        .commit();
                                setL1State(L1_S1_START_SCREEN);
                            //}
                            break;
                        case INP_CLASSIC_EOG_SCREEN_REVIEW_BUTTON_PRESSED:
                            // reviewImageIndex = currentImageCount - 1;
                            endOfGameReviewFlag=true;
                            if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                reviewPagerFragment = new ReviewScreenPagerFragment();

                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .replace(R.id.container, reviewPagerFragment, TAG_REVIEW_PAGER)
                                        .addToBackStack(null).commit();

                                // .replace(android.R.id.content, pagerFragment)
                            }

                            setL1State(L1_REVIEW_SCREEN);
                            break;

                        case INP_BACK_PRESSED:
                            ClassicGamesCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_GAMES_PLAYED, ++cumulativeClassicGamesPreference);
                            ClassicRoundsCount++;
                            savePreferenceInt(PREF_STR_CUM_CLASSIC_ROUNDS_PLAYED, ++cumulativeClassicRoundsPreference);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_REVIEW_SCREEN:
                    System.gc();
                    switch (msg.what) {
                        case INP_REVIEW_SCREEN_IMAGE_PRESSED:
                            Bundle b = msg.getData();
                            if (b == null) {
                                Log.e(TAG,
                                        "handleMessage: L1_REVIEW_SCREEN: bundle == null.  Skip processing");
                                break;
                            }
                            int sequence1 = b.getInt(PB_BUNDLE_MSG_INT_1, 0);
                            int sequence2 = b.getInt(PB_BUNDLE_MSG_INT_2, 0);

                           if (getSupportFragmentManager().findFragmentById(
                                    android.R.id.content) == null) {
                                tipsPagerFragment = new TipsScreenPagerFragment();
                                Bundle args = new Bundle();
                                args.putInt(PB_BUNDLE_MSG_INT_1, sequence1);
                                args.putInt(PB_BUNDLE_MSG_INT_2, sequence2);
                                tipsPagerFragment.setArguments(args);
                                getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                        .add(R.id.container, tipsPagerFragment)
                                        .addToBackStack(null).commit();

                            }

                            setL1State(L1_TIPS_SCREEN);
                            break;

                        case INP_BACK_PRESSED:
                            FragmentManager fm = getSupportFragmentManager();
                            if (currentMode == PB_MODE_BASIC) {
                                if(endOfGameReviewFlag){
                                    setL1State(L1_CLASSIC_END_OF_GAME_SCREEN);
                                    fm.popBackStack();
                                    endOfGameReviewFlag=false;
                                    break;
                              }
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                ClassicEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_CLASSIC_END_OF_ROUND_SCREEN);
                            } else if (currentMode == PB_MODE_SURVIVAL) {
                                if(endOfGameReviewFlag){
                                    setL1State(L1_SURVIVAL_END_OF_GAME_SCREEN);
                                    fm.popBackStack();
                                    endOfGameReviewFlag=false;
                                    break;
                                }
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                SurvivalEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_SURVIVAL_END_OF_ROUND_SCREEN);
                            }
                            //recycleFragments();
                             break;
                        case INP_REVIEW_SCREEN_REVIEW_HINT:
                            fm = getSupportFragmentManager();
                            ReviewDialogFragment dialogFrag = new ReviewDialogFragment();
                            dialogFrag.show(fm, "Hint Fragment");
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_TIPS_SCREEN:
                    switch (msg.what) {
                        case INP_TIPS_SCREEN_START_IAP:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .add(
                                            R.id.container,
                                            StoreScreenFragment.newInstance(1))
                                    .addToBackStack(null)
                                    .commit();
                            setL1State(L1_STORE_SCREEN);
                            break;

                        case INP_TIPS_SCREEN_FAVORITES_LIMIT_DIALOGUE:
                            FragmentManager fm = getSupportFragmentManager();
                            FavoriteLimitDialogFragment dialogFrag = new FavoriteLimitDialogFragment();
                            dialogFrag.show(fm, "Alert Dialog Fragment");
                            sendAnalyticsEvent("Action","ShowFavoriteLimitExceeded");
                            break;
                        case INP_TIPS_SCREEN_TIPS_HINT:
                            fm = getSupportFragmentManager();
                            TipsDialogFragment dialog = new TipsDialogFragment();
                            dialog.show(fm, "Hint Fragment");
                            break;
                        case INP_BACK_PRESSED:

                            if(getPreviousL1State() == L1_S4_CLASSIC_PLAY_SCREEN) {
                                setL1State(getPreviousL1State());
                                fm = getSupportFragmentManager();
                                fm.popBackStack();
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                            sendStateMachineMsg(INP_CLASSIC_IMAGE_WON);
                                        Log.i(TAG, "TIPS_SCREEN: INP_CLASSIC_IMAGE_WON sent");
                                    }
                                };
                                handler.postDelayed(runnable, 1200);
                                break;
                            }
                            if(getPreviousL1State() == L1_SURVIVAL_PLAY_SCREEN) {
                                setL1State(getPreviousL1State());
                                fm = getSupportFragmentManager();
                                fm.popBackStack();
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if(survivalLivesRemain==0){
                                            sendStateMachineMsg(INP_SURVIVAL_GAME_OVER);
                                            return;
                                        }
                                        sendStateMachineMsg(INP_SURVIVAL_IMAGE_WON);
                                        Log.i(TAG, "TIPS_SCREEN: INP_SURVIVAL_IMAGE_WON sent");
                                    }
                                };
                                handler.postDelayed(runnable, 1200);
                                break;
                            }
                            if(getPreviousL1State() == L1_FAVORITES_SCREEN) {
                                setL1State(getPreviousL1State());
                                if (getSupportFragmentManager().findFragmentById(
                                        android.R.id.content) == null) {
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                            .replace(
                                                    R.id.container,
                                                    FavoritesGridScreenFragment
                                                            .newInstance(1)).commit();
                                }
                                break;
                            }
                            if(getPreviousL1State() == L1_STORE_SCREEN)
                                setL1State(L1_REVIEW_SCREEN);

                            else
                                setL1State(getPreviousL1State());
                            fm = getSupportFragmentManager();
                            fm.popBackStack();
                            imageClicked=false;
                            break;
                        default:
                            break;
                    }
                    break;

                case L1_FAVORITES_SCREEN:
                    switch (msg.what) {
                        case INP_FAVORITES_SCREEN_IMAGE_PRESSED:
                            Bundle b = msg.getData();
                            if (b == null) {
                                Log.e(TAG,
                                        "handleMessage: L1_REVIEW_SCREEN: bundle == null.  Skip processing");
                                break;
                            }
                            int sequenceNumber = b.getInt(PB_BUNDLE_MSG_INT_1, 0);

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout,R.anim.zoomin, R.anim.zoomout )
                                    .replace(
                                            R.id.container,
                                            TipsFavoritesScreenFragment.newInstance(sequenceNumber))
                                    .commit();
                            setL1State(L1_TIPS_SCREEN);
                            break;

                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_AWARDS_SCREEN:
                    switch (msg.what) {
                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_HIGH_SCORES_SCREEN:
                    switch (msg.what) {
                        case INP_BACK_PRESSED:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_QUIT_PLAY_SCREEN:
                    switch (msg.what) {
                        case INP_BACK_PRESSED:
                        case INP_QUIT_PLAY_SCREEN_CONTINUE_BUTTON_PRESSED:
                            if (previousL1State == L1_S4_CLASSIC_PLAY_SCREEN) {
                                if(!imageClicked)
                                    classicGroupCount--;  // bit spaghetti
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container,
                                                ClassicPlayScreenFragment.newInstance(1),TAG_CLASSIC_PLAY)
                                        .commit();

                                setL1State(L1_S4_CLASSIC_PLAY_SCREEN);
                            } else if (previousL1State == L1_SURVIVAL_PLAY_SCREEN) {
                                survivalHighImageCount--;
                                survivalLowImageCount--;
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container,
                                                    SurvivalPlayScreenFragment.newInstance(1))
                                            .commit();
                                    setL1State(L1_SURVIVAL_PLAY_SCREEN);
                            } else if (previousL1State == L1_CLASSIC_END_OF_ROUND_SCREEN) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                ClassicEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_CLASSIC_END_OF_ROUND_SCREEN);
                            } else if (previousL1State == L1_SURVIVAL_END_OF_ROUND_SCREEN) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                SurvivalEndOfRoundScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_SURVIVAL_END_OF_ROUND_SCREEN);
                            } else  if (previousL1State == L1_S1_START_SCREEN){
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                StartScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_S1_START_SCREEN);
                            }
                            break;
                        case INP_QUIT_PLAY_SCREEN_QUIT_BUTTON_PRESSED:
                            if (previousL1State == L1_S1_START_SCREEN){
                                savePreferenceInt(PREF_STR_CUM_SESSIONS_PLAYED, ++cumulativeSessionsPreference);
                                sendAnalyticsEvent("Exit", "AppExit", "ExitAtStartScreen");
                                sendAnalyticsEvent("Session", "SessionsCumulative", "SessionsCumulative",cumulativeSessionsPreference);
                                sendAnalyticsEvent("Session", "ClassicGamesCumulative", "ClassicGamesCumulative", cumulativeClassicGamesPreference);
                                sendAnalyticsEvent("Session", "ClassicRoundsCumulative", "ClassicRoundsCumulative", cumulativeClassicRoundsPreference);
                                sendAnalyticsEvent("Session", "SurvivalGamesCumulative", "SurvivalGamesCumulative", cumulativeSurvivalGamesPreference);
                                sendAnalyticsEvent("Session", "SurvivalRoundsCumulative", "SurvivalRoundsCumulative", cumulativeSurvivalRoundsPreference);
                                sendAnalyticsEvent("Session", "ClassicGamesPerSession", "ClassicGamesPerSession", ClassicGamesCount);
                                sendAnalyticsEvent("Session", "ClassicRoundsPerSession", "ClassicRoundsPerSession", ClassicRoundsCount);
                                sendAnalyticsEvent("Session", "SurvivalGamesPerSession", "SurvivalGamesPerSession", SurvivalGamesCount);
                                sendAnalyticsEvent("Session", "SurvivalRoundsPerSession", "SurvivalRoundsPerSession", SurvivalRoundsCount);
                                exitApp();
                            }

                            if (previousL1State == L1_S4_CLASSIC_PLAY_SCREEN) {
                                ClassicGamesCount++;
                                savePreferenceInt(PREF_STR_CUM_CLASSIC_GAMES_PLAYED, ++cumulativeClassicGamesPreference);
                                sendAnalyticsEvent("Exit", "ClassicExit", "ExitAtPlay");
                            } else if (previousL1State == L1_SURVIVAL_PLAY_SCREEN) {
                                SurvivalGamesCount++;
                                savePreferenceInt(PREF_STR_CUM_SURVIVAL_GAMES_PLAYED, ++cumulativeSurvivalGamesPreference);
                                sendAnalyticsEvent("Exit", "SurvivalExit", "ExitAtPlay");
                            } else if (previousL1State == L1_CLASSIC_END_OF_ROUND_SCREEN) {
                                ClassicGamesCount++;
                                savePreferenceInt(PREF_STR_CUM_CLASSIC_GAMES_PLAYED, ++cumulativeClassicGamesPreference);
                                sendAnalyticsEvent("Exit", "ClassicExit", "ExitAtEndOfRound");
                            } else if (previousL1State == L1_SURVIVAL_END_OF_ROUND_SCREEN) {
                                SurvivalGamesCount++;
                                savePreferenceInt(PREF_STR_CUM_SURVIVAL_GAMES_PLAYED, ++cumulativeSurvivalGamesPreference);
                                sendAnalyticsEvent("Exit", "SurvivalExit", "ExitAtEndOfRound");
                            }

                            if (currentMode == PB_MODE_BASIC) {
                                sendAnalyticsEvent("Score", "ClassicFinalPoints", "ClassicFinalPoints", currentScore);
                                sendAnalyticsEvent("Score", "ClassicFinalRounds", "ClassicFinalRounds", currentPlayRound);
                                sendAnalyticsEvent("Score", "ClassicFinalLevel", "ClassicFinalLevel", currentClassicLevel);
                                if (pBudDBAdapter.insertScore(PB_USER_NAME, PB_MODE_BASIC, currentScore, 0) < 0) {
                                    Log.e(TAG,
                                            "displaySurvivalResults: insertScore() failed. currentScore="
                                                    + currentScore);
                                }

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                        .replace(R.id.container,
                                                StartScreenFragment.newInstance(1))
                                        .commit();
                                setL1State(L1_S1_START_SCREEN);
                            } else if (currentMode == PB_MODE_SURVIVAL) {
                                sendAnalyticsEvent("Score", "SurvivalFinalPoints", "SurvivalFinalPoints", currentScore);
                                sendAnalyticsEvent("Score", "SurvivalFinalRounds", "SurvivalFinalRounds", currentPlayRound);
                                if (pBudDBAdapter.insertScore(PB_USER_NAME, PB_MODE_SURVIVAL, currentScore, 0) < 0) {
                                    Log.e(TAG,
                                            "displaySurvivalResults: insertScore() failed. currentScore="
                                                    + currentScore);
                                }
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                        .replace(
                                                R.id.container,
                                                StartScreenFragment
                                                        .newInstance(1)).commit();
                                setL1State(L1_S1_START_SCREEN);

                            } else{
                                Log.e(TAG, "INP_QUIT_PLAY_SCREEN_QUIT_BUTTON_PRESSED: currentMode not valid ");
                                /*Toast.makeText(
                                        getApplicationContext(),
                                        "INP_QUIT_PLAY_SCREEN_QUIT_BUTTON_PRESSED: currentMode not valid "
                                             , Toast.LENGTH_LONG)
                                        .show();*/
                            }


                            break;
                      default:
                            break;
                    }
                    break;
                case L1_STORE_SCREEN:
                    switch (msg.what) {
                        //case INP_STORE_SCREEN_PURCHASE_DONE:
                            /*getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            StoreScreenFragment
                                                    .newInstance(1))
                                    .commit();
                            setL1State(getPreviousL1State());
                            setL1State(L1_STORE_SCREEN);*/

                            /*setL1State(getPreviousL1State());
                            FragmentManager fm = getSupportFragmentManager();
                            fm.popBackStack();*/
                            //break;
                        case INP_BACK_PRESSED:
                            setL1State(getPreviousL1State());
                            FragmentManager fm = getSupportFragmentManager();
                            fm.popBackStack();
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_ABOUT_SCREEN:
                    switch (msg.what) {
                        case INP_BACK_PRESSED:

                           getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin, R.anim.zoomout)
                                    .replace(
                                            R.id.container,
                                            StartScreenFragment
                                                    .newInstance(1)).commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                    break;
                case L1_HOW_TO_SCREEN:
                    switch (msg.what) {
                        case INP_BACK_PRESSED:

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.zoomin,R.anim.zoomout )
                                    .replace(R.id.container,
                                            StartScreenFragment.newInstance(1))
                                    .commit();
                            setL1State(L1_S1_START_SCREEN);
                            break;
                        default:
                            break;
                    }
                default:
                    Log.e(TAG,
                            "onCreate: handleMessage(), L1 Message not defined msgId="
                                    + msg.what);
                    break;

            }
            return true;
        }

    };


    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if(this.detector!=null)
            this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

    }

    @Override
    public void onDoubleTap() {

    }

    private void getCurrentAwardLevels() {
        int awardLevel;
        for (int i = (int) PB_MODE_BASIC; i <= PB_MODE_SCOREBOARD; i++) {

            Cursor cursor = pBudDBAdapter.getAllAwardsCursorByMode(i);

            if (cursor.moveToFirst()) {
                do {
                    awardLevel = (int) cursor.getLong(cursor
                            .getColumnIndex(PBudDBAdapter.KEY_AT_AWARD_LEVEL));
                    if (awardLevel > maxAwardLevelPerMode[i])
                        maxAwardLevelPerMode[i] = awardLevel;
                } while (cursor.moveToNext());
            } else {
                maxAwardLevelPerMode[i] = 0;

            }
            cursor.close();
        }
    }


    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart(): entered.");
       if (preferencesActivityStarted != 0) {
            getPrefs();
            preferencesActivityStarted = 0;
        }
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(): entered.");
        if (preferencesActivityStarted != 0) {
            getPrefs();
            preferencesActivityStarted = 0;
        }
       if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.i(TAG, "onBackPressed(): entered.");

        sendStateMachineMsg(INP_BACK_PRESSED);

        return;
    }

    @Override
    public void onStop() {
        super.onStop();
        activityStopped=true;
        Log.i(TAG, "onStop(): entered.");
    }

    @Override
    public void onResume() {
        super.onResume();
        activityStopped=false;
        Log.i(TAG, "onResume(): entered.");
      if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
        if(newRoundScreenPaused && (L1State == L1_S5_CLASSIC_NEW_ROUND_SCREEN  || L1State == L1_SURVIVAL_NEW_ROUND_SCREEN)){
            sendStateMachineMsg(INP_NEW_ROUND_SCREEN_DONE);
            newRoundScreenPaused=false;
            Log.i(TAG, "onResume(): INP_NEW_ROUND_SCREEN_DONE sent to state machine");
        }
        if(sendINP_CLASSIC_IMAGE_WON){
            sendStateMachineMsg(INP_CLASSIC_IMAGE_WON);
            sendINP_CLASSIC_IMAGE_WON=false;
            Log.i(TAG, "onResume(): INP_CLASSIC_IMAGE_WON sent to state machine");
        }
        if(sendINP_CLASSIC_IMAGE_LOST){
            sendStateMachineMsg(INP_CLASSIC_IMAGE_LOST);
            sendINP_CLASSIC_IMAGE_LOST=false;
            Log.i(TAG, "onResume(): INP_CLASSIC_IMAGE_LOST sent to state machine");
        }
        if(sendINP_SURVIVAL_IMAGE_WON){
            sendStateMachineMsg(INP_SURVIVAL_IMAGE_WON);
            sendINP_SURVIVAL_IMAGE_WON=false;
            Log.i(TAG, "onResume(): INP_SURVIVAL_IMAGE_WON sent to state machine");
        }
        if(sendINP_SURVIVAL_IMAGE_LOST){
            sendStateMachineMsg(INP_SURVIVAL_IMAGE_LOST);
            sendINP_SURVIVAL_IMAGE_LOST=false;
            Log.i(TAG, "onResume(): INP_SURVIVAL_IMAGE_LOST sent to state machine");
        }
        if(sendINP_SURVIVAL_GAME_OVER){
            sendStateMachineMsg(INP_SURVIVAL_GAME_OVER);
            sendINP_SURVIVAL_GAME_OVER=false;
            Log.i(TAG, "onResume(): INP_SURVIVAL_GAME_OVER sent to state machine");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy(): entered.");
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }

    }

    private void exitApp() {
        //System.exit(1);
        finishAffinity();
        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
            //    postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("FunPicks Photo")
                .setContentDescription(
                        "Take better photos!  Play FunPicks!")
                .setContentUrl(Uri.parse("http://funpicksgames.wixsite.com/funpicks"))
                .setImageUrl(Uri.parse("https://static.wixstatic.com/media/486cbe_e58715d3fa6d45dca20b61d299c560bf~mv2.jpg/v1/fill/w_358,h_358,al_c,lg_1,q_80/486cbe_e58715d3fa6d45dca20b61d299c560bf~mv2.jpg"))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }
    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this,
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }


    private boolean startShareDialogue() {
        Log.i(TAG, "startShareDialogue: Entered");

                Intent sendIntent = new Intent();
                //sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailEditTextPreference});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,"I just played FunPicks Photo game");
                //sendIntent.putExtra(Intent.EXTRA_TEXT,fileText);
                    sendIntent.setAction(Intent.ACTION_SEND );
                    sendIntent.setType("text/plain");
                    //sendIntent.putExtra(Intent.EXTRA_STREAM,outputFileUri);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "\nPhotography made fun and easy.\nTry FunPicks Photo game!\nhttps://play.google.com/store/apps/details?id=com.fun_picks.fpphoto");

        startActivityForResult(Intent.createChooser(sendIntent, "Sharing:"), 0x99);
                //Intent sendIntent = new Intent(Intent.ACTION_SEND);

                //Intent sendIntent = new Intent(Intent.ACTION_SEND );
                //sendIntent.setType("image/jpeg");
                //sendIntent.setType("text/html");
                //sendIntent.setType("text/plain");
                //sendIntent.setType("multipart/mixed");
                //startActivity(sendIntent);
                //sendIntent.putExtra(Intent.EXTRA_STREAM,outputFileUri);
                //sendIntent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml("Your Membuddy list is in attached file."));


                //Toast.makeText(this,"Activating Email Sender",Toast.LENGTH_LONG).show();


        return true;
    }

    private static void sendAnalyticsEvent(String category, String action){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    private static void sendAnalyticsEvent(String category, String action, String label){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    private static void sendAnalyticsEvent(String category, String action, String label, int value){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    private static void sendAnalyticsScreen(String screen){
        Log.i(TAG, "mTracker screen name: " + screen);
        mTracker.setScreenName("Screen~" + screen);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private static void savePreferenceInt(String pref, int value){
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(baseContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(pref, value);
        editor.commit();

    }

    private static void savePreferenceLong(String pref, long value){
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(baseContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(pref, value);
        editor.commit();

    }


    private static void savePreferenceBool(String pref, boolean value){
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(baseContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(pref, value);
        editor.commit();

    }

    private static final String TAG_CLASSIC_PLAY="classicPlayFragment";
    private static final String TAG_REVIEW_PAGER="reviewPagerFragment";


    private static final String PREF_STR_RATER_LAST_PROMPT="raterLastPrompt";
    private static final String PREF_STR_RATE_ME="rateMe";
    private static boolean rateMePref=true;


    private void showRatingDialog(){
        final int DAYS_UNTIL_PROMPT = 2;
        final int MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
        //final int MILLIS_UNTIL_PROMPT = 0;

        final int RATER_MIN_CLASSIC_ROUNDS = 25;
        final int RATER_MIN_CLASSIC_GAMES = 2;

        if(cumulativeClassicRoundsPreference <= RATER_MIN_CLASSIC_ROUNDS  || cumulativeClassicGamesPreference <= RATER_MIN_CLASSIC_GAMES)
            return;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();

        rateMePref = prefs.getBoolean(PREF_STR_RATE_ME, true);
        if(!rateMePref)
            return;

        long currentTime = System.currentTimeMillis();
        long lastPromptTime = prefs.getLong(PREF_STR_RATER_LAST_PROMPT, 0);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
            editor.putLong(PREF_STR_RATER_LAST_PROMPT, lastPromptTime);
            editor.commit();
        }

        if (currentTime < (lastPromptTime + MILLIS_UNTIL_PROMPT)) {
            return;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("If you enjoy this app and think others may find it fun and useful, please take a moment to rate it.  Thank you very much for your support!").append("\n");
        builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), 0);
        new AlertDialog.Builder(mContext)
                .setTitle("Please Help Us")
                .setMessage(builder)
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rateMePref=false;
                        savePreferenceBool(PREF_STR_RATE_ME, rateMePref);
                        sendAnalyticsEvent("Action","RateMeNoThanks");

                    }
                })
                .setNeutralButton("Remind Me Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long currentTime = System.currentTimeMillis();
                        savePreferenceLong(PREF_STR_RATER_LAST_PROMPT, currentTime);
                        rateMePref=true;
                        savePreferenceBool(PREF_STR_RATE_ME, rateMePref);
                        sendAnalyticsEvent("Action","RateMeRemindLater");

                    }
                })
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                        }
                        rateMePref=false;
                        savePreferenceBool(PREF_STR_RATE_ME, rateMePref);
                        sendAnalyticsEvent("Action","RateMeRateNow");

                    }
                })
                .create()
                .show();
    }



}
