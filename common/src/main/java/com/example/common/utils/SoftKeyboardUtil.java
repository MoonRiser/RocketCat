package com.example.common.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardUtil {

    /**
     * @param view 需要弹出键盘的EditTextView或者其他可获取焦点的View
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 注意这里虽然原则上需要传递一个之前弹出键盘传递的时候，传递的 View 的 windowToken ，但是实际情况是你只需要传递一个存在于当前布局 ViewTree 中，随意一个 View 的 windowToken 就可以了。
     *
     * @param view 存在于当前布局 ViewTree 中，随意一个 View
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软键盘的弹出和隐藏的状态
     *
     * @param view 存在于当前布局 ViewTree 中，随意一个 View
     */
    public static void toggleSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }

//    /**
//     * 获取当前软键盘的状态
//     *
//     * @param activity
//     * @return
//     */
//    public static boolean isSoftInputShow(Activity activity) {
//
//
//        View view = activity.getWindow().peekDecorView();
//        if (view != null) {
//
//            InputMethodManager inputManager = (InputMethodManager) activity
//                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
//            if (inputManager != null) {
//                return inputManager.isActive() && activity.getWindow().getCurrentFocus() != null;
//            }
//        }
//        return false;
//    }


//    View.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//
//        //当键盘弹出隐藏的时候会 调用此方法。
//        @Override
//        public void onGlobalLayout() {
//            final Rect rect = new Rect();
//            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//            final int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
//            final int heightDifference = screenHeight - rect.bottom;
//            boolean visible = heightDifference > screenHeight / 3;
//            if(visible){
//                Log.i(TAG,"软键盘显示");
//            }else {
//                Log.i(TAG,"软键盘隐藏");
//            }
//        }
//    });


}
