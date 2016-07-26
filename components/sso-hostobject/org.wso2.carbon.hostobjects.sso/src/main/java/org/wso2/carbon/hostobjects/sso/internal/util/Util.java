/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.hostobjects.sso.internal.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.IdResolver;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.impl.SignatureImpl;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.hostobjects.sso.SignatureVerificationException;
import org.wso2.carbon.hostobjects.sso.SignatureVerificationFailure;
import org.wso2.carbon.hostobjects.sso.internal.SSOConstants;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Util {

    private static boolean bootStrapped = false;

    private static Random random = new Random();

    private static RealmService realmService = null;

    private static final char[] charMapping = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p'};

    private static Log log = LogFactory.getLog(Util.class);

    /**
     * This method is used to initialize the OpenSAML2 library. It calls the bootstrap method, if it
     * is not initialized yet.
     */
    public static void doBootstrap() {
        if (!bootStrapped) {
            try {
                DefaultBootstrap.bootstrap();
                bootStrapped = true;
            } catch (ConfigurationException e) {
                System.err.println("Error in bootstrapping the OpenSAML2 library");
                e.printStackTrace();
            }
        }
    }

    public static XMLObject buildXMLObject(QName objectQName)
            throws Exception {

        XMLObjectBuilder builder = org.opensaml.xml.Configuration.getBuilderFactory().getBuilder(objectQName);
        if (builder == null) {
            throw new Exception("Unable to retrieve builder for object QName "
                                + objectQName);
        }
        return builder.buildObject(objectQName.getNamespaceURI(), objectQName.getLocalPart(),
                                   objectQName.getPrefix());
    }


    /**
     * Generates a unique Id for Authentication Requests
     *
     * @return generated unique ID
     */
    public static String createID() {

        byte[] bytes = new byte[20]; // 160 bits
        random.nextBytes(bytes);

        char[] chars = new char[40];

        for (int i = 0; i < bytes.length; i++) {
            int left = (bytes[i] >> 4) & 0x0f;
            int right = bytes[i] & 0x0f;
            chars[i * 2] = charMapping[left];
            chars[i * 2 + 1] = charMapping[right];
        }

        return String.valueOf(chars);
    }

    /**
     * Constructing the XMLObject Object from a String
     *
     * @param authReqStr
     * @return Corresponding XMLObject which is a SAML2 object
     * @throws Exception
     */
    public static XMLObject unmarshall(String authReqStr) throws Exception {
        try {
            doBootstrap();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new ByteArrayInputStream(authReqStr.trim().getBytes()));
            Element element = document.getDocumentElement();
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            return unmarshaller.unmarshall(element);
        } catch (Exception e) {
            throw new Exception("Error in constructing AuthRequest from " +
                                "the encoded String ", e);
        }
    }

    /**
     * Serializing a SAML2 object into a String
     *
     * @param xmlObject object that needs to serialized.
     * @return serialized object
     * @throws Exception
     */
    public static String marshall(XMLObject xmlObject) throws Exception {
        try {
            doBootstrap();
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                               "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

            MarshallerFactory marshallerFactory = org.opensaml.xml.Configuration.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
            Element element = marshaller.marshall(xmlObject);

            ByteArrayOutputStream byteArrayOutputStrm = new ByteArrayOutputStream();
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl =
                    (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            LSOutput output = impl.createLSOutput();
            output.setByteStream(byteArrayOutputStrm);
            writer.write(element, output);
            return byteArrayOutputStrm.toString();
        } catch (Exception e) {
            throw new Exception("Error Serializing the SAML Response", e);
        }
    }

    /**
     * Compressing and Encoding the response
     *
     * @param xmlString String to be encoded
     * @return compressed and encoded String
     */
    public static String encode(String xmlString) throws Exception {

        // Encoding the compressed message
        String encodedRequestMessage = Base64.encodeBytes(xmlString.getBytes("UTF-8"), Base64.DONT_BREAK_LINES);
        return encodedRequestMessage.trim();
    }

    /**
     * @param xmlString String to be encoded
     * @return
     */
    public static String deflateAndEncode(String xmlString) throws Exception {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
                byteArrayOutputStream, deflater);

        deflaterOutputStream.write(xmlString.getBytes());
        deflaterOutputStream.close();

        // Encoding the compressed message
        String encodedRequestMessage = Base64.encodeBytes(byteArrayOutputStream
                                                                  .toByteArray(), Base64.DONT_BREAK_LINES);
        return encodedRequestMessage.trim();

    }

    /**
     * Decoding and deflating the encoded AuthReq
     *
     * @param encodedStr encoded AuthReq
     * @return decoded AuthReq
     */
    public static String decode(String encodedStr) throws Exception {
        return new String(Base64.decode(encodedStr));
    }

    /**
     * This method validates the signature of the SAML Response.
     *
     * @param signature Signature
     * @return true, if signature is valid.
     */
    public static boolean validateSignature(Signature signature, String keyStoreName,
                                            String keyStorePassword, String alias, int tenantId,
                                            String tenantDomain) throws SignatureVerificationException,
                                                                        SignatureVerificationFailure {
        boolean isSigValid = false;
        try {
            KeyStore keyStore = null;
            java.security.cert.X509Certificate cert = null;
            if (tenantId != MultitenantConstants.SUPER_TENANT_ID) {
                // get an instance of the corresponding Key Store Manager instance
                KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(tenantId);
                keyStore = keyStoreManager.getKeyStore(generateKSNameFromDomainName(tenantDomain));
                cert = (java.security.cert.X509Certificate) keyStore.getCertificate(tenantDomain);
            } else {
                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(new FileInputStream(new File(keyStoreName)), keyStorePassword.toCharArray());
                cert = (java.security.cert.X509Certificate) keyStore.getCertificate(alias);
            }
            if (log.isDebugEnabled()) {
                log.debug("Validating against " + cert.getSubjectDN().getName());
            }
            try {
                SignatureImpl signImpl = (SignatureImpl) signature;
                SAMLSignatureProfileValidator signatureProfileValidator = new SAMLSignatureProfileValidator();
                signatureProfileValidator.validate(signature);
                // Following code segment is taken from org.opensaml.security.SAMLSignatureProfileValidator
                // of OpenSAML 2.6.4. This is done to get the latest XSW related fixes.
                XMLSignature apacheSig = signImpl.getXMLSignature();
                SignableSAMLObject signableObject = (SignableSAMLObject) signature.getParent();
                Reference ref = null;
                try {
                    ref = apacheSig.getSignedInfo().item(0);
                } catch (XMLSecurityException e) {
                    // This exception should never occur, because it's already checked
                    // from the previous call to signatureProfileValidator#validate
                    log.error("Apache XML Security exception obtaining Reference", e);
                    throw new SignatureVerificationException(e.getMessage(),e);
                }

                String uri = ref.getURI();

                new Util().validateReferenceURI(uri, signableObject);
                new Util().validateObjectChildren(apacheSig);

                // End of OpenSAML 2.6.4 logic
                // -----------------------------------------------------------------------------

            } catch (ValidationException ex) {
                String logMsg = "Signature do not confirm to SAML signature profile. Possible XML Signature " +
                        "Wrapping  Attack!";
                log.error(ex.getMessage(), ex);
                return false;
            }

            X509CredentialImpl credentialImpl = new X509CredentialImpl(cert);
            SignatureValidator signatureValidator = new SignatureValidator(credentialImpl);
            signatureValidator.validate(signature);
            isSigValid = true;
            return isSigValid;
        } catch (KeyStoreException e) {
            log.error("Error when getting certificate of tenant " + tenantDomain, e);
            throw new SignatureVerificationException(e);
        } catch (CertificateException e) {
            log.error("Could not load the keystore " + keyStoreName, e);
            throw new SignatureVerificationException(e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not load the keystore " + keyStoreName, e);
            throw new SignatureVerificationException(e);
        } catch (FileNotFoundException e) {
            log.error("Could not find the key store file " + keyStoreName, e);
            throw new SignatureVerificationException(e);
        } catch (IOException e) {
            log.error("Could not load the keystore " + keyStoreName, e);
            throw new SignatureVerificationException(e);
        } catch (ValidationException e) {
            //Do not log the exception here. Clients of this method use it in a fall back fashion to verify signatures
            //using different public keys. Therefore logging an error would cause unnecessary logs. Throwing an
            //exception is sufficient so that clients can decide what to do with it.
            throw new SignatureVerificationFailure(e);
        } catch (Exception e) {
            //keyStoreManager.getKeyStore throws a generic 'Exception'
            log.error("Error when getting key store of tenant " + tenantDomain, e);
            throw new SignatureVerificationException(e);
        }
    }

    public static String getDomainName(XMLObject samlObject) {
        NodeList list = samlObject.getDOM().getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "NameID");
        String domainName = null;
        if (list.getLength() > 0) {
            String userName = list.item(0).getTextContent();
            domainName = MultitenantUtils.getTenantDomain(userName);
        }
        return domainName;
    }

    /**
     * Generate the key store name from the domain name
     *
     * @param tenantDomain tenant domain name
     * @return key store file name
     */
    private static String generateKSNameFromDomainName(String tenantDomain) {
        String ksName = tenantDomain.trim().replace(".", "-");
        return (ksName + ".jks");
    }


    public static void setRealmService(RealmService realmService) {
        Util.realmService = realmService;
    }

    public static RealmService getRealmService() {
        return Util.realmService;
    }

    /** Build NameIDPolicy object given name ID policy format
     * 
     * @param nameIdPolicy Name ID policy format
     * @return
     */
    public static NameIDPolicy buildNameIDPolicy(String nameIdPolicy) {
        NameIDPolicy nameIDPolicyObj = new NameIDPolicyBuilder().buildObject();
        if (!StringUtils.isEmpty(nameIdPolicy)){
            nameIDPolicyObj.setFormat(nameIdPolicy);
        }else {
            nameIDPolicyObj.setFormat(SSOConstants.NAME_ID_POLICY_DEFAULT);
        }
        nameIDPolicyObj.setAllowCreate(true);
        return nameIDPolicyObj;
    }

    /** Build NameID object given name ID format
     * 
     * @param nameIdFormat Name ID format
     * @param subject
     * @return
     */
    public static NameID buildNameID(String nameIdFormat, String subject) {
        NameID nameIdObj = new NameIDBuilder().buildObject();
        if (!StringUtils.isEmpty(nameIdFormat)) {
            nameIdObj.setFormat(nameIdFormat);
        } else {
            nameIdObj.setFormat(SSOConstants.NAME_ID_POLICY_DEFAULT);
        }
        nameIdObj.setValue(subject);
        return nameIdObj;
    }

    /**
     * Replaces the ${} in url with system properties and returns
     *
     * @param acsUrl
     * @return
     */
    public static String processAcsUrl(String acsUrl){
        //matches shortest segments that are between '{' and '}'
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(acsUrl);
        while (matcher.find()) {
            String match = matcher.group(1);
            String property = System.getProperty(match);
            if (property != null) {
                acsUrl = acsUrl.replace("${" + match + "}", property);
            } else {
                log.warn("System Property " + match + " is not set");
            }
        }
        return acsUrl;
    }

    /**
     * Validate the Signature's Reference URI.
     * <p/>
     * First validate the Reference URI against the parent's ID itself.  Then validate that the
     * URI (if non-empty) resolves to the same Element node as is cached by the SignableSAMLObject.
     *
     * @param uri            the Signature Reference URI attribute value
     * @param signableObject the SignableSAMLObject whose signature is being validated
     * @throws ValidationException if the URI is invalid or doesn't resolve to the expected DOM node
     */
    private void validateReferenceURI(String uri, SignableSAMLObject signableObject) throws ValidationException {
        if (DatatypeHelper.isEmpty(uri)) {
            return;
        }

        String uriID = uri.substring(1);

        Element expected = signableObject.getDOM();
        if (expected == null) {
            log.error("SignableSAMLObject does not have a cached DOM Element.");
            throw new ValidationException("SignableSAMLObject does not have a cached DOM Element.");
        }
        Document doc = expected.getOwnerDocument();

        Element resolved = IdResolver.getElementById(doc, uriID);
        if (resolved == null) {
            log.error("Apache xmlsec IdResolver could not resolve the Element for id reference: " + uriID);
            throw new ValidationException("Apache xmlsec IdResolver could not resolve the Element for id reference: "
                    + uriID);
        }

        if (!expected.isSameNode(resolved)) {
            log.error("Signature Reference URI " + uri + " did not resolve to the expected parent Element");
            throw new ValidationException("Signature Reference URI did not resolve to the expected parent Element");
        }
    }

    /**
     * Validate that the Signature instance does not contain any ds:Object children.
     *
     * @param apacheSig the Apache XML Signature instance
     * @throws ValidationException if the signature contains ds:Object children
     */
    private void validateObjectChildren(org.apache.xml.security.signature.XMLSignature apacheSig) throws ValidationException {
        if (apacheSig.getObjectLength() > 0) {
            log.error("Signature contained " + apacheSig.getObjectLength() + " ds:Object child element(s)");
            throw new ValidationException("Signature contained illegal ds:Object children");
        }
    }


}
