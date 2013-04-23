/**
 * 
 */
package com.cardosos.flextimemonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author oscar.cardoso
 *
 */
public class EventAdapter extends ArrayAdapter<Event> {
	
	Context context;
	int layoutResourceId;
	List<Event> data = new ArrayList<Event>();

	public EventAdapter(Context context, int layoutResourceId, List<Event> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//Log.i("FTM", "getView( pos:" + position + ", data.length: " + data.size() + " )");
		View row = convertView;
		EventHolder holder = null;
		
		if(row == null){
//			Log.i("FTM", "row == null");
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new EventHolder();
			holder.imgIcon = new ImageView(context);
			holder.txtTitle = new TextView(context);
			holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
			holder.colorMark = (View)row.findViewById(R.id.colormark);
			
			row.setTag(holder);
		} else {
//			Log.i("FTM", "row != null");
			holder = (EventHolder)row.getTag();
		}
		
		
//		Log.i("FTM", "event_id: " + data.get(position).getId() );
//		Log.i("FTM", "event_time: " + data.get(position).getTime() );
//		Log.i("FTM", "event_type: " + data.get(position).getType() );
//		Log.i("FTM", "event_title: " + data.get(position).getTitle() );
		
		Event event = data.get(position);

		if(event.getType().equals(Event.CHECK_OUT)){
			holder.colorMark.setBackgroundColor(context.getResources().getColor((R.color.holo_red_light)));
//			holder.imgIcon.setBackgroundColor(context.getResources().getColor((R.color.holo_red_light)));
		} else {
			holder.colorMark.setBackgroundColor(context.getResources().getColor((R.color.holo_green_light)));
//			holder.imgIcon.setBackgroundColor(context.getResources().getColor((R.color.holo_green_light)));
		}

		if(holder.txtTitle == null){
			Log.i("FTM", "Null txtTitle FAIL!");
		} else {
			holder.txtTitle.setText(event.getTitle());			
		}

		if(holder.imgIcon == null){
			Log.i("FTM", "Null imgIcon FAIL!");
		} else {
			holder.imgIcon.setImageResource(event.getIcon());			
		}
		
		return row;
		
	}
	
	@Override
	public void add(Event event){
		super.add(event);
	}
	
	@Override
	public void insert(Event event, int position){
		super.insert(event, position);
	}
	
	@Override
	public void remove(Event event){
		super.remove(event);
	}
	
	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
	}
	
	static class EventHolder
	{
		long id;
		long time;
		String type;
		
		ImageView imgIcon;
		TextView txtTitle;
		View colorMark;
	}

}
