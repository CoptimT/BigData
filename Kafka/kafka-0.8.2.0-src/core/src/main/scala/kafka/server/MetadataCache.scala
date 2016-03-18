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

package kafka.server

import scala.collection.{Seq, Set, mutable}
import kafka.api._
import kafka.cluster.Broker
import java.util.concurrent.locks.ReentrantReadWriteLock
import kafka.utils.Utils._
import kafka.common.{ErrorMapping, ReplicaNotAvailableException, LeaderNotAvailableException}
import kafka.common.TopicAndPartition
import kafka.controller.KafkaController.StateChangeLogger

/**
 *  A cache for the state (e.g., current leader) of each partition. This cache is updated through
 *  UpdateMetadataRequest from the controller. Every broker maintains the same cache, asynchronously.
 */
private[server] class MetadataCache {
  private val cache: mutable.Map[String, mutable.Map[Int, PartitionStateInfo]] =
    new mutable.HashMap[String, mutable.Map[Int, PartitionStateInfo]]()
  private var aliveBrokers: Map[Int, Broker] = Map()
  private val partitionMetadataLock = new ReentrantReadWriteLock()

  def getTopicMetadata(topics: Set[String]) = {
    val isAllTopics = topics.isEmpty
    val topicsRequested = if(isAllTopics) cache.keySet else topics
    val topicResponses: mutable.ListBuffer[TopicMetadata] = new mutable.ListBuffer[TopicMetadata]
    inReadLock(partitionMetadataLock) {
      for (topic <- topicsRequested) {
        if (isAllTopics || cache.contains(topic)) {
          val partitionStateInfos = cache(topic)
          val partitionMetadata = partitionStateInfos.map {
            case (partitionId, partitionState) =>
              val replicas = partitionState.allReplicas
              val replicaInfo: Seq[Broker] = replicas.map(aliveBrokers.getOrElse(_, null)).filter(_ != null).toSeq
              var leaderInfo: Option[Broker] = None
              var isrInfo: Seq[Broker] = Nil
              val leaderIsrAndEpoch = partitionState.leaderIsrAndControllerEpoch
              val leader = leaderIsrAndEpoch.leaderAndIsr.leader
              val isr = leaderIsrAndEpoch.leaderAndIsr.isr
              val topicPartition = TopicAndPartition(topic, partitionId)
              try {
                leaderInfo = aliveBrokers.get(leader)
                if (!leaderInfo.isDefined)
                  throw new LeaderNotAvailableException("Leader not available for %s".format(topicPartition))
                isrInfo = isr.map(aliveBrokers.getOrElse(_, null)).filter(_ != null)
                if (replicaInfo.size < replicas.size)
                  throw new ReplicaNotAvailableException("Replica information not available for following brokers: " +
                    replicas.filterNot(replicaInfo.map(_.id).contains(_)).mkString(","))
                if (isrInfo.size < isr.size)
                  throw new ReplicaNotAvailableException("In Sync Replica information not available for following brokers: " +
                    isr.filterNot(isrInfo.map(_.id).contains(_)).mkString(","))
                new PartitionMetadata(partitionId, leaderInfo, replicaInfo, isrInfo, ErrorMapping.NoError)
              } catch {
                case e: Throwable =>
                  debug("Error while fetching metadata for %s. Possible cause: %s".format(topicPartition, e.getMessage))
                  new PartitionMetadata(partitionId, leaderInfo, replicaInfo, isrInfo,
                    ErrorMapping.codeFor(e.getClass.asInstanceOf[Class[Throwable]]))
              }
          }
          topicResponses += new TopicMetadata(topic, partitionMetadata.toSeq)
        }
      }
    }
    topicResponses
  }

  def getAliveBrokers = {
    inReadLock(partitionMetadataLock) {
      aliveBrokers.values.toSeq
    }
  }

  def addOrUpdatePartitionInfo(topic: String,
                               partitionId: Int,
                               stateInfo: PartitionStateInfo) {
    inWriteLock(partitionMetadataLock) {
      cache.get(topic) match {
        case Some(infos) => infos.put(partitionId, stateInfo)
        case None => {
          val newInfos: mutable.Map[Int, PartitionStateInfo] = new mutable.HashMap[Int, PartitionStateInfo]
          cache.put(topic, newInfos)
          newInfos.put(partitionId, stateInfo)
        }
      }
    }
  }

  def getPartitionInfo(topic: String, partitionId: Int): Option[PartitionStateInfo] = {
    inReadLock(partitionMetadataLock) {
      cache.get(topic) match {
        case Some(partitionInfos) => partitionInfos.get(partitionId)
        case None => None
      }
    }
  }

  def updateCache(updateMetadataRequest: UpdateMetadataRequest,
                  brokerId: Int,
                  stateChangeLogger: StateChangeLogger) {
    inWriteLock(partitionMetadataLock) {
      aliveBrokers = updateMetadataRequest.aliveBrokers.map(b => (b.id, b)).toMap
      updateMetadataRequest.partitionStateInfos.foreach { case(tp, info) =>
        if (info.leaderIsrAndControllerEpoch.leaderAndIsr.leader == LeaderAndIsr.LeaderDuringDelete) {
          removePartitionInfo(tp.topic, tp.partition)
          stateChangeLogger.trace(("Broker %d deleted partition %s from metadata cache in response to UpdateMetadata request " +
            "sent by controller %d epoch %d with correlation id %d")
            .format(brokerId, tp, updateMetadataRequest.controllerId,
            updateMetadataRequest.controllerEpoch, updateMetadataRequest.correlationId))
        } else {
          addOrUpdatePartitionInfo(tp.topic, tp.partition, info)
          stateChangeLogger.trace(("Broker %d cached leader info %s for partition %s in response to UpdateMetadata request " +
            "sent by controller %d epoch %d with correlation id %d")
            .format(brokerId, info, tp, updateMetadataRequest.controllerId,
            updateMetadataRequest.controllerEpoch, updateMetadataRequest.correlationId))
        }
      }
    }
  }

  private def removePartitionInfo(topic: String, partitionId: Int) = {
    cache.get(topic) match {
      case Some(infos) => {
        infos.remove(partitionId)
        if(infos.isEmpty) {
          cache.remove(topic)
        }
        true
      }
      case None => false
    }
  }
}

