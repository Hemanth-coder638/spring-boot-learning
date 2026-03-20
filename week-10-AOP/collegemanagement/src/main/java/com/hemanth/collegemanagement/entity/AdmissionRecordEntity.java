package com.hemanth.collegemanagement.entity;


import jakarta.persistence.*;
import lombok.*;


import java.util.Set;


/*
All entities below include detailed "why" comments describing each annotation and
choice of value types. Comments are intentionally written in the form of "why..."
to directly address your questions and reasoning.
*/


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmissionRecordEntity {


    /* why do we use @Id? --> because every JPA entity requires a primary key to uniquely identify each row in the table so the persistence context can manage entity lifecycle and identity. */
    @Id
    /* why use GenerationType.IDENTITY? --> because with PostgreSQL and many production DBs we prefer database-generated identity values so that the DB is authoritative for id assignment, avoids allocation strategies complexity, and works predictably with insert timing. */
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    /* why Integer instead of int? --> because wrapper types allow null which represents the absence of a value before persistence; JPA handles nulls gracefully. Also wrapper types integrate better with optional fields and ORM proxies. */
    private Integer fees;


    /* why @OneToOne on admission -> student? --> because one admission record belongs to exactly one student and one student has at most one admission record. This models a strict 1:1 business rule.
    why cascade = CascadeType.ALL? --> because when we persist or remove an admission record in most workflows we want the related student life-cycle changes to be propagated where appropriate (for example creating admission and student together). Use carefully in real systems.
    why fetch = FetchType.LAZY? --> because Student can be large; lazy avoids unnecessary eager loading and reduces memory usage and N+1 fetches. For OneToOne JPA default is EAGER, but we deliberately set LAZY for performance and control.
    why @JoinColumn(name = "student_id", nullable = false, unique = true)? --> because we want a clear FK column name, to enforce not null (admission must reference a student), and unique = true ensures database-level enforcement of the 1:1 association on the owning side. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private StudentEntity student;
}
