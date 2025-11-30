package jp.java.simulator.simulateAssetFormationWithNISA;

public record LifeEventParams(
    String lifeEvent1, int lifeEventAge1, double requiredFunds1,
    String lifeEvent2, int lifeEventAge2, double requiredFunds2,
    String lifeEvent3, int lifeEventAge3, double requiredFunds3,
    String lifeEvent4, int lifeEventAge4, double requiredFunds4,
    String lifeEvent5, int lifeEventAge5, double requiredFunds5
) {}
