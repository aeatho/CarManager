package com.unovo.carmanager.ui.chat.input;

/**
 * Created by chenchao on 16/1/22.
 * 录音完成回调接口
 */
public interface VoiceRecordCompleteCallback {
    void recordFinished(long duration, String voicePath);
}