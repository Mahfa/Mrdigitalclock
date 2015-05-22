package mr.digital.clock;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class Config_Screen extends Activity {
	SharedPreferences sp;
	SharedPreferences.Editor spe;
	int mAppWidgetId;
	ImageView iv1;
	ImageView iv2;
	ImageView iv3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		setContentView(R.layout.configs);
		sp = getSharedPreferences("Config", MODE_PRIVATE);
		spe = sp.edit();
		iv1 = (ImageView) findViewById(R.id.imageView2);
		iv2 = (ImageView) findViewById(R.id.imageView3);
		iv3 = (ImageView) findViewById(R.id.imageView4);
		iv1.setImageDrawable(colordrawable(sp.getInt("Color1", Color.WHITE)));
		iv2.setImageDrawable(colordrawable(sp.getInt("Color2", Color.WHITE)));
		iv3.setImageDrawable(colordrawable(sp.getInt("Color3", Color.WHITE)));
	}
	public ColorDrawable colordrawable (int color){
		return new ColorDrawable(color);
	}
	public void change_color1(View v) {
		Show_Picker_Dialog(1);
	}

	public void change_color2(View v) {
		Show_Picker_Dialog(2);
	}
	public void change_color3(View v) {
		Show_Picker_Dialog(3);
	}
	public void save(View v) {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(Config_Screen.this);
		RemoteViews views = ClockWidgetProvider.buildUpdate(this);
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	public void Show_Picker_Dialog(final int id) {
		AlertDialog.Builder bu = new AlertDialog.Builder(this);
		View v = getLayoutInflater().inflate(R.layout.config_dialog, null,
				false);
		bu.setView(v);
		final ColorPicker picker = (ColorPicker) v.findViewById(R.id.picker);
		SVBar svBar = (SVBar) v.findViewById(R.id.svbar);
		OpacityBar opacityBar = (OpacityBar) v.findViewById(R.id.opacitybar);
		SaturationBar saturationBar = (SaturationBar) v
				.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) v.findViewById(R.id.valuebar);
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);
		if (id == 1) {
			picker.setOldCenterColor(sp.getInt("Color1", Color.WHITE));
		}else if(id == 2){
			picker.setOldCenterColor(sp.getInt("Color2", Color.WHITE));
		}else {
			picker.setOldCenterColor(sp.getInt("Color3", Color.WHITE));
		}
		Button b = (Button) v.findViewById(R.id.button1);
		final AlertDialog al = bu.create();
		al.show();
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int color = picker.getColor();
				spe.putInt("Color" + id, color);
				spe.commit();
				if (id==1) {
					iv1.setImageDrawable(colordrawable(color));
				}else if(id == 2){
					iv2.setImageDrawable(colordrawable(color));
				}else {
					iv3.setImageDrawable(colordrawable(color));
				}
				al.dismiss();
			}
		});
	}
}
