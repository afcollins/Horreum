package io.hyperfoil.tools.horreum.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Banner {
   @Id
   @GeneratedValue
   public Integer id;

   @NotNull
   public Instant created;

   @NotNull
   public boolean active;

   @NotNull
   public String severity;

   @NotNull
   public String title;

   public String message;
}
