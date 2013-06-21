package org.zeroturnaround.stats.model;

import java.util.UUID;

public class RunStats {
  private long duration;
  private long timeInQueue = -1;
  private int queueId;

  private long started;
  private long queued;
  private String projectName;
  private String nodeName;
  private final String uuid;

  public RunStats() {
    super();
    this.queued = System.currentTimeMillis();
    this.uuid = UUID.randomUUID().toString();
    this.nodeName = "master";
  }

  public RunStats(long duration, long timeInQueue, long started, long queued, String projectName) {
    this();
    this.duration = duration;
    this.timeInQueue = timeInQueue;
    this.started = started;
    this.queued = queued;
    this.projectName = projectName;
  }

  public RunStats(long duration, long timeInQueue, long started, long queued, String projectName, String nodeName) {
    this();
    this.duration = duration;
    this.timeInQueue = timeInQueue;
    this.started = started;
    this.queued = queued;
    this.projectName = projectName;
    this.nodeName = nodeName;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getStarted() {
    return started;
  }

  public void setStarted(long started) {
    this.started = started;
    this.timeInQueue = started - queued;
    if (this.timeInQueue < 0)
      this.timeInQueue = 0;
  }

  public long getQueued() {
    return queued;
  }

  public void setQueued(long queued) {
    this.queued = queued;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public long getTimeInQueue() {
    return timeInQueue;
  }

  public String getUuid() {
    return uuid;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getNodeName() {
    return nodeName;
  }
  
  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (duration ^ (duration >>> 32));
    result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
    result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
    result = prime * result + queueId;
    result = prime * result + (int) (queued ^ (queued >>> 32));
    result = prime * result + (int) (started ^ (started >>> 32));
    result = prime * result + (int) (timeInQueue ^ (timeInQueue >>> 32));
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RunStats other = (RunStats) obj;
    if (duration != other.duration)
      return false;
    if (nodeName == null) {
      if (other.nodeName != null)
        return false;
    }
    else if (!nodeName.equals(other.nodeName))
      return false;
    if (projectName == null) {
      if (other.projectName != null)
        return false;
    }
    else if (!projectName.equals(other.projectName))
      return false;
    if (queueId != other.queueId)
      return false;
    if (queued != other.queued)
      return false;
    if (started != other.started)
      return false;
    if (timeInQueue != other.timeInQueue)
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    }
    else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }
}
