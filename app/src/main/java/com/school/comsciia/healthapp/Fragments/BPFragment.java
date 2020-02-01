package com.school.comsciia.healthapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.school.comsciia.healthapp.Models.DateValue;
import com.school.comsciia.healthapp.Models.User;
import com.school.comsciia.healthapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SugarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SugarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BPFragment extends Fragment {

    private EditText bpValue;
    private Button saveBtn;
    private ProgressBar loadingProgress;
    private LineChart lineChart;
    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    private OnFragmentInteractionListener mListener;

    public BPFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BPFragment newInstance(String param1, String param2) {
        BPFragment fragment = new BPFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bp, container, false);

        bpValue = (EditText) view.findViewById(R.id.bpValue);
        saveBtn = view.findViewById(R.id.saveBtn);
        loadingProgress = view.findViewById(R.id.regProgressBar);
        lineChart = (LineChart) view.findViewById(R.id.lineChart);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String bp = bpValue.getText().toString();

                SaveData(bp);
                retriveData();
                //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

            }
        });

        retriveData();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void SaveData(final String bp) {

        DateValue value = new DateValue(bp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BP")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String id = ref.push().getKey();
        ref.child(id).setValue(value);
        showMessage("Value Saved");
        saveBtn.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.INVISIBLE);
    }

    private void retriveData() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BP")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> dataVals = new ArrayList<>();

                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        DateValue value = myDataSnapshot.getValue(DateValue.class);
                        dataVals.add(new Entry(value.date,Integer.parseInt(value.value)));
                    }
                }

                showChart(dataVals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showChart(ArrayList<Entry> dataVals){
        lineDataSet.setValues(dataVals);
        lineDataSet.setLabel("BP Chart");
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.saveToGallery("BP.jpg");
    }




    private void showMessage(String message) {

    }
}
