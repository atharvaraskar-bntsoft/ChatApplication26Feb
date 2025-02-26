package com.alibou.websocket.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibou.websocket.exception.UserAlreadyExistsException;
import com.alibou.websocket.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // public void saveUser(User user) {
    //     Optional<User> existingUser = repository.findById(user.getId());

    //     if (existingUser.isPresent()) { 
          
    //         User foundUser = existingUser.get();
    //         foundUser.setStatus(Status.ONLINE);
    //         repository.save(foundUser);
    //         return;
    //     }
    //     user.setStatus(Status.ONLINE);

        
    //     if ("MANAGER".equalsIgnoreCase(user.getRole())) {
    //         user.setAssignedCustomers(new ArrayList<>()); // Ensure it's not null
    //         user.setAssignedManagerId(null); 
    //     } else if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
          
    //         if (user.getAssignedManagerId() == null) {
    //             Optional<User> manager = findManagerWithLeastCustomers();
    //             manager.ifPresent(m -> {
    //                 user.setAssignedManagerId(m.getId()); // Store only the manager's ID
    //                 m.getAssignedCustomers().add(user.getId()); // Store only customer ID
    //                 repository.save(m); // Update manager with new customer
    //             });
    //         }
    //     }
    //     repository.save(user);
    // }

    public void loginUser(String userId) {
        Optional<User> optionalUser = repository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User foundUser = optionalUser.get();
            foundUser.setStatus(Status.ONLINE);
            repository.save(foundUser);
        } else {
            throw new UserNotFoundException("Login failed: User not found With Id.");
        }
    }
    

    public User saveDirectUser(User user) {
    if (repository.existsById(user.getId())) {
        throw new UserAlreadyExistsException("User with ID " + user.getId() + " already exists.");
    }
       user.setStatus(Status.ONLINE);
        return repository.save(user);
     }

    
    
    public Optional<User> findManagerWithLeastCustomers() {
        List<User> managers = repository.findAllByRole("MANAGER");

        return managers.stream()
                .min((m1, m2) -> Integer.compare(m1.getAssignedCustomers().size(), m2.getAssignedCustomers().size()));
    }
    public void disconnect(User user) {
        var storedUser = repository.findById(user.getId()).orElse(null); // Changed nickName to id
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        List<User> onlineUsers = repository.findAllByStatus(Status.ONLINE);
    
        if (onlineUsers.isEmpty()) {
            throw new UserNotFoundException("No online users found.");
        }
        return onlineUsers;
    }

    public List<User> findALLConnectedUsers() {
        List<User> allUsers = repository.findAll();
    
        if (allUsers.isEmpty()) {
            throw new UserNotFoundException("No users found in the system.");
        }
        return allUsers;
    }


    public Optional<User> getUserById(String id) { 
        Optional<User> optionalUser = repository.findById(id);   
        if(optionalUser.isPresent()){
                return optionalUser;           
         }
        else{
            throw new UserNotFoundException("User not Found With Id: "+id);
        }      
       
    }

    public void deleteCustomerById(String id) {
        Optional<User> optionalCustomer = repository.findById(id);
    
        if (optionalCustomer.isEmpty()) {
            throw new UserNotFoundException("Customer not found with ID: " + id);
        }
    
        User customer = optionalCustomer.get();
    
        // Remove customer ID from the assigned manager's list
        if (customer.getAssignedManagerId() != null) {
            Optional<User> managerOptional = repository.findById(customer.getAssignedManagerId());
            if (managerOptional.isPresent()) {
                User manager = managerOptional.get();
                manager.getAssignedCustomers().remove(id);
                repository.save(manager); // Update manager record
            }
        }
    
        repository.deleteById(id);
    }

    
    



   public List<User> getCustomersForManager(String managerId) {
    // User manager = repository.findById(managerId)
    //                          .orElseThrow(() -> new RuntimeException("Manager not found"));
  
    User manager = repository.findById(managerId)
    .orElse(null);
       if (manager == null) {
         return new ArrayList<>(); // Or return a response with a meaningful message
        }

    // Validate if user is actually a MANAGER
    if (!"MANAGER".equalsIgnoreCase(manager.getRole())) {
        throw new RuntimeException("User is not a manager");
    }

    // Get assigned customer IDs safely
    List<String> assignedCustomerIds = 
        Optional.ofNullable(manager.getAssignedCustomers()).orElse(Collections.emptyList());

    // Fetch full customer objects
    return repository.findAllById(assignedCustomerIds);
    }
    

    

    public User getManagerForCustomer(String customerId) {
        User customer = repository.findById(customerId).orElse(null);
        if (customer != null && customer.getAssignedManagerId() != null) {
            return repository.findById(customer.getAssignedManagerId()).orElse(null);
        }
        return null;
    }

    public ResponseEntity<String> assignCustomerToManager(String managerId, String customerId) {
        Optional<User> managerOpt = repository.findById(managerId);
        Optional<User> customerOpt = repository.findById(customerId);
    
        if (managerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Manager not found.");
        }
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found.");
        }
    
        User manager = managerOpt.get();
        User customer = customerOpt.get();
    
        // Ensure the user is a manager
        if (!"MANAGER".equalsIgnoreCase(manager.getRole())) {
            return ResponseEntity.badRequest().body("User is not a manager.");
        }
    
        // Assign the customer to the manager
        manager.getAssignedCustomers().add(customerId);
        customer.setAssignedManagerId(managerId);
    
        repository.save(manager);
        repository.save(customer);
    
        return ResponseEntity.ok("Customer assigned successfully.");
    }

    public ResponseEntity<String> reassignCustomer(String newManagerId, String customerId) {
        Optional<User> customerOpt = repository.findById(customerId);
        Optional<User> newManagerOpt = repository.findById(newManagerId);
    
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found.");
        }
        if (newManagerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("New manager not found.");
        }
    
        User customer = customerOpt.get();
        User newManager = newManagerOpt.get();
    
        // Check if the new user is a manager
        if (!"MANAGER".equalsIgnoreCase(newManager.getRole())) {
            return ResponseEntity.badRequest().body("New user is not a manager.");
        }
    
        // Find the old manager and remove the customer from their assigned list
        String oldManagerId = customer.getAssignedManagerId();
        if (oldManagerId != null) {
            Optional<User> oldManagerOpt = repository.findById(oldManagerId);
            oldManagerOpt.ifPresent(oldManager -> {
                oldManager.getAssignedCustomers().remove(customerId);
                repository.save(oldManager);
            });
        }
    
        // Assign the customer to the new manager
        newManager.getAssignedCustomers().add(customerId);
        customer.setAssignedManagerId(newManagerId);
    
        // Save updates
        repository.save(newManager);
        repository.save(customer);
    
        return ResponseEntity.ok("Customer reassigned successfully.");
    }

    public boolean assignCustomersToNewManager(String oldManagerId, String newManagerId) {
        Optional<User> oldManagerOptional = repository.findById(oldManagerId);
        Optional<User> newManagerOptional = repository.findById(newManagerId);
    
        if (oldManagerOptional.isEmpty() || newManagerOptional.isEmpty()) {
            return false; 
        }
    
        User oldManager = oldManagerOptional.get();
        User newManager = newManagerOptional.get();
    
        if (oldManager.getAssignedCustomers().isEmpty()) {
            return false; // No customers to transfer
        }
    
        // Get all customer IDs assigned to the old manager
        List<String> customerIds = new ArrayList<>(oldManager.getAssignedCustomers());
    
        // Move customers from old manager to new manager
        newManager.getAssignedCustomers().addAll(customerIds);
        oldManager.getAssignedCustomers().clear();
    
        // Update assignedManagerId for each customer
        for (String customerId : customerIds) {
            Optional<User> customerOpt = repository.findById(customerId);
            customerOpt.ifPresent(customer -> {
                customer.setAssignedManagerId(newManagerId);
                repository.save(customer); // Save updated customer
            });
        }
    
        // Save both updated manager records
        repository.save(oldManager);
        repository.save(newManager);
    
        return true;
    }
    
    
    
}
