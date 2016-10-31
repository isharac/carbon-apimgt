/*
 *
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.apimgt.core.models;

/**
 * Subscriber's view of the API
 */

public final class APISubscription {

    private String apiId;
    private Application application;
    private String id;

    public APISubscription(Application application, String apiId) {
        this.application = application;
        this.apiId = apiId;
    }

    public Application getApplication() {
        return application;
    }

    public String getApiId() {
        return apiId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        APISubscription that = (APISubscription) o;
        return apiId.equals(that.apiId) && application.equals(that.application);
    }

    @Override
    public int hashCode() {
        int result = apiId.hashCode();
        result = 31 * result + application.hashCode();
        return result;
    }
}
