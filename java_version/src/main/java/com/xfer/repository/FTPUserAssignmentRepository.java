package com.xfer.repository;

import com.xfer.entity.FTPUserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FTPUserAssignmentRepository extends JpaRepository<FTPUserAssignment, Long> {
    
    List<FTPUserAssignment> findByFtpAccountId(Long ftpAccountId);
    
    List<FTPUserAssignment> findByUserId(Long userId);
    
    FTPUserAssignment findByFtpAccountIdAndUserId(Long ftpAccountId, Long userId);
    
    void deleteByFtpAccountId(Long ftpAccountId);
    
    void deleteByFtpAccountIdAndUserId(Long ftpAccountId, Long userId);
    
    @Query("SELECT fua FROM FTPUserAssignment fua JOIN FETCH fua.user WHERE fua.ftpAccountId = :ftpAccountId")
    List<FTPUserAssignment> findByFtpAccountIdWithUser(@Param("ftpAccountId") Long ftpAccountId);
    
    @Query("SELECT fua FROM FTPUserAssignment fua JOIN FETCH fua.ftpAccount WHERE fua.userId = :userId")
    List<FTPUserAssignment> findByUserIdWithFtpAccount(@Param("userId") Long userId);
}
