package com.example.arafatm.anti_socialmedia.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseUser;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNicknameFragment extends DialogFragment {

    @BindView(R.id.etNickname) EditText etNickname;
    @BindView(R.id.btDone) Button btDone;
    String nickname;
    ParseUser member;
    OnFragmentInteractionListener mListener;
    Fragment callback;

    public EditNicknameFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void onFinishEditNickname(String nickname, ParseUser member);
    }

    public static EditNicknameFragment newInstance(String name, ParseUser user) {
        EditNicknameFragment fragment = new EditNicknameFragment();
        Bundle args = new Bundle();
        args.putString("oldName", name);
        args.putParcelable("member", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = getTargetFragment();
            mListener = (OnFragmentInteractionListener) callback;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement onFinishEditNickname interface");
        }
        nickname = getArguments().getString("oldName");
        member = Parcels.unwrap(getArguments().getParcelable("member"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_nickname, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        etNickname.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname = etNickname.getText().toString();
                mListener.onFinishEditNickname(nickname, member);
            }
        });
    }
}
