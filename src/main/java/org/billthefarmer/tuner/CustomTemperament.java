package org.billthefarmer.tuner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomTemperament extends Activity {

    private static final String CUSTOM_FILE = "Custom.txt";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        int theme = Integer.parseInt(preferences.getString(Tuner.PREF_THEME,
                "0"));
        switch (theme)
        {
            case Tuner.LIGHT:
                setTheme(R.style.AppTheme);
                break;

            case Tuner.DARK:
                setTheme(R.style.AppDarkTheme);
                break;

            case Tuner.WHITE:
                setTheme(R.style.AppWhiteTheme);
                break;

            case Tuner.BLACK:
                setTheme(R.style.AppBlackTheme);
                break;
        }

        setContentView(R.layout.temperament_editor);

        EditText customTemperamentsText = findViewById(R.id.customTemperamentText);
        Button saveButton = findViewById(R.id.saveButton);

        // Check custom temperaments file
        File custom = new File(getExternalFilesDir(null),
                CUSTOM_FILE);

        if (custom.exists()) {
            try
            {
                FileInputStream fis = new FileInputStream(custom);
                InputStreamReader sr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(sr);

                StringBuilder fileText = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    fileText.append(line);
                    fileText.append("\n");
                }
                customTemperamentsText.setText(fileText);
                reader.close();
            }

            catch(Exception e)
            {
                Log.e("CustomTemperaments", "Error loading custom temperaments", e);
                return;
            }
        }

        saveButton.setOnClickListener(view -> {
            String textToSave = customTemperamentsText.getText().toString();

            try {
                if (!custom.exists()) {
                    custom.createNewFile();
                }
                FileWriter fw = new FileWriter(custom);

                fw.write(textToSave);
                fw.close();
                finish();
            } catch (IOException ioe) {
                Log.e("CustomTemperaments", "Error saving custom temperaments", ioe);
            }
        });
    }
}
