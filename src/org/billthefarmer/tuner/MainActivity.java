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

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

// Main Activity

public class MainActivity extends Activity
    implements OnClickListener, OnLongClickListener
{
    private static final String PREF_INPUT = "pref_input";
    private static final String PREF_REFERENCE = "pref_reference";

    private static final String PREF_FILTER = "pref_filter";
    private static final String PREF_DOWNSAMPLE = "pref_downsample";
    private static final String PREF_MULTIPLE = "pref_multiple";
    private static final String PREF_SCREEN = "pref_screen";
    private static final String PREF_STROBE = "pref_strobe";
    private static final String PREF_ZOOM = "pref_zoom";

    private static final String PREF_COLOUR = "pref_colour";
    private static final String PREF_CUSTOM = "pref_custom";

    // Note values for display

    private static final String notes[] =
    {"C", "C", "D", "E", "E", "F",
     "F", "G", "A", "A", "B", "B"};

    private static final String sharps[] =
    {"", "\u266F", "", "\u266D", "", "",
     "\u266F", "", "\u266D", "", "\u266D", ""};
 
    private SignalView signal;
    private Spectrum spectrum;
    private Display display;
    private Strobe strobe;
    private Status status;
    private Meter meter;
    private Scope scope;

    private Audio audio;
    private Toast toast;

    // On Create

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	// Find the views, not all may be present

	spectrum = (Spectrum)findViewById(R.id.spectrum);
	display = (Display)findViewById(R.id.display);
	strobe = (Strobe)findViewById(R.id.strobe);
	status = (Status)findViewById(R.id.status);
	meter = (Meter)findViewById(R.id.meter);
	scope = (Scope)findViewById(R.id.scope);

	// Add custom view to action bar

	ActionBar actionBar = getActionBar();
	actionBar.setCustomView(R.layout.signal_view);
	actionBar.setDisplayShowCustomEnabled(true);

	signal = (SignalView)actionBar.getCustomView();

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

	// Set up the click listeners

	setClickListeners();
    }

    // On create options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it is present.

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.activity_main, menu);

	return true;
    }

    // Set click listeners

    void setClickListeners()
    {
	// Scope

	if (scope != null)
	    scope.setOnClickListener(this);

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
	    strobe.setOnClickListener(this);

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

	    // Strobe

	case R.id.strobe:
	    audio.strobe = !audio.strobe;

	    if (audio.strobe)
		showToast(R.string.strobe_on);

	    else
		showToast(R.string.strobe_off);
	    break;

	    // Meter

	case R.id.meter:
	    audio.copyToClipboard();
	    showToast(R.string.copied_clip);
	    break;
	}	
    }

    // On long click

    @Override
    public boolean onLongClick(View v)
    {
	int id = v.getId();
	switch (id)
	{
	    // Spectrum

	case R.id.spectrum:
	    audio.downsample = !audio.downsample;

	    if (audio.downsample)
		showToast(R.string.downsample_on);
	    else
		showToast(R.string.downsample_off);
	    break;

	    // Display

	case R.id.display:
	    audio.multiple = !audio.multiple;

	    if (audio.multiple)
		showToast(R.string.multiple_on);

	    else
		showToast(R.string.multiple_off);
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
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

	    else
		window.clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
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
	    // Settings

	case R.id.settings:
	    return onSettingsClick(item);

	default:
	    return false;
	}
    }

    // On settings click

    private boolean onSettingsClick(MenuItem item)
    {
	Intent intent = new Intent(this, SettingsActivity.class);
	startActivity(intent);

	return true;
    }

    // Show toast.

    void showToast(int key)
    {
	Resources resources = getResources();
	String text = resources.getString(key);

	// Cancel the last one

	if (toast != null)
	    toast.cancel();

	// Make a new one

	toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();

	// Update status

	if (status != null)
	    status.invalidate();
    }

    // On start

    @Override
    protected void onStart()
    {
	super.onStart();
    }

    // On Resume

    @Override
    protected void onResume()
    {
	super.onResume();

	// Get preferences

	getPreferences();

	// Update status

	if (status != null)
	    status.invalidate();

	// Start the audio thread

	audio.start();
    }

    @Override
    protected void onPause()
    {
	super.onPause();

	// Save preferences

	savePreferences();

	// Stop audio thread

	audio.stop();
    }

    // On stop

    @Override
    protected void onStop()
    {
	super.onStop();
    }

    // On destroy

    @Override
    protected void onDestroy()
    {
	super.onDestroy();

	// Get rid of all those pesky objects

	audio = null;
	scope = null;
	spectrum = null;
	display = null;
	strobe = null;
	meter = null;
	status = null;
	signal = null;
	toast = null;

	// Hint that it might be a good idea

	System.runFinalization();
    }

    // Save preferences

    void savePreferences()
    {
	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(this);

	Editor editor = preferences.edit();

	editor.putBoolean(PREF_FILTER, audio.filter);
	editor.putBoolean(PREF_DOWNSAMPLE, audio.downsample);
	editor.putBoolean(PREF_MULTIPLE, audio.multiple);
	editor.putBoolean(PREF_SCREEN, audio.screen);
	editor.putBoolean(PREF_STROBE, audio.strobe);
	editor.putBoolean(PREF_ZOOM, audio.zoom);

	editor.commit();
    }

    // Get preferences

    void getPreferences()
    {
	// Load preferences

	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(this);

	// Set preferences

	if (audio != null)
	{
	    audio.input =
		Integer.parseInt(preferences.getString(PREF_INPUT, "0"));
	    audio.reference = preferences.getInt(PREF_REFERENCE, 440);
	    audio.filter = preferences.getBoolean(PREF_FILTER, false);
	    audio.downsample = preferences.getBoolean(PREF_DOWNSAMPLE, false);
	    audio.multiple = preferences.getBoolean(PREF_MULTIPLE, false);
	    audio.screen = preferences.getBoolean(PREF_SCREEN, false);
	    audio.strobe = preferences.getBoolean(PREF_STROBE, false);
	    audio.zoom = preferences.getBoolean(PREF_ZOOM, true);

	    // Check screen

	    if (audio.screen)
	    {
		Window window = getWindow();
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }

	    else
	    {
		Window window = getWindow();
		window.clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }

	    // Check for strobe before setting colours

	    if (strobe != null)
	    {
		strobe.colour =
		    Integer.valueOf(preferences.getString(PREF_COLOUR, "0"));

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

		    catch (JSONException e)
		    {
			e.printStackTrace();
		    }
		}

		// Ensure the view dimensions have been set

		if (strobe.width > 0 && strobe.height > 0)
		    strobe.createShaders();
	    }
	}
    }

    // Show alert

    void showAlert(int appName, int errorBuffer)
    {
	// Create an alert dialog builder

	AlertDialog.Builder builder =
	    new AlertDialog.Builder(this);

	// Set the title, message and button

	builder.setTitle(appName);
	builder.setMessage(errorBuffer);
	builder.setNeutralButton(android.R.string.ok,
				 new DialogInterface.OnClickListener()
				 {				
				     @Override
				     public void onClick(DialogInterface dialog,
							 int which)
				     {
					 // Dismiss dialog

					 dialog.dismiss();	
				     }
				 });
	// Create the dialog

	AlertDialog dialog = builder.create();

	// Show it

	dialog.show();
    }

    // Audio

    protected class Audio implements Runnable
    {
	// Preferences

	protected int input;

	protected boolean lock;
	protected boolean zoom;
	protected boolean filter;
	protected boolean screen;
	protected boolean strobe;
	protected boolean multiple;
	protected boolean downsample;

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
	private static final int RANGE = SAMPLES * 3 / 8;
	private static final int STEP = SAMPLES / OVERSAMPLE;
	private static final int SIZE = 4096;

	private static final int OCTAVE = 12;
	private static final int C5_OFFSET = 57;
	private static final long TIMER_COUNT = 24; 
	private static final double MIN = 0.5;

	private static final double G = 3.023332184e+01;
	private static final double K = 0.9338478249;

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
	    Thread t = thread;
	    thread = null;

	    // Wait for the thread to exit

	    while (t != null && t.isAlive())
		Thread.yield();
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
	    for (int rate: rates)
	    {
		// Check sample rate

		size =
		    AudioRecord.getMinBufferSize(rate,
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
		    runOnUiThread(new Runnable()
			{
			    @Override
			    public void run()
			    {
				showAlert(R.string.app_name,
					  R.string.error_buffer);
			    }
			});

		    thread = null;
		    return;
		}

		// Set divisor

		divisor = divisors[index];

		// Create the AudioRecord object

		audioRecord =
		    new AudioRecord(input, rate,
				    AudioFormat.CHANNEL_IN_MONO,
				    AudioFormat.ENCODING_PCM_16BIT,
				    Math.max(size, SIZE * divisor));
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
		runOnUiThread(new Runnable()
		    {
			@Override
			public void run()
			{
			    showAlert(R.string.app_name,
				      R.string.error_buffer);
			}
		    });

		thread = null;
		return;
	    }

	    // Check AudioRecord initialised

	    if (state != AudioRecord.STATE_INITIALIZED)
	    {
		runOnUiThread(new Runnable()
		    {
			@Override
			public void run()
			{
			    showAlert(R.string.app_name,
				      R.string.error_init);
			}
		    });

		audioRecord.release();
		thread = null;
		return;
	    }

	    // Calculate fps and expect

	    fps = ((double)sample / divisor) / SAMPLES;
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

		size = audioRecord.read(data, 0, STEP * divisor);

		// Stop the thread if no data

		if (size == 0)
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

		// Butterworth filter, 3dB/octave

		for (int i = 0; i < STEP; i++)
		{
		    xv[0] = xv[1];
		    xv[1] = data[i * divisor] / G;

		    yv[0] = yv[1];
		    yv[1] = (xv[0] + xv[1]) + (K * yv[0]);

		    // Choose filtered/unfiltered data

		    buffer[(SAMPLES - STEP) + i] =
			audio.filter? yv[1]: data[i * divisor];

		    // Find root mean signal

		    double v = data[i * divisor] / 32768.0;
		    rm += v * v;
		}
		
		// Signal value

		rm /= STEP;
		signal = (float)Math.sqrt(rm);

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

		    int qpd = (int)(dp / Math.PI);

		    if (qpd >= 0)
			qpd += qpd & 1;

		    else
			qpd -= qpd & 1;

		    dp -=  Math.PI * qpd;

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
			    x2[i] += xa[(i * 2) + j] / 2.0;
		    }

		    // x3 = xa << 3

		    for (int i = 0; i < RANGE / 3; i++)
		    {
			x3[i] = 0.0;

			for (int j = 0; j < 3; j++)
			    x3[i] += xa[(i * 3) + j] / 3.0;
		    }

		    // x4 = xa << 4

		    for (int i = 0; i < RANGE / 4; i++)
		    {
			x4[i] = 0.0;

			for (int j = 0; j < 4; j++)
			    x2[i] += xa[(i * 4) + j] / 4.0;
		    }

		    // x5 = xa << 5

		    for (int i = 0; i < RANGE / 5; i++)
		    {
			x5[i] = 0.0;

			for (int j = 0; j < 5; j++)
			    x5[i] += xa[(i * 5) + j] / 5.0;
		    }

		    // Add downsamples

		    for (int i = 1; i < RANGE; i++)
		    {
			if (i < RANGE / 2)
			    xa[i] += x2[i];

			if (i < RANGE / 3)
			    xa[i] += x3[i];

			if (i < RANGE / 4)
			    xa[i] += x4[i];

			if (i < RANGE / 5)
			    xa[i] += x5[i];

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

			// Cents relative to reference

			double cf =
			    -12.0 * log2(reference / xf[i]);

			// Reference note

			maxima.r[count] = reference *
			    Math.pow(2.0, Math.round(cf) / 12.0);

			// Note number

			maxima.n[count] = (int)(Math.round(cf) + C5_OFFSET);

			// Don't use if negative

			if (maxima.n[count] < 0)
			{
			    maxima.n[count] = 0;
			    continue;
			}

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

		    double cf =
			-12.0 * log2(reference / frequency);

		    // Don't count silly values

		    if (Double.isNaN(cf))
			continue;

		    // Reference note

		    nearest = audio.reference *
			Math.pow(2.0, Math.round(cf) / 12.0);

		    // Lower and upper freq

		    lower = reference *
			Math.pow(2.0, (Math.round(cf) - 0.55) / 12.0);
		    higher = reference *
			Math.pow(2.0, (Math.round(cf) + 0.55) / 12.0);

		    // Note number

		    note = (int)Math.round(cf) + C5_OFFSET;

		    if (note < 0)
		    {
			note = 0;
			found = false;
		    }
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

		    // Ignore if not within 50 cents of reference note

		    if (Math.abs(cents) > 50.0)
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
			}

			// Update spectrum

			if (spectrum != null)
			    spectrum.postInvalidate();
		    }
		}

		timer++;
	    }

	    // Stop and release the audio recorder

	    if (audioRecord != null)
	    {
		audioRecord.stop();
		audioRecord.release();
	    }
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

	@SuppressLint("DefaultLocale")
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
			String.format("%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n",
				      notes[maxima.n[i] % OCTAVE], sharps[maxima.n[i] % OCTAVE],
				      maxima.n[i] / OCTAVE, cents, maxima.r[i], maxima.f[i],
				      maxima.r[i] - maxima.f[i]);
		}

		if (count == 0)
		    text =
			String.format("%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n",
				      notes[note % OCTAVE], sharps[note % OCTAVE], note / OCTAVE, cents,
				      nearest, frequency, difference);
	    }

	    else
		text =
		    String.format("%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n",
				  notes[note % OCTAVE], sharps[note % OCTAVE], note / OCTAVE, cents,
				  nearest, frequency, difference);

	    ClipboardManager clipboard =
		(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

	    clipboard.setPrimaryClip(ClipData.newPlainText("Tuner clip", text));
	}
    }

    // Log2

    protected double log2(double d)
    {
	return Math.log(d) / Math.log(2.0);
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
