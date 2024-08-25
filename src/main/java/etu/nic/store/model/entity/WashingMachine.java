package etu.nic.store.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class WashingMachine extends Product{
    private Integer spinSpeed;

}
