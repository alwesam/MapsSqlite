package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveGroupPreference {

	SharedPreferences pref;
	Editor editor;

	private static final String PREFER_NAME = "groupslist";
	public static final String KEY_CHECKED = "IsChecked";
	public static final String KEY_NAME = "group";

	public SaveGroupPreference(Context c) {
		pref = c.getSharedPreferences(PREFER_NAME, 0);
		editor = pref.edit();
	}

	public void checkPref(List<String> group, List<Boolean> checked) {

		Gson gson = new Gson();
		String jsonGroup = gson.toJson(group);
		String jsonChecked = gson.toJson(checked);

		editor.putString(KEY_NAME, jsonGroup);
		editor.putString(KEY_CHECKED, jsonChecked);
		editor.commit();
	}

	// TODO put in a hash map of two arraylist instead of two separate methods!
	public ArrayList<String> getPrefGroup() {

		// experiment
		// Map<String, Boolean> map = new HashMap<String, Boolean>();

		List<String> groups;

		if (pref.contains(KEY_NAME)) {
			String jsonGroup = pref.getString(KEY_NAME, null);
			Gson gson = new Gson();
			groups = Arrays.asList(gson.fromJson(jsonGroup, String[].class));
			groups = new ArrayList<String>(groups);
		} else
			return null;

		return (ArrayList<String>) groups;
	}

	public ArrayList<Boolean> getPrefCheck() {

		List<Boolean> groups;

		if (pref.contains(KEY_CHECKED)) {
			String jsonGroup = pref.getString(KEY_CHECKED, null);
			Gson gson = new Gson();
			groups = Arrays.asList(gson.fromJson(jsonGroup, Boolean[].class));
			groups = new ArrayList<Boolean>(groups);
		} else
			return null;

		return (ArrayList<Boolean>) groups;
	}

}
