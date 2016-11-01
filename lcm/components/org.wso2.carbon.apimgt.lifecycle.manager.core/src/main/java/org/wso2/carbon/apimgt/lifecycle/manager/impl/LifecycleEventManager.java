/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.lifecycle.manager.impl;

import org.wso2.carbon.apimgt.lifecycle.manager.exception.LifecycleException;
import org.wso2.carbon.apimgt.lifecycle.manager.sql.beans.LifecycleHistoryBean;
import org.wso2.carbon.apimgt.lifecycle.manager.sql.beans.LifecycleStateBean;
import org.wso2.carbon.apimgt.lifecycle.manager.sql.dao.LifecycleMgtDAO;
import org.wso2.carbon.apimgt.lifecycle.manager.sql.exception.LifecycleManagerDatabaseException;

import java.util.List;

/**
 * This class communicate with DAO layer to perform lifecycle operations.
 */
public class LifecycleEventManager {

    /**
     * Associates lifecycle with an asset. Sets the initial state as the current state.
     *
     * @param lcName                        Name of lifecycle which asset being associated with.
     * @param initialState                  initial state provided in the scxml configuration.
     * @param user                          The user who invoked the action. This will be used for auditing purposes.
     * @return                              uuid generated for that particular asset.
     * @throws LifecycleException
     */
    public String associateLifecycle(String lcName, String initialState, String user) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().addLifecycleState(initialState, lcName, user);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while associating lifecycle " + lcName, e);
        }
    }

    /**
     * Changes the lifecycle state.
     *
     * @param currentState                  Current state
     * @param requiredState                 The expected state
     * @param id                            uuid of the current state which maps with the asset.
     * @param user                          The user who invoked the action. This will be used for auditing purposes.
     * @throws LifecycleException
     */
    public void changeLifecycleState(String currentState, String requiredState, String id, String user)
            throws LifecycleException {
        LifecycleStateBean lifecycleStateBean = new LifecycleStateBean();
        lifecycleStateBean.setPreviousStatus(currentState);
        lifecycleStateBean.setPostStatus(requiredState);
        lifecycleStateBean.setStateId(id);
        try {
            getLCMgtDAOInstance().changeLifecycleState(lifecycleStateBean, user);
            getLCMgtDAOInstance().clearCheckListItemData(id, currentState);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while changing lifecycle state to  " + requiredState, e);
        }
    }

    /**
     * Remove lifecycle state data from LC_DATA table
     *
     * @param uuid                        uuid of the state.
     * @throws LifecycleException
     */
    public void removeLifecycleStateData(String uuid) throws LifecycleException {
        try {
            getLCMgtDAOInstance().removeLifecycleState(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while deleting lifecycle data for id : " + uuid);
        }
    }

    /**
     * Get data related to particular uuid from LC_DATA table
     *
     * @param uuid                        uuid of the state.
     * @return                            Lifecycle state data associated with the uuid.
     * @throws LifecycleException
     */
    public LifecycleStateBean getLifecycleStateData(String uuid) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().getLifecycleStateDataFromId(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle data for id : " + uuid);
        }
    }

    public void changeCheckListItemData(String uuid, String currentState, String checkListItemName, boolean value)
            throws LifecycleException {
        try {
            getLCMgtDAOInstance().changeCheckListItemData(uuid, currentState, checkListItemName, value);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while adding checklist data for item " + checkListItemName, e);
        }
    }

    /**
     * This method provides set of operations performed to a particular lifecycle id.
     *
     * @param uuid                  Lifecycle Id which requires history.
     * @return                      List of lifecycle history objects.
     * @throws LifecycleException
     */
    public List<LifecycleHistoryBean> getLifecycleHistoryFromId(String uuid) throws LifecycleException {
        try {
           return getLCMgtDAOInstance().getLifecycleHistoryFromId(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle data from uuid : " + uuid, e);
        }
    }

    /**
     * This method provides set of lifecycle ids in a particular state.
     * @param state`
     * @return  List of lifecycle ids in the given state.
     * @throws LifecycleException
     */
    public List<String> getLifecycleIds (String state) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().getLifecycleIdsFromState(state);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle ids in state : " + state, e);
        }
    }

    private LifecycleMgtDAO getLCMgtDAOInstance() {
        return LifecycleMgtDAO.getInstance();
    }
}
