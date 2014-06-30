/*
 * Copyright (C) 2014 The MoKee OpenSource Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mokee.helper.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class HttpRequestExecutor {
    private HttpClient mHttpClient;
    private HttpRequestBase mRequest;
    private boolean mAborted;

    public HttpRequestExecutor() {
        mHttpClient = new DefaultHttpClient();
        mAborted = false;
    }

    public HttpEntity execute(HttpRequestBase request) throws IOException {
        synchronized (this) {
            mAborted = false;
            mRequest = request;
        }

        HttpResponse response = mHttpClient.execute(request);
        HttpEntity entity = null;

        if (!mAborted && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            entity = response.getEntity();
        }

        synchronized (this) {
            mRequest = null;
        }

        return entity;
    }

    public synchronized void abort() {
        if (mRequest != null) {
            mRequest.abort();
        }
        mAborted = true;
    }

    public synchronized boolean isAborted() {
        return mAborted;
    }
}