package com.unovo.carmanager.ui.chat.input;

import android.text.TextWatcher;
import android.view.View;

/**
 * Created by chenchao on 16/1/22.
 */
public interface InputOperate {
    String getContent();

    void clearContent();

    void setContent(String s);

    void hideKeyboard();

    void insertText(String s);

    boolean isPopCustomKeyboard();

    void closeCustomKeyboard();

    void setClickSend(View.OnClickListener click);

    void addTextWatcher(TextWatcher textWatcher);
}
