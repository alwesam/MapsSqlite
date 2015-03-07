package com.vandevsam.sharespot;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AddressAdapter extends ArrayAdapter<String> {

	private List<String> list;
	private Context ctx;

	HashMap<String, List<String>> groups;

	AddressAdapter(List<String> a, HashMap<String, List<String>> h, Context c) {
		super(c, R.layout.list_search_item, a);
		list = a;
		ctx = c;
		groups = h;
	}

	@Override
	public int getItemViewType(int position) {
		return (groups.containsKey(list.get(position))) ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int type = getItemViewType(position);
		if (v == null) {
			// Inflate the layout according to the view type
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (type == 0) {
				// Inflate the layout with image
				v = inflater.inflate(R.layout.list_search_item_bolded, parent,
						false);
			} else {
				v = inflater.inflate(R.layout.list_search_item, parent, false);
			}
		}
		TextView strdisplay = (TextView) v
				.findViewById(R.id.list_search_item_textview);
		strdisplay.setText(list.get(position));

		// strdisplay.setText("Hit");

		return v;
	}

}
