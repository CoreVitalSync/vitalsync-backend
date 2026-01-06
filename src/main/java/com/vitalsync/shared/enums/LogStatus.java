package com.vitalsync.shared.enums;

public enum LogStatus {
    TAKEN,   // Tomou
    MISSED,  // Esqueceu/Passou do horário
    SKIPPED, // Pulou intencionalmente (ex: indicação médica)
    PENDING  // Aguardando horário (Futuro)
}