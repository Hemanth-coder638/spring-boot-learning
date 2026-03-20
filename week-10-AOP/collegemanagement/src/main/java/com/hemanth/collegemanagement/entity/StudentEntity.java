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
public class StudentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    /* why name length 15 and nullable = false? --> business rule assumes student names are mandatory and limited to reasonable length; DB-level constraint prevents dirty data and validates early. */
    @Column(nullable = false, length = 15)
    private String name;


    /* why @ManyToMany with explicit @JoinTable on student side as owning side? --> because ManyToMany requires a join table; choosing a single owning side prevents Hibernate from creating two join tables. Student is chosen as owning side because use cases often add students to classes or professors from the student enrollment flow.
    why cascade = {PERSIST, MERGE}? --> because when creating or merging a student we often want the relationship entries to persist/update; we avoid REMOVE cascade to prevent deleting professors via student deletions.
    why fetch = LAZY? --> collections should be loaded only when needed to keep queries efficient. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_professor",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private Set<ProfessorEntity> professorEntities=new HashSet<>();


    /* why another @ManyToMany for student-subject with an explicit join table? --> because the relationship is conceptually symmetric (students enroll in subjects). Having a dedicated join table with clear column names improves readability and database indexing.
    why cascade = {PERSIST, MERGE}? --> same rationale: allow cascading of persistence/merge for ease of entity creation, but avoid REMOVE to protect subject catalog integrity.
    why use Set rather than List? --> Set prevents duplicate associations naturally and aligns with relation semantics (a student-subject pair should be unique). */
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_subject",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<SubjectEntity> subjectEntities=new HashSet<>();


    /* why @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true)? --> because AdmissionRecord is the owning side (has the FK). We map back to admissionRecord for navigation. orphanRemoval = true ensures that if the student loses its admission record reference, the admission record row gets removed to avoid orphans when that is the intended domain behavior. */
    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true)
    private AdmissionRecordEntity admissionRecord;



    //convenience helpers for maintaining both sides
    public void addProfessor(ProfessorEntity p) {
        this.professorEntities.add(p);
        p.getStudentEntities().add(this);
    }

    public void removeProfessor(ProfessorEntity p) {
        this.professorEntities.remove(p);
        p.getStudentEntities().remove(this);
    }

    public void addSubject(SubjectEntity s) {
        this.subjectEntities.add(s);
        s.getStudentEntities().add(this);
    }

    public void removeSubject(SubjectEntity s) {
        this.subjectEntities.remove(s);
        s.getStudentEntities().remove(this);
    }

    public void setAdmissionRecordBiDirectional(AdmissionRecordEntity record) {
        this.admissionRecord = record;
        if (record != null) record.setStudent(this);
    }
}

