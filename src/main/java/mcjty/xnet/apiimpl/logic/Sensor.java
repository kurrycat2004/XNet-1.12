package mcjty.xnet.apiimpl.logic;

import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.api.channels.Color;
import mcjty.xnet.api.gui.IEditorGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

import static mcjty.xnet.api.channels.Color.OFF;
import static mcjty.xnet.api.channels.Color.COLORS;

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

        private static final Map<String, Operator> OPERATOR_MAP = new HashMap<>();
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

    private final int index;

    private SensorMode sensorMode = SensorMode.OFF;
    private Operator operator = Operator.EQUAL;
    private int amount = 0;
    private Color outputColor = OFF;
    private ItemStack filter = ItemStackTools.getEmptyStack();

    public Sensor(int index) {
        this.index = index;
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

    public Color getOutputColor() {
        return outputColor;
    }

    public void setOutputColor(Color outputColor) {
        this.outputColor = outputColor;
    }

    public boolean isEnabled(String tag) {
        if ((TAG_MODE+index).equals(tag)) {
            return true;
        }
        if ((TAG_OPERATOR+index).equals(tag)) {
            return true;
        }
        if ((TAG_AMOUNT+index).equals(tag)) {
            return true;
        }
        if ((TAG_COLOR+index).equals(tag)) {
            return true;
        }
        if ((TAG_STACK+index).equals(tag)) {
            return sensorMode == SensorMode.FLUID || sensorMode == SensorMode.ITEM;
        }
        return false;
    }

    public void createGui(IEditorGui gui) {
        gui
                .choices(TAG_MODE+index, "Sensor mode", sensorMode, SensorMode.values())
                .choices(TAG_OPERATOR+index, "Operator", operator, Operator.values())
                .integer(TAG_AMOUNT+index, "Amount to compare with", amount)
                .colors(TAG_COLOR+index, "Output color", outputColor.getColor(), COLORS)
                .ghostSlot(TAG_STACK+index, filter)
                .nl();
    }

    public void update(Map<String, Object> data) {
        sensorMode = SensorMode.valueOf(((String) data.get(TAG_MODE+ index)).toUpperCase());
        operator = Operator.valueOfCode(((String) data.get(TAG_OPERATOR+ index)).toUpperCase());
        amount = (Integer) data.get(TAG_AMOUNT+ index);
        outputColor = Color.colorByValue((Integer) data.get(TAG_COLOR+ index));
        filter = (ItemStack) data.get(TAG_STACK+ index);
    }

    public void readFromNBT(NBTTagCompound tag) {
        sensorMode = SensorMode.values()[tag.getByte("sensorMode"+ index)];
        operator = Operator.values()[tag.getByte("operator"+ index)];
        amount = tag.getInteger("amount"+ index);
        outputColor = Color.values()[tag.getByte("color"+ index)];
        if (tag.hasKey("filter" + index)) {
            NBTTagCompound itemTag = tag.getCompoundTag("filter" + index);
            filter = ItemStackTools.loadFromNBT(itemTag);
        } else {
            filter = ItemStackTools.getEmptyStack();
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("sensorMode"+ index, (byte) sensorMode.ordinal());
        tag.setByte("operator"+ index, (byte) operator.ordinal());
        tag.setInteger("amount"+ index, amount);
        tag.setByte("color"+ index, (byte) outputColor.ordinal());
        if (ItemStackTools.isValid(filter)) {
            NBTTagCompound itemTag = new NBTTagCompound();
            filter.writeToNBT(itemTag);
            tag.setTag("filter" + index, itemTag);
        }
    }

}
