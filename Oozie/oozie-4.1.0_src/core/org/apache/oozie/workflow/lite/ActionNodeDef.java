/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.oozie.workflow.lite;

import org.apache.oozie.util.ParamChecker;
import org.apache.oozie.util.XLog;

import java.util.Arrays;

/**
 * Node definition for workflow action. This node definition is serialized object and should provide
 * readFields() and write() for read and write of fields in this class.  
 *
 */
public class ActionNodeDef extends NodeDef {

    ActionNodeDef() {
    }

    public ActionNodeDef(String name, String conf, Class<? extends ActionNodeHandler> actionHandlerClass, String onOk,
                         String onError) {
        super(name, ParamChecker.notNull(conf, "conf"), actionHandlerClass, Arrays.asList(onOk, onError));
    }
    
    public ActionNodeDef(String name, String conf, Class<? extends ActionNodeHandler> actionHandlerClass, String onOk,
            String onError, String cred) {
        super(name, ParamChecker.notNull(conf, "conf"), actionHandlerClass, Arrays.asList(onOk, onError), cred);
    }
    
    public ActionNodeDef(String name, String conf, Class<? extends ActionNodeHandler> actionHandlerClass, String onOk,
            String onError, String cred, String userRetryMax, String userRetryInterval) {
        super(name, ParamChecker.notNull(conf, "conf"), actionHandlerClass, Arrays.asList(onOk, onError), cred, userRetryMax, userRetryInterval);
    }
}
