package org.billthefarmer.tuner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class AboutPreference extends DialogPreference
{

	public AboutPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onBindDialogView(View view)
	{
		Context context = getContext();
		PackageManager manager = context.getPackageManager();

		PackageInfo info = null;
		try
		{
			 info = manager.getPackageInfo("org.billthefarmer.tuner", 0);
		}
		
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		String version = info.versionName;
	}
}
