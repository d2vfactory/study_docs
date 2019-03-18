package com.d2vfactory.resttodolist.model.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ReferenceForm {

    @NotNull
    private Long[] referenceIds;

    public ReferenceForm(Long... referenceIds) {
        this.referenceIds = referenceIds;
    }
}
