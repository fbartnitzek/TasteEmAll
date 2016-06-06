package com.fbartnitzek.tasteemall.tasks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import com.fbartnitzek.tasteemall.MainActivity;
import com.fbartnitzek.tasteemall.data.DatabaseContract;
import com.fbartnitzek.tasteemall.data.QueryColumns;
import com.fbartnitzek.tasteemall.data.pojo.User;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TaskUtils {

    public static final String ACTION_DATA_CHANGED
            = TaskUtils.class.getPackage().getName() + ".ACTION_DATA_CHANGED";
//    public static final String ACTION_PRODUCER_LOCATION_INSERTED
//            = TaskUtils.class.getPackage().getName() + ".ACTION_PRODUCER_LOCATION_INSERTED";

    public static void updateWidgets(Activity activity) {
        // only TasteEmAll app can receive broadcast
        Intent dataUpdatedIntent = new Intent(TaskUtils.ACTION_DATA_CHANGED).setPackage(
                MainActivity.class.getPackage().getName());
        activity.sendBroadcast(dataUpdatedIntent);
    }

//    public static void broadcastProducerLocation(Activity activity, Uri locationUri) {
//        Intent dataUpdatedIntent = new Intent(TaskUtils.ACTION_PRODUCER_LOCATION_INSERTED).setPackage(
//                MainActivity.class.getPackage().getName());
//        activity.sendBroadcast(dataUpdatedIntent);
//    }

    public static Cursor queryWithExactUserName(Activity activity, String userName) {
        return activity.getContentResolver().query(
                DatabaseContract.UserEntry.CONTENT_URI,
                QueryColumns.ReviewFragment.UserQuery.COLUMNS,
                User.NAME + " = '" + userName + "'", null ,
                null);
    }

}
