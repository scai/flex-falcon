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

package org.apache.flex.compiler.internal.driver;

import java.util.Collection;
import java.util.List;

import org.apache.flex.abc.visitors.IABCVisitor;
import org.apache.flex.compiler.config.Configurator;
import org.apache.flex.compiler.internal.as.codegen.CmcJSEmitter;
import org.apache.flex.compiler.internal.as.codegen.InterfaceDirectiveProcessor;
import org.apache.flex.compiler.internal.as.codegen.JSEmitter;
import org.apache.flex.compiler.internal.as.codegen.JSGeneratingReducer;
import org.apache.flex.compiler.internal.as.codegen.JSGenerator;
import org.apache.flex.compiler.internal.as.codegen.JSGlobalDirectiveProcessor;
import org.apache.flex.compiler.internal.as.codegen.JSClassDirectiveProcessor;
import org.apache.flex.compiler.internal.as.codegen.JSWriter;
import org.apache.flex.compiler.internal.as.codegen.LexicalScope;
import org.apache.flex.compiler.internal.projects.CompilerProject;
import org.apache.flex.compiler.internal.projects.ISourceFileHandler;
import org.apache.flex.compiler.internal.tree.as.ClassNode;
import org.apache.flex.compiler.internal.tree.as.InterfaceNode;
import org.apache.flex.compiler.problems.ICompilerProblem;
import org.apache.flex.compiler.projects.ICompilerProject;
import org.apache.flex.compiler.targets.ISWFTarget;
import org.apache.flex.compiler.targets.ITargetProgressMonitor;
import org.apache.flex.compiler.targets.ITargetSettings;
import org.apache.flex.compiler.units.ICompilationUnit;
import org.apache.flex.swf.ISWF;
import org.apache.flex.swf.io.ISWFWriter;

public interface IBackend
{
    // called by MXMLJSC
    public Configurator createConfigurator();

    public ISWFWriter createSWFWriter(ICompilerProject project, List<ICompilerProblem> problems, ISWF swf, boolean useCompression, boolean enableDebug);

    public JSWriter createJSWriter(ICompilerProject project, List<ICompilerProblem> problems, Collection<ICompilationUnit> compilationUnits, boolean useCompression);

    public ISWFTarget createSWFTarget(CompilerProject project, ITargetSettings targetSettings, ITargetProgressMonitor progressMonitor) throws InterruptedException;

    public ISourceFileHandler getSourceFileHandlerInstance();

    // TODO: can all reducers be derived from IGenerator?
    // called by JSCompilationUnit.
    public JSGenerator createGenerator();

    // called by JSGenerator.
    public JSEmitter createEmitter(ICompilationUnit.Operation buildPhase, ICompilerProject project, JSGenerator generator);

    // TODO: can all reducers be derived from ICmcEmitter?
    // called by JSGenerator.
    public CmcJSEmitter createCmcJSEmitter();

    // TODO: can all reducers be derived from IReducer?
    // called by JSGenerator.
    public JSGeneratingReducer createReducer();

    // TODO: GlobalDirectiveProcessor is not visible
    // called by JSGenerator.
    public JSGlobalDirectiveProcessor createGlobalDirectiveProcessor(JSGenerator generator, LexicalScope current_scope, IABCVisitor emitter);

    // TODO: ClassDirectiveProcessor is not visible
    public JSClassDirectiveProcessor createClassDirectiveProcessor(JSGenerator generator, ClassNode c, LexicalScope enclosing_scope, IABCVisitor emitter);

    public InterfaceDirectiveProcessor createInterfaceDirectiveProcessor(JSGenerator jsGenerator, InterfaceNode in, LexicalScope enclosing_scope, IABCVisitor emitter);

    public RuntimeException createException(String msg);

    public String getOutputExtension();
}
