package com.metronome.ui.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.gmail.specifickarma.metronome.R;

public class ReviewDialog extends Dialog {

    public ReviewDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.fragment_dialog);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_dialog).setOnClickListener(v -> {
            dismiss();
            getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getContext().getPackageName())));
        });

        findViewById(R.id.btn_dialog2).setOnClickListener(v -> dismiss());
    }
}
