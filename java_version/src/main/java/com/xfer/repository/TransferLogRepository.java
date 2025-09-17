package com.xfer.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xfer.entity.TransferLog;

@Repository
public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {
    
    List<TransferLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<TransferLog> findByFtpAccountIdOrderByCreatedAtDesc(Long ftpAccountId);
    
    @Query("SELECT tl FROM TransferLog tl WHERE tl.userId = :userId ORDER BY tl.createdAt DESC")
    List<TransferLog> findRecentLogsByUserId(@Param("userId") Long userId);
    
    @Query(value = "SELECT * FROM transfer_logs WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<TransferLog> findRecentLogsByUserIdWithLimit(@Param("userId") Long userId, @Param("limit") int limit);
    
    List<TransferLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.userId = :userId AND tl.status = 'success'")
    Long countSuccessfulTransfersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.userId = :userId AND tl.status = 'error'")
    Long countFailedTransfersByUserId(@Param("userId") Long userId);
    
    @Query(value = "SELECT * FROM transfer_logs ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<TransferLog> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query(value = "SELECT * FROM transfer_logs WHERE action = :action ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<TransferLog> findByActionOrderByCreatedAtDesc(@Param("action") String action, @Param("limit") int limit);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(tl.fileSize), 0) FROM TransferLog tl WHERE tl.status = :status")
    Long sumFileSizeByStatus(@Param("status") String status);
}
