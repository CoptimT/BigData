/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.api

import kafka.common.ErrorMapping
import java.nio.ByteBuffer
import kafka.api.ApiUtils._
import collection.mutable.HashMap
import collection.Map


object LeaderAndIsrResponse {
  def readFrom(buffer: ByteBuffer): LeaderAndIsrResponse = {
    val correlationId = buffer.getInt
    val errorCode = buffer.getShort
    val numEntries = buffer.getInt
    val responseMap = new HashMap[(String, Int), Short]()
    for (i<- 0 until numEntries){
      val topic = readShortString(buffer)
      val partition = buffer.getInt
      val partitionErrorCode = buffer.getShort
      responseMap.put((topic, partition), partitionErrorCode)
    }
    new LeaderAndIsrResponse(correlationId, responseMap, errorCode)
  }
}


case class LeaderAndIsrResponse(correlationId: Int,
                                responseMap: Map[(String, Int), Short],
                                errorCode: Short = ErrorMapping.NoError)
    extends RequestOrResponse() {
  def sizeInBytes(): Int ={
    var size =
      4 /* correlation id */ + 
      2 /* error code */ +
      4 /* number of responses */
    for ((key, value) <- responseMap) {
      size +=
        2 + key._1.length /* topic */ +
        4 /* partition */ +
        2 /* error code for this partition */
    }
    size
  }

  def writeTo(buffer: ByteBuffer) {
    buffer.putInt(correlationId)
    buffer.putShort(errorCode)
    buffer.putInt(responseMap.size)
    for ((key:(String, Int), value) <- responseMap) {
      writeShortString(buffer, key._1)
      buffer.putInt(key._2)
      buffer.putShort(value)
    }
  }

  override def describe(details: Boolean):String = { toString }

}