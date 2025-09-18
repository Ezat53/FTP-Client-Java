package com.xfer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xfer.entity.TransferLog;
import com.xfer.repository.TransferLogRepository;

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
    
    public List<TransferLog> getAllTransferLogs(int limit) {
        return transferLogRepository.findTopByOrderByCreatedAtDesc(limit);
    }
    
    public List<TransferLog> getRecentTransferLogs(int limit) {
        return transferLogRepository.findTopByOrderByCreatedAtDesc(limit);
    }
    
    public long getTotalTransferCount() {
        return transferLogRepository.count();
    }
    
    public long getTodayTransferCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return transferLogRepository.countByCreatedAtBetween(startOfDay, endOfDay);
    }
    
    public long getTotalTransferSize() {
        return transferLogRepository.sumFileSizeByStatus("success");
    }
    
    public TransferStats getTransferStats() {
        long totalTransfers = transferLogRepository.count();
        long successfulTransfers = transferLogRepository.countByStatus("success");
        long failedTransfers = transferLogRepository.countByStatus("error");
        
        double successRate = totalTransfers > 0 ? (double) successfulTransfers / totalTransfers * 100 : 0;
        
        return new TransferStats(totalTransfers, successfulTransfers, failedTransfers, successRate);
    }
    
    public List<TransferLog> getTransferLogsByAction(String action, int limit) {
        return transferLogRepository.findByActionOrderByCreatedAtDesc(action, limit);
    }
    
    // Transfer Stats class
    public static class TransferStats {
        private long totalTransfers;
        private long successfulTransfers;
        private long failedTransfers;
        private double successRate;
        
        public TransferStats(long totalTransfers, long successfulTransfers, long failedTransfers, double successRate) {
            this.totalTransfers = totalTransfers;
            this.successfulTransfers = successfulTransfers;
            this.failedTransfers = failedTransfers;
            this.successRate = successRate;
        }
        
        // Getters
        public long getTotalTransfers() { return totalTransfers; }
        public long getSuccessfulTransfers() { return successfulTransfers; }
        public long getFailedTransfers() { return failedTransfers; }
        public double getSuccessRate() { return successRate; }
    }
}
