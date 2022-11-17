package com.akka.tools.time;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class StopWatch {

    public StopWatch(String watchName) {
        this.watchName = watchName;
    }

    private volatile long totalTime;
    private Map<Long, TaskInfo> taskInfos = new ConcurrentHashMap<>();

    private String watchName;
    private static final AtomicLongFieldUpdater<StopWatch> TOTAL_TIME;

    static {
        TOTAL_TIME = AtomicLongFieldUpdater.newUpdater(StopWatch.class, "totalTime");
    }


    public void startTask(long id, String taskName) {

        if (taskInfos.containsKey(id)) {
            throw new IllegalArgumentException("task: " + id + "already exist");
        }

        TaskInfo taskInfo = new TaskInfo();
        TaskInfo old = taskInfos.putIfAbsent(id, taskInfo);

        if (old != null) {
            throw new RuntimeException("task: " + id + " repeat the create");
        }

        taskInfo.taskName = taskName;
        taskInfo.id = id;
        taskInfo.running = true;
        taskInfo.startTime = System.currentTimeMillis();
    }


    public void stopTask(long id) {
        if (!taskInfos.containsKey(id)) {
            throw new IllegalArgumentException("task: " + id + " not exist");
        }
        TaskInfo taskInfo = taskInfos.get(id);
        if (taskInfo.running) {
            synchronized (taskInfo) {
                if (taskInfo.running) {
                    taskInfo.running = false;
                    taskInfo.endTime = System.currentTimeMillis();
                    TOTAL_TIME.addAndGet(this, taskInfo.endTime - taskInfo.startTime);
                } else {
                    throw new RuntimeException("task: " + id + " already stop");
                }
            }
        } else {
            throw new RuntimeException("task: " + id + " already stop");
        }
    }


    public String shortSummary() {
        return "StopWatch: " + watchName + " total running time: " + totalTime + "\n";
    }

    public String prettyPrint() {
        StringBuilder pretty = new StringBuilder(shortSummary());
        pretty.append("-----------------------------------------\n");
        pretty.append("ms     %     Task name\n");
        pretty.append("-----------------------------------------\n");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(5);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (TaskInfo task : taskInfos.values()) {
            if (!task.running) {
                long time = task.endTime - task.startTime;
                pretty.append(nf.format(time)).append("  ");
                pretty.append(pf.format((double) time / totalTime)).append("  ");
                pretty.append(task.taskName).append("\n");

            }
        }
        return pretty.toString();
    }


    public Map<Long, TaskInfo> getTaskInfo() {
        return taskInfos;
    }

    static class TaskInfo {
        private long id;
        private long startTime;
        private long endTime;
        private String taskName;

        private volatile boolean running;
    }
}
