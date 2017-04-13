/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.storagemanager.deletionhelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.storagemanager.ButtonBarProvider;
import com.android.storagemanager.R;

/**
 * The DeletionHelperActivity is an activity for deleting apps, photos, and downloaded files which
 * have not been recently used.
 */
public class DeletionHelperActivity extends Activity implements ButtonBarProvider {

    private ViewGroup mButtonBar;
    private Button mNextButton, mSkipButton;
    private DeletionHelperSettings mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_prefs);

        // If we are not returning from an existing activity, create a new fragment.
        if (savedInstanceState == null) {
            setIsEmptyState(false /* isEmptyState */);
            FragmentManager manager = getFragmentManager();
            mFragment = DeletionHelperSettings.newInstance(AppsAsyncLoader.NORMAL_THRESHOLD);
            manager.beginTransaction().replace(R.id.main_content, mFragment).commit();
        }

        mButtonBar = (ViewGroup) findViewById(R.id.button_bar);
        mNextButton = (Button) findViewById(R.id.next_button);
        mSkipButton = (Button) findViewById(R.id.skip_button);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deletion_helper_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Note: This menu allows us to change the threshold and is only temporary until the UI/UX
        // is finalized.
        // TODO(b/34395589): This needs to be changed once the UX for this is reworked.
        FragmentManager manager = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.no_threshold:
                mFragment = DeletionHelperSettings.newInstance(AppsAsyncLoader.NO_THRESHOLD);
                manager.beginTransaction().replace(R.id.main_content, mFragment).commit();
                return true;
            case R.id.default_threshold:
                setIsEmptyState(false /* isEmptyState */);
                mFragment = DeletionHelperSettings.newInstance(AppsAsyncLoader.NORMAL_THRESHOLD);
                manager.beginTransaction().replace(R.id.main_content, mFragment).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setIsEmptyState(boolean isEmptyState) {
        findViewById(R.id.main_content).setVisibility(isEmptyState ? View.GONE : View.VISIBLE);
        findViewById(R.id.empty_state).setVisibility(isEmptyState ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_bar).setVisibility(isEmptyState ? View.GONE : View.VISIBLE);
        setTitle(isEmptyState ? R.string.empty_state_title : R.string.deletion_helper_title);
    }

    @Override
    public ViewGroup getButtonBar() {
        return mButtonBar;
    }

    @Override
    public Button getNextButton() {
        return mNextButton;
    }

    @Override
    public Button getSkipButton() {
        return mSkipButton;
    }
}