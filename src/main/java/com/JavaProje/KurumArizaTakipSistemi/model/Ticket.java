package com.JavaProje.KurumArizaTakipSistemi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The core entity representing a support ticket in the Fault Tracking System.
 * Maps to the 'Tickets' table.
 */
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class  Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketId")
    private Integer ticketId;

    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    /**
     * The description can be long, so we define it as text/NVARCHAR(MAX) equivalent.
     */
    @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * The user who created the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequesterId", nullable = false)
    private User requester;

    /**
     * The technician assigned to solve the ticket.
     * Can be null initially until the system auto-assigns it.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedTechnicianId")
    private User assignedTechnician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StatusId", nullable = false)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryId", nullable = false)
    private TicketCategory category;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    /**
     * JPA Callback: Automatically updates the 'UpdatedAt' timestamp
     * before any update operation is executed on this entity.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}