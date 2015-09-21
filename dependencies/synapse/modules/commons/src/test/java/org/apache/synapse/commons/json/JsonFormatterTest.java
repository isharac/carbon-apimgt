/**
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

package org.apache.synapse.commons.json;

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.context.MessageContext;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonFormatterTest extends TestCase {

    private static final String xmlInput = "<pizza>" +
                                           "    <name>Meat Sizzler</name>" +
                                           "    <price>500.0</price>" +
                                           "    <toppings>" +
                                           "        <topping>" +
                                           "            <id>9999</id>" +
                                           "            <name>Steak</name>" +
                                           "            <extraPrice>4.00</extraPrice>" +
                                           "            <category>NONVEG</category>" +
                                           "        </topping>" +
                                           "        <topping>" +
                                           "            <id>9998</id>" +
                                           "            <name>Sun Dried Tomato</name>" +
                                           "            <extraPrice>4.00</extraPrice>" +
                                           "            <category>VEGETARIAN</category>" +
                                           "        </topping>" +
                                           "        <topping>" +
                                           "            <id>9997</id>" +
                                           "            <name>Mixed Peppers</name>" +
                                           "            <extraPrice>3.00</extraPrice>" +
                                           "            <category>VEGETARIAN</category>" +
                                           "        </topping>" +
                                           "        <topping>" +
                                           "            <id>9996</id>" +
                                           "            <name>Cajun Chicken</name>" +
                                           "            <extraPrice>3.00</extraPrice>" +
                                           "            <category>NONVEG</category>" +
                                           "        </topping>" +
                                           "        <topping>" +
                                           "            <id>9995</id>" +
                                           "            <name>Chorizo Sausage</name>" +
                                           "            <extraPrice>4.00</extraPrice>" +
                                           "            <category>NONVEG</category>" +
                                           "        </topping>" +
                                           "    </toppings>" +
                                           "</pizza>";

    public static final String jsonOut = "{\"pizza\":{\"name\":\"Meat Sizzler\",\"price\":500.0,\"toppings\":{\"topping\":[{\"id\":9999,\"name\":\"Steak\",\"extraPrice\":4.00,\"category\":\"NONVEG\"},{\"id\":9998,\"name\":\"Sun Dried Tomato\",\"extraPrice\":4.00,\"category\":\"VEGETARIAN\"},{\"id\":9997,\"name\":\"Mixed Peppers\",\"extraPrice\":3.00,\"category\":\"VEGETARIAN\"},{\"id\":9996,\"name\":\"Cajun Chicken\",\"extraPrice\":3.00,\"category\":\"NONVEG\"},{\"id\":9995,\"name\":\"Chorizo Sausage\",\"extraPrice\":4.00,\"category\":\"NONVEG\"}]}}}";

    public static final String xmlInput_1 = "<pizza>" +
                                           "    <name>Meat Sizzler</name>" +
                                           "    <price>500.0</price>" +
                                           "    <toppings>" +
                                           "        <topping>" +
                                           "            <id>9999</id>" +
                                           "            <name>Steak</name>" +
                                           "            <extraPrice>4.00</extraPrice>" +
                                           "            <category>NONVEG</category>" +
                                           "        </topping>" +
                                           "    </toppings>" +
                                           "</pizza>";

    public static final String jsonOut_1 = "{\"pizza\":{\"name\":\"Meat Sizzler\",\"price\":500.0,\"toppings\":{\"topping\":{\"id\":9999,\"name\":\"Steak\",\"extraPrice\":4.00,\"category\":\"NONVEG\"}}}}";

    public static final String xmlInput_2 = "<pizza>" +
                                            "    <name>Meat Sizzler</name>" +
                                            "    <price>500.0</price>" +
                                            "    <toppings>" +
                                            "        <topping>" +
                                            "            <id>9999</id>" +
                                            "            <name>Steak</name>" +
                                            "            <extraPrice>4.00</extraPrice>" +
                                            "            <category>NONVEG</category>" +
                                            "        </topping>" +
                                            "        <topping/>" +
                                            "    </toppings>" +
                                            "</pizza>";

    public static final String jsonOut_2 = "{\"pizza\":{\"name\":\"Meat Sizzler\",\"price\":500.0,\"toppings\":{\"topping\":[{\"id\":9999,\"name\":\"Steak\",\"extraPrice\":4.00,\"category\":\"NONVEG\"},null]}}}";

    public static final String xmlInput_3 = "<pizza>" +
                                            "    <name>Meat Sizzler</name>" +
                                            "    <price>500.0</price>" +
                                            "    <toppings>" +
                                            "        <topping>" +
                                            "            <id>9999</id>" +
                                            "            <name>Steak</name>" +
                                            "            <extraPrice>4.00</extraPrice>" +
                                            "            <category>NONVEG</category>" +
                                            "        </topping>" +
                                            "        <topping></topping>" +
                                            "    </toppings>" +
                                            "</pizza>";

    public static final String xmlInput_4 = "<jsonObject>" +
                                            "    <pizza>" +
                                            "        <name>Meat Sizzler</name>" +
                                            "        <price>500.0</price>" +
                                            "        <toppings>" +
                                            "            <topping>" +
                                            "                <id>9999</id>" +
                                            "                <name>Steak</name>" +
                                            "                <extraPrice>4.00</extraPrice>" +
                                            "                <category>NONVEG</category>" +
                                            "            </topping>" +
                                            "        </toppings>" +
                                            "    </pizza>" +
                                            "</jsonObject>";

    public static final String xmlInput_5 = "<jsonArray>" +
                                            "    <jsonElement>" +
                                            "        <pizza>" +
                                            "            <name>Meat Sizzler</name>" +
                                            "            <price>500.0</price>" +
                                            "            <toppings>" +
                                            "                <topping>" +
                                            "                    <id>9999</id>" +
                                            "                    <name>Steak</name>" +
                                            "                    <extraPrice>4.00</extraPrice>" +
                                            "                    <category>NONVEG</category>" +
                                            "                </topping>" +
                                            "            </toppings>" +
                                            "        </pizza>" +
                                            "    </jsonElement>" +
                                            "</jsonArray>";

    public static final String xmlInput_6 = "<jsonArray>" +
                                               "    <jsonElement>" +
                                               "        <pizza>" +
                                               "            <name>Meat Sizzler</name>" +
                                               "            <price>500.0</price>" +
                                               "            <toppings>" +
                                               "                <topping>" +
                                               "                    <id>9999</id>" +
                                               "                    <name>Steak</name>" +
                                               "                    <extraPrice>4.00</extraPrice>" +
                                               "                    <category>NONVEG</category>" +
                                               "                </topping>" +
                                               "            </toppings>" +
                                               "        </pizza>" +
                                               "    </jsonElement>" +
                                               "    <jsonElement/>" +
                                               "</jsonArray>";

    public static final String jsonOut_6 = "[{\"pizza\":{\"name\":\"Meat Sizzler\",\"price\":500.0,\"toppings\":{\"topping\":{\"id\":9999,\"name\":\"Steak\",\"extraPrice\":4.00,\"category\":\"NONVEG\"}}}},null]";

    /**
     * NOTE: Under this test class, we cannot test the creation of JSON arrays by giving xml processing instructions to the
     * xml input string object. The reason is that when doing stringToOm and setting the SOAP child with addChild() method,
     * the addChild() method throws an exception saying that it does not support the xml PI node type.
     */

    public void testCase() {
        try {
            InputStream inputStream = Util.getJson(0);

            MessageFormatter formatter = Util.newJsonFormatter();
            MessageContext messageContext = Util.newMessageContext();
            JsonUtil.newJsonPayload(messageContext, inputStream, false, false);
            OutputStream out = Util.newOutputStream();
            formatter.writeTo(messageContext, null, out, false);
            assertTrue(JsonDataSourceTest.expectedJSON.equals(out.toString().replace("\n", "").replace("\r", "")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertTrue(false);
        }
    }

    public void testCase2() {
        try {
            InputStream inputStream = Util.getJson(0);
            runTest(xmlInput, jsonOut, inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testCase3() {
        runTest(xmlInput_1, jsonOut_1, null);
    }

    public void testCase4() {
        runTest(xmlInput_2, jsonOut_2, null);
    }

    public void testCase5() {
        runTest(xmlInput_3, jsonOut_2, null);
    }

    public void testCase6() {
        runTest(xmlInput_4, jsonOut_1, null);
    }

    public void testCase7() {
        runTest(xmlInput_5, jsonOut_1, null);
    }

    public void testCase8() {
        runTest(xmlInput_6, jsonOut_6, null);
    }

    private void runTest(String xmlInput, String jsonOut, InputStream inputStream) {
        try {
            MessageFormatter formatter = Util.newJsonFormatter();
            MessageContext messageContext = Util.newMessageContext(xmlInput);
            JsonUtil.newJsonPayload(messageContext, inputStream, false, false);
            OutputStream out = Util.newOutputStream();
            formatter.writeTo(messageContext, null, out, false);
            assertTrue(jsonOut.equals(out.toString()));
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertTrue(false);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            assertTrue(false);
        } catch (Exception e) {
            System.out.println(e);
            assertTrue(false);
        }
    }
}
