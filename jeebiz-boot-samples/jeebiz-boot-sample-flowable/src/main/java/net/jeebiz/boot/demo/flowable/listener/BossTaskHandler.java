/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.flowable.listener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class BossTaskHandler implements TaskListener {
	 
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("老板");
    }
 
}
