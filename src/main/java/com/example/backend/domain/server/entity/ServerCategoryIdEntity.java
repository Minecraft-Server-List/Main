package com.example.backend.domain.server.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ServerCategoryIdEntity implements Serializable {
    private Long server;
    private Long category;
}