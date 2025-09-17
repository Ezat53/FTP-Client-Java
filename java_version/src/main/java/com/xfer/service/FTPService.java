package com.xfer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xfer.entity.FTPAccount;
import com.xfer.entity.FTPUserAssignment;
import com.xfer.entity.User;
import com.xfer.repository.FTPAccountRepository;
import com.xfer.repository.FTPUserAssignmentRepository;
import com.xfer.repository.UserRepository;

@Service
@Transactional
public class FTPService {
    
    @Autowired
    private FTPAccountRepository ftpAccountRepository;
    
    @Autowired
    private FTPUserAssignmentRepository ftpUserAssignmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public FTPAccount createAccount(String name, String protocol, String host, Integer port,
                                  String username, String password, Long ownerId, List<Long> userIds) {
        FTPAccount account = new FTPAccount(name, protocol, host, port, username, password, ownerId);
        account = ftpAccountRepository.save(account);
        
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(account.getId(), userId, ownerId);
                ftpUserAssignmentRepository.save(assignment);
            }
        }
        
        return account;
    }
    
    public Optional<FTPAccount> getAccountById(Long accountId) {
        return ftpAccountRepository.findById(accountId);
    }
    
    public List<FTPAccount> getUserAccounts(Long userId) {
        return ftpAccountRepository.findByAssignedUserId(userId);
    }
    
    public List<FTPAccount> getAllAccounts() {
        return ftpAccountRepository.findAllWithOwner();
    }
    
    public List<User> getAccountAssignments(Long accountId) {
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
        return assignments.stream()
                .map(FTPUserAssignment::getUser)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public boolean assignAccountToUsers(Long accountId, List<Long> userIds, Long assignedBy) {
        try {
            // Remove existing assignments
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            
            // Add new assignments
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(accountId, userId, assignedBy);
                ftpUserAssignmentRepository.save(assignment);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean unassignAccountFromUser(Long accountId, Long userId) {
        try {
            ftpUserAssignmentRepository.deleteByFtpAccountIdAndUserId(accountId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean unassignAccountFromAllUsers(Long accountId) {
        try {
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public FTPAccount updateAccount(Long accountId, String name, String protocol, String host,
                                  Integer port, String username, String password, List<Long> userIds) {
        FTPAccount account = ftpAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        account.setName(name);
        account.setProtocol(protocol);
        account.setHost(host);
        account.setPort(port);
        account.setUsername(username);
        if (password != null && !password.trim().isEmpty()) {
            account.setPassword(password);
        }
        
        // Update user assignments if provided
        if (userIds != null) {
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(accountId, userId, account.getOwnerId());
                ftpUserAssignmentRepository.save(assignment);
            }
        }
        
        return ftpAccountRepository.save(account);
    }
    
    public boolean deleteAccount(Long accountId) {
        try {
            // Delete related assignments first
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            
            // Delete the account
            ftpAccountRepository.deleteById(accountId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasUserAccess(Long accountId, Long userId) {
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
        return assignments.stream()
                .anyMatch(assignment -> assignment.getUserId().equals(userId));
    }
}
