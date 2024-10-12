package org.parcial_1.services;

import org.parcial_1.entities.Dna;
import org.parcial_1.repositories.DnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class DnaService {

    private final DnaRepository dnaRepository;
    private static final int SEQUENCE_LENGTH = 4;

    @Autowired
    public DnaService(DnaRepository dnaRepository) {
        this.dnaRepository = dnaRepository;
    }

    public static boolean isMutant(String[] dna) {
        int n = dna.length;

        // Verificamos si hay más de una secuencia en filas, columnas o diagonales
        long sequenceCount = Stream.of(
                        checkDirection(dna, n, 0, 1),   // Filas
                        checkDirection(dna, n, 1, 0),   // Columnas
                        checkDirection(dna, n, 1, 1),   // Diagonales ↘
                        checkDirection(dna, n, 1, -1)   // Diagonales ↙
                ).mapToLong(count -> count)  // Conteo total de secuencias encontradas
                .sum();

        return sequenceCount > 1;  // Es mutante si hay más de 1 secuencia mutante
    }

    private static long checkDirection(String[] dna, int n, int dx, int dy) {
        // Usamos `flatMap` para recorrer toda la matriz y contar todas las secuencias en la dirección (dx, dy)
        return IntStream.range(0, n)
                .flatMap(i -> IntStream.range(0, n)
                        .map(j -> countSequencesInDirection(dna, i, j, dx, dy, n)))
                .sum();  // Sumar todas las secuencias mutantes encontradas en la dirección
    }

    private static int countSequencesInDirection(String[] dna, int x, int y, int dx, int dy, int n) {
        int count = 0;

        // Verifica si podemos analizar una secuencia completa dentro de los límites
        while (x + (SEQUENCE_LENGTH - 1) * dx < n && y + (SEQUENCE_LENGTH - 1) * dy >= 0 && y + (SEQUENCE_LENGTH - 1) * dy < n) {
            char first = dna[x].charAt(y);
            boolean isMutantSequence = true;

            // Verificamos la secuencia de longitud SEQUENCE_LENGTH
            for (int i = 1; i < SEQUENCE_LENGTH; i++) {
                if (dna[x + i * dx].charAt(y + i * dy) != first) {
                    isMutantSequence = false;
                    break;
                }
            }

            // Si encontramos una secuencia mutante, la contamos
            if (isMutantSequence) {
                count++;
                // Continuar buscando otras secuencias en la misma dirección desplazando la posición
                x += SEQUENCE_LENGTH * dx;
                y += SEQUENCE_LENGTH * dy;
            } else {
                // Si no es mutante, avanzamos solo 1 posición
                x += dx;
                y += dy;
            }
        }
        return count;
    }

    public boolean analyzeDna(String[] dna) {
        String dnaSequence = String.join(",", dna);

        // Verificamos si el ADN ya existe en la base de datos
        Optional<Dna> existingDna = dnaRepository.findByDna(dnaSequence);
        if (existingDna.isPresent()) {
            // Si el ADN ya fue analizado, retornamos el resultado
            return existingDna.get().isMutant();
        }

        // Determinamos si el ADN es mutante y lo guardamos en la base de datos
        boolean isMutant = isMutant(dna);
        Dna dnaEntity = Dna.builder()
                .dna(dnaSequence)
                .isMutant(isMutant)
                .build();
        dnaRepository.save(dnaEntity);

        return isMutant;
    }
}
