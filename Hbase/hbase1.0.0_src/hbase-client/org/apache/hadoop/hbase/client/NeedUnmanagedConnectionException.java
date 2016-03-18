/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.client;

import org.apache.hadoop.hbase.classification.InterfaceAudience;

import java.io.IOException;

/**
 * Used for internal signalling that a Connection implementation needs to be
 * user-managed to be used for particular request types.
 */
@InterfaceAudience.Private
public class NeedUnmanagedConnectionException extends IOException {
  private static final long serialVersionUID = 1876775844L;

  public NeedUnmanagedConnectionException() {
    super("The connection has to be unmanaged.");
  }
}
