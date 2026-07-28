package com.sportzone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AuditLog")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditId")
    private Integer auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private NguoiDung user;

    @Column(name = "Action", nullable = false, length = 100)
    private String action;

    @Column(name = "Entity", nullable = false, length = 100)
    private String entityName;

    @Column(name = "EntityId")
    private Integer entityId;

    @Column(name = "OldValue", columnDefinition = "nvarchar(max)")
    private String oldValue;

    @Column(name = "NewValue", columnDefinition = "nvarchar(max)")
    private String newValue;

    @Column(name = "IPAddress", length = 45)
    private String ipAddress;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Result", nullable = false, length = 30)
    private String result;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (result == null) {
            result = "SUCCESS";
        }
    }

    public Integer getAuditId() { return auditId; }
    public void setAuditId(Integer auditId) { this.auditId = auditId; }
    public NguoiDung getUser() { return user; }
    public void setUser(NguoiDung user) { this.user = user; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
