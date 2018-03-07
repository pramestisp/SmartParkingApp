package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Shindy on 05-Mar-18.
 */

public class SlotState extends View {
    private static final int[] STATE_AVAILABLE = {R.attr.state_available};

    private boolean mIsAvailable = false;

    public void setAvailable(boolean isAvailable) {
        mIsAvailable = isAvailable;
    }


    public SlotState(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (mIsAvailable) {
            mergeDrawableStates(drawableState, STATE_AVAILABLE);
        }
        return drawableState;
                //super.onCreateDrawableState(extraSpace);
    }
}
