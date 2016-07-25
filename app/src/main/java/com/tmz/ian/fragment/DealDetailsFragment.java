package com.tmz.ian.fragment;import android.app.ProgressDialog;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.support.annotation.Nullable;import android.util.Log;import android.util.TypedValue;import android.view.LayoutInflater;import android.view.MotionEvent;import android.view.ScaleGestureDetector;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import android.widget.Toast;import com.android.volley.Request;import com.android.volley.VolleyError;import com.tmz.ian.R;import com.tmz.ian.util.Constants;import com.tmz.ian.util.network.NetworkUtil;import org.json.JSONArray;import org.json.JSONObject;/** * Created by Ratan on 7/29/2015. */public class DealDetailsFragment extends BaseFragment implements View.OnTouchListener {    ScaleGestureDetector scaleGestureDetector;    private TextView mDealDescriptionTextView,mDealName_TextView,mIsSponseredTextview,mDealdateTextView,mDealByTextView,mDealByDesgTextView,            mDealStatusTextView,mDealAmountTextView,mAddresstextView,assigneeName,mSectionTextView,mDealTypeTextview,mDealAttachmentCount,mDealCommentsCount;    private ImageView mMesseageImageView,mDealceoImage,mAttachmentImageView;    private ProgressDialog mDialog;    private String dealId ;    private View mView;    Context mContext;    private NetworkUtil networkUtil;    @Nullable    @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        mView =  inflater.inflate(R.layout.deal_detail_main_fragment,null);        bindView(mView);        Bundle args = getArguments();        if(args != null)        {            dealId = args.getString("dealId");        }        //Seting badges on messeage        scaleGestureDetector = new ScaleGestureDetector(getActivity(), new simpleOnScaleGestureListener());        loadJsonData(Request.Method.GET, Constants.DEALSDETAILVIEW + dealId+"/", null, Constants.DEALSDETAILVIEW,false);        return mView;    }    public void bindView(View view)    {        mDealDescriptionTextView = (TextView) view.findViewById(R.id.dealDescriptionTextView);        mDealName_TextView = (TextView) view.findViewById(R.id.dealName_TextView);        mIsSponseredTextview = (TextView) view.findViewById(R.id.isSponseredTextview);        mDealStatusTextView = (TextView) view.findViewById(R.id.dealStatusTextView);        mDealAmountTextView = (TextView) view.findViewById(R.id.dealAmountTextView);        mAddresstextView = (TextView) view.findViewById(R.id.addresstextView);        assigneeName = (TextView) view.findViewById(R.id.assigneeName);        mDealCommentsCount = (TextView) view.findViewById(R.id.discussion_count);        mDealAttachmentCount = (TextView) view.findViewById(R.id.attachment_count);        mSectionTextView = (TextView) view.findViewById(R.id.sectionTextView);        mDealdateTextView= (TextView) view.findViewById(R.id.dealdateTextView);        mDealTypeTextview= (TextView) view.findViewById(R.id.dealTypeTextview);        mDealceoImage = (ImageView) view.findViewById(R.id.dealceoImage);        mAttachmentImageView = (ImageView) view.findViewById(R.id.attachmentImageView);        mDealByTextView = (TextView) view.findViewById(R.id.dealByTextView);        mDealByDesgTextView = (TextView) view.findViewById(R.id.dealByDesgTextView);        //dealByTextView = (TextView) view.findViewById(R.id.dealByTextView);        mMesseageImageView =(ImageView)view.findViewById(R.id.messeageImageView);//        mMesseageImageView.setOnClickListener(new View.OnClickListener() {//            @Override//            public void onClick(View v) {//                if(networkUtil.isConnected()) {//                    Intent mainActivityInt = new Intent(getActivity(),DiscussionActivity.class);//                    Bundle dealbundle = new Bundle();//                    dealbundle.putString("dealId",dealId);//                    dealbundle.putInt("pageId",2);//                    mainActivityInt.putExtras(dealbundle);//                    startActivity(mainActivityInt);////                }//                else//                {//                    Toast.makeText(getActivity(),R.string.network_not_connected_error_msg,Toast.LENGTH_LONG).show();//                }//            }//        });    }    @Override    public boolean onTouch(View v, MotionEvent event) {        // mDealDescriptionTextView = (TextView) v.findViewById(R.id.dealDescriptionTextView);        scaleGestureDetector.onTouchEvent(event);        return true;    }    @Override    public void showProgress(boolean show, String tag) {        if (show) {            showDialog();        } else            hideDialog();    }    @Override    public void onSuccess(JSONObject response, String tag) {        Log.v("DEALSDETAILVIEW",response.toString());        /*        {"status":200,"message":"Deal Detail",        "data":{"dealId":4,"dealName":"Cloud computing","dealBy":"Ent three",        "dealStatus":"New","dealType":"Idea","dealSector":"Business","dealCommentsNumber":0,        "isSponsoredDeal":false,"currencyType":"INR",        "dealAttachments":[{"url":"http:\/\/192.168.10.235:8000\/media\/pitch_deck_20160421-131617.xlsx","type":"xlsx"}]        ,"requiredMoney":"2,500,000","dealDate":"2016-04-21","dealNumberOfAttachments":1,"dealTime":"07:46:21",        "dealDescription":"Hello , i need funding","dealReviewMemberName":"",        "dealOwnerDesignation":"Founder, CEO","dealLocation":"delghi, delgi, India"}}         */        if(response!=null)        {            int status = response.optInt("status");            if(status == 200)            {                JSONObject dataObj = response.optJSONObject("data");                int currentDealId= dataObj.optInt("dealId");                String dealName=  dataObj.optString("dealName");                String dealBy = dataObj.optString("dealBy");                String dealStatus = dataObj.optString("dealBy");                String dealType = dataObj.optString("dealType");                String dealSector = dataObj.optString("dealSector");                int dealCommentsNumber = dataObj.optInt("dealCommentsNumber");                String currencyType = dataObj.optString("currencyType");                String requiredMoney = dataObj.optString("requiredMoney");                String dealDate = dataObj.optString("dealDate");                int dealNumberOfAttachments = dataObj.optInt("dealNumberOfAttachments");                String dealTime = dataObj.optString("dealTime");                String dealDescription = dataObj.optString("dealDescription");                String dealReviewMemberName = dataObj.optString("dealReviewMemberName");                String dealOwnerDesignation = dataObj.optString("dealOwnerDesignation");                String dealLocation = dataObj.optString("dealLocation");                Boolean isSponsoredDeal =  dataObj.optBoolean("isSponsoredDeal");                JSONArray dealAttachmentsArrayObj = dataObj.optJSONArray("dealAttachments");//                showBadge(mAttachmentImageView,dealNumberOfAttachments);//                showBadge(mMesseageImageView,dealCommentsNumber);                mDealAttachmentCount.setText(""+dealNumberOfAttachments);                mDealCommentsCount.setText(""+dealCommentsNumber);                mDealName_TextView.setText(dealName);                mDealDescriptionTextView.setText(dealDescription);                mAddresstextView.setText(dealLocation);                mDealStatusTextView.setText(dealStatus);                assigneeName.setText(dealReviewMemberName);                mSectionTextView.setText(dealSector);                mDealTypeTextview.setText(dealType);                mDealAmountTextView.setText(requiredMoney);                mDealdateTextView.setText(dealDate);                mDealByDesgTextView.setText(dealOwnerDesignation);                mDealByTextView.setText(dealBy);            }        }    }    @Override    public void onError(VolleyError error, String message, String tag) {    }    /**     * For zoom in and zoom out for textview text size     */    public class simpleOnScaleGestureListener extends            ScaleGestureDetector.SimpleOnScaleGestureListener {        @Override        public boolean onScale(ScaleGestureDetector detector) {            float size = mDealDescriptionTextView.getTextSize();            Log.d("TextSizeStart", String.valueOf(size));            float factor = detector.getScaleFactor();            Log.d("Factor", String.valueOf(factor));            float product = size*factor;            Log.d("TextSize", String.valueOf(product));            mDealDescriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);            size = mDealDescriptionTextView.getTextSize();            Log.d("TextSizeEnd", String.valueOf(size));            return true;        }    }    public void showDialog() {        mDialog = new ProgressDialog(getActivity());        mDialog.setMessage("Please wait...");        mDialog.setCancelable(true);        mDialog.show();    }    public void hideDialog() {        if (mDialog != null && mDialog.isShowing()) {            mDialog.dismiss();        }    }//    public void showBadge(View view,int count)//    {//        BadgeView badge = new BadgeView(getContext(), view);//        badge.setText(""+count);//        badge.setTextColor(Color.parseColor("#000000"));//        badge.setBadgeBackgroundColor(Color.parseColor("#FFFFFF"));//        badge.show();////    }}