package com.aresid.simplepasswordgeneratorapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created on: 06.08.2019
 * For Project: SimplePasswordGeneratorApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */

public class MainFragment
		extends Fragment {

	private static final String TAG = "MainFragment";
	private OnMainFragmentInteractionListener mListener;

	private MainFragment() {

		Log.d(TAG, "MainFragment:true");

	}

	static MainFragment newInstance() {

		Bundle args = new Bundle();

		MainFragment fragment = new MainFragment();
		fragment.setArguments(args);

		return fragment;

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		Log.d(TAG, "onCreate:true");

		super.onCreate(savedInstanceState);

		mListener.removeBackArrow();

	}

	@Override
	public void onDestroyView() {

		Log.d(TAG, "onDestroyView:true");

		super.onDestroyView();

		mListener = null;

	}

	@Override
	public void onAttach(@NonNull Context context) {

		Log.d(TAG, "onAttach:true");

		super.onAttach(context);

		if (context instanceof OnMainFragmentInteractionListener) {

			mListener = (OnMainFragmentInteractionListener) context;

		} else {

			throw new ClassCastException(context.toString() + "must implement OnMainFragmentInteractionListener");

		}

	}

	interface OnMainFragmentInteractionListener {

		void removeBackArrow();

	}

}
