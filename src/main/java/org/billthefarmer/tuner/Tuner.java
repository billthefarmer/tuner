////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.tuner;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

// Main Activity
public class Tuner extends Activity
    implements View.OnClickListener, View.OnLongClickListener,
    PopupMenu.OnMenuItemClickListener
{
    private static final String TAG = "Tuner";

    public static final String PREF_ABOUT = "pref_about";
    public static final String PREF_BACH = "pref_bach";
    public static final String PREF_COLOUR = "pref_colour";
    public static final String PREF_CUSTOM = "pref_custom";
    public static final String PREF_DOWN = "pref_down";
    public static final String PREF_FILTER = "pref_filter";
    public static final String PREF_FILTERS = "pref_filters";
    public static final String PREF_FUND = "pref_fund";
    public static final String PREF_INPUT = "pref_input";
    public static final String PREF_KEY = "pref_key";
    public static final String PREF_MULT = "pref_mult";
    public static final String PREF_NOTE = "pref_note";
    public static final String PREF_OCTAVE = "pref_octave";
    public static final String PREF_PROPS = "pref_props";
    public static final String PREF_REFER = "pref_refer";
    public static final String PREF_SCREEN = "pref_screen";
    public static final String PREF_SOLFA = "pref_solfa";
    public static final String PREF_STROBE = "pref_strobe";
    public static final String PREF_TEMPER = "pref_temper";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_TRANSPOSE = "pref_transpose";
    public static final String PREF_ZOOM = "pref_zoom";

    private static final String SHOW_STAFF = "show_staff";

    private static final int REQUEST_PERMISSIONS = 1;

    public static final int VERSION_CODE_S_V2 = 32;

    // Note values for display
    private static final String notes[] =
    {
        "C", "C", "D", "E", "E", "F",
        "F", "G", "A", "A", "B", "B"
    };

    private static final String sharps[] =
    {
        "", "\u266F", "", "\u266D", "", "",
        "\u266F", "", "\u266D", "", "\u266D", ""
    };

    private static final String CLIP_FORMAT =
        "%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n";


    // Temperaments
    private static final double temperament_values[][] =
    {
        // Kirnberger II
        {1.000000000, 1.053497163, 1.125000000, 1.185185185,
         1.250000000, 1.333333333, 1.406250000, 1.500000000,
         1.580245745, 1.677050983, 1.777777778, 1.875000000},

        // Kirnberger III
        {1.000000000, 1.053497163, 1.118033989, 1.185185185,
         1.250000000, 1.333333333, 1.406250000, 1.495348781,
         1.580245745, 1.671850762, 1.777777778, 1.875000000},

        // Werckmeister III
        {1.000000000, 1.053497942, 1.117403309, 1.185185185,
         1.252827249, 1.333333333, 1.404663923, 1.494926960,
         1.580246914, 1.670436332, 1.777777778, 1.879240873},

        // Werckmeister IV
        {1.000000000, 1.048750012, 1.119929822, 1.185185185,
         1.254242806, 1.333333333, 1.404663923, 1.493239763,
         1.573125018, 1.672323742, 1.785826183, 1.872885231},

        // Werckmeister V
        {1.000000000, 1.057072991, 1.125000000, 1.189207115,
         1.257078722, 1.337858004, 1.414213562, 1.500000000,
         1.580246914, 1.681792831, 1.783810673, 1.885618083},

        // Werckmeister VI
        {1.000000000, 1.053497942, 1.114163307, 1.187481762,
         1.255862545, 1.333333333, 1.410112936, 1.497099016,
         1.580246914, 1.674483394, 1.781222643, 1.883793818},

        // Bach (Klais)
        {262.76, 276.87, 294.30, 311.46, 328.70, 350.37,
         369.18, 393.70, 415.30, 440.00, 467.18, 492.26},

        // Just (Barbour)
        {264.00, 275.00, 297.00, 316.80, 330.00, 352.00,
         371.25, 396.00, 412.50, 440.00, 475.20, 495.00},

        // Equal
        {1.000000000, 1.059463094, 1.122462048, 1.189207115,
         1.259921050, 1.334839854, 1.414213562, 1.498307077,
         1.587401052, 1.681792831, 1.781797436, 1.887748625},

        // Pythagorean
        {1.000000000, 1.067871094, 1.125000000, 1.185185185,
         1.265625000, 1.333333333, 1.423828125, 1.500000000,
         1.601806641, 1.687500000, 1.777777778, 1.898437500},

        // Van Zwolle
        {1.000000000, 1.053497942, 1.125000000, 1.185185185,
         1.265625000, 1.333333333, 1.404663923, 1.500000000,
         1.580246914, 1.687500000, 1.777777778, 1.898437500},

        // Meantone (-1/4)
        {1.000000000, 1.044906727, 1.118033989, 1.196279025,
         1.250000000, 1.337480610, 1.397542486, 1.495348781,
         1.562500000, 1.671850762, 1.788854382, 1.869185977},

        // Silbermann (-1/6)
        {1.000000000, 1.052506113, 1.120351187, 1.192569588,
         1.255186781, 1.336096753, 1.406250000, 1.496897583,
         1.575493856, 1.677050983, 1.785154534, 1.878886059},

        // Salinas (-1/3)
        {1.000000000, 1.037362210, 1.115721583, 1.200000000,
         1.244834652, 1.338865900, 1.388888889, 1.493801582,
         1.549613310, 1.666666667, 1.792561899, 1.859535972},

        // Zarlino (-2/7)
        {1.000000000, 1.041666667, 1.117042372, 1.197872314,
         1.247783660, 1.338074130, 1.393827219, 1.494685500,
         1.556964062, 1.669627036, 1.790442378, 1.865044144},

        // Rossi (-1/5)
        {1.000000000, 1.049459749, 1.119423732, 1.194051981,
         1.253109491, 1.336650124, 1.402760503, 1.496277870,
         1.570283397, 1.674968957, 1.786633554, 1.875000000},

        // Rossi (-2/9)
        {1.000000000, 1.047433739, 1.118805855, 1.195041266,
         1.251726541, 1.337019165, 1.400438983, 1.495864870,
         1.566819334, 1.673582375, 1.787620248, 1.872413760},

        // Rameau (-1/4)
        {1.000000000, 1.051417112, 1.118033989, 1.179066456,
         1.250000000, 1.337480610, 1.401889482, 1.495348781,
         1.577125668, 1.671850762, 1.775938357, 1.869185977},

        // Kellner
        {1.000000000, 1.053497942, 1.118918532, 1.185185185,
         1.251978681, 1.333333333, 1.404663923, 1.495940194,
         1.580246914, 1.673835206, 1.777777778, 1.877968022},

        // Vallotti
        {1.000000000, 1.055879962, 1.119929822, 1.187864958,
         1.254242806, 1.336348077, 1.407839950, 1.496616064,
         1.583819943, 1.676104963, 1.781797436, 1.877119933},

        // Young II
        {1.000000000, 1.053497942, 1.119929822, 1.185185185,
         1.254242806, 1.333333333, 1.404663923, 1.496616064,
         1.580246914, 1.676104963, 1.777777778, 1.877119933},

        // Bendeler III
        {1.000000000, 1.057072991, 1.117403309, 1.185185185,
         1.257078722, 1.333333333, 1.409430655, 1.494926960,
         1.585609487, 1.676104963, 1.777777778, 1.879240873},

        // Neidhardt I
        {1.000000000, 1.055879962, 1.119929822, 1.186524315,
         1.254242806, 1.333333333, 1.407839950, 1.496616064,
         1.583819943, 1.676104963, 1.777777778, 1.879240873},

        // Neidhardt II
        {1.000000000, 1.057072991, 1.119929822, 1.187864958,
         1.255659964, 1.334839854, 1.411023157, 1.496616064,
         1.583819943, 1.676104963, 1.781797436, 1.883489946},

        // Neidhardt III
        {1.000000000, 1.057072991, 1.119929822, 1.187864958,
         1.255659964, 1.333333333, 1.411023157, 1.496616064,
         1.583819943, 1.676104963, 1.779786472, 1.883489946},

        // Bruder 1829
        {1.000000000, 1.056476308, 1.124364975, 1.187194447,
         1.253534828, 1.334086381, 1.409032810, 1.499576590,
         1.583819943, 1.678946488, 1.779786472, 1.879240873},

        // Barnes 1977
        {1.000000000, 1.055879962, 1.119929822, 1.187864958,
         1.254242806, 1.336348077, 1.407839950, 1.496616064,
         1.583819943, 1.676104963, 1.781797436, 1.881364210},

        // Lambert 1774
        {1.000000000, 1.055539344, 1.120652732, 1.187481762,
         1.255862545, 1.335916983, 1.407385792, 1.497099016,
         1.583309016, 1.677728102, 1.781222643, 1.880150581},

        // Schlick (H. Vogel)
        {1.000000000, 1.050646611, 1.118918532, 1.185185185,
         1.251978681, 1.336951843, 1.400862148, 1.495940194,
         1.575969916, 1.673835206, 1.782602458, 1.872885231},

        // Meantone # (-1/4)
        {1.000000000, 1.044906727, 1.118033989, 1.168241235,
         1.250000000, 1.337480610, 1.397542486, 1.495348781,
         1.562500000, 1.671850762, 1.746928107, 1.869185977},

        // Meantone b (-1/4)
        {1.000000000, 1.069984488, 1.118033989, 1.196279025,
         1.250000000, 1.337480610, 1.431083506, 1.495348781,
         1.600000000, 1.671850762, 1.788854382, 1.869185977},

        // Lehman-Bach
        {1.000000000, 1.058267368, 1.119929822, 1.187864958,
         1.254242806, 1.336348077, 1.411023157, 1.496616064,
         1.585609487, 1.676104963, 1.779786472, 1.881364210},
    };

    public final static int LIGHT  = 0;
    public final static int DARK   = 1;
    public final static int SYSTEM = 2;
    public final static int WHITE  = 3;
    public final static int BLACK  = 4;

    private double temperaments[][];
    private String names[];

    private Toolbar toolbar;
    private Spectrum spectrum;
    private Display display;
    private Strobe strobe;
    private Status status;
    private Meter meter;
    private Scope scope;
    private Staff staff;

    private Audio audio;
    private Toast toast;

    private boolean show;

    private int theme;

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        getPreferences();

        Configuration config = getResources().getConfiguration();
        int night = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (theme)
        {
        case LIGHT:
            setTheme(R.style.AppTheme);
            break;

        case DARK:
            setTheme(R.style.AppDarkTheme);
            break;

        case SYSTEM:
            switch (night)
            {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppDarkTheme);
                break;
            }
            break;

        case WHITE:
            setTheme(R.style.AppWhiteTheme);
            break;

        case BLACK:
            setTheme(R.style.AppBlackTheme);
            break;
        }

        setContentView(R.layout.main);

        // Find the views, not all may be present
        spectrum = findViewById(R.id.spectrum);
        display = findViewById(R.id.display);
        strobe = findViewById(R.id.strobe);
        status = findViewById(R.id.status);
        meter = findViewById(R.id.meter);
        scope = findViewById(R.id.scope);
        staff = findViewById(R.id.staff);

        // Add custom view to action bar
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.custom);
        actionBar.setDisplayShowCustomEnabled(true);

        SignalView signal = findViewById(R.id.signal);

        // Find toolbar
        toolbar = findViewById(getResources().getIdentifier("action_bar",
                                                            "id", "android"));
        // Set up navigation
        switch (theme)
        {
        case LIGHT:
        case DARK:
        case BLACK:
        case SYSTEM:
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            break;

        case WHITE:
            toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
            break;
        }
        toolbar.setNavigationOnClickListener((v) ->
        {
            PopupMenu popup = new PopupMenu(this, v);
            popup.inflate(R.menu.navigation);
            popup.setOnMenuItemClickListener(this);
            popup.show();
        });

        // Create audio
        audio = new Audio();

        // Connect views to audio
        if (spectrum != null)
            spectrum.audio = audio;

        if (display != null)
            display.audio = audio;

        if (strobe != null)
            strobe.audio = audio;

        if (status != null)
            status.audio = audio;

        if (signal != null)
            signal.audio = audio;

        if (meter != null)
            meter.audio = audio;

        if (scope != null)
            scope.audio = audio;

        if (staff != null)
            staff.audio = audio;

        // Set up the click listeners
        setClickListeners();
    }

    // Set click listeners
    void setClickListeners()
    {
        // Scope
        if (scope != null)
        {
            scope.setOnClickListener(this);
            scope.setOnLongClickListener(this);
        }

        // Spectrum
        if (spectrum != null)
        {
            spectrum.setOnClickListener(this);
            spectrum.setOnLongClickListener(this);
        }

        // Display
        if (display != null)
        {
            display.setOnClickListener(this);
            display.setOnLongClickListener(this);
        }

        // Strobe
        if (strobe != null)
        {
            strobe.setOnClickListener(this);
            strobe.setOnLongClickListener(this);
        }

        // Staff
        if (staff != null)
        {
            staff.setOnClickListener(this);
            staff.setOnLongClickListener(this);
        }

        // Meter
        if (meter != null)
        {
            meter.setOnClickListener(this);
            meter.setOnLongClickListener(this);
        }
    }

    // On click
    @Override
    public void onClick(View v)
    {
        // Get config
        Configuration config = getResources().getConfiguration();

        // Get id
        int id = v.getId();
        switch (id)
        {
        // Scope
        case R.id.scope:
            audio.filter = !audio.filter;

            if (audio.filter)
                showToast(R.string.filter_on);
            else
                showToast(R.string.filter_off);
            break;

        // Spectrum
        case R.id.spectrum:
            audio.zoom = !audio.zoom;

            if (audio.zoom)
                showToast(R.string.zoom_on);
            else
                showToast(R.string.zoom_off);
            break;

        // Display
        case R.id.display:
            audio.lock = !audio.lock;
            if (display != null)
                display.invalidate();

            if (audio.lock)
                showToast(R.string.lock_on);
            else
                showToast(R.string.lock_off);
            break;

        // Strobe / Staff
        case R.id.strobe:
        case R.id.staff:
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                if (strobe != null && staff != null)
                {
                    audio.strobe = !audio.strobe;

                    if (audio.strobe)
                    {
                        animateViews(staff, strobe);
                        showToast(R.string.strobe_on);
                    }

                    else
                    {
                        animateViews(strobe, staff);
                        showToast(R.string.strobe_off);
                    }
                }
            }

            else
            {
                audio.lock = !audio.lock;
                if (display != null)
                    display.invalidate();

                if (audio.lock)
                    showToast(R.string.lock_on);
                else
                    showToast(R.string.lock_off);
            }
            break;

        // Meter
        case R.id.meter:
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                if (display != null && staff != null)
                {
                    show = !show;

                    if (show)
                        animateViews(display, staff);

                    else
                        animateViews(staff, display);
                }
            }

            else
            {
                audio.copyToClipboard();
                showToast(R.string.copied_clip);
            }
            break;
        }
    }

    // onMenuItemClick
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Help
        case R.id.help:
            return onHelpClick(item);

        // Settings
        case R.id.settings:
            return onSettingsClick(item);

        default:
            return false;
        }
    }

    // animateViews
    public void animateViews(View hidden, View visible)
    {
        // Animation
        startAnimation(hidden, R.anim.activity_close_exit, View.GONE);
        startAnimation(visible, R.anim.activity_open_enter, View.VISIBLE);
    }

    // startAnimation
    private void startAnimation(View view, int anim, int visibility)
    {
        Animation animation = AnimationUtils.loadAnimation(this, anim);
        view.startAnimation(animation);
        view.setVisibility(visibility);
    }

    // On long click
    @Override
    public boolean onLongClick(View v)
    {
        // Get id
        int id = v.getId();
        switch (id)
        {
        // Scope
        case R.id.scope:
            audio.fund = !audio.fund;

            if (audio.fund)
                showToast(R.string.fund_on);
            else
                showToast(R.string.fund_off);
            break;

        // Spectrum
        case R.id.spectrum:
            audio.downsample = !audio.downsample;

            if (audio.downsample)
                showToast(R.string.down_on);
            else
                showToast(R.string.down_off);
            break;

        // Display
        case R.id.display:
            audio.multiple = !audio.multiple;

            if (audio.multiple)
                showToast(R.string.mult_on);

            else
                showToast(R.string.mult_off);
            break;

        // Strobe / Staff
        case R.id.strobe:
        case R.id.staff:
            if (++theme > BLACK)
                theme = LIGHT;

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                recreate();
            break;

        // Meter
        case R.id.meter:
            audio.screen = !audio.screen;

            if (audio.screen)
                showToast(R.string.screen_on);

            else
                showToast(R.string.screen_off);

            Window window = getWindow();

            if (audio.screen)
                window.addFlags(WindowManager
                                .LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                window.clearFlags(WindowManager
                                  .LayoutParams.FLAG_KEEP_SCREEN_ON);
            break;
        }
        return true;
    }

    // On options item
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Help
        case R.id.help:
            return onHelpClick(item);

        // Settings
        case R.id.settings:
            return onSettingsClick(item);

        default:
            return false;
        }
    }

    // On help click
    private boolean onHelpClick(MenuItem item)
    {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);

        return true;
    }

    // On settings click
    private boolean onSettingsClick(MenuItem item)
    {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

        return true;
    }

    // Show toast.
    void showToast(int key)
    {
        String text = getString(key);

        // Cancel the last one
        if (toast != null)
            toast.cancel();

        // Make a new one
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        // Fix for android 13
        View view = toast.getView();
        if (view != null && Build.VERSION.SDK_INT > VERSION_CODE_S_V2)
            view.setBackgroundResource(R.drawable.toast_frame);
        toast.show();

        // Update status
        if (status != null)
            status.invalidate();
    }

    // On Resume
    @Override
    protected void onResume()
    {
        super.onResume();

        int current = theme;

        // Get preferences
        getPreferences();

        // Change theme
        if (current != theme)
        {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                recreate();
        }

        // Load custom temperaments
        loadCustomTemperaments();

        // Set temperament text
        String keys[] =
            getResources().getStringArray(R.array.pref_note_entries);
        String text = names[audio.temper];
        // Don't show key on equal temperament
        if (audio.temper != getResources().getInteger(R.integer.default_temper))
            text = String.format("%s  %s", names[audio.temper],
                                    keys[audio.key]);

        TextView textView = findViewById(R.id.temperament);
        if (textView != null)
            textView.setText(text);

        // Update status
        if (status != null)
            status.invalidate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.RECORD_AUDIO,
                     Manifest.permission.READ_EXTERNAL_STORAGE},
                                   REQUEST_PERMISSIONS);

                return;
            }
        }

        // Start the audio thread
        audio.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults)
    {
        if (requestCode == REQUEST_PERMISSIONS)
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission.RECORD_AUDIO) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    // Granted, recreate or start audio thread
                    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                        recreate();

                    else
                        audio.start();
                }
    }

    // onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        show = savedInstanceState.getBoolean(SHOW_STAFF);
    }

    // onPause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Save preferences
        savePreferences();

        // Stop audio thread
        audio.stop();
    }

    // onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SHOW_STAFF, show);
    }

    // Save preferences
    void savePreferences()
    {
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PREF_THEME, String.valueOf(theme));

        if (audio != null)
        {
            editor.putBoolean(PREF_BACH, audio.bach);
            editor.putBoolean(PREF_DOWN, audio.downsample);
            editor.putBoolean(PREF_FILTER, audio.filter);
            editor.putBoolean(PREF_FILTERS, audio.filters);
            editor.putBoolean(PREF_FUND, audio.fund);
            editor.putBoolean(PREF_MULT, audio.multiple);
            editor.putBoolean(PREF_SCREEN, audio.screen);
            editor.putBoolean(PREF_SOLFA, audio.solfa);
            editor.putBoolean(PREF_STROBE, audio.strobe);
            editor.putBoolean(PREF_ZOOM, audio.zoom);
        }

        editor.apply();
    }

    // Get preferences
    void getPreferences()
    {
        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Set preferences
        theme = Integer.parseInt(preferences.getString(PREF_THEME, "1"));

        if (audio != null)
        {
            try
            {
                audio.reference = preferences.getFloat(PREF_REFER, 440);
            }

            catch (Exception e)
            {
                audio.reference = preferences.getInt(PREF_REFER, 440);
            }

            audio.input =
                Integer.parseInt(preferences.getString(PREF_INPUT, "0"));
            audio.transpose =
                Integer.parseInt(preferences.getString(PREF_TRANSPOSE, "0"));
            audio.temper =
                Integer.parseInt(preferences.getString(PREF_TEMPER, "8"));
            audio.key =
                Integer.parseInt(preferences.getString(PREF_KEY, "0"));

            audio.bach = preferences.getBoolean(PREF_BACH, false);
            audio.downsample = preferences.getBoolean(PREF_DOWN, false);
            audio.filter = preferences.getBoolean(PREF_FILTER, false);
            audio.filters = preferences.getBoolean(PREF_FILTERS, false);
            audio.fund = preferences.getBoolean(PREF_FUND, false);
            audio.multiple = preferences.getBoolean(PREF_MULT, false);
            audio.screen = preferences.getBoolean(PREF_SCREEN, false);
            audio.solfa = preferences.getBoolean(PREF_SOLFA, false);
            audio.strobe = preferences.getBoolean(PREF_STROBE, false);
            audio.zoom = preferences.getBoolean(PREF_ZOOM, true);

            // Note filter
            Set<String> notes = preferences.getStringSet(PREF_NOTE, null);
            if (notes != null)
            {
                for (int index = 0; index < audio.noteFilter.length; index++)
                    audio.noteFilter[index] = false;

                for (String note : notes)
                {
                    int index = Integer.parseInt(note);
                    audio.noteFilter[index] = true;
                }
            }

            // Octave filter
            Set<String> octaves = preferences.getStringSet(PREF_OCTAVE, null);
            if (octaves != null)
            {
                for (int index = 0; index < audio.octaveFilter.length; index++)
                    audio.octaveFilter[index] = false;

                for (String octave : octaves)
                {
                    int index = Integer.parseInt(octave);
                    audio.octaveFilter[index] = true;
                }
            }

            // Check screen
            if (audio.screen)
            {
                Window window = getWindow();
                window.addFlags(WindowManager
                                .LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            else
            {
                Window window = getWindow();
                window.clearFlags(WindowManager
                                  .LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            // Get config
            Configuration config = getResources().getConfiguration();

            // Strobe
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                if (strobe != null)
                    strobe.setVisibility(audio.strobe? View.VISIBLE: View.GONE);
                if (staff != null)
                    staff.setVisibility(audio.strobe? View.GONE: View.VISIBLE);
            }

            else
            {
                if (staff != null)
                    staff.setVisibility(show? View.VISIBLE: View.GONE);
                if (display != null)
                    display.setVisibility(show? View.GONE: View.VISIBLE);
            }
         }

        // Check for strobe before setting colours
        if (strobe != null)
        {
            strobe.colour =
                Integer.parseInt(preferences.getString(PREF_COLOUR, "0"));

            if (strobe.colour == 3)
            {
                JSONArray custom;

                try
                {
                    custom =
                        new JSONArray(preferences.getString(PREF_CUSTOM,
                                                            null));
                    strobe.foreground = custom.getInt(0);
                    strobe.background = custom.getInt(1);
                }

                catch (Exception e) {}
            }

            // Ensure the view dimensions have been set
            if (strobe.width > 0 && strobe.height > 0)
                strobe.createShaders();
        }
    }

    // loadCustomTemperaments
    private void loadCustomTemperaments()
    {
        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        Properties props = new Properties();
        try (StringReader reader = new StringReader
             (preferences.getString(PREF_PROPS, "")))
        {
            props.load(reader);
        }

        catch (Exception e) {}

        // Sort the entries
        String order[] = props.stringPropertyNames().toArray(new String[0]);
        Arrays.sort(order);

        // Get temperament names
        String entries[] =
            getResources().getStringArray(R.array.pref_temper_entries);
        List<String> entryList = new ArrayList<String>(Arrays.asList(entries));
        // Get temperament values
        List<double[]> valueList =
            new ArrayList<double[]>(Arrays.asList(temperament_values));
        // Add the names and values from the properties
        for (String entry: order)
        {
            entryList.add(entry);
            String value = props.getProperty(entry);
            String a[] = value.split(",");
            double d[] = new double[a.length];
            Arrays.fill(d, 1.0);
            int i = 0;
            for (String v: a)
            {
                try
                {
                    d[i++] = Double.valueOf(v);
                }

                catch (Exception e) {}
            }
            valueList.add(d);
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Properties " + props);

        names = entryList.toArray(new String[0]);
        temperaments = valueList.toArray(new double[0][0]);
    }

    // Show alert
    void showAlert(int title, int message)
    {
        // Create an alert dialog builder
        AlertDialog.Builder builder =
            new AlertDialog.Builder(this);

        // Set the title, message and button
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok,
                                 (dialog, which) ->
        {
            // Dismiss dialog
            dialog.dismiss();
        });
        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show it
        dialog.show();
    }

    // Log2
    protected double log2(double d)
    {
        return Math.log(d) / Math.log(2.0);
    }

    // Audio
    protected class Audio implements Runnable
    {
        // Preferences
        protected int key;
        protected int input;
        protected int temper;
        protected int transpose;

        protected boolean lock;
        protected boolean zoom;
        protected boolean fund;
        protected boolean bach;
        protected boolean solfa;
        protected boolean filter;
        protected boolean screen;
        protected boolean strobe;
        protected boolean filters;
        protected boolean multiple;
        protected boolean downsample;

        protected boolean noteFilter[] =
        {
            true, true, true, true, true, true,
            true, true, true, true, true, true
        };

        protected boolean octaveFilter[] =
        {
            true, true, true, true, true,
            true, true, true, true
        };

        protected double reference;

        // Data
        protected Thread thread;
        protected double buffer[];
        protected short data[];
        protected int sample;

        // Output data
        protected double lower;
        protected double higher;
        protected double nearest;
        protected double frequency;
        protected double difference;
        protected double cents;
        protected double fps;

        protected int count;
        protected int note;

        // Private data
        private long timer;
        private int divisor = 1;

        private AudioRecord audioRecord;

        private static final int MAXIMA = 8;
        private static final int OVERSAMPLE = 16;
        private static final int SAMPLES = 16384;
        private static final int RANGE = SAMPLES * 7 / 16;
        private static final int STEP = SAMPLES / OVERSAMPLE;
        private static final int SIZE = 4096;

        private static final int OCTAVE = 12;
        private static final int EQUAL = 8;
        private static final int A_OFFSET = 9;
        private static final int C5_OFFSET = 57;
        private static final long TIMER_COUNT = 24;
        private static final double MIN = 0.5;
        private static final double SPAN = 0.6;
        private static final double MARGIN = 60.0;

        // Butterworth filter parameters
        private static final double G = 3.023332184e+01;
        private static final double K = 0.9338478249;

        // Butterworth filter buffers
        private double xv[];
        private double yv[];

        private Complex x;

        protected float signal;

        protected Maxima maxima;

        protected double xa[];

        private double xp[];
        private double xf[];
        private double dx[];

        private double x2[];
        private double x3[];
        private double x4[];
        private double x5[];

        // Constructor
        protected Audio()
        {
            buffer = new double[SAMPLES];

            xv = new double[2];
            yv = new double[2];

            x = new Complex(SAMPLES);

            maxima = new Maxima(MAXIMA);

            xa = new double[RANGE];
            xp = new double[RANGE];
            xf = new double[RANGE];
            dx = new double[RANGE];

            x2 = new double[RANGE / 2];
            x3 = new double[RANGE / 3];
            x4 = new double[RANGE / 4];
            x5 = new double[RANGE / 5];
        }

        // Start audio
        protected void start()
        {
            // Start the thread
            thread = new Thread(this, "Audio");
            thread.start();
        }

        // Run
        @Override
        public void run()
        {
            processAudio();
        }

        // Stop
        protected void stop()
        {
            // Stop and release the audio recorder
            cleanUpAudioRecord();

            Thread t = thread;
            thread = null;

            try
            {
                // Wait for the thread to exit
                if (t != null && t.isAlive())
                    t.join();
            }

            catch (Exception e) {}
        }

        // Stop and release the audio recorder
        private void cleanUpAudioRecord()
        {
            if (audioRecord != null &&
                audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
            {
                try
                {
                    if (audioRecord.getRecordingState() ==
                        AudioRecord.RECORDSTATE_RECORDING)
                        audioRecord.stop();

                    audioRecord.release();
                }

                catch (Exception e) {}
            }
        }

        // Process Audio
        protected void processAudio()
        {
            // Sample rates to try
            Resources resources = getResources();

            int rates[] = resources.getIntArray(R.array.sample_rates);
            int divisors[] = resources.getIntArray(R.array.divisors);

            int size = 0;
            int state = 0;
            int index = 0;
            for (int rate : rates)
            {
                // Check sample rate
                size = AudioRecord
                    .getMinBufferSize(rate,
                                      AudioFormat.CHANNEL_IN_MONO,
                                      AudioFormat.ENCODING_PCM_16BIT);
                // Loop if invalid sample rate
                if (size == AudioRecord.ERROR_BAD_VALUE)
                {
                    index++;
                    continue;
                }

                // Check valid input selected, or other error
                if (size == AudioRecord.ERROR)
                {
                    runOnUiThread(() -> showAlert(R.string.app_name,
                                                  R.string.error_buffer));
                    thread = null;
                    return;
                }

                // Set divisor
                divisor = divisors[index];

                // Create the AudioRecord object
                try
                {
                    audioRecord =
                        new AudioRecord(input, rate,
                                        AudioFormat.CHANNEL_IN_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        Math.max(size, SIZE * divisor));
                }

                // Exception
                catch (Exception e)
                {
                    runOnUiThread(() -> showAlert(R.string.app_name,
                                                  R.string.error_init));
                    thread = null;
                    return;
                }

                // Check state
                state = audioRecord.getState();
                if (state != AudioRecord.STATE_INITIALIZED)
                {
                    audioRecord.release();
                    index++;
                    continue;
                }

                // Must be a valid sample rate
                sample = rate;
                break;
            }

            // Check valid sample rate
            if (size == AudioRecord.ERROR_BAD_VALUE)
            {
                runOnUiThread(() -> showAlert(R.string.app_name,
                                              R.string.error_buffer));
                thread = null;
                return;
            }

            // Check AudioRecord initialised
            if (state != AudioRecord.STATE_INITIALIZED)
            {
                runOnUiThread(() -> showAlert(R.string.app_name,
                                              R.string.error_init));

                audioRecord.release();
                thread = null;
                return;
            }

            // Calculate fps and expect
            fps = ((double) sample / divisor) / SAMPLES;
            final double expect = 2.0 * Math.PI *
                                  STEP / SAMPLES;

            // Create buffer for input data
            data = new short[STEP * divisor];

            // Start recording
            audioRecord.startRecording();

            // Max data
            double dmax = 0.0;

            // Continue until the thread is stopped
            while (thread != null)
            {
                // Read a buffer of data
                // NOTE: audioRecord.read(short[], int, int) can block
                // indefinitely, until audioRecord.stop() is called
                // from another thread
                size = audioRecord.read(data, 0, STEP * divisor);

                // Stop the thread if no data or error state
                if (size <= 0)
                {
                    thread = null;
                    break;
                }

                // If display not locked update scope
                if (scope != null && !lock)
                    scope.postInvalidate();

                // Move the main data buffer up
                System.arraycopy(buffer, STEP, buffer, 0, SAMPLES - STEP);

                // Max signal
                double rm = 0;

                // Butterworth filter, 120Hz, 3dB/octave
                for (int i = 0; i < STEP; i++)
                {
                    xv[0] = xv[1];
                    xv[1] = data[i * divisor] / G;

                    yv[0] = yv[1];
                    yv[1] = (xv[0] + xv[1]) + (K * yv[0]);

                    // Choose filtered/unfiltered data
                    buffer[(SAMPLES - STEP) + i] =
                        audio.filter ? yv[1] : data[i * divisor];

                    // Find root mean signal
                    double v = data[i * divisor] / 32768.0;
                    rm += v * v;
                }

                // Signal value
                rm /= STEP;
                signal = (float) Math.sqrt(rm);

                // Maximum value
                if (dmax < 4096.0)
                    dmax = 4096.0;

                // Calculate normalising value
                double norm = dmax;

                dmax = 0.0;

                // Copy data to FFT input arrays for tuner
                for (int i = 0; i < SAMPLES; i++)
                {
                    // Find the magnitude
                    if (dmax < Math.abs(buffer[i]))
                        dmax = Math.abs(buffer[i]);

                    // Calculate the window
                    double window =
                        0.5 - 0.5 * Math.cos(2.0 * Math.PI *
                                             i / SAMPLES);

                    // Normalise and window the input data
                    x.r[i] = buffer[i] / norm * window;
                }

                // do FFT for tuner
                fftr(x);

                // Process FFT output for tuner
                for (int i = 1; i < RANGE; i++)
                {
                    double real = x.r[i];
                    double imag = x.i[i];

                    xa[i] = Math.hypot(real, imag);

                    // Do frequency calculation
                    double p = Math.atan2(imag, real);
                    double dp = xp[i] - p;

                    xp[i] = p;

                    // Calculate phase difference
                    dp -= i * expect;

                    int qpd = (int) (dp / Math.PI);

                    if (qpd >= 0)
                        qpd += qpd & 1;

                    else
                        qpd -= qpd & 1;

                    dp -= Math.PI * qpd;

                    // Calculate frequency difference
                    double df = OVERSAMPLE * dp / (2.0 * Math.PI);

                    // Calculate actual frequency from slot frequency plus
                    // frequency difference and correction value
                    xf[i] = i * fps + df * fps;

                    // Calculate differences for finding maxima
                    dx[i] = xa[i] - xa[i - 1];
                }

                // Downsample
                if (downsample)
                {
                    // x2 = xa << 2
                    for (int i = 0; i < RANGE / 2; i++)
                    {
                        x2[i] = 0.0;

                        for (int j = 0; j < 2; j++)
                            x2[i] += xa[(i * 2) + j];
                    }

                    // x3 = xa << 3
                    for (int i = 0; i < RANGE / 3; i++)
                    {
                        x3[i] = 0.0;

                        for (int j = 0; j < 3; j++)
                            x3[i] += xa[(i * 3) + j];
                    }

                    // x4 = xa << 4
                    for (int i = 0; i < RANGE / 4; i++)
                    {
                        x4[i] = 0.0;

                        for (int j = 0; j < 4; j++)
                            x4[i] += xa[(i * 4) + j];
                    }

                    // x5 = xa << 5
                    for (int i = 0; i < RANGE / 5; i++)
                    {
                        x5[i] = 0.0;

                        for (int j = 0; j < 5; j++)
                            x5[i] += xa[(i * 5) + j];
                    }

                    // Add downsamples
                    for (int i = 1; i < RANGE; i++)
                    {
                        xa[i] *= (i < RANGE / 2)? x2[i]: 0.0;
                        xa[i] *= (i < RANGE / 3)? x3[i]: 0.0;
                        xa[i] *= (i < RANGE / 4)? x4[i]: 0.0;
                        xa[i] *= (i < RANGE / 5)? x5[i]: 0.0;

                        // Recalculate differences
                        dx[i] = xa[i] - xa[i - 1];
                    }
                }

                // Maximum FFT output
                double max = 0.0;

                count = 0;
                int limit = RANGE - 1;

                // Find maximum value, and list of maxima
                for (int i = 1; i < limit; i++)
                {
                    // Cents relative to reference
                    double cf = -12.0 * log2(reference / xf[i]);

                    // Note number
                    int note = (int) (Math.round(cf) + C5_OFFSET);

                    // Don't use if negative
                    if (note < 0)
                        continue;

                    // Check fundamental
                    if (fund && (count > 0) &&
                        ((note % OCTAVE) != (maxima.n[0] % OCTAVE)))
                        continue;

                    if (filters)
                    {
                        // Get note and octave
                        int n = note % OCTAVE;
                        int o = note / OCTAVE;

                        // Don't use if too high
                        if (o >= octaveFilter.length)
                            continue;

                        // Check the filters
                        if (!noteFilter[n] ||
                            !octaveFilter[o])
                            continue;
                    }

                    // Find maximum value
                    if (xa[i] > max)
                    {
                        max = xa[i];
                        frequency = xf[i];
                    }

                    // If display not locked, find maxima and add to list
                    if (!lock && count < MAXIMA &&
                            xa[i] > MIN && xa[i] > (max / 4.0) &&
                            dx[i] > 0.0 && dx[i + 1] < 0.0)
                    {
                        maxima.f[count] = xf[i];

                        // Note number
                        maxima.n[count] = note;

                        // Octave note number
                        int n = (note - key + OCTAVE) % OCTAVE;
                        // A note number
                        int a = (A_OFFSET - key + OCTAVE) % OCTAVE;

                        // Temperament ratio
                        double temperRatio = temperaments[temper][n] /
                            temperaments[temper][a];
                        // Equal ratio
                        double equalRatio = temperaments[EQUAL][n] /
                            temperaments[EQUAL][a];

                        // Temperament adjustment
                        double temperAdjust = temperRatio / equalRatio;

                        // Reference note
                        maxima.r[count] = reference *
                                          Math.pow(2.0, Math.round(cf) /
                                                   12.0) * temperAdjust;

                        // Set limit to octave above
                        if (!downsample && (limit > i * 2))
                            limit = i * 2 - 1;

                        count++;
                    }
                }

                // Found flag
                boolean found = false;

                // Do the note and cents calculations
                if (max > MIN)
                {
                    found = true;

                    // Frequency
                    if (!downsample)
                        frequency = maxima.f[0];

                    // Cents relative to reference
                    double cf = -12.0 * log2(reference / frequency);

                    // Don't count silly values
                    if (Double.isNaN(cf))
                    {
                        cf = 0.0;
                        found = false;
                    }

                    // Note number
                    note = (int) Math.round(cf) + C5_OFFSET;

                    if (note < 0)
                    {
                        note = 0;
                        found = false;
                    }

                    // Octave note number
                    int n = (note - key + OCTAVE) % OCTAVE;
                    // A note number
                    int a = (A_OFFSET - key + OCTAVE) % OCTAVE;

                        // Temperament ratio
                    double temperRatio = temperaments[temper][n] /
                        temperaments[temper][a];
                    // Equal ratio
                    double equalRatio = temperaments[EQUAL][n] /
                        temperaments[EQUAL][a];

                    // Temperament adjustment
                    double temperAdjust = temperRatio / equalRatio;

                    // Reference note
                    nearest = reference *
                        Math.pow(2.0, Math.round(cf) / 12.0) * temperAdjust;

                    // Lower and upper freq
                    lower = reference *
                        Math.pow(2.0, (Math.round(cf) - SPAN) /
                                 12.0) * temperAdjust;
                    higher = reference *
                        Math.pow(2.0, (Math.round(cf) + SPAN) /
                                 12.0) * temperAdjust;

                    // Find nearest maximum to reference note
                    double df = 1000.0;

                    for (int i = 0; i < count; i++)
                    {
                        if (Math.abs(maxima.f[i] - nearest) < df)
                        {
                            df = Math.abs(maxima.f[i] - nearest);
                            frequency = maxima.f[i];
                        }
                    }

                    // Cents relative to reference note
                    cents = -12.0 * log2(nearest / frequency) * 100.0;

                    // Ignore silly values
                    if (Double.isNaN(cents))
                    {
                        cents = 0.0;
                        found = false;
                    }

                    // Ignore if not within margin of reference note
                    if (Math.abs(cents) > MARGIN)
                    {
                        cents = 0.0;
                        found = false;
                    }

                    // Difference
                    difference = frequency - nearest;
                }

                // Found
                if (found)
                {
                    // If display not locked
                    if (!lock)
                    {
                        // Update spectrum
                        if (spectrum != null)
                            spectrum.postInvalidate();

                        // Update display
                        if (display != null)
                            display.postInvalidate();

                        // Update staff
                        if (staff != null)
                            staff.postInvalidate();
                    }

                    // Reset count;
                    timer = 0;
                }
                else
                {
                    // If display not locked
                    if (!lock)
                    {
                        if (timer > TIMER_COUNT)
                        {
                            difference = 0.0;
                            frequency = 0.0;
                            nearest = 0.0;
                            higher = 0.0;
                            lower = 0.0;
                            cents = 0.0;
                            count = 0;
                            note = 0;

                            // Update display
                            if (display != null)
                                display.postInvalidate();

                            // Update staff
                            if (staff != null)
                                staff.postInvalidate();
                        }

                        // Update spectrum
                        if (spectrum != null)
                            spectrum.postInvalidate();
                    }
                }

                timer++;
            }

            cleanUpAudioRecord();
        }

        // Real to complex FFT, ignores imaginary values in input array
        private void fftr(Complex a)
        {
            final int n = a.r.length;
            final double norm = Math.sqrt(1.0 / n);

            for (int i = 0, j = 0; i < n; i++)
            {
                if (j >= i)
                {
                    double tr = a.r[j] * norm;

                    a.r[j] = a.r[i] * norm;
                    a.i[j] = 0.0;

                    a.r[i] = tr;
                    a.i[i] = 0.0;
                }

                int m = n / 2;
                while (m >= 1 && j >= m)
                {
                    j -= m;
                    m /= 2;
                }
                j += m;
            }

            for (int mmax = 1, istep = 2 * mmax; mmax < n;
                    mmax = istep, istep = 2 * mmax)
            {
                double delta = (Math.PI / mmax);
                for (int m = 0; m < mmax; m++)
                {
                    double w = m * delta;
                    double wr = Math.cos(w);
                    double wi = Math.sin(w);

                    for (int i = m; i < n; i += istep)
                    {
                        int j = i + mmax;
                        double tr = wr * a.r[j] - wi * a.i[j];
                        double ti = wr * a.i[j] + wi * a.r[j];
                        a.r[j] = a.r[i] - tr;
                        a.i[j] = a.i[i] - ti;
                        a.r[i] += tr;
                        a.i[i] += ti;
                    }
                }
            }
        }

        // Copy to clipboard
        protected void copyToClipboard()
        {
            String text = "";

            if (multiple)
            {
                for (int i = 0; i < count; i++)
                {
                    // Calculate cents
                    double cents = -12.0 * log2(maxima.r[i] /
                                                maxima.f[i]) * 100.0;
                    // Ignore silly values
                    if (Double.isNaN(cents))
                        continue;

                    text +=
                        String
                        .format(Locale.getDefault(), CLIP_FORMAT,
                                notes[(maxima.n[i] - transpose +
                                       OCTAVE) % OCTAVE],
                                sharps[(maxima.n[i] - transpose +
                                        OCTAVE) % OCTAVE],
                                (maxima.n[i] - transpose) / OCTAVE, cents,
                                maxima.r[i], maxima.f[i],
                                maxima.r[i] - maxima.f[i]);
                }

                if (count == 0)
                    text =
                        String
                        .format(Locale.getDefault(), CLIP_FORMAT,
                                notes[(note - transpose + OCTAVE) % OCTAVE],
                                sharps[(note - transpose + OCTAVE) % OCTAVE],
                                (note - transpose) / OCTAVE, cents,
                                nearest, frequency, difference);
            }
            else
                text =
                    String
                    .format(Locale.getDefault(), CLIP_FORMAT,
                            notes[(note - transpose + OCTAVE) % OCTAVE],
                            sharps[(note - transpose + OCTAVE) % OCTAVE],
                            (note - transpose) / OCTAVE, cents,
                            nearest, frequency, difference);

            ClipboardManager clipboard =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            clipboard.setPrimaryClip(ClipData.newPlainText("Tuner clip", text));
        }
    }

    // These two objects replace arrays of structs in the C version
    // because initialising arrays of objects in Java is, IMHO, barmy

    // Complex
    private class Complex
    {
        double r[];
        double i[];

        private Complex(int l)
        {
            r = new double[l];
            i = new double[l];
        }
    }

    // Maximum
    protected class Maxima
    {
        double f[];
        double r[];
        int n[];

        protected Maxima(int l)
        {
            f = new double[l];
            r = new double[l];
            n = new int[l];
        }
    }
}
