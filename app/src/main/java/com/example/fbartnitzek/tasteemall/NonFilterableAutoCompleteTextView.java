package com.example.fbartnitzek.tasteemall;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * source: http://stackoverflow.com/questions/20971629/error-when-using-cursorloader-to-load-data-from-contentprovider
 *
 * An AutoCompleteTextView which does not perform any background filtering. This class will
 * not perform any filtering and is intended to be used with CursorLoaders and CursorAdapters and
 * have the cursor in the adapter swapped when the loader has new data.
 * <p/>
 * This is required since using the standard AutoCompleteTextView with CursorLoaders and swapCursor
 * causes races conditions with the widget's own filtering happening in the background. The default filtering mechanism
 * will run on a background thread with an instance of the old cursor.
 *
 * @author AngraX
 */
public class NonFilterableAutoCompleteTextView extends AutoCompleteTextView {

    public NonFilterableAutoCompleteTextView(Context context) {
        super(context);
    }

    public NonFilterableAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonFilterableAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // I say NO!
    }
}
