package com.unovo.carmanager.ui.chat.input;

/**
 * Created by chenchao on 16/1/22.
 * 用于控制输入法面板的弹出关闭
 */
public interface KeyboardControl {
  void showSystemInput(boolean show);

  void showVoiceInput();

  void showEmojiInput();

  void hideCustomInput();

  void OnVoiceRcdInitReuqest();

  void OnVoiceRcdStartRequest();

  void OnVoiceRcdCancelRequest();

  void OnVoiceRcdStopRequest();

  void OnSendTextMessageRequest(CharSequence text);
}
