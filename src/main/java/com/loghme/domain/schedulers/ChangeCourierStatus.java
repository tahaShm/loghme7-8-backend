package com.loghme.domain.schedulers;

import com.loghme.repository.LoghmeRepository;

import java.util.TimerTask;

public class ChangeCourierStatus extends TimerTask {
    private int orderId;

    public ChangeCourierStatus(int orderId) {this.orderId = orderId;}

    public void run() {
        LoghmeRepository.getInstance().updateOrderStatus("done", orderId);
        cancel();
    }
}
