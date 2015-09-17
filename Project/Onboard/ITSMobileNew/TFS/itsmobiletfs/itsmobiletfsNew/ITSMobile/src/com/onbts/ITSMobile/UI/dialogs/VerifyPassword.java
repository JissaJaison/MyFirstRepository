package com.onbts.ITSMobile.UI.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.util.RijndaelCrypt;
import com.onbts.ITSMobile.util.Settings;
@Deprecated
public class VerifyPassword extends DialogFragment {

	EditText _passwordBox;

	public interface VerifyPasswordDialogListener {
		public enum Result {
			OK, Cancel
		};

		void onFinishVerifyPasswordDialog(Result theResult);
	}

	public static VerifyPassword newInstance() {
		VerifyPassword f = new VerifyPassword();

		return f;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View view = inflater.inflate(R.layout.dialog_verify_password, null);
		builder.setView(view);

		_passwordBox = (EditText) view.findViewById(R.id.txt_password);
		Button cancel = (Button) view.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getDialog().cancel();
			}

		});

		Button ok = (Button) view.findViewById(R.id.btn_save);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = _passwordBox.getText().toString();
				RijndaelCrypt crypto = new RijndaelCrypt("Onboard@sosftware");
				if (crypto != null) {
					String encrypted = crypto.encrypt(password);
					if (encrypted != null) {
						password = encrypted;
					}
				}

				if (password.compareTo(Settings.getInstance(getActivity())
						.getSettingAsString("password")) == 0)

				{
					VerifyPasswordDialogListener activity = (VerifyPasswordDialogListener) getActivity();

					activity.onFinishVerifyPasswordDialog(VerifyPasswordDialogListener.Result.OK);

					getDialog().cancel();
				} else {
					_passwordBox
							.setError(getString(R.string.error_incorrect_password));
				}
			}

		});

		return builder.create();
	}
}
