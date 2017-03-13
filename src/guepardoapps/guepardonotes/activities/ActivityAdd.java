package guepardoapps.guepardonotes.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import guepardoapps.guepardonotes.R;
import guepardoapps.guepardonotes.common.constants.*;
import guepardoapps.guepardonotes.controller.DatabaseController;
import guepardoapps.guepardonotes.model.Note;

import guepardoapps.toolset.common.Logger;
import guepardoapps.toolset.controller.*;

public class ActivityAdd extends Activity {

	private static final String TAG = ActivityAdd.class.getSimpleName();
	private Logger _logger;

	private String _title;
	private String _content;

	private EditText _editTitle;
	private EditText _editContent;
	private Button _btnSave;

	private Context _context;

	private DatabaseController _databaseController;
	private DialogController _dialogController;
	private NavigationController _navigationController;

	private Runnable _trySaveNewNoteCallback = new Runnable() {
		public void run() {
			_logger.Debug("_trySaveNewNoteCallback run");

			if (_title == "") {
				Toasty.error(_context, "Please enter a title!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (_content == "") {
				Toasty.error(_context, "Please enter a note!", Toast.LENGTH_SHORT).show();
				return;
			}

			_databaseController.SaveNote(new Note(0, _title, _content, 0, 0, 0));
			_finishCallback.run();
		}
	};

	private Runnable _finishCallback = new Runnable() {
		public void run() {
			_logger.Debug("_finishCallback run");

			_navigationController.NavigateTo(ActivityNotes.class, true);
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_add);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR_COLOR));

		_logger = new Logger(TAG, Enables.DEBUGGING_ENABLED);
		_logger.Debug("onCreate");

		_context = this;

		_databaseController = new DatabaseController(_context);
		_dialogController = new DialogController(_context, getResources().getColor(R.color.TextIcon),
				getResources().getColor(R.color.Primary));
		_navigationController = new NavigationController(_context);

		_editTitle = (EditText) findViewById(R.id.addTitle);
		_editTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_title = _editTitle.getText().toString();
			}
		});

		_editContent = (EditText) findViewById(R.id.addContent);
		_editContent.setScroller(new Scroller(_context));
		_editContent.setMaxLines(1);
		_editContent.setVerticalScrollBarEnabled(true);
		_editContent.setMovementMethod(new ScrollingMovementMethod());
		_editContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_content = _editContent.getText().toString();
			}
		});

		_btnSave = (Button) findViewById(R.id.btnSave);
		_btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnSave onClick");

				_trySaveNewNoteCallback.run();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		_logger.Debug("onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (_title != null || _content != null) {
				_dialogController.ShowDialogTriple("Warning!",
						"The created note is not saved! Do you want to save the note?", "Yes", _trySaveNewNoteCallback,
						"No", _finishCallback, "Cancel", _dialogController.CloseDialogCallback, true);
			} else {
				_finishCallback.run();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}