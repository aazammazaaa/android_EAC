package com.tmz.ian.activity;import android.Manifest;import android.content.Intent;import android.content.pm.PackageManager;import android.os.Build;import android.os.Bundle;import android.support.v4.app.ActivityCompat;import android.support.v4.app.FragmentManager;import android.support.v4.content.ContextCompat;import android.support.v4.content.LocalBroadcastManager;import android.support.v7.app.AppCompatActivity;import android.text.TextUtils;import com.android.volley.Cache;import com.android.volley.DefaultRetryPolicy;import com.android.volley.Request;import com.android.volley.Response;import com.android.volley.VolleyError;import com.tmz.ian.util.Constants;import com.tmz.ian.util.app.IANappApplication;import com.tmz.ian.util.network.volley.JsonRequest;import com.tmz.ian.util.network.volley.VolleyErrorListener;import org.json.JSONException;import org.json.JSONObject;import java.io.UnsupportedEncodingException;import java.util.EnumSet;import java.util.HashMap;import java.util.Map;/** * Containing common features to be used in application Activitiy */public abstract class BaseActivity extends AppCompatActivity {    protected FragmentManager fm;    public static final String TAPP_INTENT_ACTION = "TAPP_INTENT_ACTION";    public static final String TAPP_INTENT_ACTION_VALIDATION_CHECKER = "TAPP_INTENT_ACTION_VALIDATION_CHECKER";    public static final String TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_ONE = "TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_ONE";    public static final String TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_TWO = "TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_TWO";    public static final String TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_THREE = "TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_THREE";    public static final String TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_FOUR = "TAPP_INTENT_ACTION_VALIDATION_CHECKER_PAGE_FOUR";    public enum ACTION {        CAMERA(11), READ_CONTACTS(12), WRITE_EXTERNAL_STORAGE(13);        private int requestCode;        ACTION(int code) {            this.requestCode = code;        }        public int getRequestCode() {            return this.requestCode;        }        private static final Map<Integer, ACTION> lookup = new HashMap<>();        static {            for (ACTION act : EnumSet.allOf(ACTION.class)) {                lookup.put(act.getRequestCode(), act);            }        }        public static ACTION fromOrdinal(int ordinal) {            return lookup.get(ordinal);        }    }    @Override    protected void onResume() {        super.onResume();    }    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        fm = getSupportFragmentManager();    }    public void checkPermission(ACTION action) {        checkPermission(action, action.getRequestCode());    }    public void checkPermission(ACTION action, int code) {        String permission = null;        if (action.equals(ACTION.CAMERA)) {            permission = Manifest.permission.CAMERA;        } else if (action.equals(ACTION.READ_CONTACTS)) {            permission = Manifest.permission.READ_CONTACTS;        }        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {            if (action == null) {                throw new RuntimeException("Invalid Runtime Permission.");            }            // Assume thisActivity is the current activity            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);            // Here, thisActivity is the current activity            if (permissionCheck                    != PackageManager.PERMISSION_GRANTED) {                // Should we show an explanation?                // if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {                // Show an expanation to the user *asynchronously* -- don't block                // this thread waiting for the user's response! After the user                // sees the explanation, try again to request the permission.                // } else {                // No explanation needed, we can request the permission.                ActivityCompat.requestPermissions(this, new String[]{permission}, code);                //}            } else {                onRequestPermissionsResult(code, new String[]{permission}, new int[]{PackageManager.PERMISSION_GRANTED});            }        } else {            onRequestPermissionsResult(code, new String[]{permission}, new int[]{PackageManager.PERMISSION_GRANTED});        }    }    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {        ACTION action = ACTION.fromOrdinal(requestCode);        if (grantResults.length > 0                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {            // permission was granted, yay! Do the            // contacts-related task you need to do.            onPermissionGranted(action,requestCode);        } else {            onPermissionRevoked(action,requestCode);            // permission denied, boo! Disable the            // functionality that depends on this permission.        }    }    /*public void switchFragment(Fragment mTaskFragment) {        UiUTil.switchFragment(fm, mTaskFragment, false, getTagName(),                R.anim.anim_scale_in, R.anim.anim_scale_out);    }    public void switchFragment(Fragment mTaskFragment, boolean addToBackStack, String tag,                               int enterAnim, int exitAnim) {        UiUTil.switchFragment(fm, mTaskFragment, false, getTagName(),                R.anim.anim_scale_in, R.anim.anim_scale_out);    }*/    protected abstract String getTagName();    protected void onPermissionGranted(ACTION action,int requestCode) {    }    protected void onPermissionRevoked(ACTION action,int requestCode) {    }  /*  @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        BaseFragment mTaskFragment = (BaseFragment) fm.findFragmentByTag(getTagName());        if (mTaskFragment != null) {            mTaskFragment.onActivityResult(requestCode, resultCode, data);        }    }*/    public BaseActivity getBaseActivity() {        return this;    }    public abstract void showProgress(boolean show, String tag);    public abstract void onSuccess(JSONObject response, String tag);    public abstract void onError(VolleyError error, String message, String tag);    protected void loadJsonData(String url, String formattedJson,                                final String reqTag) {        loadJsonData(Request.Method.POST, url, formattedJson, reqTag,false);    }    protected void loadJsonData(String url, String formattedJson,                                final String reqTag,boolean shouldCache) {        loadJsonData(Request.Method.POST, url, formattedJson, reqTag,shouldCache);    }    /**     * Method to make json object request where json response starts wtih {     */    protected void loadJsonData(int type, String url, String formattedJson,                                final String reqTag,boolean shouldCache) {        url = Constants.getBaseAPIUrl() + url;        // Show a progress spinner, and kick off a background task to        // perform the user login attempt.        showProgress(true, reqTag);        Cache cache = IANappApplication.getInstance().getRequestQueue().getCache();        Cache.Entry entry = cache.get(url);        if (entry != null) {            try {//                String data = new String(entry.data, Constants.CHARSET);                String data = new String(entry.data, Constants.CONTENT_TYPE_JSON);                JSONObject response = new JSONObject(data);                onSuccess(response, reqTag);            } catch (JSONException je) {                je.printStackTrace();            } catch (UnsupportedEncodingException e) {                e.printStackTrace();            }            showProgress(false, reqTag);        } else {            JSONObject reqParams = null;            try {                reqParams = TextUtils.isEmpty(formattedJson) ? null                        : new JSONObject(formattedJson);            } catch (JSONException ex) {                ex.printStackTrace();            }            JsonRequest jsonObjReq = new JsonRequest(type, url, reqParams,                    new Response.Listener<JSONObject>() {                        @Override                        public void onResponse(JSONObject response) {                            onSuccess(response, reqTag);                            showProgress(false, reqTag);                        }                    }, new VolleyErrorListener(getApplicationContext()) {                @Override                public void handleVolleyError(VolleyError error,                                              String message) {                    onError(error, message, reqTag);                    showProgress(false, reqTag);                }            });            jsonObjReq.setShouldCache(shouldCache);            // Adding request to request queue            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));            IANappApplication.getInstance().addToRequestQueue(jsonObjReq);        }    }    /*Use Send Broadcast Reciever*/    protected void sendLocalBroadcast(String data, int resultCode, int requestCode) {        Intent intent = new Intent(BaseActivity.TAPP_INTENT_ACTION);        intent.setAction(BaseActivity.TAPP_INTENT_ACTION);        intent.putExtra("dataURI", data);        intent.putExtra("requestCode", requestCode);        intent.putExtra("resultCode", resultCode);        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);    }    @Override    public void onBackPressed() {        super.onBackPressed();    }}