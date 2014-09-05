package gntikos.plugin.selectvalue;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SelectValue extends CordovaPlugin  {
  
	// ---------- CordovaPlugin overrides ----------
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException  {


		if (action.equals("fromRange")) {
			try {
				JSONObject parameters = args.getJSONObject(0);
				
				String title = parameters.getString("title");
				int min = parameters.getInt("min");
				int max = parameters.getInt("max");
				int value = parameters.getInt("value");
				
				this.selectFromRange(title, value, min, max, callbackContext);
				
				return true;
			} catch (JSONException e) {
				callbackContext.error("Wrong parameters to fromRange() method");
				return false;
			}
		}
		
		if (action.equals("fromList")) {
			try {
				JSONObject parameters = args.getJSONObject(0);
				
				String title = parameters.getString("title");
				
				JSONArray jsonOptions = parameters.getJSONArray("options");
				CharSequence[] options = new CharSequence[jsonOptions.length()];
				for (int i=0; i<options.length; i++)
					options[i] = jsonOptions.getString(i);
				
				int selected = parameters.getInt("selected");
				
				this.selectFromList(title, options, selected, callbackContext);
				
				return true;
			} catch (JSONException e) {
				callbackContext.error("Wrong parameters to fromList() method");
				return false;
			}
		}
		
		return false;
	}
	

	// * ================================ [ Private Methods ] ================================ *
	
	private synchronized void selectFromRange(final String title, final int value, final int min, final int max, final CallbackContext callbackContext) throws JSONException {
		final CordovaInterface cordova = this.cordova;
		
		Resources resources = cordova.getActivity().getApplication().getResources();
        String package_name = cordova.getActivity().getApplication().getPackageName();
        
        final View view = cordova.getActivity().getLayoutInflater().inflate(resources.getIdentifier("selector", "layout", package_name), null);
		final SeekBar bar = (SeekBar) view.findViewById(resources.getIdentifier("theSeekBar", "id", package_name));
		final TextView valueText = (TextView) view.findViewById(resources.getIdentifier("valueText", "id", package_name));
		
		bar.setMax(max-min);
		bar.setProgress(value-min);
		valueText.setText("Value: " + Integer.toString(value));
		
		OnSeekBarChangeListener barListener = new OnSeekBarChangeListener() {
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				valueText.setText("Value: " + Integer.toString(arg1 + min));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
			
		};
		
		bar.setOnSeekBarChangeListener(barListener);
		
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(cordova.getActivity())
					.setTitle(title)
					.setView(view)
					.setPositiveButton("Apply", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							callbackContext.success(min + bar.getProgress());
						}
					})
					.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							callbackContext.error("No value selected");
						}
					})
					.create()
					.show();
			}
		};
		
		this.cordova.getActivity().runOnUiThread(runnable);
	}
	
	private synchronized void selectFromList(final String title, final CharSequence[] options, final int selected, final CallbackContext callbackContext) throws JSONException {
		final CordovaInterface cordova = this.cordova;
		
		Runnable runnable = new Runnable() {

			public void run() {
				AlertDialog.Builder dlg = new AlertDialog.Builder(cordova.getActivity());
				dlg.setTitle(title);
				
				dlg.setSingleChoiceItems(options, selected, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						callbackContext.success(which);
					}
				});
				
				dlg.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						callbackContext.error("No value selected");
					}
				});
					
				dlg.create().show();
			}	
		};
		
		this.cordova.getActivity().runOnUiThread(runnable);
	}
}
