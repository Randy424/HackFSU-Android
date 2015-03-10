package com.hackfsu.hackfsu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import org.json.JSONArray;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

/**
 * Countdown Fragment.
 *
 * ToDo:
 * Add Logo in middle.
 * Fix timing - make it so that it counts down 30hrs.
 * Improve look.
 *
 * @author Iosif
 */
public class CountdownFragment extends Fragment {
    public CountdownFragment() {
        // Required empty public constructor
    }

    private static final String TAG_TITLE = "title";
    private static final String TAG_START = "startTime";
    private static final String TAG_END = "endTime";
    private static final String TL = "HackFsu";

    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    CountDownTimer countDownTimer;

    JSONArray countdowns = null;
    ArrayList<HashMap<String,String>> countdownsList;
    ListView lv;
    View rootView;
    private OnFragmentInteractionListener mListener;

    //CircleThangs
    private HoloCircularProgressBar mHoloCircularProgressBar;
    private ObjectAnimator mProgressBarAnimator;
    private TextView countTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_countdown, container, false);
        mHoloCircularProgressBar = (HoloCircularProgressBar) rootView.findViewById(R.id.countdown);
        countTv = (TextView) rootView.findViewById(R.id.countdownTV);

        if(mProgressBarAnimator != null)
            mProgressBarAnimator.cancel();

        countdownsList = new ArrayList<HashMap<String, String>>();
        new ParseUpdates().execute();


        //Display Countdown Until Hacking Ends
        //todo delete below
        //Calculate how many seconds until Hackathon ends.
        //Set it to progress.
        //animate(mHoloCircularProgressBar, null, 1f, 5000);
        //mHoloCircularProgressBar.setMarkerProgress(1f);
        //countTv.setText("HackFSU in " + day + " days!");

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        PushService.subscribe(getActivity(), "Countdowns", MainSplashActivity.class);
        lv = (ListView) getActivity().findViewById(R.id.countdown_list);
        countTv = (TextView) getActivity().findViewById(R.id.countTv);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Animate.
     *
     * @param progressBar the progress bar
     * @param listener the listener
     */
    private void animate(final HoloCircularProgressBar progressBar,
                         final Animator.AnimatorListener listener) {

        final float progress = (float) (Math.random() * 2);
        int duration = 60000;
        animate(progressBar, listener, progress, duration);
    }

    private void animate(final HoloCircularProgressBar progressBar, final Animator.AnimatorListener listener,
                         final float progress, final int duration) {
        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        mProgressBarAnimator.setDuration(duration);
        mProgressBarAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {
            }
            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(progress);
            }
            @Override
            public void onAnimationRepeat(final Animator animation) {
            }
            @Override
            public void onAnimationStart(final Animator animation) {
            }
        });
        if (listener != null) {
            mProgressBarAnimator.addListener(listener);
        }
        mProgressBarAnimator.reverse();
        mProgressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

    /*
    *
    * Parse Things
    *
    * */
    private class ParseUpdates extends AsyncTask<String,String, List<ParseObject>> {
        private ProgressDialog loadDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog = new ProgressDialog(getActivity());
            loadDialog.setMessage("Getting Count .. probably.");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(true);
            loadDialog.show();
        }

        @Override
        protected List<ParseObject> doInBackground(String... args) {
            List<ParseObject> countdwnsList = null;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Countdowns");
            query.orderByDescending("createdAt");

            try {
                countdwnsList = query.find();
                return countdwnsList;
            } catch (ParseException e) {
                e.printStackTrace();
                return countdwnsList;
            }
        }

        @Override
        protected void onPostExecute(List<ParseObject> countdwnsItems) {
            super.onPostExecute(countdwnsItems);
            loadDialog.dismiss();

            if (countdwnsItems != null) {
                // looping through all updates
                for (int i = 0; i < countdwnsItems.size(); i++) {

                    // tmp hashmap for single update
                    HashMap<String, String> count = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    count.put(TAG_TITLE, countdwnsItems.get(i).getString("title"));

                    Format formatter = new SimpleDateFormat("h:mma", Locale.US);
                    String s = formatter.format(countdwnsItems.get(i).getDate(
                            "startTime"));
                            //formatter.format(countdwnsItems.get(i).getDate("endTime"));
                    count.put(TAG_START, s);
                    count.put(TAG_END, countdwnsItems.get(i).getString("endTime"));

                    // adding to list
                    countdownsList.add(count);

                    //Getting current, start, and end dates as well as the
                    //difference between current and end dates
                    startDate.setTime(countdwnsItems.get(0).getDate(TAG_START));
                    Calendar current = Calendar.getInstance();
                    endDate.setTime(countdwnsItems.get(0).getDate(TAG_END));

                    //todo finish countdown timer: determine what will be the passed in values, what gets updated on tick,
                    //countDownTimer updates the time value according to the
                    // current system time and end value. It displays the time
                    // left in the format: dd:hh:mm. Any values which become 0's
                    // are removed along with the ":" after them except for hh:mm.
                    //      d = Day, h = Hour, m = Minute
                    countDownTimer = new CountDownTimer(endDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 60000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //lengths of time in milliseconds
                            long remainingTime = millisUntilFinished;
                            int day = 86400000;
                            int hour = 3600000;
                            int minute = 60000;
                            int second = 1000;
                            String text = "";

                            if (remainingTime >= day) {
                                text = (remainingTime/day) + ":";
                                remainingTime = remainingTime%day;
                                countTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 66);
                            }else{
                                countTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 88);
                            }
                            //***NOTE: To include seconds
                            // Uncomment all line below in this method
                            // and change the count down interval above from 60000 to 1000
                            //if (remainingTime >= hour) {
                                text += String.format("%02d:",(remainingTime/hour));
                                remainingTime = remainingTime%hour;
                            //}
                            text += String.format("%02d", (remainingTime / minute));
                            //remainingTime = remainingTime % minute;
                            //if (remainingTime < hour) {
                            //    text += String.format(":%02d",(remainingTime/second));
                            //}

                            countTv.setTextColor(Color.BLACK);
                            countTv.setText(text);
                            Log.d(TL,"CountDownTimer has gone into onTick and set text = " + text);
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TL, "CountDownTimer has gone into onFinish");
                            countTv.setText("00:00");
                            countTv.setTextColor(Color.RED);
                        }
                    };

                    // VERY IMPORTANT: THIS IS WHERE THE COUNTER IS CALLED TO START
                    countDownTimer.start();

                    //Setting up the Progress Circle
                    float progress = 0f;
                    int duration = (int)(endDate.getTimeInMillis() - startDate.getTimeInMillis());
                    if (duration > 0) {
                        long timeDiff = endDate.getTimeInMillis() - current.getTimeInMillis();
                        progress = timeDiff / duration;
                    }else{
                        duration = 0;
                    }

                    animate(mHoloCircularProgressBar, null, progress, duration);
                    mHoloCircularProgressBar.setMarkerProgress(progress);
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }


            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(CountdownFragment.this.getActivity(),
                    countdownsList,R.layout.countdown_item,
                    new String[] { TAG_TITLE, TAG_START },
                    new int[] { R.id.countdownTV, R.id.startTimeTV });

            lv.setAdapter(adapter);
        }
    }
}
