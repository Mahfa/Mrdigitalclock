package mr.digital.clock;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.format.Time;
import android.util.TypedValue;
import android.widget.RemoteViews;


public class ClockWidgetProvider extends AppWidgetProvider {
	/**
	 * Inner class representing the update service.
	 */
	public static class ClockUpdateService extends Service {
		private static final String ACTION_UPDATE =
			"mr.UPDATE";

		private final static IntentFilter intentFilter;

		static {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_TIME_TICK);
			intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
			intentFilter.addAction(ACTION_UPDATE);
		}

		/**
		 * BroadcastReceiver receiving the updates.
		 */
		private final BroadcastReceiver clockChangedReceiver = new
				BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				updateTime(context);
			}
		};
		public IBinder onBind(Intent intent) {
			return null;
		}
		public void onCreate() {
			super.onCreate();

			registerReceiver(clockChangedReceiver, intentFilter);
		}
		public void onDestroy() {
			super.onDestroy();

			unregisterReceiver(clockChangedReceiver);
		}
		public void onStart(Intent intent, int startId) {
			if (intent != null && intent.getAction() != null) {
				if (intent.getAction().equals(ACTION_UPDATE)) {
					updateTime(this);
				}
			}
		}
	}

	/**
	 * Method buildUpdate builds the RemoveViews to be displayed.
	 * @param context used to retrieve resources.
	 * @return RemoveViews instance.
	 */
	public static RemoteViews buildUpdate(Context context) {
		Roozh rz = new Roozh();
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		int hour = today.hour;
		String hh = Integer.toString(hour);
		int minute = today.minute;
		String mm = Integer.toString(minute);
		if (mm.length()==1) {
			mm = ":0"+mm;
		}else {
			mm = ":"+mm;
		}
		SharedPreferences sp = context.getSharedPreferences("Config", Context.MODE_PRIVATE);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		views.setImageViewBitmap(R .id.imageView1, getFontBitmap(context, hh, sp.getInt("Color1", Color.WHITE), 110,"bold.ttf"));
		views.setImageViewBitmap(R.id.imageView2, getFontBitmap(context, mm, sp.getInt("Color2", Color.WHITE), 110,"thin.ttf"));
		views.setImageViewBitmap(R.id.imageView3, getFontBitmap(context, rz.todayShamsi(), sp.getInt("Color3", Color.WHITE), 30,"bold.ttf"));
		
		
		PackageManager packageManager = context.getPackageManager();
		Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

		// Verify clock implementation
		String clockImpls[][] = {
		        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
		        {"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
		        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
		        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
		        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"},
		        {"Standar Alarm Clock2", "com.google.android.deskclock", "com.android.deskclock.AlarmClock"}
		};

		boolean foundClockImpl = false;

		for(int i=0; i<clockImpls.length; i++) {
		    String vendor = clockImpls[i][0];
		    String packageName = clockImpls[i][1];
		    String className = clockImpls[i][2];
		    try {
		        ComponentName cn = new ComponentName(packageName, className);
		        ActivityInfo aInfo = packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
		        alarmClockIntent.setComponent(cn);
		        foundClockImpl = true;
		    } catch (NameNotFoundException e) {
		    	
		    }
		}

		if (foundClockImpl) {
		    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
		        views.setOnClickPendingIntent(R.id.root, pendingIntent);
		}

		return views;
	}
	public void onDisabled(Context context) {
		super.onDisabled(context);

		context.stopService(new Intent(context,
			ClockUpdateService.class));
	}
	public void onEnabled(Context context) {
		super.onEnabled(context);

		context.startService(new Intent(
			ClockUpdateService.ACTION_UPDATE));
	}
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		context.startService(new Intent(
			ClockUpdateService.ACTION_UPDATE));
	}
	/**
	 * Method updateTime updates the time.
	 * @param context used to retrieve resources.
	 */
	private static void updateTime(Context context) {
		RemoteViews remoteViews = buildUpdate(context);

		ComponentName clockWidget = new ComponentName(context,
			ClockWidgetProvider.class);

		AppWidgetManager appWidgetManager = AppWidgetManager.
			getInstance(context);
		appWidgetManager.updateAppWidget(clockWidget, remoteViews);
	}
	public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP, String font) {
	    int fontSizePX = convertDiptoPix(context, fontSizeSP);
	    int pad = (fontSizePX / 9);
	    Paint paint = new Paint();
	    Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
	    paint.setAntiAlias(true);
	    paint.setTypeface(typeface);
	    paint.setColor(color);
	    paint.setTextSize(fontSizePX);
	    int textWidth = (int) (paint.measureText(text) + pad * 2);
	    int height = (int) (fontSizePX / 0.75);
	    Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
	    Canvas canvas = new Canvas(bitmap);
	    float xOriginal = pad;
	    canvas.drawText(text, xOriginal, fontSizePX, paint);
	    return bitmap;
	}
	public static int convertDiptoPix(Context context, float dip) {
	    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
	    return value;
	}
	
}
