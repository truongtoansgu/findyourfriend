package com.sgu.findyourfriend.screen;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

import com.sgu.findyourfriend.R;

public class BaseFragment extends Fragment {
	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.replace(R.id.container_framelayout, fragment);
		transaction.commit();
		// getChildFragmentManager().executePendingTransactions();
	}

	public boolean popFragment() {
		Log.e("test", "pop fragment: "
				+ getFragmentManager().getBackStackEntryCount());
		boolean isPop = false;
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getFragmentManager().popBackStack();
		}
		return isPop;
		// return true;
	}
}