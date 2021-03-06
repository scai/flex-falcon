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

package org.apache.flex.compiler.internal.codegen.js.jx;

import org.apache.flex.compiler.asdoc.flexjs.ASDocComment;
import org.apache.flex.compiler.clients.MXMLJSC;
import org.apache.flex.compiler.codegen.ISubEmitter;
import org.apache.flex.compiler.codegen.js.IJSEmitter;
import org.apache.flex.compiler.definitions.IClassDefinition;
import org.apache.flex.compiler.definitions.IFunctionDefinition;
import org.apache.flex.compiler.internal.codegen.as.ASEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.JSSubEmitter;
import org.apache.flex.compiler.internal.codegen.js.flexjs.JSFlexJSEmitter;
import org.apache.flex.compiler.internal.codegen.js.goog.JSGoogEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.utils.DocEmitterUtils;
import org.apache.flex.compiler.internal.codegen.js.utils.EmitterUtils;
import org.apache.flex.compiler.internal.tree.as.IdentifierNode;
import org.apache.flex.compiler.tree.ASTNodeID;
import org.apache.flex.compiler.tree.as.IASNode;
import org.apache.flex.compiler.tree.as.IAccessorNode;
import org.apache.flex.compiler.tree.as.IClassNode;
import org.apache.flex.compiler.tree.as.IDefinitionNode;
import org.apache.flex.compiler.tree.as.IExpressionNode;
import org.apache.flex.compiler.tree.as.IFunctionNode;
import org.apache.flex.compiler.tree.as.IVariableNode;

public class ClassEmitter extends JSSubEmitter implements
        ISubEmitter<IClassNode>
{

    public ClassEmitter(IJSEmitter emitter)
    {
        super(emitter);
    }

    @Override
    public void emit(IClassNode node)
    {
        getModel().pushClass(node.getDefinition());

        // TODO (mschmalle) will remove this cast as more things get abstracted
        JSFlexJSEmitter fjs = (JSFlexJSEmitter) getEmitter();
        
        ASDocComment asDoc = (ASDocComment) node.getASDocComment();
        if (asDoc != null && MXMLJSC.keepASDoc)
            DocEmitterUtils.loadImportIgnores(fjs, asDoc.commentNoEnd());

        IClassDefinition definition = node.getDefinition();

        IFunctionDefinition ctorDefinition = definition.getConstructor();

        // look for force-linking pattern in scope block node
        int childNodeCount = node.getChildCount();
        for (int i = 0; i < childNodeCount; i++)
        {
        	IASNode child = node.getChild(i);
        	if (child.getNodeID() == ASTNodeID.BlockID)
        	{
        		int blockNodeCount = child.getChildCount();
        		for (int j = 0; j < blockNodeCount - 1; j++)
        		{
        			IASNode blockChild = child.getChild(j);
        			if (blockChild.getNodeID() == ASTNodeID.ImportID)
        			{
        				IASNode afterChild = child.getChild(j + 1);
        				if (afterChild.getNodeID() == ASTNodeID.IdentifierID)
        				{
                            write(JSGoogEmitterTokens.GOOG_REQUIRE);
                            write(ASEmitterTokens.PAREN_OPEN);
                            write(ASEmitterTokens.SINGLE_QUOTE);
                            getEmitter().emitIdentifier((IdentifierNode)afterChild);                            
                            write(ASEmitterTokens.SINGLE_QUOTE);
                            write(ASEmitterTokens.PAREN_CLOSE);
                            writeNewline(ASEmitterTokens.SEMICOLON);
                            writeNewline();
                            writeNewline();
        				}
        			}
        		}
        		break;
        	}        	
        }
        
        // Static-only (Singleton) classes may not have a constructor
        if (ctorDefinition != null)
        {
            IFunctionNode ctorNode = (IFunctionNode) ctorDefinition.getNode();
            if (ctorNode != null)
            {
                // constructor
                getEmitter().emitMethod(ctorNode);
                write(ASEmitterTokens.SEMICOLON);
            }
            else
            {
                String qname = definition.getQualifiedName();
                if (qname != null && !qname.equals(""))
                {
                    write(getEmitter().formatQualifiedName(qname));
                    write(ASEmitterTokens.SPACE);
                    writeToken(ASEmitterTokens.EQUAL);
                    write(ASEmitterTokens.FUNCTION);
                    write(ASEmitterTokens.PAREN_OPEN);
                    write(ASEmitterTokens.PAREN_CLOSE);
                    write(ASEmitterTokens.SPACE);
                    write(ASEmitterTokens.BLOCK_OPEN);
                    writeNewline();
                    fjs.emitComplexInitializers(node);
                    write(ASEmitterTokens.BLOCK_CLOSE);
                    write(ASEmitterTokens.SEMICOLON);
                }
            }
        }

        IDefinitionNode[] dnodes = node.getAllMemberNodes();
        for (IDefinitionNode dnode : dnodes)
        {
            if (dnode.getNodeID() == ASTNodeID.VariableID)
            {
                writeNewline();
                writeNewline();
                writeNewline();
                getEmitter().emitField((IVariableNode) dnode);
                startMapping(dnode, dnode);
                write(ASEmitterTokens.SEMICOLON);
                endMapping(dnode);
            }
            else if (dnode.getNodeID() == ASTNodeID.FunctionID)
            {
                if (!((IFunctionNode) dnode).isConstructor())
                {
                    writeNewline();
                    writeNewline();
                    writeNewline();
                    getEmitter().emitMethod((IFunctionNode) dnode);
                    write(ASEmitterTokens.SEMICOLON);
                }
            }
            else if (dnode.getNodeID() == ASTNodeID.GetterID
                    || dnode.getNodeID() == ASTNodeID.SetterID)
            {
                //writeNewline();
                //writeNewline();
                //writeNewline();
                fjs.emitAccessors((IAccessorNode) dnode);
                //this shouldn't write anything, just set up
                //a data structure for emitASGettersAndSetters
                //write(ASEmitterTokens.SEMICOLON);
            }
            else if (dnode.getNodeID() == ASTNodeID.BindableVariableID)
            {
                writeNewline();
                writeNewline();
                writeNewline();
                getEmitter().emitField((IVariableNode) dnode);
                startMapping(dnode, dnode);
                write(ASEmitterTokens.SEMICOLON);
                endMapping(dnode);
            }
        }

        fjs.getBindableEmitter().emit(definition);
        fjs.getAccessorEmitter().emit(definition);
        
        if (fjs.getFieldEmitter().hasComplexStaticInitializers)
        {
            writeNewline();
            boolean complexInitOutput = false;
	        for (IDefinitionNode dnode : dnodes)
	        {
	            if (dnode.getNodeID() == ASTNodeID.VariableID)
	            {
                    complexInitOutput = fjs.getFieldEmitter().emitFieldInitializer((IVariableNode) dnode) || complexInitOutput;
	            }
	            else if (dnode.getNodeID() == ASTNodeID.BindableVariableID)
	            {
                    complexInitOutput = fjs.getFieldEmitter().emitFieldInitializer((IVariableNode) dnode) || complexInitOutput;
	            }
	        }
	        if (complexInitOutput) {
                writeNewline();
                writeNewline();
            }
        }
        
        fjs.getPackageFooterEmitter().emitClassInfo(node);

        getModel().popClass();
    }
    
    public void emitComplexInitializers(IClassNode node)
    {
    	boolean wroteOne = false;
        IDefinitionNode[] dnodes = node.getAllMemberNodes();
        for (IDefinitionNode dnode : dnodes)
        {
            if (dnode.getNodeID() == ASTNodeID.VariableID)
            {
            	IVariableNode varnode = ((IVariableNode)dnode);
                IExpressionNode vnode = varnode.getAssignedValueNode();
                if (vnode != null && (!(dnode.getDefinition().isStatic() || EmitterUtils.isScalar(vnode))))
                {
                    writeNewline();
                    write(ASEmitterTokens.THIS);
                    write(ASEmitterTokens.MEMBER_ACCESS);
                    write(dnode.getName());
                    write(ASEmitterTokens.SPACE);
                    writeToken(ASEmitterTokens.EQUAL);
                    getEmitter().getWalker().walk(vnode);
                    write(ASEmitterTokens.SEMICOLON);
                    wroteOne = true;
                }
            }
        }    
        if (wroteOne)
        	writeNewline();
    }
}
