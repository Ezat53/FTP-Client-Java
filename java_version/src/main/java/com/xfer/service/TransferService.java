package com.xfer.service;

import com.xfer.entity.TransferLog;
import com.xfer.repository.TransferLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransferService {
    
    @Autowired
    private TransferLogRepository transferLogRepository;
    
    public TransferLog logTransfer(Long userId, Long ftpAccountId, String action, 
                                 String filename, Long fileSize, String status, String errorMessage) {
        TransferLog log = new TransferLog(userId, ftpAccountId, action, filename, fileSize, status, errorMessage);
        return transferLogRepository.save(log);
    }
    
    public List<TransferLog> getUserTransferLogs(Long userId) {
        return transferLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<TransferLog> getRecentUserTransferLogs(Long userId, int limit) {
        return transferLogRepository.findRecentLogsByUserIdWithLimit(userId, limit);
    }
    
    public List<TransferLog> getAccountTransferLogs(Long ftpAccountId) {
        return transferLogRepository.findByFtpAccountIdOrderByCreatedAtDesc(ftpAccountId);
    }
    
    public List<TransferLog> getUserTransferLogsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transferLogRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);
    }
    
    public Long getSuccessfulTransferCount(Long userId) {
        return transferLogRepository.countSuccessfulTransfersByUserId(userId);
    }
    
    public Long getFailedTransferCount(Long userId) {
        return transferLogRepository.countFailedTransfersByUserId(userId);
    }
}
