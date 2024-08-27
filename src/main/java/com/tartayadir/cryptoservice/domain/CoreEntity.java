package com.tartayadir.cryptoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * CoreEntity is a base class that provides common properties for entities in the crypto service.
 * It includes an identifier and a creation timestamp, making it useful for tracking entity instances.
 *
 * <p>This class is annotated with @MappedSuperclass, which means that its properties will be inherited by subclasses
 * and mapped to the database, but the class itself will not be mapped to a database table.</p>
 *
 * <p>Properties:</p>
 * <ul>
 *   <li><strong>id:</strong> The unique identifier of the entity, which is non-updatable once set.</li>
 *   <li><strong>createTime:</strong> The timestamp indicating when the entity was created. This field is managed by the database and is non-updatable and non-insertable by application code.</li>
 * </ul>
 *
 * <p>Implements:</p>
 * <ul>
 *   <li>Serializable: Allows the entity to be serialized, which is useful for caching or transferring entity states.</li>
 * </ul>
 *
 * <p>Constructors:</p>
 * <ul>
 *   <li>Default constructor: No-argument constructor for JPA and serialization frameworks.</li>
 *   <li>Parameterized constructor: Allows creating an instance with specified values for all fields.</li>
 * </ul>
 *
 * @see java.io.Serializable
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class CoreEntity implements java.io.Serializable {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createTime;
}