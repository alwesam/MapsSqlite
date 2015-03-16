package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vandevsam.sharespot.data.GroupDataManager;
import com.vandevsam.sharespot.data.MarkerDataManager;
import com.vandevsam.sharespot.data.MyGroupObj;
import com.vandevsam.sharespot.data.MyMarkerObj;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class ServerUtilFunctions {

	Context mContext;
	private static final String webServer = "108.59.82.39"; // my google CE ip
															// address
	
	//Error messages
	private static final String status404 = "Requested resource cannot found";
	private static final String status500 = "Error at server side. Try again later";
	private static final String statusOther = "Device might not be connected to network. " +
											"Try again later or restart device";
	
	private static final String jsonInvalid = "Server JSON response might be invalid";

	GroupDataManager group_data;
	MarkerDataManager marker_data;
	ProgressDialog prgDialog;

	public ServerUtilFunctions(Context c) {
		mContext = c;
	}

	public ServerUtilFunctions(Context c, String message) {
		mContext = c;
		// Initialize Progress Dialog properties
		prgDialog = new ProgressDialog(mContext);
		prgDialog.setMessage(message);
		prgDialog.setCancelable(false);
	}

	public void joinGroup(String member, final String group) {

		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		params.put("user", member);
		params.put("group", group);

		client.post("http://" + webServer
				+ "/b/phpfiles/phplogin/join_group.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject jObject = new JSONObject(response);
							if (jObject.getBoolean("status")) {

								Toast.makeText(mContext,
										"Successfully joined " + group + "!",
										Toast.LENGTH_LONG).show();

								// update join status!
								group_data = new GroupDataManager(mContext);
								group_data.open();
								// update join status
								group_data.updateStatus(group, "yes");
								group_data.close();

								// make sure it's checked!
								// TODO move into a method
								SaveGroupPreference pref = new SaveGroupPreference(
										mContext);
								List<String> name = new ArrayList<String>();
								List<Boolean> check = new ArrayList<Boolean>();
								name.add(group);
								check.add(true);
								pref.checkPref(name, check);
								// now download markers
								prgDialog.setMessage("Downloading Markers...");
								syncMySQLDBSQLite();
							}
						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}

					}
					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});

	}
	
	public void leaveGroup(String member, final String group) {

		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		params.put("user", member);
		params.put("group", group);
		// TODO temp fix!!

		client.post("http://" + webServer
				+ "/b/phpfiles/phplogin/leave_group.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject jObject = new JSONObject(response);
							if (jObject.getBoolean("status")) {

								Toast.makeText(mContext,
										"Successfully left " + group + "!",
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}

					}
					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});

	}

	// TODO combine this with the above
	public void listAllGroup(String member) {
		group_data = new GroupDataManager(mContext);
		group_data.open();
		group_data.clear();
		prgDialog.show();
		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		params.put("user", member);
		client.post("http://" + webServer
				+ "/b/phpfiles/phplogin/list_all_group.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						prgDialog.hide();

						try {
							JSONArray arr = new JSONArray(response);
							// If no of array elements is not zero
							if (arr.length() != 0) {
								// Loop through each array element, get JSON
								// object which id,title,snippet,position
								for (int i = 0; i < arr.length(); i++) {
									// Get JSON object
									JSONObject obj = (JSONObject) arr.get(i);
									if (!group_data.queryGroup(obj.get("group")
											.toString()))
										group_data.createGroup(new MyGroupObj(
												obj.get("group").toString(),
												obj.get("description")
														.toString(), obj.get(
														"type").toString(), obj
														.get("join_status")
														.toString()));

								}
								Toast.makeText(mContext,
										"Groups successfully listed",
										Toast.LENGTH_LONG).show();
							}

							group_data.close();

							// now download markers
							prgDialog.setMessage("Downloading Markers...");
							syncMySQLDBSQLite();

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(mContext, jsonInvalid,
									Toast.LENGTH_LONG).show();
						}
					}

					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// create a group
	public void createGroup(String user, String group, String description,
			String type) {
		// Create AsycHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		prgDialog.show();
		params.put("name", user);
		params.put("group", group);
		params.put("description", description);
		params.put("type", type);

		client.post("http://" + webServer
				+ "/b/phpfiles/phplogin/create_group.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// Hide ProgressBar
						prgDialog.hide();

						try {
							JSONObject jObject = new JSONObject(response);
							if (jObject.getBoolean("status")) {
								Toast.makeText(mContext,
										"Group successfully created!",
										Toast.LENGTH_LONG).show();

							}
						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}

					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// syncing markers from local sqlite to mysql
	public void syncSQLiteMySQLDB() {
		marker_data = new MarkerDataManager(mContext);
		marker_data.open();
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		prgDialog.show();
		params.put("locationsJSON", marker_data.composeJSONfromSQLite());
		client.post("http://" + webServer + "/b/phpfiles/insertmarker.php",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						;
						prgDialog.hide();
						try {
							// TODO review this logic, doesn't come across as
							// right
							int count = 0;
							int flag = 0;
							JSONArray arr = new JSONArray(response);
							for (int i = 0; i < arr.length(); i++) {
								flag = 1;
								JSONObject obj = (JSONObject) arr.get(i);
								marker_data.updateSyncStatus(obj.get("id")
										.toString(), obj.get("status")
										.toString());
								if (obj.get("status").toString()
										.equalsIgnoreCase("no"))
									count++;
							}
							
							if (flag==1) {
									if (count > 0)
										Toast.makeText(mContext,
												count+ " markers were not uploaded to remote server",
												Toast.LENGTH_LONG).show();
									else
										Toast.makeText(mContext,
												"Markers successfully uploaded",
												Toast.LENGTH_LONG).show();
							}

						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// download markers from remote MySQL to local SQLite DB
	public void syncMySQLDBSQLite() {
		// Create AsycHttpClient object
		marker_data = new MarkerDataManager(mContext);
		marker_data.open();
		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		prgDialog.show();
		// Make Http call to getusers.php
		// params.put("group", group);
		group_data = new GroupDataManager(mContext);
		group_data.open();
		params.put("groupsJSON", group_data.composegroupJSONfromSQLite());
		group_data.close();
		// TODO fix!!! fix sellocations!
		client.post("http://" + webServer + "/b/phpfiles/getsellocations.php",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// Hide ProgressBar
						prgDialog.hide();

						try {
							// Extract JSON array from the response
							JSONArray arr = new JSONArray(response);

							// If no of array elements is not zero
							if (arr.length() != 0) {
								// Loop through each array element, get JSON
								// object which id,title,snippet,position
								for (int i = 0; i < arr.length(); i++) {
									// Get JSON object
									JSONObject obj = (JSONObject) arr.get(i);
									// Insert User into SQLite DB
									// double check if data already exists in
									// database
									if (!marker_data.queryPosition(obj.get(
											"position").toString())
											&& !marker_data.queryAddress(obj
													.get("title").toString())) {
										// since info is received from server,
										// by default it's synced to server
										marker_data.addMarker(new MyMarkerObj(
												obj.get("title").toString(),
												obj.get("snippet").toString(),
												obj.get("position").toString(),
												obj.get("group").toString(),
												"yes")); // TODO fix it in the
															// backend
										Toast.makeText(
												mContext,
												"Markers successfully obtained from remote server ",
												Toast.LENGTH_LONG).show();
									}
								}
								//TODO put a conditional
								prgDialog.setMessage("Uploading Markers...");
								syncSQLiteMySQLDB();
							}
						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}

					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// TODO temp fix
	public void removeMarker(String coordinates, String group) {

		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		params.put("coordinates", coordinates);
		params.put("group", group);

		client.post("http://" + webServer + "/b/phpfiles/removemarker.php",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {

						try {
							JSONObject jObject = new JSONObject(response);
							if (jObject.getBoolean("status")) {
								Toast.makeText(mContext, "marker removed",
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Toast.makeText(mContext,
									jsonInvalid,
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}

					// When error occurred
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide ProgressBar
						// prgDialog.hide();
						if (statusCode == 404) {
							Toast.makeText(mContext,
									status404,
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(mContext,
									status500,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext,
									statusOther,
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

}
