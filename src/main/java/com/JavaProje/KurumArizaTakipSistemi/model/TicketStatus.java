package com.JavaProje.KurumArizaTakipSistemi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.Set;

/**
 * Entity representing the status of a ticket in the Fault Tracking System.
 * Maps to the 'TicketStatuses' table in the database.
 */
@Entity
@Table(name = "ticket_Statuses")
@Getter
@Setter
@NoArgsConstructor
public class TicketStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatusId")
    private Integer statusId;

    @Column(name = "StatusName", nullable = false, length = 50)
    private String statusName;

    @Column(name = "StatusNameEn", nullable = false,length = 50)
    private String statusNameEn;

    /**
     * Bidirectional relationship: One status can belong to many tickets.
     */
    @OneToMany(mappedBy = "status")
    private Set<Ticket> tickets;


}