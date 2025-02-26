package com.alibou.websocket.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibou.websocket.dto.AssignRequest;
import com.alibou.websocket.dto.ReassignCustomersDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
     private final SimpMessagingTemplate messagingTemplate;

    //   @MessageMapping("/user.addUser")
    //  public void addUser(@Payload User user) {
    //      userService.saveUser(user);
    //      messagingTemplate.convertAndSend("/user/" + user.getId() + "/queue/messages", user);
    //  }

     @MessageMapping("/user.addUser")
    public void addUser(@Payload User user) {
        userService.loginUser(user.getId());// Call login method
        messagingTemplate.convertAndSend("/user/" + user.getId() + "/queue/messages", user);
    }


      @PostMapping("/addDirectUser")
    public ResponseEntity<User> addDirectUser(@RequestBody User user) {
        User savedUser = userService.saveDirectUser(user);
        return ResponseEntity.ok(savedUser);
    }
     
 
     @MessageMapping("/user.disconnectUser")
     public void disconnectUser(@Payload User user) {
         userService.disconnect(user);
         messagingTemplate.convertAndSend("/user/" + user.getId() + "/queue/messages", user);
     }
 

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

   
    @GetMapping("/allusers")
    public ResponseEntity<List<User>> findALLConnectedUsers() {
        return ResponseEntity.ok(userService.findALLConnectedUsers());
    }

    @GetMapping("user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
           User user = userService.getUserById(id).get();
           return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/manager/{managerId}/customers")
    public ResponseEntity<List<User>> getCustomersForManager(@PathVariable String managerId) {
        return ResponseEntity.ok(userService.getCustomersForManager(managerId));
    }

    @DeleteMapping("customer/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String id) {
        userService.deleteCustomerById(id);
        return new ResponseEntity<>("Customer deleted successfully", HttpStatus.OK);
    }

  
    @GetMapping("/customer/{customerId}/manager")
    public ResponseEntity<User> getManagerForCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(userService.getManagerForCustomer(customerId));
    }

    @PostMapping("/assign")
     public ResponseEntity<String> assignCustomersToManager(@RequestBody AssignRequest request) {
          return userService.assignCustomerToManager(request.getManagerId(),request.getCustomerId());
        }


    @PostMapping("/reassign")
    public ResponseEntity<String> reassignCustomerToNewManager(@RequestBody AssignRequest request) {
        return userService.reassignCustomer(request.getManagerId(), request.getCustomerId());
    }
        
        

    @PutMapping("/reassignallcustomers")
    public ResponseEntity<String> assignCustomersToNewManager(@RequestBody ReassignCustomersDTO dto) {
        boolean isUpdated = userService.assignCustomersToNewManager(dto.getOldManagerId(), dto.getNewManagerId());

        if (isUpdated) {
            return ResponseEntity.ok("Customers reassigned from Manager " + dto.getOldManagerId() + " to " + dto.getNewManagerId());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Reassignment failed. Check if manager IDs are valid.");
        }
    }    

   

}
