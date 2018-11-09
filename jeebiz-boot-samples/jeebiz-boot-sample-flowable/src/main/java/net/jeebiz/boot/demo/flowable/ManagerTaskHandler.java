/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.flowable;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
 
public class ManagerTaskHandler implements TaskListener {
 
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("经理");
    }
 
}
