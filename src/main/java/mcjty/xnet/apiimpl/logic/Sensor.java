package mcjty.xnet.apiimpl.logic;

import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.api.gui.IEditorGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static mcjty.xnet.apiimpl.logic.LogicConnectorSettings.Color.WHITE;

class Sensor {
    enum SensorMode {
        OFF,
        ITEM,
        FLUID,
        ENERGY,
        REDSTONE
    }

    enum Operator {
        EQUAL("="),
        NOTEQUAL("!="),
        LESS("<"),
        GREATER(">"),
        LESSOREQUAL("<="),
        GREATOROREQUAL(">=");

        private final String code;

        Operator(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private SensorMode sensorMode = SensorMode.OFF;
    private Operator operator = Operator.EQUAL;
    private int amount = 0;
    private LogicConnectorSettings.Color outputColor = WHITE;
    private ItemStack itemFilter = ItemStackTools.getEmptyStack();
    private FluidStack fluidFilter = null;

    public Sensor() {

    }

    public SensorMode getSensorMode() {
        return sensorMode;
    }

    public void setSensorMode(SensorMode sensorMode) {
        this.sensorMode = sensorMode;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LogicConnectorSettings.Color getOutputColor() {
        return outputColor;
    }

    public void setOutputColor(LogicConnectorSettings.Color outputColor) {
        this.outputColor = outputColor;
    }

    public void createGui(IEditorGui gui) {
    }
}
