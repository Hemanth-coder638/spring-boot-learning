package com.hemanth.collegemanagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(nullable = false, length = 20)
    private String title;


    /* why @ManyToOne on subject -> professor and why this side is owning? --> because many subjects point to a single professor and the foreign key should live on the subject table (subject.professor_id). This makes Subject the owning side of a OneToMany/ManyToOne relation.
    why fetch = LAZY? --> to avoid eager fetch of professor when we only need subject data.
    why @JoinColumn(name = "professor_id")? --> to provide a stable, explicit column name in the DB and avoid default generated names which can be inconsistent across environments. */
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private ProfessorEntity professor;


    /* why @ManyToMany(mappedBy = "subjectEntities", fetch = FetchType.LAZY)? --> because Student is the owning side of student-subject relation and manages the join table; subject only needs inverse mapping for navigation and should not create duplicate join tables.
    why use Set? --> to avoid duplicate enrollments and keep semantics clear. */
    @ManyToMany(mappedBy = "subjectEntities",cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<StudentEntity> studentEntities=new HashSet<>();
}