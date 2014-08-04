//------------------------------------------------------------------------------
// Copyright 2014 Microsoft Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Description: See the class level JavaDoc comments.
//------------------------------------------------------------------------------

package com.microsoft.live;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

class DownloadRequest extends ApiRequest<InputStream> {

    public static final String METHOD = HttpGet.METHOD_NAME;

    public DownloadRequest(LiveConnectSession session, HttpClient client, String path) {
        super(session,
              client,
              InputStreamResponseHandler.INSTANCE,
              path,
              ResponseCodes.UNSUPPRESSED,
              Redirects.UNSUPPRESSED);
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    protected HttpUriRequest createHttpRequest() throws LiveOperationException {
        return new HttpGet(this.requestUri.toString());
    }
}
