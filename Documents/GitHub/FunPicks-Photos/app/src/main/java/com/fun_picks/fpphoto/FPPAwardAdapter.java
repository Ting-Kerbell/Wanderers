
package com.fun_picks.fpphoto;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import android.content.Context;

import java.util.*;

import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class FPPAwardAdapter extends ArrayAdapter<FPPAward> {
	public static final String TAG = "FPPAwardAdapter";
	int resource;
	PBudDBAdapter pbDBAdapter;

	public FPPAwardAdapter(Context _context,
						   int _resource,
						   List<FPPAward> _awards) {
		super(_context, _resource, _awards);
		resource = _resource;
		pbDBAdapter = new PBudDBAdapter(_context);

		//     Open or create the database
		pbDBAdapter.open();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Log.i(TAG, "getView: entered. position="+position);
		LinearLayout pbudView;
		FPPAward item = getItem(position);
		String userName = item.getUserName();
		long gameMode = item.getGameMode();
		long creationTime = item.getCreationTime();
		long awardId =  item.getAwardId();
		long awardLevel =  item.getAwardLevel();
        String awardTitle = item.getAwardTitle();
        String awardDescription = item.getAwardDescription();
        boolean awardOn = item.isAwardOn();
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy   HH:mm"); 
		String dateString = formatter.format(new Date(creationTime));

		if (convertView == null) {
			pbudView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			if (vi != (LayoutInflater)null)
				vi.inflate(resource, pbudView, true);
			else{
				Log.e(TAG, "getView: getSystemService(inflater) returned null");
				throw new InflateException("getSystemService(inflater) returned null");
			}
		} else {
			pbudView = (LinearLayout) convertView;
		}

		TextView taskView = (TextView)pbudView.findViewById(R.id.awardRow);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/forced_square.ttf");
		taskView.setTypeface(font);
		if(taskView == null){
			Log.e(TAG, "getView: findViewById(R.id.row) returned null");
			throw new InvalidParameterException("findViewById(R.id.row) returned null");
		}

        ImageView awardIconViewOn = (ImageView)pbudView.findViewById(R.id.awardIconOn);
        ImageView awardIconViewOff = (ImageView)pbudView.findViewById(R.id.awardIconOff);
		String str;
        if(awardOn){
			str = "" + "<font color=#b0d506><big>"+ awardTitle + "</big></font>" +  "<br />" + "<font color=#838383><small>" + awardDescription + "</small></font>";
			awardIconViewOn.setVisibility(View.VISIBLE);
            awardIconViewOff.setVisibility(View.INVISIBLE);
        } else {
			str = "" + "<font color=#afafaf><big>"+ awardTitle + "</big></font>" +  "<br />" + "<font color=#838383><small>" + awardDescription + "</small></font>";
			awardIconViewOn.setVisibility(View.INVISIBLE);
            awardIconViewOff.setVisibility(View.VISIBLE);
        }
		/*if(awardIconViewOn == null){
			Log.e(TAG, "getView: findViewById(R.id.awardIcon) returned null");
			throw new InvalidParameterException("getView: findViewById(R.id.awardIcon) returned null");
		}*/

		taskView.setText(Html.fromHtml(str));

		return pbudView;
	}



}