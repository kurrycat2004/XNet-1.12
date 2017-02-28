package mcjty.xnet.apiimpl.logic;

class Sensor {
    enum SensorMode {
        OFF,
        ITEM,
        FLUID,
        ENERGY,
        REDSTONE
    }

    enum Operator {
        EQUAL,
        NOTEQUAL,
        LESS,
        GREATER,
        LESSOREQUAL,
        GREATOROREQUAL
    }

    private final SensorMode sensorMode;
    private final Operator operator;
    private final int amount;
    private final LogicConnectorSettings.Color outputColor;

    public Sensor(SensorMode sensorMode, Operator operator, int amount, LogicConnectorSettings.Color outputColor) {
        this.sensorMode = sensorMode;
        this.operator = operator;
        this.amount = amount;
        this.outputColor = outputColor;
    }

    public SensorMode getSensorMode() {
        return sensorMode;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getAmount() {
        return amount;
    }

    public LogicConnectorSettings.Color getOutputColor() {
        return outputColor;
    }
}
