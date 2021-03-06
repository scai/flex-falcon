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

package org.apache.flex.compiler.tree.as.decorators;

import org.apache.flex.compiler.definitions.IDefinition;

/**
 * Decorates a variable's type based on context about the variable
 */
public interface IVariableTypeDecorator
{
    /**
     * Decorates this variables type based on context, and returns back the
     * actual type of this variable
     * 
     * @param variable the variable to decorate
     * @return an {@link IDefinition} or null
     */
    IDefinition decorateVariableType(IDefinition variable);

    /**
     * Determines if this decorator is applicable for the given
     * {@link IDefinition}
     * 
     * @param variable the {@link IDefinition} we may want to decorate
     * @return true if applicable
     */
    boolean isApplicable(IDefinition variable);
}
