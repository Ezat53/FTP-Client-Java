package com.xfer.repository;

import com.xfer.entity.FTPAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FTPAccountRepository extends JpaRepository<FTPAccount, Long> {
    
    List<FTPAccount> findByOwnerId(Long ownerId);
    
    @Query("SELECT fa FROM FTPAccount fa JOIN fa.assignedUsers u WHERE u.id = :userId")
    List<FTPAccount> findByAssignedUserId(@Param("userId") Long userId);
    
    @Query("SELECT fa FROM FTPAccount fa JOIN FETCH fa.owner WHERE fa.ownerId = :ownerId")
    List<FTPAccount> findByOwnerIdWithOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT fa FROM FTPAccount fa JOIN FETCH fa.owner")
    List<FTPAccount> findAllWithOwner();
}
