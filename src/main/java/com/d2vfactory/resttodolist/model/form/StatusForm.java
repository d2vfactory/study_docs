package com.d2vfactory.resttodolist.model.form;

import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.validator.StatusConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusForm {

    @StatusConstraint
    private String status = Status.ACTIVE.name();

}
