package com.alibou.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReassignCustomersDTO {
    private String oldManagerId;
    private String newManagerId;

    public String getOldManagerId() {
        return oldManagerId;
    }

    public void setOldManagerId(String oldManagerId) {
        this.oldManagerId = oldManagerId;
    }

    public String getNewManagerId() {
        return newManagerId;
    }

    public void setNewManagerId(String newManagerId) {
        this.newManagerId = newManagerId;
    }

  
}
