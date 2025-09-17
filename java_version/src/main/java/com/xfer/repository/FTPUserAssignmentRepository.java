package com.xfer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xfer.entity.FTPUserAssignment;

@Repository
public interface FTPUserAssignmentRepository extends JpaRepository<FTPUserAssignment, Long> {
    
    List<FTPUserAssignment> findByFtpAccountId(Long ftpAccountId);
    
    List<FTPUserAssignment> findByUserId(Long userId);
    
    FTPUserAssignment findByFtpAccountIdAndUserId(Long ftpAccountId, Long userId);
    
    @Modifying
    @Query("DELETE FROM FTPUserAssignment fua WHERE fua.ftpAccountId = :ftpAccountId")
    void deleteByFtpAccountId(@Param("ftpAccountId") Long ftpAccountId);
    
    @Modifying
    @Query("DELETE FROM FTPUserAssignment fua WHERE fua.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM FTPUserAssignment fua WHERE fua.ftpAccountId = :ftpAccountId AND fua.userId = :userId")
    void deleteByFtpAccountIdAndUserId(@Param("ftpAccountId") Long ftpAccountId, @Param("userId") Long userId);
    
    @Query("SELECT fua FROM FTPUserAssignment fua JOIN FETCH fua.user WHERE fua.ftpAccountId = :ftpAccountId")
    List<FTPUserAssignment> findByFtpAccountIdWithUser(@Param("ftpAccountId") Long ftpAccountId);
    
    @Query("SELECT fua FROM FTPUserAssignment fua JOIN FETCH fua.ftpAccount WHERE fua.userId = :userId")
    List<FTPUserAssignment> findByUserIdWithFtpAccount(@Param("userId") Long userId);
}
