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
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.Locale;
import java.util.Set;

/**
 * Entity representing the category of a ticket.
 * Maps to the 'TicketCategories' table.
 */
@Entity
@Table(name = "ticket_categories")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private Integer categoryId;

    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "CategoryNameEn", nullable = false,length = 100)
    private String categoryNameEn;

    /**
     * Bidirectional relationship: One category can contain many tickets.
     */
    @OneToMany(mappedBy = "category")
    private Set<Ticket> tickets;


}