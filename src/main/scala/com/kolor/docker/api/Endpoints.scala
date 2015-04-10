package com.kolor.docker.api

//import com.netaporter.uri.dsl._
//import com.netaporter.uri.String.parse
//import com.netaporter.uri.String
import com.kolor.docker.api.entities._

object Endpoints {

  implicit protected def host(implicit docker: DockerClient) = docker.dockerHost
  implicit protected def port(implicit docker: DockerClient) = docker.dockerPort
  
  def baseUri(implicit docker: DockerClient): String = raw"http://$host:$port"
  
  def dockerInfo(implicit docker: DockerClient): String = {
    raw"$baseUri/info"
  }
  
  def dockerPing(implicit docker: DockerClient): String = {
    raw"$baseUri/_ping"
  }
  
  def dockerVersion(implicit docker: DockerClient): String = {
    raw"$baseUri/version"
  }
  
  def dockerAuth(implicit docker: DockerClient): String = {
    raw"$baseUri/auth"
  }
  
  def dockerBuild(tag: String, verbose: Boolean = false, noCache: Boolean = false, forceRm: Boolean = false)(implicit docker: DockerClient): String = {
    raw"$baseUri/build?q=$verbose&nocache=$noCache&t=$tag&forcerm=$forceRm"
  }
  
  def dockerEvents(since: Option[Long] = None, until: Option[Long] = None)(implicit docker: DockerClient): String = {
    since match {
      case Some(l) if (l > 0) => until match {
        case Some(u) if (u > 0) => raw"$baseUri/events?since=$l&until=$u"
        case _ => raw"$baseUri/events?since=$l"
      }
      case _ => raw"$baseUri/events"
    }
  }
  
  /**
   * docker container endpoints
   */
  
  
  def containers(all:Boolean = true, limit: Option[Int] = None, sinceId: Option[String] = None, beforeId: Option[String] = None, showSize: Boolean = true)(implicit docker: DockerClient) = {
    raw"$baseUri/containers/json?all=$all&limit=$limit&sinceId=$sinceId&beforeId=$beforeId&showSize=$showSize"
  }
  
  def containerCreate(name: Option[String] = None)(implicit docker: DockerClient): String = {
   name match {
     case Some(str) => raw"$baseUri/containers/create?name=$str"
     case None => raw"$baseUri/containers/create"
  }
 }
  
  def containerInspect(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/json"
  }
  
  def containerProcesses(id: ContainerId, psArgs: Option[String] = None)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/top?ps_args=$psArgs"
  }
  
  def containerChangelog(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/changes"
  }
  
  def containerExport(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/export"
  }
  
  def containerStart(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/start"
  }
  
  def containerStop(id: ContainerId, timeoutToKill: Int = 60)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/stop?t=$timeoutToKill"
  }
  
  def containerRestart(id: ContainerId, timeoutToKill: Int = 60)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/restart?t=$timeoutToKill"
  }
  
  def containerKill(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/kill"
  }
  
  def containerAttach(id: ContainerId, stream: Boolean, stdin: Boolean = false, stdout: Boolean = true, stderr: Boolean = false, logs: Boolean = true)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/attach?stream=$stream&stdin=$stdin&stdout=$stdout&stderr=$stderr&logs=$logs"
  }
        
  def containerLogs(id: ContainerId, stream: Boolean, stdout: Boolean = true, stderr: Boolean = false, withTimestamps: Boolean = false, tail: Option[Int] = None)(implicit docker: DockerClient): String = {
    tail match {
      case Some(n) => raw"$baseUri/containers/${id.toString}/logs?follow=$stream&stdout=$stdout&stderr=$stderr&timestamps=$withTimestamps&tail=$n"
      case _ => raw"$baseUri/containers/${id.toString}/logs?follow=$stream&stdout=$stdout&stderr=$stderr&timestamps=$withTimestamps"
    }
  }
  
  def containerWait(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/wait"
  }
  
  def containerRemove(id: ContainerId, withVolumes: Boolean = false, force: Boolean = false)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}?v=$withVolumes&force=$force"
  }
  
  def containerCopy(id: ContainerId)(implicit docker: DockerClient): String = {
    raw"$baseUri/containers/${id.toString}/copy"
  }
  
  def containerCommit(id: ContainerId, repo: String, tag: Option[String], runConfig: Option[String] = None, message: Option[String] = None, author: Option[String] = None, pause: Boolean = true)(implicit docker: DockerClient): String = {
    raw"$baseUri/commit?container=${id.toString}&repo=$repo&tag=$tag&m=$message&author=$author&pause=$pause&run=$runConfig"
  }
  
  /**
   * docker images endpoints
   */
  
  def images(all: Boolean = false)(implicit docker: DockerClient): String = {
    all match {
      case true => raw"$baseUri/images/json?all=$all"
      case false => raw"$baseUri/images/json" 
    }
  }
  
  def imageCreate(fromImage: String, fromSource: Option[String] = None, repo: Option[String], tag: Option[String], registry: Option[String])(implicit docker: DockerClient): String = {
    raw"$baseUri/images/create?fromImage=$fromImage&fromSource=$fromSource&repo=$repo&tag=$tag&registry=$registry"
  }
  
  def imageInsert(name: String, imageTargetPath: String, source: java.net.URI)(implicit docker: DockerClient): String = {    
    docker match {
      case d:DockerClientV19 => 
        raw"$baseUri/images/name/insert?path=$imageTargetPath&url=${source.toString}"
      case _ => throw new RuntimeException("imageInsert endpoint removed with api v1.12")
    }
  }
  
  def imageInspect(name: String)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/name/json"
  }
  
  def imageHistory(name: String)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/name/history"
  }
  
  def imagePush(name: String, registry: Option[String])(implicit docker: DockerClient): String = {
    raw"$baseUri/images/name/push?registry=$registry"
  }
  
  def imageTag(name: String, repo: String, force: Boolean = false)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/name/tag?repo=$repo&force=$force"
  }
  
  def imageRemove(name: String, force: Boolean = false, noPrune: Boolean = false)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/name?force=$force&noprune=$noPrune"
  }
  
  def imageSearch(term: String)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/search?term=$term"
  }
  
  def imageExport(image: String)(implicit docker: DockerClient): String = {
    raw"$baseUri/images/image/get"
  }
  
  def imagesLoad(implicit docker: DockerClient): String = {
    raw"$baseUri/images/load"
  }
  
}
