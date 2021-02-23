package com.GMMAS.gwon_ocheol.schoolapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by lghlo on 2017-08-15.
 */

public class CreateAnimator {
    public final int DURATION = 400; // 애니메이션 시간

    /*------- 애니메이션 방향 (수평, 수직, 대각선) -------*/
    public enum Direction {HORIZONTAL, VERTICAL, DIAGONAL}


    /* 보이기 애니메이션 */
    protected Animator createShowAnimator(View item, View fab, float fixValue, Direction dir){
        /* fixValue는 애니메이션 종료 좌표를 구하기 위한 사용자 지정값
         * (애니메이션이 지나치게 멀리 이동하거나 좌표가 이상할 경우 조절)
         */
        float x = fab.getX();
        float y = fab.getY();
        float dx = x - (x/fixValue) - item.getX();
        float dy = y - (y/fixValue) - item.getY();

        Animator anim = null;
        item.setRotation(0f);

        switch(dir){
            case HORIZONTAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 0f, 720f),
                        PropertyValuesHolder.ofFloat("translationX", dx, 0f));
                break;

            case VERTICAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 0f, 720f),
                        PropertyValuesHolder.ofFloat("translationY", dy, 0f));
                break;

            case DIAGONAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 0f, 720f),
                        PropertyValuesHolder.ofFloat("translationX", dx, 0f),
                        PropertyValuesHolder.ofFloat("translationY", dy, 0f));
                break;
        }

        anim.setDuration(DURATION);
        return anim;
    }


    /* 숨기기 애니메이션 */
    protected Animator createHideAnimator(final View item, View fab, float fixValue, Direction dir){
        float x = fab.getX();
        float y = fab.getY();
        float dx = x - (x/fixValue) - item.getX();
        float dy = y - (y/fixValue) - item.getY();

        Animator anim = null;

        switch(dir){
            case HORIZONTAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 720f, 0f),
                        PropertyValuesHolder.ofFloat("translationX", 0f, dx));
                break;

            case VERTICAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 720f, 0f),
                        PropertyValuesHolder.ofFloat("translationY", 0f, dy));
                break;

            case DIAGONAL:
                anim = ObjectAnimator.ofPropertyValuesHolder(item,
                        PropertyValuesHolder.ofFloat("rotation", 720f, 0f),
                        PropertyValuesHolder.ofFloat("translationX", 0f, dx),
                        PropertyValuesHolder.ofFloat("translationY", 0f, dy));
                break;
        }

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        anim.setDuration(DURATION);
        return anim;
    }
}
