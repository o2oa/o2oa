package jiguang.chat.pickerimage.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import jiguang.chat.pickerimage.view.UIView;


public abstract class BaseFragment extends Fragment {
    private static final Handler handler = new Handler();

    private int containerId;

    private boolean destroyed;

    protected final boolean isDestroyed() {
        return destroyed;
    }

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        destroyed = false;
    }

    public void onDestroy() {
        super.onDestroy();


        destroyed = true;
    }

    protected final Handler getHandler() {
        return handler;
    }

    protected final void postRunnable(final Runnable runnable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // validate
                // TODO use getActivity ?
                if (!isAdded()) {
                    return;
                }

                // run
                runnable.run();
            }
        });
    }

    protected final void postDelayed(final Runnable runnable, long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // validate
                // TODO use getActivity ?
                if (!isAdded()) {
                    return;
                }

                // run
                runnable.run();
            }
        }, delay);
    }

    protected void showKeyboard(boolean isShow) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        if (isShow) {
            if (activity.getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(activity.getCurrentFocus(), 0);
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }

    protected void hideKeyboard(View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        imm.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected <T extends View> T findView(int resId) {
        return (T) (getView().findViewById(resId));
    }

    protected void setToolBar(int toolbarId, int titleId, int logoId) {
        if (getActivity() != null && getActivity() instanceof UIView) {
            ((UIView)getActivity()).setToolBar(toolbarId, titleId, logoId);
        }
    }
    protected void setTitle(int titleId) {
        if (getActivity() != null && getActivity() instanceof UIView) {
            getActivity().setTitle(titleId);
        }
    }
}
