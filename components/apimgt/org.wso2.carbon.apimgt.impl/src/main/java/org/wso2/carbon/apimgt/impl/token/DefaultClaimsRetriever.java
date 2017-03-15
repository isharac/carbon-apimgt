/*
*Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.apimgt.impl.token;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.utils.ClaimCache;
import org.wso2.carbon.apimgt.impl.utils.ClaimCacheKey;
import org.wso2.carbon.apimgt.impl.utils.UserClaims;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.user.api.Claim;
import org.wso2.carbon.user.api.ClaimMapping;
import org.wso2.carbon.user.api.ClaimManager;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.cache.Cache;
import javax.cache.Caching;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class is the default implementation of ClaimsRetriever.
 * It reads user claim values from the default carbon user store.
 * The user claims are encoded to the JWT in the natural order of the claimURIs.
 * To engage this class its fully qualified class name should be mentioned under
 * api-manager.xml -> APIConsumerAuthentication -> ClaimsRetrieverImplClass
 */
public class DefaultClaimsRetriever implements ClaimsRetriever {
    //TODO refactor caching implementation
    private static final Log log = LogFactory.getLog(JWTGenerator.class);
    private String dialectURI = ClaimsRetriever.DEFAULT_DIALECT_URI;

    /**
     * Reads the DialectURI of the ClaimURIs to be retrieved from api-manager.xml ->
     * APIConsumerAuthentication -> ConsumerDialectURI.
     * If not configured it uses http://wso2.org/claims as default
     */
    public void init() {
        dialectURI = ServiceReferenceHolder.getInstance().getAPIManagerConfigurationService().
                getAPIManagerConfiguration().getFirstProperty(CONSUMER_DIALECT_URI);
        if (dialectURI == null) {
            dialectURI = ClaimsRetriever.DEFAULT_DIALECT_URI;
        }
    }

    protected Cache getClaimsLocalCache() {
        return Caching.getCacheManager("API_MANAGER_CACHE").getCache("claimsLocalCache");
    }

    public SortedMap<String, String> getClaims(String endUserName) throws APIManagementException {
        SortedMap<String, String> claimValues;
        log.debug("Retrieving claims for endUserName " + endUserName);
        try {
            if (endUserName != null) {
                int tenantId = APIUtil.getTenantId(endUserName);
                String tenantAwareUserName = MultitenantUtils.getTenantAwareUsername(endUserName);
                log.debug("Retrieving claims for tenantAwareUserName =" + tenantAwareUserName );

                //check in local cache
            String key = endUserName + ":" + tenantId;
            ClaimCacheKey cacheKey = new ClaimCacheKey(key);
            log.debug("Retrieving claims for key =" + key);
                //Object result = claimsLocalCache.getValueFromCache(cacheKey);
            Object result = getClaimsLocalCache().get(cacheKey);
            if (result != null) {
                claimValues = ((UserClaims) result).getClaimValues();
                log.debug("Claims from local cache =" + claimValues.values().size());
                return claimValues;
            } else {
                ClaimManager claimManager = ServiceReferenceHolder.getInstance().getRealmService().
                        getTenantUserRealm(tenantId).getClaimManager();
                //Claim[] claims = claimManager.getAllClaims(dialectURI);
                log.debug("Claim dialectURI =" + dialectURI);
                ClaimMapping[] claims = claimManager.getAllClaimMappings(dialectURI);
                log.debug("No of Claim Mappings received =" + claims.length);
                for(ClaimMapping claimmapping : claims){
                    log.debug("Claim Mapping =" + claimmapping.getMappedAttribute());
                }
                String[] claimURIs = claimMappingtoClaimURIString(claims);
                log.debug("No of claimURIs received =" + claimURIs.length);
                for(String claimURI : claimURIs){
                    log.debug("Claim claimURI =" + claimURI);
                }
                UserStoreManager userStoreManager =
                        ServiceReferenceHolder.getInstance().getRealmService().
                                getTenantUserRealm(tenantId).getUserStoreManager();

                claimValues = new TreeMap(
                        userStoreManager.getUserClaimValues(tenantAwareUserName, claimURIs, null));
                UserClaims userClaims = new UserClaims(claimValues);
                //add to cache
                getClaimsLocalCache().put(cacheKey, userClaims);
                return claimValues;
            }
            }
        } catch (UserStoreException e) {
            throw new APIManagementException("Error while retrieving user claim values from user store for the user "
                    + endUserName, e);
        }
        return null;
    }

    /**
     * Always returns the ConsumerDialectURI configured in api-manager.xml
     */
    public String getDialectURI(String endUserName) {
        return dialectURI;
    }

    /**
     * Helper method to convert array of <code>Claim</code> object to
     * array of <code>String</code> objects corresponding to the ClaimURI values.
     */
    private String[] claimMappingtoClaimURIString(ClaimMapping[] claims) {
        String[] temp = new String[claims.length];
        for (int i = 0; i < claims.length; i++) {
            temp[i] = claims[i].getClaim().getClaimUri().toString();
       
        }
        return temp;
    }
}
