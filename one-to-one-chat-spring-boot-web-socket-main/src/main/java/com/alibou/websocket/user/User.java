package com.alibou.websocket.user;



import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Document
public class User {
    @Id
    private String id;  
    private String fullName;
    private Status status;
    private String role; 
     private List<String> assignedCustomers = new ArrayList<>(); // Store only customer IDs (For MANAGER)
    private String assignedManagerId; 

    // public String getId() {
    //     return id;
    // }

    // public void setId(String id) {
    //     this.id = id;
    // }

    // public Status getStatus() {
    //     return status;
    // }

    // public void setStatus(Status status) {
    //     this.status = status;
    // }
}

