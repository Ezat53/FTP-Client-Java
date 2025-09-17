package com.xfer.repository;

import com.xfer.entity.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {
    
    List<TransferLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<TransferLog> findByFtpAccountIdOrderByCreatedAtDesc(Long ftpAccountId);
    
    @Query("SELECT tl FROM TransferLog tl WHERE tl.userId = :userId ORDER BY tl.createdAt DESC")
    List<TransferLog> findRecentLogsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT tl FROM TransferLog tl WHERE tl.userId = :userId ORDER BY tl.createdAt DESC LIMIT :limit")
    List<TransferLog> findRecentLogsByUserIdWithLimit(@Param("userId") Long userId, @Param("limit") int limit);
    
    List<TransferLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.userId = :userId AND tl.status = 'success'")
    Long countSuccessfulTransfersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(tl) FROM TransferLog tl WHERE tl.userId = :userId AND tl.status = 'error'")
    Long countFailedTransfersByUserId(@Param("userId") Long userId);
}
