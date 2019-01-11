/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.contacts.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.android.contacts.model.account.AccountInfo;
import com.android.contacts.model.account.AccountWithDataSet;
import com.android.contacts.util.AccountsListAdapter;

import java.util.List;

public class DefaultAccountPreference extends Preference {
    private ContactsPreferences mPreferences;
    private AccountsListAdapter mListAdapter;
    private List<AccountInfo> mAccounts;
    private int mChosenIndex = -1;

    public DefaultAccountPreference(Context context) {
        super(context);
        prepare();
    }

    public DefaultAccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepare();
    }

    @Override
    protected void onClick() {
        super.onClick();
        showDialog();
    }

    public void setAccounts(List<AccountInfo> accounts) {
        prepare();
        mAccounts = accounts;
        if (mListAdapter != null) {
            mListAdapter.setAccounts(accounts, null);
            notifyChanged();
        }
    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setAdapter(mListAdapter, (dialog, position) -> {
            mChosenIndex = position;
            final AccountWithDataSet currentDefault = mPreferences.getDefaultAccount();
            if (mChosenIndex != -1) {
                final AccountWithDataSet chosenAccount = mListAdapter.getItem(mChosenIndex);
                if (!chosenAccount.equals(currentDefault)) {
                    mPreferences.setDefaultAccount(chosenAccount);
                    notifyChanged();
                }
            }
        });
        builder.show();
    }

    private void prepare() {
        mPreferences = new ContactsPreferences(getContext());
        mListAdapter = new AccountsListAdapter(getContext());
        if (mAccounts != null) {
            mListAdapter.setAccounts(mAccounts, null);
        }
    }

    @Override
    protected boolean shouldPersist() {
        return false;   // This preference takes care of its own storage
    }

    @Override
    public CharSequence getSummary() {
        final AccountWithDataSet defaultAccount = mPreferences.getDefaultAccount();
        if (defaultAccount == null || mAccounts == null ||
                !AccountInfo.contains(mAccounts, defaultAccount)) {
            return null;
        } else {
            return AccountInfo.getAccount(mAccounts, defaultAccount).getNameLabel();
        }
    }
}
