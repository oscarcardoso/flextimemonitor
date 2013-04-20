/**
 * 
 */
package com.cardosos.flextimemonitor;

import android.app.Activity;
import android.content.Context;
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
	Event  data[] = null;

	public EventAdapter(Context context, int layoutResourceId, Event[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		EventHolder holder = null;
		
		if(row == null){
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new EventHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			
			row.setTag(holder);
		} else {
			holder = (EventHolder) row.getTag();
		}
		
		Event event = data[position];
		holder.txtTitle.setText(event.getTitle());
		
		holder.imgIcon.setImageResource(event.getIcon());
		
		return row;
		
	}
	
	static class EventHolder
	{
		long id;
		long time;
		String type;
		
		ImageView imgIcon;
		TextView txtTitle;
	}

}
