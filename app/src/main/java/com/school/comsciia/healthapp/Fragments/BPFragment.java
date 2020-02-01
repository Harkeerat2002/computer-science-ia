package com.school.comsciia.healthapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.school.comsciia.healthapp.Models.User;
import com.school.comsciia.healthapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BPFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BPFragment extends Fragment {

    private EditText userAge,userGender,userLanguage,userName;
    private Button saveBtn;
    private ProgressBar loadingProgress;

    private OnFragmentInteractionListener mListener;

    public BPFragment() {
        // Required empty public constructor
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
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        userAge = (EditText) view.findViewById(R.id.regAge);
        userGender = (EditText) view.findViewById(R.id.regGender);
        userLanguage = (EditText) view.findViewById(R.id.regLanguage);
        userName = (EditText) view.findViewById(R.id.regName);
        saveBtn = view.findViewById(R.id.saveBtn);
        loadingProgress = view.findViewById(R.id.regProgressBar);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mainSnapshot) {

                User userProfile = mainSnapshot.getValue(User.class);
                userAge.setText(userProfile.age);
                userGender.setText(userProfile.gender);
                userLanguage.setText(userProfile.language);
                userName.setText(userProfile.name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String name = userName.getText().toString();
                final String age = userAge.getText().toString();
                final String gender = userGender.getText().toString();
                final String language = userLanguage.getText().toString();

                UpdateUserAccount(name,age,gender,language);

            }
        });

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

    private void UpdateUserAccount(final String name, final String age, final String gender, final String language) {

        User user = new User(
                age,
                gender,
                language,
                name
        );

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingProgress.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    // user account created successfully
                    showMessage("Account created");
                } else {
                    //display a failure message
                    // account creation failed
                    showMessage("account creation failed" + task.getException().getMessage());
                    saveBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    // simple method to show toast message
    private void showMessage(String message) {

        //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
