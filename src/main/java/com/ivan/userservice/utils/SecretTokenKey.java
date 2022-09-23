package com.ivan.userservice.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecretTokenKey {

    // придумываем секрет
    // генерируем при старте приложения
    private final byte[] secret = UUID.randomUUID().toString().getBytes();

    /**
     * Получить секрет в виде массива байтов
     * */
    public byte[] getInstanceBytes() {
        return secret;

    }

}
