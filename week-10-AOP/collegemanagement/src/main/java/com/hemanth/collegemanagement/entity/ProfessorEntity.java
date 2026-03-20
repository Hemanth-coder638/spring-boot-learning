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
public class ProfessorEntity {


    /* why Long id and GenerationType.IDENTITY? --> same reason as above: Long provides large range for identifiers and IDENTITY delegates id generation to the DB which is stable and simple for Postgres. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    /* why @Column(nullable = false, length = 20)? --> because business requires a non-null title limited to 20 characters; column length helps DB allocate space and prevents overly long input. The nullable constraint enforces data integrity at DB level. */
    @Column(nullable = false, length = 20)
    private String title;


    /* why use @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)?
    --> why OneToMany mappedBy? because Subject owns the foreign key (many subjects reference one professor), so Professor is the inverse side and should not create duplicate join tables. mappedBy keeps a single source of truth for the relationship.
    --> why cascade = ALL? because lifecycle operations on Professor (persist, merge, remove) should by default apply to Subjects that are tightly coupled and conceptually owned by the professor; this simplifies create/update flows.
    --> why orphanRemoval = true? because if a Subject is removed from a Professor's subject set, we want it to be deleted from DB automatically to avoid orphaned subjects without a professor (when that is the business intent).
    --> why fetch = LAZY? because collections can be large; fetch lazily to avoid performance penalties and control when the collection loads. */
    @OneToMany(mappedBy = "professor",cascade={CascadeType.MERGE,CascadeType.PERSIST},orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SubjectEntity> subjectEntities=new HashSet<>();


    /* why @ManyToMany(mappedBy = "professorEntities") and not owning side? --> because Student will be the owning side with explicit @JoinTable. This avoids duplicate join table creation and centralizes join configuration.
    why no cascade here? --> because Professors and Students are typically independent lifecycles; cascading remove from a Professor to Students would delete student records unexpectedly. We intentionally avoid cascading remove to protect student data.
    why fetch = LAZY? --> to avoid loading students list every time we fetch a professor. */
    @ManyToMany(mappedBy = "professorEntities", fetch = FetchType.LAZY)
    private Set<StudentEntity> studentEntities=new HashSet<>();

    //convenience helper
    public void addSubject(SubjectEntity s) {
        subjectEntities.add(s);
        s.setProfessor(this);
    }
    public void removeSubject(SubjectEntity s) {
        subjectEntities.remove(s);
        s.setProfessor(null);
    }

}