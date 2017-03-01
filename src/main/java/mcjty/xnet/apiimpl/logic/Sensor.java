package mcjty.xnet.apiimpl.logic;

import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.api.gui.IEditorGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static mcjty.xnet.apiimpl.logic.LogicConnectorSettings.Color.BLACK;
import static mcjty.xnet.apiimpl.logic.LogicConnectorSettings.Color.COLORS;

class Sensor {

    public static final String TAG_MODE = "mode";
    public static final String TAG_OPERATOR = "op";
    public static final String TAG_AMOUNT = "amount";
    public static final String TAG_COLOR = "color";
    public static final String TAG_STACK = "stack";


    enum SensorMode {
        OFF,
        ITEM,
        FLUID,
        ENERGY,
        RS
    }

    enum Operator {
        EQUAL("="),
        NOTEQUAL("!="),
        LESS("<"),
        GREATER(">"),
        LESSOREQUAL("<="),
        GREATOROREQUAL(">=");

        private final String code;

        private final static Map<String, Operator> OPERATOR_MAP = new HashMap<>();
        static {
            for (Operator operator : values()) {
                OPERATOR_MAP.put(operator.code, operator);
            }
        }

        Operator(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }

        public static Operator valueOfCode(String code) {
            return OPERATOR_MAP.get(code);
        }
    }

    private SensorMode sensorMode = SensorMode.OFF;
    private Operator operator = Operator.EQUAL;
    private int amount = 0;
    private LogicConnectorSettings.Color outputColor = BLACK;
    private ItemStack filter = ItemStackTools.getEmptyStack();

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
        gui
                .choices(TAG_MODE, "Sensor mode", sensorMode, SensorMode.values())
                .choices(TAG_OPERATOR, "Operator", operator, Operator.values())
                .integer(TAG_AMOUNT, "Amount to compare with", amount)
                .colors(TAG_COLOR, "Output color", outputColor.getColor(), COLORS)
                .ghostSlot(TAG_STACK, filter)
                .nl();
    }

    public void update(int i, Map<String, Object> data) {
        sensorMode = SensorMode.valueOf((String) data.get(TAG_MODE+i));
        operator = Operator.valueOfCode((String) data.get(TAG_OPERATOR+i));
        amount = (Integer) data.get(TAG_AMOUNT+i);
        outputColor = LogicConnectorSettings.Color.colorByValue((Integer) data.get(TAG_COLOR+i));
        filter = (ItemStack) data.get(TAG_STACK+i);
    }

    public void readFromNBT(int i, NBTTagCompound tag) {
        sensorMode = SensorMode.values()[tag.getByte("sensorMode"+i)];
        operator = Operator.values()[tag.getByte("operator"+i)];
        amount = tag.getInteger("amount"+i);
        outputColor = LogicConnectorSettings.Color.values()[tag.getByte("color"+i)];
        if (tag.hasKey("filter" + i)) {
            NBTTagCompound itemTag = tag.getCompoundTag("filter" + i);
            filter = ItemStackTools.loadFromNBT(itemTag);
        } else {
            filter = ItemStackTools.getEmptyStack();
        }
    }

    public void writeToNBT(int i, NBTTagCompound tag) {
        tag.setByte("sensorMode"+i, (byte) sensorMode.ordinal());
        tag.setByte("operator"+i, (byte) operator.ordinal());
        tag.setInteger("amount"+i, amount);
        tag.setByte("color"+i, (byte) outputColor.ordinal());
        if (ItemStackTools.isValid(filter)) {
            NBTTagCompound itemTag = new NBTTagCompound();
            filter.writeToNBT(itemTag);
            tag.setTag("filter" + i, itemTag);
        }
    }

}
