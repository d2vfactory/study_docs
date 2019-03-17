package com.d2vfactory.resttodolist.model.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoForm {

    @NotEmpty
    private String content;

    private Long[] referenceIds;

}
