package com.artifex.mupdfdemo;

import java.io.InputStream;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.csair.impc.R;

class ThreadPerTaskExecutor implements Executor {
	public void execute(Runnable r) {
		new Thread(r).start();
	}
}

public class MuPDFActivity extends Activity implements OnClickListener
{
	/* The core rendering instance */
	private MuPDFCore    core;
	private String       mFileName;
	private MuPDFReaderView mDocView;
	private View         mButtonsView;
	private boolean      mButtonsVisible;
	private EditText     mPasswordView;
	private SeekBar      mPageSlider;
	private int          mPageSliderRes;
	private ViewAnimator mTopBarSwitcher;
	private RelativeLayout pdf_title_layout;
	private boolean      mTopBarIsSearch;
	private AlertDialog.Builder mAlertBuilder;
	private boolean    mLinkHighlight = false;
	private final Handler mHandler = new Handler();
	private boolean mAlertsActive= false;
	private boolean mReflow = false;
	private AsyncTask<Void,Void,MuPDFAlert> mAlertTask;
	private AlertDialog mAlertDialog;

	public void createAlertWaiter() {
		mAlertsActive = true;
		// All mupdf library calls are performed on asynchronous tasks to avoid stalling
		// the UI. Some calls can lead to javascript-invoked requests to display an
		// alert dialog and collect a reply from the user. The task has to be blocked
		// until the user's reply is received. This method creates an asynchronous task,
		// the purpose of which is to wait of these requests and produce the dialog
		// in response, while leaving the core blocked. When the dialog receives the
		// user's response, it is sent to the core via replyToAlert, unblocking it.
		// Another alert-waiting task is then created to pick up the next alert.
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		mAlertTask = new AsyncTask<Void,Void,MuPDFAlert>() {

			@Override
			protected MuPDFAlert doInBackground(Void... arg0) {
				if (!mAlertsActive)
					return null;

				return core.waitForAlert();
			}

			@Override
			protected void onPostExecute(final MuPDFAlert result) {
				// core.waitForAlert may return null when shutting down
				if (result == null)
					return;
				final MuPDFAlert.ButtonPressed pressed[] = new MuPDFAlert.ButtonPressed[3];
				for(int i = 0; i < 3; i++)
					pressed[i] = MuPDFAlert.ButtonPressed.None;
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mAlertDialog = null;
						if (mAlertsActive) {
							int index = 0;
							switch (which) {
							case AlertDialog.BUTTON1: index=0; break;
							case AlertDialog.BUTTON2: index=1; break;
							case AlertDialog.BUTTON3: index=2; break;
							}
							result.buttonPressed = pressed[index];
							// Send the user's response to the core, so that it can
							// continue processing.
							core.replyToAlert(result);
							// Create another alert-waiter to pick up the next alert.
							createAlertWaiter();
						}
					}
				};
				mAlertDialog = mAlertBuilder.create();
				mAlertDialog.setTitle(result.title);
				mAlertDialog.setMessage(result.message);
				switch (result.iconType)
				{
				case Error:
					break;
				case Warning:
					break;
				case Question:
					break;
				case Status:
					break;
				}
				switch (result.buttonGroupType)
				{
				case OkCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON2, "Cancel", listener);
					pressed[1] = MuPDFAlert.ButtonPressed.Cancel;
				case Ok:
					mAlertDialog.setButton(AlertDialog.BUTTON1, "Ok", listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Ok;
					break;
				case YesNoCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON3, "Cancel", listener);
					pressed[2] = MuPDFAlert.ButtonPressed.Cancel;
				case YesNo:
					mAlertDialog.setButton(AlertDialog.BUTTON1, "Yes", listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Yes;
					mAlertDialog.setButton(AlertDialog.BUTTON2, "No", listener);
					pressed[1] = MuPDFAlert.ButtonPressed.No;
					break;
				}
				mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						mAlertDialog = null;
						if (mAlertsActive) {
							result.buttonPressed = MuPDFAlert.ButtonPressed.None;
							core.replyToAlert(result);
							createAlertWaiter();
						}
					}
				});

				mAlertDialog.show();
			}
		};

		mAlertTask.executeOnExecutor(new ThreadPerTaskExecutor());
	}

	public void destroyAlertWaiter() {
		mAlertsActive = false;
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
	}

	private MuPDFCore openFile(String path)
	{
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		System.out.println("Trying to open "+path);
		try
		{
			core = new MuPDFCore(path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	private MuPDFCore openBuffer(byte buffer[])
	{
		System.out.println("Trying to open byte buffer");
		try
		{
			core = new MuPDFCore(buffer);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		
		
		mAlertBuilder = new AlertDialog.Builder(this);
		
		
		
		if (core == null) {
			core = (MuPDFCore)getLastNonConfigurationInstance();

			if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
				mFileName = savedInstanceState.getString("FileName");
			}
		}
		if (core == null) {
			Intent intent = getIntent();
			byte buffer[] = null;
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://")) {
					// Handle view requests from the Transformer Prime's file manager
					// Hopefully other file managers will use this same scheme, if not
					// using explicit paths.
					Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
					if (cursor.moveToFirst()) {
						String str = cursor.getString(0);
						String failString = null;
						if (str == null) {
							try {
								InputStream is = getContentResolver().openInputStream(uri);
								int len = is.available();
								buffer = new byte[len];
								is.read(buffer, 0, len);
								is.close();
							}
							catch (java.lang.OutOfMemoryError e)
							{
								System.out.println("Out of memory during buffer reading");
								failString = e.toString();
							}
							catch (Exception e) {
								failString = e.toString();
							}
							if (failString != null)
							{
								buffer = null;
								Resources res = getResources();
								AlertDialog alert = mAlertBuilder.create();
								String contentFailure = res.getString(R.string.content_failure);
								String openFailed = res.getString(R.string.open_failed);
								setTitle(String.format(contentFailure, openFailed, failString));
								alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										});
								alert.show();
								return;
							}
						} else {
							uri = Uri.parse(str);
						}
					}
				}
				if (buffer != null) {
					core = openBuffer(buffer);
				} else {
					core = openFile(Uri.decode(uri.getEncodedPath()));
				}
				SearchTaskResult.set(null);
			}
			if (core != null && core.needsPassword()) {
				requestPassword(savedInstanceState);
				return;
			}
		}
		if (core == null)
		{
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}

		createUI(savedInstanceState);
	}

	public void requestPassword(final Bundle savedInstanceState) {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (core.authenticatePassword(mPasswordView.getText().toString())) {
					createUI(savedInstanceState);
				} else {
					requestPassword(savedInstanceState);
				}
			}
		});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert.show();
	}

	public void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;

		// Now create the UI.
		// First create the document view
		mDocView = new MuPDFReaderView(this) {
			@Override
			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				mPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
				mPageSlider.setProgress(i * mPageSliderRes);
				super.onMoveToChild(i);
			}

			@Override
			protected void onTapMainDocArea() {
				if (!mButtonsVisible) {
					showButtons();
				} else {
					hideButtons();
				}
			}

			@Override
			protected void onDocMotion() {
				hideButtons();
			}
		};
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));


		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		// Set up the page slider
		int smax = Math.max(core.countPages()-1,1);
		mPageSliderRes = ((10 + smax - 1)/smax) * 2;

		// Set the file-name text

		// Activate the seekbar
		mPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDocView.setDisplayedViewIndex((seekBar.getProgress()+mPageSliderRes/2)/mPageSliderRes);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updatePageNumView((progress+mPageSliderRes/2)/mPageSliderRes);
			}
		});

		// Activate the search-preparing button

		// Activate the reflow button

		// Activate the select button

		final Context context = this;


		// Search invoking buttons are disabled while there is no text specified

		// React to interaction with the text widget

		//React to Done button on keyboard


		// Activate search invoking buttons


		// Reenstate last state if it was recorded
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));

		if (savedInstanceState == null || !savedInstanceState.getBoolean("ButtonsHidden", false))
			showButtons();
		if(savedInstanceState != null && savedInstanceState.getBoolean("SearchMode", false))
			searchModeOn();

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		
		//layout.setBackgroundResource(R.color.canvas);
		setContentView(layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode >= 0)
			mDocView.setDisplayedViewIndex(resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance()
	{
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	private void toggleReflow() {
		mReflow = !mReflow;
		if (mReflow) {
			mDocView.setAdapter(new MuPDFReflowAdapter(this, core));
		} else {
			mDocView.setAdapter(new MuPDFPageAdapter(this, core));
		}
		mDocView.refresh(mReflow);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

		if (!mButtonsVisible)
			outState.putBoolean("ButtonsHidden", true);

		if (mTopBarIsSearch)
			outState.putBoolean("SearchMode", true);
	}

	@Override
	protected void onPause() {
		super.onPause();


		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}

	public void onDestroy()
	{
		if (core != null)
			core.onDestroy();
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		core = null;
		super.onDestroy();
	}

	void showButtons() {
		if (core == null)
			return;
		if (!mButtonsVisible) {
			mButtonsVisible = true;
			// Update page number text and slider
			pdf_title_layout.setVisibility(View.VISIBLE);
			int index = mDocView.getDisplayedViewIndex();
			updatePageNumView(index);
			mPageSlider.setMax((core.countPages()-1)*mPageSliderRes);
			mPageSlider.setProgress(index*mPageSliderRes);
			if (mTopBarIsSearch) {
				showKeyboard();
			}

			Animation anim = new TranslateAnimation(0, 0, -mTopBarSwitcher.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mTopBarSwitcher.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageSlider.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	void hideButtons() {
		if (mButtonsVisible) {
			mButtonsVisible = false;
			hideKeyboard();
			pdf_title_layout.setVisibility(View.GONE);
			Animation anim = new TranslateAnimation(0, 0, 0, -mTopBarSwitcher.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mTopBarSwitcher.setVisibility(View.INVISIBLE);
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mPageSlider.setVisibility(View.INVISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	void searchModeOn() {
		if (!mTopBarIsSearch) {
			mTopBarIsSearch = true;
			//Focus on EditTextWidget
			showKeyboard();
			mTopBarSwitcher.setDisplayedChild(1);
		}
	}

	void searchModeOff() {
		if (mTopBarIsSearch) {
			mTopBarIsSearch = false;
			hideKeyboard();
			mTopBarSwitcher.setDisplayedChild(0);
			SearchTaskResult.set(null);
			// Make the ReaderView act on the change to mSearchTaskResult
			// via overridden onChildSetup method.
			mDocView.resetupChildren();
		}
	}

	void updatePageNumView(int index) {
		if (core == null)
			return;
	}

	void makeButtonsView() {
		mButtonsView = getLayoutInflater().inflate(R.layout.buttons,null);
		mPageSlider = (SeekBar)mButtonsView.findViewById(R.id.pageSlider);
		mTopBarSwitcher = (ViewAnimator)mButtonsView.findViewById(R.id.switcher);
		pdf_title_layout = (RelativeLayout) mButtonsView.findViewById(R.id.pdf_title_layout);
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);
		Button pdf_back_btn = (Button) mButtonsView.findViewById(R.id.pdf_back_btn);
		pdf_back_btn.setOnClickListener(this);
		TextView pdf_title  = (TextView) mButtonsView.findViewById(R.id.pdf_title);
		String pdfTielt = getIntent().getStringExtra("pdfTitle");
		pdf_title.setText(pdfTielt);
	}

	void showKeyboard() {}

	void hideKeyboard() {}

	void search(int direction) {}

	@Override
	public boolean onSearchRequested() {
		if (mButtonsVisible && mTopBarIsSearch) {
			hideButtons();
		} else {
			showButtons();
			searchModeOn();
		}
		return super.onSearchRequested();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mButtonsVisible && !mTopBarIsSearch) {
			hideButtons();
		} else {
			showButtons();
			searchModeOff();
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		if (core != null)
		{
			core.startAlerts();
			createAlertWaiter();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		if (core != null)
		{
			destroyAlertWaiter();
			core.stopAlerts();
		}

		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (core.hasChanges()) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which == AlertDialog.BUTTON_POSITIVE)
						core.save();

					finish();
				}
			};
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle("MuPDF");
			alert.setMessage("Document has changes. Save them?");
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", listener);
			alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No", listener);
			alert.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pdf_back_btn:
			finish();
			break;

		default:
			break;
		}
	}
}
