package com.mjuAppSW.joA.domain.member.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.EnumSet;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getStatusStr();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return EnumSet.allOf(Status.class).stream()
                .filter(e -> e.getStatusStr().equals(dbData))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}

