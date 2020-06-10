package com.loghme.domain.schedulers;

import com.loghme.repository.LoghmeRepository;

import java.sql.SQLException;
import java.util.TimerTask;

public class ChangeCourierStatus extends TimerTask {
    private int orderId;

    public ChangeCourierStatus(int orderId) {this.orderId = orderId;}

    public void run() {
        try {
            LoghmeRepository.getInstance().updateOrderStatus("done", orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cancel();
    }
}
