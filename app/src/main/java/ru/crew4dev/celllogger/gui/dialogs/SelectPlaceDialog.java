package ru.crew4dev.celllogger.gui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.gui.TowerActivity;

public class SelectPlaceDialog extends Dialog implements android.view.View.OnClickListener {

    private TowerActivity hostActivity;
    private Button yes, no;
    private EditText editGroupName;

    public SelectPlaceDialog(TowerActivity a) {
        super(a);
        this.hostActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_place_dialog);
        yes = findViewById(R.id.buttonOk);
        no = findViewById(R.id.buttonCancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        editGroupName = findViewById(R.id.editGroupName);
        editGroupName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                yes.setEnabled(s.length() > 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                hostActivity.saveNewGroup(editGroupName.getText().toString());
                break;
            case R.id.buttonCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}