package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TypeVaccinUpdateRequest(@NotBlank String nom, @NotNull Long quantiteTotale) {
}
