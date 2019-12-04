package com.proj.medicalClinic.dto;

import com.proj.medicalClinic.model.DrugsType;
import com.proj.medicalClinic.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {

    private Long id;
    private boolean approved;
    private Set<DrugsType> drug;

    public PrescriptionDTO(Prescription prescription){
        this.id = prescription.getId();
        this.approved = prescription.getApproved();
        this.drug = prescription.getDrug();
    }
}