package com.proj.medicalClinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdminClinicalCenter extends AppUser {

    @ManyToOne
    @JoinColumn(name = "clinical_center_id", nullable = false)
    private ClinicalCenter clinicalCenter;
}
