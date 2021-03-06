/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.flex.compiler.internal.codegen.mxml.flexjs;

import org.apache.flex.compiler.internal.codegen.js.flexjs.JSFlexJSEmitter;
import org.apache.flex.compiler.internal.driver.js.goog.JSGoogConfiguration;
import org.apache.flex.compiler.internal.projects.FlexJSProject;
import org.apache.flex.compiler.internal.test.FlexJSTestBase;
import org.apache.flex.compiler.tree.mxml.IMXMLDocumentNode;
import org.apache.flex.compiler.tree.mxml.IMXMLFileNode;
import org.apache.flex.utils.ITestAdapter;
import org.apache.flex.utils.TestAdapterFactory;
import org.junit.Test;

import java.io.File;

public class TestFlexJSMXMLApplication extends FlexJSTestBase
{
    private static ITestAdapter testAdapter = TestAdapterFactory.getTestAdapter();

    @Override
    public void setUp()
    {
        super.setUp();
    	((FlexJSProject)project).config = new JSGoogConfiguration();
    }

    @Test
    public void testFile()
    {
        String fileName = "wildcard_import";

        IMXMLFileNode node = compileMXML(fileName, true,
                new File(testAdapter.getUnitTestBaseDir(), "flexjs/files").getPath(), false);

        mxmlBlockWalker.visitFile(node);
        
        //writeResultToFile(writer.toString(), fileName);

        assertOutWithMetadata(getCodeFromFile(fileName + "_result", true, "flexjs/files"));
    }

    @Test
    public void testFlexJSMainFile()
    {
        String fileName = "FlexJSTest_again";

        IMXMLFileNode node = compileMXML(fileName, true,
                new File(testAdapter.getUnitTestBaseDir(), "flexjs/files").getPath(), false);

        mxmlBlockWalker.visitFile(node);

        //writeResultToFile(writer.toString(), fileName);

        assertOutWithMetadata(getCodeFromFile(fileName + "_result", true, "flexjs/files"));
    }

    @Test
    public void testFlexJSInitialViewFile()
    {
        String fileName = "MyInitialView";

        IMXMLFileNode node = compileMXML(fileName, true,
                new File(testAdapter.getUnitTestBaseDir(), "flexjs/files").getPath(), false);

        mxmlBlockWalker.visitFile(node);

        //writeResultToFile(writer.toString(), fileName);

        assertOutWithMetadata(getCodeFromFile(fileName + "_result", true, "flexjs/files"));
    }

    @Test
    public void testInterfaceAttribute()
    {
        String code = "<basic:Application xmlns:fx=\"http://ns.adobe.com/mxml/2009\" xmlns:basic=\"library://ns.apache.org/flexjs/basic\" implements=\"org.apache.flex.core.IChrome\">"
        		+ "<fx:Script><![CDATA["
                + "    import org.apache.flex.core.IChrome;"
                + "]]></fx:Script></basic:Application>";

        IMXMLDocumentNode dnode = (IMXMLDocumentNode) getNode(code,
        		IMXMLDocumentNode.class, FlexJSTestBase.WRAP_LEVEL_NONE);

        ((JSFlexJSEmitter)(mxmlBlockWalker.getASEmitter())).getModel().setCurrentClass(dnode.getDefinition());
        mxmlBlockWalker.visitDocument(dnode);
        String appName = dnode.getQualifiedName();
        String outTemplate = "/**\n" +
        		" * AppName\n" +
        		" *\n" +
        		" * @fileoverview\n" +
        		" *\n" +
        		" * @suppress {checkTypes|accessControls}\n" +
        		" */\n" +
        		"\n" +
        		"goog.provide('AppName');\n" +
        		"\n" +
        		"goog.require('org.apache.flex.core.Application');\n" +
        		"goog.require('org.apache.flex.core.IChrome');\n" +
        		"\n" +
        		"\n" +
        		"\n" +
        		"/**\n" +
        		" * @constructor\n" +
        		" * @extends {org.apache.flex.core.Application}\n" +
        		" */\n" +
        		"AppName = function() {\n" +
        		"  AppName.base(this, 'constructor');\n" +
        		"  \n" +
        		"  /**\n" +
        		"   * @private\n" +
        		"   * @type {Array}\n" +
        		"   */\n" +
        		"  this.mxmldd;\n" +
        		"  \n" +
        		"  /**\n" +
        		"   * @private\n" +
        		"   * @type {Array}\n" +
        		"   */\n" +
        		"  this.mxmldp;\n" +
        		"};\n" +
        		"goog.inherits(AppName, org.apache.flex.core.Application);\n" +
        		"\n" +
        		"\n" +
				"\n" +
        		"/**\n" +
        		" * Metadata\n" +
        		" *\n" +
        		" * @type {Object.<string, Array.<Object>>}\n" +
        		" */\n" +
        		"AppName.prototype.FLEXJS_CLASS_INFO = { names: [{ name: 'AppName', qName: 'AppName', kind: 'class'  }], interfaces: [org.apache.flex.core.IChrome] };\n" +
          		"\n" +
        		"\n" +
        		"/**\n" +
        		" * Prevent renaming of class. Needed for reflection.\n" +
        		" */\n" +
        		"goog.exportSymbol('AppName', AppName);\n" +
          		"\n" +
        		"\n" +
        		"\n" +
        		"/**\n" +
        		" * Reflection\n" +
        		" *\n" +
        		" * @return {Object.<string, Function>}\n" +
        		" */\n" +
        		"AppName.prototype.FLEXJS_REFLECTION_INFO = function () {\n" +
        		"  return {\n" +
        		"    variables: function () {return {};},\n" +
				"    accessors: function () {return {};},\n" +
        		"    methods: function () {\n" +
        		"      return {\n" +
				"        'AppName': { type: '', declaredBy: 'AppName'}\n"+
        		"      };\n" +
        		"    }\n" +
        		"  };\n" +
        		"};\n" +
        		"\n" +
        		"\n";

        assertOutWithMetadata(outTemplate.replaceAll("AppName", appName));
    }

    @Test
    public void testTwoInterfaceAttribute()
    {
        String code = "<basic:Application xmlns:fx=\"http://ns.adobe.com/mxml/2009\" xmlns:basic=\"library://ns.apache.org/flexjs/basic\" implements=\"org.apache.flex.core.IChrome, org.apache.flex.core.IPopUp\">"
        		+ "<fx:Script><![CDATA["
                + "    import org.apache.flex.core.IPopUp;"
                + "    import org.apache.flex.core.IChrome;"
                + "]]></fx:Script></basic:Application>";

        IMXMLDocumentNode dnode = (IMXMLDocumentNode) getNode(code,
        		IMXMLDocumentNode.class, FlexJSTestBase.WRAP_LEVEL_NONE);

        ((JSFlexJSEmitter)(mxmlBlockWalker.getASEmitter())).getModel().setCurrentClass(dnode.getDefinition());
        mxmlBlockWalker.visitDocument(dnode);
        String appName = dnode.getQualifiedName();
        String outTemplate = "/**\n" +
        		" * AppName\n" +
        		" *\n" +
        		" * @fileoverview\n" +
        		" *\n" +
        		" * @suppress {checkTypes|accessControls}\n" +
        		" */\n" +
        		"\n" +
        		"goog.provide('AppName');\n" +
        		"\n" +
        		"goog.require('org.apache.flex.core.Application');\n" +
        		"goog.require('org.apache.flex.core.IChrome');\n" +
        		"goog.require('org.apache.flex.core.IPopUp');\n" +
        		"\n" +
        		"\n" +
        		"\n" +
        		"/**\n" +
        		" * @constructor\n" +
        		" * @extends {org.apache.flex.core.Application}\n" +
        		" */\n" +
        		"AppName = function() {\n" +
        		"  AppName.base(this, 'constructor');\n" +
        		"  \n" +
        		"  /**\n" +
        		"   * @private\n" +
        		"   * @type {Array}\n" +
        		"   */\n" +
        		"  this.mxmldd;\n" +
        		"  \n" +
        		"  /**\n" +
        		"   * @private\n" +
        		"   * @type {Array}\n" +
        		"   */\n" +
        		"  this.mxmldp;\n" +
        		"};\n" +
        		"goog.inherits(AppName, org.apache.flex.core.Application);\n" +
        		"\n" +
        		"\n" +
				"\n" +
        		"/**\n" +
        		" * Metadata\n" +
        		" *\n" +
        		" * @type {Object.<string, Array.<Object>>}\n" +
        		" */\n" +
        		"AppName.prototype.FLEXJS_CLASS_INFO = { names: [{ name: 'AppName', qName: 'AppName', kind: 'class'  }], interfaces: [org.apache.flex.core.IChrome, org.apache.flex.core.IPopUp] };\n" +
          		"\n" +
        		"\n" +
        		"/**\n" +
        		" * Prevent renaming of class. Needed for reflection.\n" +
        		" */\n" +
        		"goog.exportSymbol('AppName', AppName);\n" +
          		"\n" +
        		"\n" +
        		"\n" +
        		"/**\n" +
        		" * Reflection\n" +
        		" *\n" +
        		" * @return {Object.<string, Function>}\n" +
        		" */\n" +
        		"AppName.prototype.FLEXJS_REFLECTION_INFO = function () {\n" +
				"  return {\n" +
				"    variables: function () {return {};},\n" +
				"    accessors: function () {return {};},\n" +
				"    methods: function () {\n" +
				"      return {\n" +
				"        'AppName': { type: '', declaredBy: 'AppName'}\n"+
				"      };\n" +
				"    }\n" +
				"  };\n" +
				"};\n" +
        		"\n" +
        		"\n" ;

        assertOutWithMetadata(outTemplate.replaceAll("AppName", appName));
    }

}
