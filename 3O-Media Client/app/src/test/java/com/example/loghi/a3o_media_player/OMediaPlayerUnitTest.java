package com.example.loghi.a3o_media_player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.media.MediaPlayer;


import static junit.framework.TestCase.assertTrue;

public class OMediaPlayerUnitTest {
    private static final String SAMPLE_PATH ="";
    private static final String REMOTE_PATH ="https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3";
    private static final String TAG = "MediaPlayerSeekToStateUnitTest";
    private static final int SEEK_TO_END  = 135110;  // Milliseconds.
    private static int WAIT_FOR_COMMAND_TO_COMPLETE = 1000;  // Milliseconds.

    private MediaPlayer mMediaPlayer = null;
    private boolean mInitialized = false;
    private boolean mOnCompletionHasBeenCalled = false;
    private Looper mLooper = null;
    private final Object lock = new Object();
    // An Handler object is absolutely necessary for receiving callback
    // messages from MediaPlayer objects.
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /*
            switch(msg.what) {
                case MediaPlayerStateErrors.MEDIA_PLAYER_ERROR:
                    Log.v(TAG, "handleMessage: received MEDIA_PLAYER_ERROR message");
                    break;
                default:
                    Log.v(TAG, "handleMessage: received unknown message");
                break;
            }
            */
        }
    };

    private void setMediaPlayerToIdleStateAfterReset() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.reset();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToIdleStateAfterReset: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToInitializedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToInitializedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToPreparedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToPreparedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToPreparedStateAfterStop() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.stop();
            mMediaPlayer.prepare();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToPreparedStateAfterStop: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToStartedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToStartedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToStartedStateAfterPause() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.pause();

            // pause() is an asynchronous call and returns immediately, but
            // PV player engine may take quite a while to actually set the
            // player state to Paused; if we call start() right after pause()
            // without waiting, start() may fail.
            try {
                Thread.sleep(2000);
            } catch(Exception ie) {
                Log.v(TAG, "sleep was interrupted and terminated prematurely");
            }

            mMediaPlayer.start();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToStartedStateAfterPause: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToPausedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.pause();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToPausedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToStoppedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.stop();
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToStoppedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
    }

    private void setMediaPlayerToPlaybackCompletedState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(SEEK_TO_END);
            mMediaPlayer.start();
            synchronized(lock) {
                try {
                    lock.wait(WAIT_FOR_COMMAND_TO_COMPLETE);
                } catch(Exception e) {
                    Log.v(TAG, "setMediaPlayerToPlaybackCompletedState: wait was interrupted.");
                }
            }
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToPlaybackCompletedState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(false);
        }
        Log.v(TAG, "setMediaPlayerToPlaybackCompletedState: done.");
    }

    /*
     * There are a lot of ways to force the MediaPlayer object to enter
     * the Error state. The impact (such as onError is called or not) highly
     * depends on how the Error state is entered.
     */
    private void setMediaPlayerToErrorState() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(REMOTE_PATH);
            mMediaPlayer.start();
            synchronized(lock) {
                try {
                    lock.wait(WAIT_FOR_COMMAND_TO_COMPLETE);
                } catch(Exception e) {
                    Log.v(TAG, "setMediaPlayerToErrorState: wait was interrupted.");
                }
            }
        } catch(Exception e) {
            Log.v(TAG, "setMediaPlayerToErrorState: Exception " + e.getClass().getName() + " was thrown.");
            assertTrue(e instanceof IllegalStateException);
        }
        Log.v(TAG, "setMediaPlayerToErrorState: done.");
    }


    private void notifyStateError() {
        mHandler.sendMessage(mHandler.obtainMessage());
    }


    private void checkMethodUnderTestInAllPossibleStates() {

    }

    /*
     * Terminates the message looper thread.
     */
    private void terminateMessageLooper() {
        mLooper.quit();
        mMediaPlayer.release();
    }

}


